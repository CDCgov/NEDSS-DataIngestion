"""
Configuration for the nbs7 end-to-end ELR ingestion tool.

Every value has a default matching the local `nbs7-deploy.sh` / docker-compose
deployment (see NEDSS-DataIngestion/docker-compose.shared.yml and
NEDSS-DataIngestion/containers/keycloak/imports/nbs.json). Every default can be
overridden by an environment variable, which can in turn be overridden by a CLI
flag. Precedence: CLI flag > environment variable > built-in default.
"""

import os
from dataclasses import dataclass, field
from pathlib import Path


def _load_dotenv_if_present(path: str = ".env") -> None:
    """Minimal, dependency-free .env loader. Does not override already-set env vars."""
    p = Path(path)
    if not p.is_file():
        return
    for line in p.read_text().splitlines():
        line = line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, _, value = line.partition("=")
        key = key.strip()
        value = value.strip().strip('"').strip("'")
        os.environ.setdefault(key, value)


def _env(name: str, default: str) -> str:
    return os.environ.get(name, default)


@dataclass
class Config:
    # --- Keycloak (hop 1: direct token from Keycloak) ---
    keycloak_host: str = field(default_factory=lambda: _env("NBS7_KEYCLOAK_HOST", "localhost"))
    keycloak_port: int = field(default_factory=lambda: int(_env("NBS7_KEYCLOAK_PORT", "8100")))
    keycloak_realm: str = field(default_factory=lambda: _env("NBS7_KEYCLOAK_REALM", "NBS"))

    # --- data-processing-service (hop 2: "another JWT", used for submission) ---
    dps_host: str = field(default_factory=lambda: _env("NBS7_DPS_HOST", "localhost"))
    dps_port: int = field(default_factory=lambda: int(_env("NBS7_DPS_PORT", "8082")))
    dps_context_path: str = field(default_factory=lambda: _env("NBS7_DPS_CONTEXT_PATH", "/rti"))

    # --- data-ingestion-service (final ELR submission target) ---
    dis_host: str = field(default_factory=lambda: _env("NBS7_DIS_HOST", "localhost"))
    dis_port: int = field(default_factory=lambda: int(_env("NBS7_DIS_PORT", "8081")))
    dis_context_path: str = field(default_factory=lambda: _env("NBS7_DIS_CONTEXT_PATH", "/ingestion"))

    # --- Keycloak client credentials (di-keycloak-client, from the local realm import) ---
    client_id: str = field(default_factory=lambda: _env("NBS7_CLIENT_ID", "di-keycloak-client"))
    client_secret: str = field(
        default_factory=lambda: _env("NBS7_CLIENT_SECRET", "OhBq1ar96aep8cnirHwkCNfgsO9yybZI")
    )

    # --- MSSQL (nbs-mssql container; used only for optional post-submission status checks) ---
    db_host: str = field(default_factory=lambda: _env("NBS7_DB_HOST", "localhost"))
    db_port: int = field(default_factory=lambda: int(_env("NBS7_DB_PORT", "2433")))
    db_user: str = field(default_factory=lambda: _env("NBS7_DB_USER", "sa"))
    db_password: str = field(default_factory=lambda: _env("NBS7_DB_PASSWORD", "fake.fake.fake.1234"))
    db_name: str = field(default_factory=lambda: _env("NBS7_DB_NAME", "NBS_DataIngest"))
    db_driver: str = field(
        default_factory=lambda: _env("NBS7_DB_DRIVER", "ODBC Driver 17 for SQL Server")
    )

    # --- ELR submission headers ---
    msg_type: str = field(default_factory=lambda: _env("NBS7_MSG_TYPE", "hl7"))
    msg_version: str = field(default_factory=lambda: _env("NBS7_MSG_VERSION", "2"))

    # --- request behavior ---
    request_timeout: float = field(
        default_factory=lambda: float(_env("NBS7_REQUEST_TIMEOUT", "30"))
    )

    @property
    def keycloak_token_url(self) -> str:
        return (
            f"http://{self.keycloak_host}:{self.keycloak_port}"
            f"/realms/{self.keycloak_realm}/protocol/openid-connect/token"
        )

    @property
    def dps_token_url(self) -> str:
        return f"http://{self.dps_host}:{self.dps_port}{self.dps_context_path}/api/auth/token"

    @property
    def dis_elr_url(self) -> str:
        return f"http://{self.dis_host}:{self.dis_port}{self.dis_context_path}/api/elrs"

    def dis_status_url(self, elr_id: str) -> str:
        return f"http://{self.dis_host}:{self.dis_port}{self.dis_context_path}/api/elrs/status/{elr_id}"


def load_config_from_env(dotenv_path: str = ".env") -> Config:
    _load_dotenv_if_present(dotenv_path)
    return Config()
