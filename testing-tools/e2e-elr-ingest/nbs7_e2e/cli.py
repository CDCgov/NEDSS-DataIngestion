import argparse
import logging
import sys
from pathlib import Path

import requests

from .auth import AuthError, TokenManager
from .config import Config, load_config_from_env
from .db_status import check_elr_status
from .generate_hl7 import generate_files
from .hl7_files import discover_hl7_files, format_directory, formatted_dir_for, load_hl7_file
from .ingest_client import ElrIngestClient


def build_arg_parser(defaults: Config) -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(
        prog="nbs7-elr-ingest",
        description=(
            "Ingest HL7 ELR files from a directory into the nbs7 local "
            "data-ingestion-service / data-processing-service stack."
        ),
    )

    p.add_argument("--input-dir", required=True, help="Directory containing .hl7 files to submit (or to generate into, with --generate).")
    p.add_argument("--pattern", default="*.hl7", help="Glob pattern for HL7 files (default: *.hl7).")
    p.add_argument("--recursive", action="store_true", help="Recurse into subdirectories of --input-dir.")
    p.add_argument(
        "--generate",
        type=int,
        metavar="COUNT",
        help=(
            "Generate COUNT fake ELR HL7 files into --input-dir before ingesting "
            "(creates the directory if needed). Omit to ingest existing files instead."
        ),
    )

    p.add_argument("--msg-type", default=defaults.msg_type, help="msgType header value (default: hl7).")
    p.add_argument("--msg-version", default=defaults.msg_version, help="version header value (default: 2, RTI).")

    kc = p.add_argument_group("Keycloak (hop 1)")
    kc.add_argument("--keycloak-host", default=defaults.keycloak_host)
    kc.add_argument("--keycloak-port", type=int, default=defaults.keycloak_port)
    kc.add_argument("--keycloak-realm", default=defaults.keycloak_realm)

    dps = p.add_argument_group("data-processing-service (hop 2)")
    dps.add_argument("--dps-host", default=defaults.dps_host)
    dps.add_argument("--dps-port", type=int, default=defaults.dps_port)
    dps.add_argument("--dps-context-path", default=defaults.dps_context_path)

    dis = p.add_argument_group("data-ingestion-service (submission target)")
    dis.add_argument("--dis-host", default=defaults.dis_host)
    dis.add_argument("--dis-port", type=int, default=defaults.dis_port)
    dis.add_argument("--dis-context-path", default=defaults.dis_context_path)

    creds = p.add_argument_group("Client credentials")
    creds.add_argument("--client-id", default=defaults.client_id)
    creds.add_argument("--client-secret", default=defaults.client_secret)

    db = p.add_argument_group("Database (optional post-submission status check)")
    db.add_argument("--check-status", action="store_true", help="Query NBS_DataIngest after submission for each file's pipeline status.")
    db.add_argument("--db-host", default=defaults.db_host)
    db.add_argument("--db-port", type=int, default=defaults.db_port)
    db.add_argument("--db-user", default=defaults.db_user)
    db.add_argument("--db-password", default=defaults.db_password)
    db.add_argument("--db-name", default=defaults.db_name)
    db.add_argument("--db-driver", default=defaults.db_driver)

    p.add_argument("--request-timeout", type=float, default=defaults.request_timeout)
    p.add_argument("--dotenv", default=".env", help="Path to a .env file to load (default: ./.env, if present).")
    p.add_argument("-v", "--verbose", action="store_true", help="Debug logging.")

    return p


def config_from_args(args: argparse.Namespace) -> Config:
    return Config(
        keycloak_host=args.keycloak_host,
        keycloak_port=args.keycloak_port,
        keycloak_realm=args.keycloak_realm,
        dps_host=args.dps_host,
        dps_port=args.dps_port,
        dps_context_path=args.dps_context_path,
        dis_host=args.dis_host,
        dis_port=args.dis_port,
        dis_context_path=args.dis_context_path,
        client_id=args.client_id,
        client_secret=args.client_secret,
        db_host=args.db_host,
        db_port=args.db_port,
        db_user=args.db_user,
        db_password=args.db_password,
        db_name=args.db_name,
        db_driver=args.db_driver,
        msg_type=args.msg_type,
        msg_version=args.msg_version,
        request_timeout=args.request_timeout,
    )


