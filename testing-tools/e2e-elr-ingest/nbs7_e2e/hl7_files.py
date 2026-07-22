"""Discover and load HL7 files from a target directory."""

import re
from pathlib import Path
from typing import List

# Segment IDs that can plausibly appear in an ORU^R01 ELR message. Only used
# as a last-resort fallback when a file has zero real segment terminators.
_KNOWN_SEGMENT_IDS = [
    "MSH", "SFT", "PID", "PD1", "NK1", "PV1", "PV2", "ORC", "OBR", "NTE",
    "OBX", "SPM", "TQ1", "TQ2", "CTD", "DG1", "ROL", "PR1", "IN1", "IN2", "IN3",
]
_SEGMENT_BOUNDARY_RE = re.compile(r"(?=(?:" + "|".join(_KNOWN_SEGMENT_IDS) + r")\|)")


def discover_hl7_files(directory: str, pattern: str = "*.hl7", recursive: bool = False) -> List[Path]:
    root = Path(directory)
    if not root.is_dir():
        raise NotADirectoryError(f"Input directory does not exist: {directory}")
    globber = root.rglob if recursive else root.glob
    return sorted(p for p in globber(pattern) if p.is_file())


def normalize_segments(hl7_text: str) -> str:
    """
    Ensure a message uses explicit CRLF segment terminators.

    Splits on whichever real terminator is present (\\r\\n, bare \\r, or \\n)
    and rejoins with \\r\\n. Falls back to detecting segment boundaries by
    known segment ID prefixes only if the message has no terminators at all.
    """
    for term in ("\r\n", "\r", "\n"):
        if term in hl7_text:
            segments = [s for s in hl7_text.split(term) if s.strip()]
            break
    else:
        segments = [s for s in _SEGMENT_BOUNDARY_RE.split(hl7_text) if s.strip()]

    return "\r\n".join(segments) + "\r\n"


def load_hl7_file(path: Path) -> str:
    with open(path, "rb") as f:
        raw = f.read().decode("utf-8")
    return normalize_segments(raw)


def formatted_dir_for(input_dir: Path) -> Path:
    """<input_dir>-formatted, as a sibling of input_dir (not nested inside it)."""
    return input_dir.parent / f"{input_dir.name}-formatted"


def format_directory(
    input_dir: Path, pattern: str = "*.hl7", recursive: bool = False
) -> List[Path]:
    """
    Normalize every matching file under input_dir and write the result into a
    new sibling directory named "<input_dir name>-formatted", mirroring each
    file's path relative to input_dir. Returns the list of written paths.
    """
    output_dir = formatted_dir_for(input_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    written = []
    for src in discover_hl7_files(str(input_dir), pattern=pattern, recursive=recursive):
        rel = src.relative_to(input_dir)
        dest = output_dir / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        dest.write_text(load_hl7_file(src), newline="")
        written.append(dest)

    return written
