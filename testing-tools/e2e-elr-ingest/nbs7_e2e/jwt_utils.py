"""Local (non-verifying) JWT inspection - just enough to read the `exp` claim
so the token manager can proactively refresh before the token actually expires."""

import base64
import json
from typing import Optional


def _b64url_decode(segment: str) -> bytes:
    padding = "=" * (-len(segment) % 4)
    return base64.urlsafe_b64decode(segment + padding)


def decode_jwt_payload(token: str) -> Optional[dict]:
    parts = token.split(".")
    if len(parts) != 3:
        return None
    try:
        return json.loads(_b64url_decode(parts[1]))
    except (ValueError, UnicodeDecodeError):
        return None


def get_expiry_epoch(token: str) -> Optional[float]:
    payload = decode_jwt_payload(token)
    if not payload:
        return None
    return payload.get("exp")
