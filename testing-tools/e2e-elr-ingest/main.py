#!/usr/bin/env python3
"""
Entry point for the nbs7 end-to-end ELR ingestion tool.

Usage:
    python main.py --input-dir /path/to/hl7/files
    python main.py --input-dir /path/to/hl7/files --check-status
    python main.py --help
"""

import sys

from nbs7_e2e.cli import main

if __name__ == "__main__":
    sys.exit(main())
