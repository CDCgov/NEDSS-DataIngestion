#!/usr/bin/env bash
# Orchestrates a combined local deployment of NEDSS-DataIngestion and
# NEDSS-DataReporting: one shared database, one shared Kafka cluster, one
# shared Debezium/Connect worker, both projects' app services on one
# Docker network ("nbs7-shared").
#
# It drives the two docker-compose.shared.yml files (one per project) as a
# single Compose project ("nbs7") via multiple -f flags.
#
# This file is intentionally duplicated (kept byte-identical) in
# NEDSS-DataReporting/scripts/nbs7-deploy.sh, since there is no dedicated
# shared/parent repo for this pair - either repo, checked out alongside its
# sibling, is a self-sufficient starting point for the combined stack. If you
# change this script, please mirror the change in the other repo's copy.
#
# Usage (run from either repo, once both are checked out as siblings under one
# parent folder):
#   ./scripts/nbs7-deploy.sh up [--build] [--sas]   # start everything (default command)
#   ./scripts/nbs7-deploy.sh down [--volumes]       # stop everything
#   ./scripts/nbs7-deploy.sh restart [--build] [--sas]
#   ./scripts/nbs7-deploy.sh ps
#   ./scripts/nbs7-deploy.sh logs [service...]
#   ./scripts/nbs7-deploy.sh build
#   ./scripts/nbs7-deploy.sh register-connectors    # (re)register the ingestion CDC connector
#
set -euo pipefail

NBS7_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
export NBS7_ROOT

REPORTING_DIR="$NBS7_ROOT/NEDSS-DataReporting"
INGESTION_DIR="$NBS7_ROOT/NEDSS-DataIngestion"
REPORTING_COMPOSE="$REPORTING_DIR/docker-compose.shared.yml"
INGESTION_COMPOSE="$INGESTION_DIR/docker-compose.shared.yml"
PROJECT_NAME="nbs7"

CONNECTOR_NAME="nbs-cdc-test"
CONNECTOR_FILE="$INGESTION_DIR/containers/debezium/connectors/connector.json"
DEBEZIUM_URL="http://localhost:8085/connectors"

for f in "$REPORTING_COMPOSE" "$INGESTION_COMPOSE"; do
  if [ ! -f "$f" ]; then
    echo "Missing expected compose file: $f" >&2
    exit 1
  fi
done

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is not installed or not on PATH." >&2
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "docker compose (v2) is required." >&2
  exit 1
fi

dc() {
  docker compose -p "$PROJECT_NAME" -f "$REPORTING_COMPOSE" -f "$INGESTION_COMPOSE" "$@"
}

register_connectors() {
  echo "==> Waiting for Debezium REST API at $DEBEZIUM_URL..."
  local tries=0
  until curl -sf "$DEBEZIUM_URL" >/dev/null 2>&1; do
    tries=$((tries + 1))
    if [ "$tries" -ge 60 ]; then
      echo "Debezium did not become reachable in time; skipping connector registration." >&2
      echo "Register manually later with: $0 register-connectors" >&2
      return 1
    fi
    sleep 3
  done

  if curl -sf "$DEBEZIUM_URL/$CONNECTOR_NAME" >/dev/null 2>&1; then
    echo "==> Connector '$CONNECTOR_NAME' already registered."
  else
    echo "==> Registering ingestion CDC connector '$CONNECTOR_NAME'..."
    curl -sf -i -X POST \
      -H "Accept:application/json" -H "Content-Type:application/json" \
      "$DEBEZIUM_URL/" -d @"$CONNECTOR_FILE"
    echo
  fi
}

print_urls() {
  cat <<EOF

==> Shared stack is up. Useful local endpoints:
  NBS 6 (Wildfly)........... http://localhost:7003/nbs/login
  Keycloak (DataIngestion)... http://localhost:8100
  data-ingestion-service..... http://localhost:8081/ingestion/swagger-ui/index.html
  data-processing-service.... http://localhost:8082/rti/swagger-ui/index.html
  Record Linker.............. http://localhost:8070
  reporting-pipeline-service.. http://localhost:8095
  Kafka Connect (JDBC sink).. http://localhost:8083/connectors
  Debezium (CDC source)...... http://localhost:8085/connectors
  Shared MSSQL............... localhost:3433 (alias: localhost:2433), sa / \${DATABASE_PASSWORD:-fake.fake.fake.1234}
EOF
}

cmd="${1:-up}"
[ $# -gt 0 ] && shift || true

case "$cmd" in
  up)
    BUILD_FLAG=()
    PROFILE_FLAG=()
    for arg in "$@"; do
      case "$arg" in
        --build) BUILD_FLAG=(--build) ;;
        --sas) PROFILE_FLAG=(--profile sas) ;;
        *) echo "Unknown argument to 'up': $arg" >&2; exit 1 ;;
      esac
    done
    echo "==> Starting shared nbs7 stack (project: $PROJECT_NAME)..."
    dc "${PROFILE_FLAG[@]+"${PROFILE_FLAG[@]}"}" up -d --wait "${BUILD_FLAG[@]+"${BUILD_FLAG[@]}"}"
    register_connectors || true
    print_urls
    ;;

  down)
    VOL_FLAG=()
    for arg in "$@"; do
      case "$arg" in
        --volumes) VOL_FLAG=(--volumes) ;;
        *) echo "Unknown argument to 'down': $arg" >&2; exit 1 ;;
      esac
    done
    echo "==> Stopping shared nbs7 stack..."
    dc down --remove-orphans "${VOL_FLAG[@]+"${VOL_FLAG[@]}"}"
    ;;

  restart)
    "$0" down
    "$0" up "$@"
    ;;

  ps)
    dc ps
    ;;

  logs)
    dc logs -f "$@"
    ;;

  build)
    dc --profile sas build "$@"
    ;;

  register-connectors)
    register_connectors
    ;;

  *)
    echo "Usage: $0 {up [--build] [--sas]|down [--volumes]|restart|ps|logs [service...]|build|register-connectors}" >&2
    exit 1
    ;;
esac