def main(argv=None) -> int:
    # Load .env early so its values are visible as defaults when building the parser.
    early_defaults = load_config_from_env(
        next((a.split("=", 1)[1] for a in (argv or sys.argv[1:]) if a.startswith("--dotenv=")), ".env")
    )

    parser = build_arg_parser(early_defaults)
    args = parser.parse_args(argv)

    logging.basicConfig(
        level=logging.DEBUG if args.verbose else logging.INFO,
        format="%(asctime)s %(levelname)-7s %(name)s: %(message)s",
    )
    logger = logging.getLogger("nbs7_e2e.cli")

    config = config_from_args(args)

    input_dir = Path(args.input_dir)

    if args.generate is not None:
        logger.info("Generating %d fake ELR file(s) into %s", args.generate, input_dir)
        generated = generate_files(input_dir, args.generate)
        logger.info("Generated %d file(s)", len(generated))
    elif not input_dir.is_dir():
        logger.error("Input directory does not exist: %s", input_dir)
        return 2

    formatted_dir = formatted_dir_for(input_dir)
    logger.info("Formatting HL7 files from %s -> %s", input_dir, formatted_dir)
    written = format_directory(input_dir, pattern=args.pattern, recursive=args.recursive)
    if not written:
        logger.error("No files matching %r found in %s to format", args.pattern, input_dir)
        return 2
    logger.info("Wrote %d formatted file(s) to %s", len(written), formatted_dir)
    effective_dir = formatted_dir

    try:
        files = discover_hl7_files(str(effective_dir), pattern=args.pattern, recursive=args.recursive)
    except NotADirectoryError as exc:
        logger.error(str(exc))
        return 2

    if not files:
        logger.error("No files matching %r found in %s", args.pattern, effective_dir)
        return 2

    logger.info("Ingesting %d HL7 file(s) from %s", len(files), effective_dir)

    session = requests.Session()
    token_manager = TokenManager(config, session)
    client = ElrIngestClient(config, token_manager, session)

    try:
        token_manager.get_token()
    except AuthError as exc:
        logger.error("Initial authentication failed: %s", exc)
        return 1
    logger.info("Authenticated (Keycloak -> data-processing-service token chain OK).")

    results = []
    for path in files:
        try:
            payload = load_hl7_file(path)
        except (OSError, UnicodeDecodeError) as exc:
            logger.error("%s: failed to read file: %s", path, exc)
            results.append(None)
            continue

        try:
            result = client.submit(payload, source=str(path))
        except AuthError as exc:
            logger.error("%s: authentication failed during submission: %s", path, exc)
            results.append(None)
            continue
        except requests.RequestException as exc:
            logger.error("%s: request failed: %s", path, exc)
            results.append(None)
            continue

        results.append(result)
        retried_note = " (after token refresh + retry)" if result.retried_after_401 else ""
        if result.success:
            logger.info("%s: OK%s -> elr id(s): %s", path, retried_note, result.elr_id)
        else:
            logger.error(
                "%s: FAILED%s (HTTP %s): %s", path, retried_note, result.status_code, result.body[:300]
            )

    succeeded = [r for r in results if r and r.success]
    failed_count = len(results) - len(succeeded)

    print()
    print(f"Summary: {len(succeeded)} succeeded, {failed_count} failed, {len(files)} total")

    if args.check_status and succeeded:
        print("\nDB status check (NBS_DataIngest):")
        all_ids = []
        for r in succeeded:
            all_ids.extend(id_.strip() for id_ in r.elr_id.split(",") if id_.strip())
        status_rows = check_elr_status(config, all_ids)
        if status_rows is None:
            print("  (status check unavailable - see warning above)")
        else:
            for row in status_rows:
                if row["dlt_status"]:
                    print(f"  {row['elr_id']}: DEAD-LETTERED ({row['dlt_status']}) - {row.get('dlt_error', '')[:150]}")
                elif row["validated"]:
                    print(f"  {row['elr_id']}: raw=OK validated=OK")
                elif row["raw"]:
                    print(f"  {row['elr_id']}: raw=OK validated=pending")
                else:
                    print(f"  {row['elr_id']}: not found in elr_raw")

    return 0 if failed_count == 0 else 1


if __name__ == "__main__":
    sys.exit(main())
