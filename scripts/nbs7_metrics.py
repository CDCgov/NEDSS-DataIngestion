#!/usr/bin/env python3
"""
Polls observability metrics for the combined nbs7 local stack (see
scripts/nbs7-deploy.sh) without needing a Prometheus/Grafana instance -
scrapes reporting-pipeline-service's /actuator/prometheus and /actuator/lag
directly, optionally data-ingestion-service's /actuator/prometheus, and
queries NBS_MSGOUTE/NBS_DataIngest/RDB_MODERN for pipeline status.

This is intentionally duplicated (kept byte-identical) in
NEDSS-DataReporting/scripts/nbs7_metrics.py - either repo, checked out
alongside its sibling, is a self-sufficient starting point for the combined
stack. If you change this script, please mirror the change in the other
repo's copy.

Every host/port/credential defaults to the local nbs7-deploy.sh stack and can
be overridden by environment variable (see Config below).

Usage:
    python3 scripts/nbs7_metrics.py snapshot
    python3 scripts/nbs7_metrics.py snapshot --out baseline.json
    python3 scripts/nbs7_metrics.py diff --baseline baseline.json
    python3 scripts/nbs7_metrics.py diff --baseline baseline.json --current final.json
    python3 scripts/nbs7_metrics.py watch-drain
    python3 scripts/nbs7_metrics.py watch-drain --interval 30 --stall-after 10

Requires: requests. Optional: pyodbc + the Microsoft ODBC Driver for SQL
Server (DB-side metrics are skipped with a warning if unavailable).
"""

import argparse
import json
import os
import re
import sys
import time
from dataclasses import dataclass, field
from datetime import datetime, timezone
from typing import Dict, List, Optional

import requests

try:
    import pyodbc

    PYODBC_AVAILABLE = True
except ImportError:
    PYODBC_AVAILABLE = False


def _env(name: str, default: str) -> str:
    return os.environ.get(name, default)


@dataclass
class Config:
    reporting_host: str = field(default_factory=lambda: _env("NBS7_REPORTING_HOST", "localhost"))
    reporting_port: int = field(default_factory=lambda: int(_env("NBS7_REPORTING_PORT", "8095")))

    di_host: str = field(default_factory=lambda: _env("NBS7_DI_HOST", "localhost"))
    di_port: int = field(default_factory=lambda: int(_env("NBS7_DI_PORT", "8081")))
    di_context_path: str = field(default_factory=lambda: _env("NBS7_DI_CONTEXT_PATH", "/ingestion"))

    keycloak_host: str = field(default_factory=lambda: _env("NBS7_KEYCLOAK_HOST", "localhost"))
    keycloak_port: int = field(default_factory=lambda: int(_env("NBS7_KEYCLOAK_PORT", "8100")))
    keycloak_realm: str = field(default_factory=lambda: _env("NBS7_KEYCLOAK_REALM", "NBS"))
    client_id: str = field(default_factory=lambda: _env("NBS7_CLIENT_ID", "di-keycloak-client"))
    client_secret: str = field(
        default_factory=lambda: _env("NBS7_CLIENT_SECRET", "OhBq1ar96aep8cnirHwkCNfgsO9yybZI")
    )

    db_host: str = field(default_factory=lambda: _env("NBS7_DB_HOST", "localhost"))
    db_port: int = field(default_factory=lambda: int(_env("NBS7_DB_PORT", "2433")))
    db_user: str = field(default_factory=lambda: _env("NBS7_DB_USER", "sa"))
    # DATABASE_PASSWORD matches docker-compose.shared.yml's own var name.
    db_password: str = field(default_factory=lambda: _env("DATABASE_PASSWORD", "fake.fake.fake.1234"))
    db_driver: str = field(default_factory=lambda: _env("NBS7_DB_DRIVER", "ODBC Driver 17 for SQL Server"))

    request_timeout: float = 10.0

    @property
    def reporting_prometheus_url(self) -> str:
        return f"http://{self.reporting_host}:{self.reporting_port}/actuator/prometheus"

    @property
    def reporting_lag_url(self) -> str:
        return f"http://{self.reporting_host}:{self.reporting_port}/actuator/lag"

    @property
    def di_prometheus_url(self) -> str:
        return f"http://{self.di_host}:{self.di_port}{self.di_context_path}/actuator/prometheus"

    @property
    def keycloak_token_url(self) -> str:
        return f"http://{self.keycloak_host}:{self.keycloak_port}/realms/{self.keycloak_realm}/protocol/openid-connect/token"


