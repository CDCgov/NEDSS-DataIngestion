"""
Two-hop authentication flow:

  Hop 1: POST directly to Keycloak's own token endpoint (client_credentials
         grant) -> a JWT issued straight from Keycloak.
  Hop 2: POST that JWT to data-processing-service's /api/auth/token endpoint
         (clientid/clientsecret headers, Bearer token from hop 1) -> a second
         JWT ("another JWT from data-processing-service").

The hop-2 JWT is what's actually used to submit ELRs to data-ingestion-service.

Note: data-processing-service's token endpoint is Spring-Security-whitelisted
(permitAll) and its handler code never reads the incoming Authorization header -
it independently re-requests a fresh token from Keycloak using the clientid/
clientsecret headers. So technically hop 1's token isn't required for hop 2 to
succeed. We still perform hop 1 and forward its token on hop 2 (as an
Authorization header) because that's the flow that was specified, it's a
real/working call, and it's harmless to include.

data-ingestion-service validates the hop-2 JWT via OAuth2 *token introspection*
against Keycloak on every protected request (see
data-ingestion-service's CustomAuthenticationManagerResolver) - not local
signature/expiry checks. That means an expired or revoked token fails at
request time with HTTP 401, which is what the retry-on-401 logic in
ingest_client.py watches for.
"""

import logging
import time

import requests

from .config import Config
from .jwt_utils import get_expiry_epoch

logger = logging.getLogger(__name__)

# Refresh this many seconds before the locally-decoded `exp` claim to avoid
# racing a request against the token expiring mid-flight.
EXPIRY_SAFETY_MARGIN_SECONDS = 15


class AuthError(RuntimeError):
    """Raised when either hop of the token flow fails."""


class TokenManager:
    def __init__(self, config: Config, session: requests.Session):
        self.config = config
        self.session = session
        self._token: str | None = None
        self._expiry_epoch: float | None = None

    def _fetch_keycloak_token(self) -> str:
        """Hop 1: client_credentials grant straight against Keycloak."""
        url = self.config.keycloak_token_url
        body = {
            "grant_type": "client_credentials",
            "client_id": self.config.client_id,
            "client_secret": self.config.client_secret,
        }
        logger.debug("Requesting hop-1 token from Keycloak: %s", url)
        resp = self.session.post(url, data=body, timeout=self.config.request_timeout)
        if resp.status_code != 200:
            raise AuthError(
                f"Keycloak token request failed ({resp.status_code}): {resp.text[:500]}"
            )
        try:
            access_token = resp.json()["access_token"]
        except (ValueError, KeyError) as exc:
            raise AuthError(f"Unexpected Keycloak token response: {resp.text[:500]}") from exc
        return access_token

    def _fetch_processing_service_token(self, keycloak_token: str) -> str:
        """Hop 2: exchange for "another JWT" via data-processing-service."""
        url = self.config.dps_token_url
        headers = {
            "clientid": self.config.client_id,
            "clientsecret": self.config.client_secret,
            "Authorization": f"Bearer {keycloak_token}",
        }
        logger.debug("Requesting hop-2 token from data-processing-service: %s", url)
        resp = self.session.post(url, headers=headers, timeout=self.config.request_timeout)
        if resp.status_code != 200:
            raise AuthError(
                f"data-processing-service token request failed ({resp.status_code}): "
                f"{resp.text[:500]}"
            )
        token = resp.text.strip()
        if not token:
            raise AuthError("data-processing-service returned an empty token")
        return token

    def _refresh(self) -> str:
        keycloak_token = self._fetch_keycloak_token()
        token = self._fetch_processing_service_token(keycloak_token)
        self._token = token
        self._expiry_epoch = get_expiry_epoch(token)
        return token

    def get_token(self, force_refresh: bool = False) -> str:
        """Return a usable token, refreshing (both hops) if missing, expired, or forced."""
        if force_refresh or self._token is None:
            return self._refresh()

        if self._expiry_epoch is not None:
            if time.time() >= (self._expiry_epoch - EXPIRY_SAFETY_MARGIN_SECONDS):
                logger.info("Cached token is at/near expiry, refreshing.")
                return self._refresh()

        return self._token
