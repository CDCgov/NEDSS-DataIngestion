# NBS7 Local Suite: Deployment, ELR Generation, and Performance Testing

This guide covers standing up the full combined `NEDSS-DataIngestion` +
`NEDSS-DataReporting` stack locally, generating synthetic ELR test data, and
running/observing a performance test - all without needing a shared
environment or a Prometheus/Grafana instance.

This file is intentionally duplicated (kept byte-identical) in
`NEDSS-DataReporting/PERFORMANCE_TESTING_GUIDE.md`. If you update it, please
mirror the change in the other repo's copy.

## 1. Prerequisites

- Docker + Docker Compose v2
- Both repos checked out as **sibling directories** under one common parent
  folder, e.g.:
  ```
  ~/dev/nbs7/
    NEDSS-DataIngestion/
    NEDSS-DataReporting/
  ```
- Python 3.9+ (for the ELR generation/ingest tool and the metrics script)
- Microsoft ODBC Driver 17 for SQL Server - optional, only needed for
  `--check-status` on the ingest tool and the database checks in
  `nbs7_metrics.py`

## 2. Deploy the full nbs7 suite locally

```bash
./scripts/nbs7-deploy.sh up
```

Run from either repo's root - it drives both repos' `docker-compose.shared.yml`
files together as one Compose project (`nbs7`). Add `--build` to rebuild
images, `--sas` to also start the SAS container.