# --- Prometheus text format -------------------------------------------------

_PROM_LINE_RE = re.compile(r"^(\S+?)(\{[^}]*\})?\s+([0-9.eE+-]+)$")

REPORTING_DOMAIN_PREFIXES = [
    "person_msg", "org_msg", "obs_msg", "inv_msg", "ldf_msg", "ntf_msg", "post_msg", "post_dm",
]
REPORTING_HIKARI_METRICS = [
    "hikaricp_connections_active", "hikaricp_connections_idle", "hikaricp_connections_pending",
    "hikaricp_connections_max", "hikaricp_connections_min", "hikaricp_connections_timeout_total",
    "hikaricp_connections_acquire_seconds_max",
]
REPORTING_JVM_METRICS = [
    "jvm_memory_used_bytes", "jvm_gc_pause_seconds_sum", "jvm_gc_pause_seconds_count",
    "process_cpu_usage", "system_cpu_usage",
]
DI_METRIC_PREFIXES = ["elr_raw", "elr_validated", "elr_dlt", "tokens_requested", "messages_processed"]


def parse_prometheus_text(text: str, prefixes: List[str]) -> Dict[str, float]:
    """Parse a Prometheus exposition-format text body into {metric_key: value},
    keeping only lines whose metric name starts with one of `prefixes`."""
    values: Dict[str, float] = {}
    for line in text.splitlines():
        if not line or line.startswith("#"):
            continue
        m = _PROM_LINE_RE.match(line.strip())
        if not m:
            continue
        name = m.group(1)
        if not any(name.startswith(p) for p in prefixes):
            continue
        key = name + (m.group(2) or "")
        values[key] = float(m.group(3))
    return values


# --- Fetchers ----------------------------------------------------------------

def fetch_reporting_metrics(config: Config) -> Dict[str, float]:
    resp = requests.get(config.reporting_prometheus_url, timeout=config.request_timeout)
    resp.raise_for_status()
    prefixes = REPORTING_DOMAIN_PREFIXES + REPORTING_HIKARI_METRICS + REPORTING_JVM_METRICS
    return parse_prometheus_text(resp.text, prefixes)


def fetch_lag(config: Config) -> Optional[dict]:
    try:
        resp = requests.get(config.reporting_lag_url, timeout=config.request_timeout)
        resp.raise_for_status()
        return resp.json()
    except requests.RequestException as exc:
        print(f"warning: could not fetch {config.reporting_lag_url}: {exc}", file=sys.stderr)
        return None


def get_keycloak_token(config: Config) -> Optional[str]:
    try:
        resp = requests.post(
            config.keycloak_token_url,
            data={
                "grant_type": "client_credentials",
                "client_id": config.client_id,
                "client_secret": config.client_secret,
            },
            timeout=config.request_timeout,
        )
        resp.raise_for_status()
        return resp.json()["access_token"]
    except (requests.RequestException, KeyError, ValueError) as exc:
        print(f"warning: could not fetch a Keycloak token: {exc}", file=sys.stderr)
        return None


def fetch_di_metrics(config: Config) -> Dict[str, float]:
    token = get_keycloak_token(config)
    if not token:
        return {}
    headers = {
        "Authorization": f"Bearer {token}",
        "clientid": config.client_id,
        "clientsecret": config.client_secret,
    }
    try:
        resp = requests.get(config.di_prometheus_url, headers=headers, timeout=config.request_timeout)
        resp.raise_for_status()
    except requests.RequestException as exc:
        print(f"warning: could not fetch {config.di_prometheus_url}: {exc}", file=sys.stderr)
        return {}
    return parse_prometheus_text(resp.text, DI_METRIC_PREFIXES)


def _db_connect(config: Config, database: str):
    conn_str = (
        f"DRIVER={{{config.db_driver}}};SERVER={config.db_host},{config.db_port};"
        f"DATABASE={database};UID={config.db_user};PWD={config.db_password};"
        "TrustServerCertificate=yes"
    )
    return pyodbc.connect(conn_str, timeout=10)


