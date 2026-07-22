"""
Optional post-submission status check against the local MSSQL container.

Requires the `db` extra (pyodbc) and the Microsoft ODBC Driver for SQL Server
to be installed on the host. Failures here (missing driver, unreachable DB)
are reported but never abort a run - ingestion already happened over HTTP;
this is just a convenience lookup mirroring elr_raw -> elr_validated ->
elr_dlt / NBS_interface.record_status_cd.
"""

import logging
from typing import List, Optional

from .config import Config

logger = logging.getLogger(__name__)

try:
    import pyodbc

    PYODBC_AVAILABLE = True
except ImportError:
    PYODBC_AVAILABLE = False


def _connect(config: Config, database: str):
    conn_str = (
        f"DRIVER={{{config.db_driver}}};SERVER={config.db_host},{config.db_port};"
        f"DATABASE={database};UID={config.db_user};PWD={config.db_password};"
        "TrustServerCertificate=yes"
    )
    return pyodbc.connect(conn_str, timeout=10)


def check_elr_status(config: Config, elr_ids: List[str]) -> Optional[List[dict]]:
    """
    Look up each raw-message id (as returned by POST /api/elrs) across
    elr_raw / elr_validated / elr_dlt in NBS_DataIngest.

    Returns None (with a logged reason) if the check couldn't be performed at
    all; otherwise a list of per-id status dicts.
    """
    if not PYODBC_AVAILABLE:
        logger.warning(
            "pyodbc not installed - skipping DB status check. "
            "Install with: pip install '.[db]'"
        )
        return None

    try:
        conn = _connect(config, config.db_name)
    except Exception as exc:  # noqa: BLE001 - report and degrade, don't crash the run
        logger.warning("Could not connect to %s for status check: %s", config.db_name, exc)
        return None

    results = []
    try:
        cur = conn.cursor()
        for elr_id in elr_ids:
            row = {"elr_id": elr_id, "raw": False, "validated": False, "dlt_status": None}

            cur.execute("SELECT 1 FROM dbo.elr_raw WHERE id = ?", elr_id)
            row["raw"] = cur.fetchone() is not None

            cur.execute("SELECT 1 FROM dbo.elr_validated WHERE raw_message_id = ?", elr_id)
            row["validated"] = cur.fetchone() is not None

            cur.execute(
                "SELECT dlt_status, error_stack_trace_short FROM dbo.elr_dlt WHERE error_message_id = ?",
                elr_id,
            )
            dlt_row = cur.fetchone()
            if dlt_row:
                row["dlt_status"] = dlt_row[0]
                row["dlt_error"] = dlt_row[1]

            results.append(row)
    finally:
        conn.close()

    return results
