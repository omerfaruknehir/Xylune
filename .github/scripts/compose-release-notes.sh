#!/usr/bin/env bash
set -euo pipefail

version="${1:?Usage: compose-release-notes.sh VERSION}"
english="$(bash ci/resolve-release-notes.sh "$version")"
turkish="docs/releases/tr/RELEASE_NOTES_${version}.md"

if [[ ! -s "$turkish" ]]; then
  echo "Missing Turkish release notes: $turkish" >&2
  exit 1
fi

output="${RUNNER_TEMP:-/tmp}/xylune-release-notes-bilingual-${version}.md"
python3 - "$english" "$turkish" "$output" <<'PY'
from pathlib import Path
import re
import sys

en_path, tr_path, output_path = map(Path, sys.argv[1:])

def body(path: Path) -> str:
    text = path.read_text(encoding="utf-8").replace("\r\n", "\n").strip()
    # GitHub already displays the release title. Avoid repeating the per-version H1
    # inside each language section while preserving all actual release-note headings.
    text = re.sub(r"^#\s+Xylune\s+[^\n]+\n+", "", text, count=1)
    return text.strip()

english = body(en_path)
turkish = body(tr_path)
if not english or not turkish:
    raise SystemExit("Both English and Turkish release notes must contain content")

combined = f"""<!-- xylune-release-notes:en -->
## English

{english}

<!-- xylune-release-notes:tr -->
## Türkçe

{turkish}
"""
output_path.write_text(combined, encoding="utf-8")
PY

printf '%s\n' "$output"