def fetch_db_status(config: Config) -> Optional[dict]:
    if not PYODBC_AVAILABLE:
        print("warning: pyodbc not installed - skipping DB status (pip install pyodbc)", file=sys.stderr)
        return None

    result: dict = {}
    try:
        conn = _db_connect(config, "NBS_DataIngest")
        cur = conn.cursor()
        cur.execute("SELECT COUNT(*) FROM dbo.elr_raw")
        result["elr_raw_count"] = cur.fetchone()[0]
        cur.execute("SELECT COUNT(*) FROM dbo.elr_validated")
        result["elr_validated_count"] = cur.fetchone()[0]
        cur.execute("SELECT dlt_status, COUNT(*) FROM dbo.elr_dlt GROUP BY dlt_status")
        result["elr_dlt_status"] = {row[0]: row[1] for row in cur.fetchall()}
        conn.close()
    except pyodbc.Error as exc:
        print(f"warning: could not query NBS_DataIngest: {exc}", file=sys.stderr)

    try:
        conn = _db_connect(config, "NBS_MSGOUTE")
        cur = conn.cursor()
        cur.execute("SELECT record_status_cd, COUNT(*) FROM dbo.NBS_interface GROUP BY record_status_cd")
        result["nbs_interface_status"] = {row[0]: row[1] for row in cur.fetchall()}
        conn.close()
    except pyodbc.Error as exc:
        print(f"warning: could not query NBS_MSGOUTE: {exc}", file=sys.stderr)

    try:
        conn = _db_connect(config, "RDB_MODERN")
        cur = conn.cursor()
        cur.execute("SELECT COUNT(*) FROM dbo.nrt_dead_letter_log")
        result["nrt_dead_letter_log_count"] = cur.fetchone()[0]
        cur.execute("SELECT COUNT(*) FROM dbo.nrt_backfill")
        result["nrt_backfill_count"] = cur.fetchone()[0]
        cur.execute("SELECT COUNT(*) FROM dbo.job_flow_log")
        result["job_flow_log_count"] = cur.fetchone()[0]
        conn.close()
    except pyodbc.Error as exc:
        print(f"warning: could not query RDB_MODERN: {exc}", file=sys.stderr)

    return result or None


# --- snapshot / diff ----------------------------------------------------------

def take_snapshot(config: Config, include_di: bool = True, include_db: bool = True) -> dict:
    snapshot = {
        "timestamp": datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ"),
        "reporting_pipeline": fetch_reporting_metrics(config),
        "lag": fetch_lag(config),
    }
    if include_di:
        snapshot["data_ingestion"] = fetch_di_metrics(config)
    if include_db:
        snapshot["db"] = fetch_db_status(config)
    return snapshot


def _fmt_metric_key(key: str) -> str:
    return key.split("{")[0]


def print_snapshot(snapshot: dict) -> None:
    print(f"Snapshot @ {snapshot['timestamp']}\n")

    print("=== reporting-pipeline-service: per-domain counters ===")
    for key in sorted(snapshot["reporting_pipeline"]):
        base = _fmt_metric_key(key)
        if any(base.startswith(p) for p in REPORTING_DOMAIN_PREFIXES):
            print(f"  {key:60s} {snapshot['reporting_pipeline'][key]:.0f}")

    print("\n=== reporting-pipeline-service: HikariCP ===")
    for key in sorted(snapshot["reporting_pipeline"]):
        base = _fmt_metric_key(key)
        if any(base.startswith(p) for p in REPORTING_HIKARI_METRICS):
            print(f"  {key:60s} {snapshot['reporting_pipeline'][key]}")

    print("\n=== reporting-pipeline-service: JVM ===")
    for key in sorted(snapshot["reporting_pipeline"]):
        base = _fmt_metric_key(key)
        if any(base.startswith(p) for p in REPORTING_JVM_METRICS):
            print(f"  {key:60s} {snapshot['reporting_pipeline'][key]}")

    if snapshot.get("lag") is not None:
        print("\n=== reporting-pipeline-service: /actuator/lag ===")
        print(f"  {json.dumps(snapshot['lag'])}")

    if snapshot.get("data_ingestion"):
        print("\n=== data-ingestion-service ===")
        for key in sorted(snapshot["data_ingestion"]):
            print(f"  {key:60s} {snapshot['data_ingestion'][key]}")

    if snapshot.get("db"):
        db = snapshot["db"]
        print("\n=== database status ===")
        if "elr_raw_count" in db:
            print(f"  NBS_DataIngest.elr_raw:       {db['elr_raw_count']}")
            print(f"  NBS_DataIngest.elr_validated: {db['elr_validated_count']}")
            print(f"  NBS_DataIngest.elr_dlt:       {db.get('elr_dlt_status', {})}")
        if "nbs_interface_status" in db:
            print(f"  NBS_MSGOUTE.NBS_interface:    {db['nbs_interface_status']}")
        if "nrt_dead_letter_log_count" in db:
            print(f"  RDB_MODERN.nrt_dead_letter_log: {db['nrt_dead_letter_log_count']}")
            print(f"  RDB_MODERN.nrt_backfill:        {db['nrt_backfill_count']}")
            print(f"  RDB_MODERN.job_flow_log:        {db['job_flow_log_count']}")


