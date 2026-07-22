"""Submits HL7 ELR payloads to data-ingestion-service, retrying once on a
token-expired (401) response with a freshly-refreshed token."""

import logging
from dataclasses import dataclass
from typing import Optional

import requests

from .auth import TokenManager
from .config import Config

logger = logging.getLogger(__name__)


@dataclass
class SubmissionResult:
    source: str
    success: bool
    status_code: Optional[int]
    body: str
    elr_id: Optional[str]
    retried_after_401: bool


class ElrIngestClient:
    def __init__(self, config: Config, token_manager: TokenManager, session: requests.Session):
        self.config = config
        self.token_manager = token_manager
        self.session = session

    def _post(self, payload: str, token: str) -> requests.Response:
        headers = {
            "Authorization": f"Bearer {token}",
            "clientid": self.config.client_id,
            "clientsecret": self.config.client_secret,
            "msgType": self.config.msg_type,
            "version": self.config.msg_version,
            "Content-Type": "text/plain",
        }
        return self.session.post(
            self.config.dis_elr_url,
            data=payload.encode("utf-8"),
            headers=headers,
            timeout=self.config.request_timeout,
        )

    def submit(self, payload: str, source: str = "<inline>") -> SubmissionResult:
        token = self.token_manager.get_token()
        resp = self._post(payload, token)
        retried = False

        if resp.status_code == 401:
            logger.warning("%s: got 401 (token rejected), refreshing token and retrying once.", source)
            token = self.token_manager.get_token(force_refresh=True)
            resp = self._post(payload, token)
            retried = True

        success = resp.status_code == 200
        elr_id = resp.text.strip() if success else None

        return SubmissionResult(
            source=source,
            success=success,
            status_code=resp.status_code,
            body=resp.text,
            elr_id=elr_id,
            retried_after_401=retried,
        )
