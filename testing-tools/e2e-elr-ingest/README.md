# nbs7 End-to-End ELR Ingestion Tester

Generates and/or submits HL7 ELR files into the local `nbs7` Docker Compose
deployment (`data-ingestion-service` + `data-processing-service` +
Keycloak + MSSQL), handling the two-hop auth flow and retrying automatically
if a token gets rejected mid-run. HL7 segment-terminator normalization always
runs before ingestion - there's no flag to opt out of it.

## Setup

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -e .
# optional, only needed for --check-status:
pip install -e ".[db]"
```

`--check-status` also requires the Microsoft ODBC Driver for SQL Server to be
installed on the host (`brew install msodbcsql18` on macOS via the
`microsoft/mssql-release` tap, or see Microsoft's docs for your platform).

## Usage

```bash
python main.py --input-dir /path/to/hl7/files            # ingest existing files
python main.py --input-dir /path/to/hl7/files --recursive --check-status
python main.py --input-dir ./generated --generate 1000    # generate 1000 fake ELRs, then ingest them
python main.py --help
```

Every host/port/credential has a default matching the local `nbs7-deploy.sh`
stack; override via `.env` (copy `.env.example`), real environment variables,
or CLI flags. Precedence: **CLI flag > environment variable > built-in
default**.

## Auth flow

1. **Hop 1** - `POST {keycloak}/realms/NBS/protocol/openid-connect/token`
   (`grant_type=client_credentials`) using `--client-id`/`--client-secret` -
   a JWT issued directly by Keycloak.
2. **Hop 2** - `POST {data-processing-service}/rti/api/auth/token` with
   `clientid`/`clientsecret` headers and the hop-1 JWT as `Authorization:
   Bearer`. Returns a second JWT, which is what's actually used for
   submission.
3. **Submission** - `POST {data-ingestion-service}/ingestion/api/elrs` with
   the hop-2 JWT (`Authorization: Bearer`), plus `clientid`/`clientsecret`,
   `msgType`, and `version` headers, and the raw HL7 text as the body.

data-ingestion-service validates the token via live **OAuth2 token
introspection** against Keycloak on every request (not local JWT signature
checking), so an expired/revoked token fails at request time with HTTP 401.
When that happens, the tool refreshes the token (both hops) and retries the
same submission exactly once before giving up on that file.

(Implementation note: data-processing-service's token endpoint is
Spring-Security-whitelisted and its own code never actually reads the
incoming Authorization header - it independently re-fetches a token from
Keycloak using the client credentials. So hop 1's JWT isn't strictly required
for hop 2 to succeed. This tool still performs hop 1 and forwards its token
into hop 2, per the two-hop flow this was built to follow.)

## Generating fake ELRs

Pass `--generate COUNT` to generate `COUNT` synthetic HL7 v2.5.1 ORU^R01 ELR
messages into `--input-dir` (creating the directory if needed) before
formatting/ingesting - random patients, facilities, and lab results/LOINC
codes drawn from a small fixed pool designed to match the local NBS_SRTE dev
reference data. Ported from
`NEDSS-DataReporting/testing-tools/performance-testing/generate.py` into
`nbs7_e2e/generate_hl7.py`. Omit `--generate` to ingest files that already
exist in `--input-dir` instead.

```bash
python main.py --input-dir ./generated --generate 1000
```

## HL7 file handling

Files are read from `--input-dir` (glob `--pattern`, default `*.hl7`;
`--recursive` to walk subdirectories). Every matching file is always
normalized to explicit CRLF (`\r\n`) segment terminators and written into a
new sibling directory named `<input-dir name>-formatted` (relative paths
preserved under `--recursive`) - handling files that use bare `\r`, `\n`, or
(in the worst case) no terminators at all - and the tool ingests from that
formatted directory. There's no flag to skip this; it always runs.

```bash
# input:  ./hl7_input/*.hl7
# output: ./hl7_input-formatted/*.hl7  (ingested from here)
python main.py --input-dir ./hl7_input
```

## Status check

`--check-status` looks up each returned raw-message id in
`NBS_DataIngest.dbo.elr_raw` / `elr_validated` / `elr_dlt` after submission
and prints a one-line summary per file (validated OK, dead-lettered with
error, or still pending). This is a convenience layer on top of HTTP
ingestion, not a substitute for it - ingestion success/failure is always
determined by the HTTP response first.

## Project layout

```
main.py               entry point
nbs7_e2e/
  config.py            defaults + env var / CLI overrides
  auth.py               two-hop token manager
  jwt_utils.py           local (non-verifying) exp-claim decode, for proactive refresh
  generate_hl7.py         fake ELR HL7 generator (ported from NEDSS-DataReporting)
  hl7_files.py              directory discovery + segment-terminator normalization
  ingest_client.py            submission + retry-on-401
  db_status.py                  optional post-submission DB lookup
  cli.py                          argparse wiring
```