def print_diff(baseline: dict, current: dict) -> None:
    print(f"Baseline @ {baseline['timestamp']}  ->  Current @ {current['timestamp']}\n")

    print("=== reporting-pipeline-service: per-domain counters (delta) ===")
    print(f"  {'metric':60s} {'baseline':>10s} {'current':>10s} {'delta':>10s}")
    keys = sorted(set(baseline["reporting_pipeline"]) | set(current["reporting_pipeline"]))
    for key in keys:
        base = _fmt_metric_key(key)
        if not any(base.startswith(p) for p in REPORTING_DOMAIN_PREFIXES):
            continue
        b = baseline["reporting_pipeline"].get(key, 0)
        c = current["reporting_pipeline"].get(key, 0)
        print(f"  {key:60s} {b:>10.0f} {c:>10.0f} {c - b:>10.0f}")

    if baseline.get("db") and current.get("db"):
        b_db, c_db = baseline["db"], current["db"]
        print("\n=== database status (baseline -> current) ===")
        if "elr_raw_count" in c_db:
            print(f"  NBS_DataIngest.elr_raw:       {b_db.get('elr_raw_count')} -> {c_db.get('elr_raw_count')}")
            print(f"  NBS_DataIngest.elr_validated: {b_db.get('elr_validated_count')} -> {c_db.get('elr_validated_count')}")
        if "nbs_interface_status" in c_db:
            print(f"  NBS_MSGOUTE.NBS_interface:    {b_db.get('nbs_interface_status')} -> {c_db.get('nbs_interface_status')}")
        if "nrt_dead_letter_log_count" in c_db:
            print(f"  RDB_MODERN.nrt_dead_letter_log: {b_db.get('nrt_dead_letter_log_count')} -> {c_db.get('nrt_dead_letter_log_count')}")
            print(f"  RDB_MODERN.nrt_backfill:        {b_db.get('nrt_backfill_count')} -> {c_db.get('nrt_backfill_count')}")

    if current.get("lag") is not None:
        print(f"\n=== current /actuator/lag ===\n  {json.dumps(current['lag'])}")


# --- watch-drain ---------------------------------------------------------------