This starts: shared MSSQL (serving both projects' databases), shared Kafka,
shared Debezium/Kafka Connect, and both projects' app services
(`data-ingestion-service`, `data-processing-service`,
`reporting-pipeline-service`, `di-record-linker`, `di-keycloak`, `wildfly`) -
all on one Docker network (`nbs7-shared`).

Useful endpoints (printed on startup):

| Service | URL |
| --- | --- |
| NBS 6 (Wildfly) | http://localhost:7003/nbs/login |
| Keycloak | http://localhost:8100 |
| data-ingestion-service | http://localhost:8081/ingestion/swagger-ui/index.html |
| data-processing-service | http://localhost:8082/rti/swagger-ui/index.html |
| reporting-pipeline-service | http://localhost:8095 |
| Kafka Connect (JDBC sink) | http://localhost:8083/connectors |
| Debezium (CDC source) | http://localhost:8085/connectors |
| Shared MSSQL | `localhost:3433` (alias `localhost:2433`), `sa` / `DATABASE_PASSWORD` (default `fake.fake.fake.1234`) |

Other commands: `down [--volumes]`, `restart [--build] [--sas]`, `ps`,
`logs [service...]`, `build`, `register-connectors`.

### Known gotcha: Debezium connectors after an MSSQL restart

If the `nbs-mssql` container restarts (host reboot, manual restart, resource
pressure, etc.), the ODSE-facing Debezium source connectors
(`odse-main-connector`, `odse-schema-only-connector`, `odse-meta-connector`)
can come back with the *connector* marked `RUNNING` but the underlying *task*
`FAILED` - and they do not self-heal.

**Symptom:** `reporting-pipeline-service`'s domain counters
(`person_msg_processed_total`, etc.) stop moving entirely even though
ingestion (RTI) is completing successfully, while `/actuator/lag` still
reports `READY` / `caughtUp: true`. That's misleading here - a dead source
connector simply means there's nothing new arriving to lag behind, so it
looks identical to "caught up, all good."

Check connector/task health directly:
```bash
curl -s http://localhost:8085/connectors/odse-main-connector/status | python3 -m json.tool
```
If a task shows `FAILED`, restart it:
```bash
curl -X POST http://localhost:8085/connectors/odse-main-connector/restart
# or just the failed task:
curl -X POST http://localhost:8085/connectors/odse-main-connector/tasks/0/restart
```

## 3. Generate ELRs

The `nbs7-e2e-elr-ingest` CLI (`testing-tools/e2e-elr-ingest/`) generates
synthetic HL7 v2.5.1 ELRs and/or ingests existing ones through the full auth
chain (Keycloak -> data-processing-service -> data-ingestion-service).

Setup:
```bash
cd testing-tools/e2e-elr-ingest
python3 -m venv .venv
source .venv/bin/activate
pip install -e .
pip install -e ".[db]"   # optional: needed for --check-status
```

Generate + ingest in one step:
```bash
python main.py --input-dir ./generated --generate 1000
```
This generates 1,000 fake HL7 ELRs into `./generated`, normalizes their
segment terminators into a sibling `./generated-formatted` directory,
authenticates through the two-hop token chain, and submits each file to
`data-ingestion-service` - automatically refreshing and retrying once if a
token gets rejected mid-run.

Ingest existing files instead of generating:
```bash
python main.py --input-dir /path/to/existing/hl7/files
```

Every host/port/credential defaults to the local `nbs7-deploy.sh` stack and
can be overridden via `.env` (copy `.env.example`), real environment
variables, or CLI flags - see `testing-tools/e2e-elr-ingest/README.md` for
the full flag reference and a detailed explanation of the auth flow.

## 4. Execute performance testing

Combine ELR generation with `scripts/nbs7_metrics.py` to run and observe a
load test without standing up Prometheus/Grafana.

### Basic flow

```bash
# 1. Baseline snapshot before the run
python3 scripts/nbs7_metrics.py snapshot --out baseline.json

# 2. Generate + ingest a large batch
cd testing-tools/e2e-elr-ingest
python main.py --input-dir ./perf-batch --generate 20000
cd ../..

# 3. Watch it drain through data-processing-service and reporting-pipeline-service
python3 scripts/nbs7_metrics.py watch-drain

# 4. Compare against the baseline once drained
python3 scripts/nbs7_metrics.py diff --baseline baseline.json
```

`watch-drain` accepts `--interval` (seconds between checks, default 30),
`--consecutive` (drained checks required before declaring done, default 3),
and `--stall-after` (checks with no progress before warning, default 10).

### What `watch-drain` checks

Polls `NBS_interface.record_status_cd` (specifically `RTI_PENDING`) in
`NBS_MSGOUTE` and `reporting-pipeline-service`'s `/actuator/lag`, declaring
the batch fully drained once `RTI_PENDING` reaches 0 and Kafka lag is caught
up, held across several consecutive checks.

It also detects the specific stall failure mode found during APP-850
performance testing: if `RTI_PENDING` stops changing for several checks
while still > 0, that's not "slow" - it's a silently stalled async
processing queue, usually triggered by a SQL Server deadlock storm under
concurrent load. If you see this warning, check `data-processing-service`
logs for `SQLState: 40001` / "chosen as the deadlock victim" errors around
the time progress stopped.

### What "healthy" looks like

- `NBS_interface`: everything reaches `RTI_SUCCESS` (a handful of
  pre-existing `RTI_FAILURE_STEP_1`/`Failure`/`QUEUED` rows are normal
  baseline noise; a small number of new failures per run is expected
  data-quality noise in synthetic samples, not necessarily a bug)
- `RDB_MODERN.nrt_dead_letter_log` / `nrt_backfill`: no growth
- HikariCP (`reporting-pipeline-service`): 0 pending, 0 timeouts throughout
- `reporting-pipeline-service` domain counters (`person_msg`, `org_msg`,
  `obs_msg`, `post_msg`): failure counters stay at 0, processed counters
  climb roughly in line with the batch size

### Interpreting a stall (no reporting-side movement)

If ingestion (RTI) succeeds but `reporting-pipeline-service`'s counters never
move, don't assume it's just slow - check Debezium connector/task status
directly (see the gotcha in section 2). `/actuator/lag` alone can look
"caught up" even when the underlying CDC source is completely dead.

## Reference

- `scripts/nbs7-deploy.sh` - stack orchestrator (duplicated identically in both repos)
- `scripts/nbs7_metrics.py` - metrics polling: `snapshot` / `diff` / `watch-drain` (duplicated identically in both repos)
- `testing-tools/e2e-elr-ingest/` - ELR generation/ingestion CLI (duplicated identically in both repos)
- Findings referenced above (the deadlock-driven async stall, the Debezium connector recovery gap) are documented in detail on APP-850.