def watch_drain(config: Config, interval: int, consecutive_required: int, stall_after: int) -> int:
    """
    Poll NBS_interface.RTI_PENDING (data-processing-service's async queue) and
    reporting-pipeline-service's /actuator/lag until both are fully drained,
    holding for `consecutive_required` consecutive checks.

    Also watches for a stall: if RTI_PENDING stops changing for `stall_after`
    consecutive checks while still > 0, prints a warning (this exact pattern -
    Kafka lag hitting 0 while NBS_interface.RTI_PENDING sits frozen - is what a
    silent async-executor stall (e.g. after a burst of SQL deadlocks) looks
    like; see data-processing-service logs for "Error processing NBS" /
    deadlock errors around when RTI_PENDING stopped moving).
    """
    if not PYODBC_AVAILABLE:
        print("error: pyodbc is required for watch-drain (pip install pyodbc)", file=sys.stderr)
        return 2

    consecutive_done = 0
    last_pending: Optional[int] = None
    unchanged_checks = 0
    warned_stall = False

    while consecutive_done < consecutive_required:
        ts = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
        try:
            conn = _db_connect(config, "NBS_MSGOUTE")
            cur = conn.cursor()
            cur.execute("SELECT record_status_cd, COUNT(*) FROM dbo.NBS_interface GROUP BY record_status_cd")
            nbs = {row[0]: row[1] for row in cur.fetchall()}
            conn.close()
        except pyodbc.Error as exc:
            print(f"{ts} error querying NBS_interface: {exc}", file=sys.stderr)
            time.sleep(interval)
            continue

        lag = fetch_lag(config)
        pending = nbs.get("RTI_PENDING", 0)
        caught_up = bool(lag and lag.get("details", {}).get("caughtUp"))
        done_now = pending == 0 and caught_up

        print(f"{ts} RTI_PENDING={pending} lag_caughtUp={caught_up} done_now={done_now}")

        if pending == last_pending and pending > 0:
            unchanged_checks += 1
            if unchanged_checks >= stall_after and not warned_stall:
                print(
                    f"  WARNING: RTI_PENDING has not moved in {unchanged_checks} checks "
                    f"({unchanged_checks * interval}s) while still > 0 and Kafka lag is caught up. "
                    "This looks like a stalled async processing queue in data-processing-service, "
                    "not slow processing - check its logs for deadlock errors "
                    "('Error processing NBS ...', SQLState 40001).",
                    file=sys.stderr,
                )
                warned_stall = True
        else:
            unchanged_checks = 0
            warned_stall = False
        last_pending = pending

        consecutive_done = consecutive_done + 1 if done_now else 0
        if consecutive_done < consecutive_required:
            time.sleep(interval)

    print(f"\n=== FULLY DRAINED ({consecutive_required} consecutive checks) ===")
    return 0


# --- CLI -----------------------------------------------------------------------

def main(argv=None) -> int:
    parser = argparse.ArgumentParser(
        description="Poll nbs7 stack observability metrics (reporting-pipeline-service "
        "prometheus/lag, data-ingestion-service prometheus, and DB pipeline status) "
        "without standing up Prometheus/Grafana."
    )
    sub = parser.add_subparsers(dest="command", required=True)

    p_snap = sub.add_parser("snapshot", help="Capture and print a metrics snapshot.")
    p_snap.add_argument("--out", help="Also write the snapshot as JSON to this file (for later `diff`).")
    p_snap.add_argument("--no-di", action="store_true", help="Skip data-ingestion-service metrics.")
    p_snap.add_argument("--no-db", action="store_true", help="Skip database status checks.")

    p_diff = sub.add_parser("diff", help="Compare a baseline snapshot against current (or another saved) state.")
    p_diff.add_argument("--baseline", required=True, help="Path to a JSON snapshot from `snapshot --out`.")
    p_diff.add_argument("--current", help="Path to another saved snapshot. Omit to poll live current state.")

    p_watch = sub.add_parser("watch-drain", help="Poll until the pipeline is fully drained.")
    p_watch.add_argument("--interval", type=int, default=30, help="Seconds between checks (default: 30).")
    p_watch.add_argument("--consecutive", type=int, default=3, help="Consecutive drained checks required (default: 3).")
    p_watch.add_argument("--stall-after", type=int, default=10, help="Warn if RTI_PENDING is unchanged for this many checks (default: 10).")

    args = parser.parse_args(argv)
    config = Config()

    if args.command == "snapshot":
        snapshot = take_snapshot(config, include_di=not args.no_di, include_db=not args.no_db)
        print_snapshot(snapshot)
        if args.out:
            with open(args.out, "w") as f:
                json.dump(snapshot, f, indent=2)
            print(f"\nWrote {args.out}")
        return 0

    if args.command == "diff":
        with open(args.baseline) as f:
            baseline = json.load(f)
        if args.current:
            with open(args.current) as f:
                current = json.load(f)
        else:
            current = take_snapshot(config)
        print_diff(baseline, current)
        return 0

    if args.command == "watch-drain":
        return watch_drain(config, args.interval, args.consecutive, args.stall_after)

    return 1


if __name__ == "__main__":
    sys.exit(main())
