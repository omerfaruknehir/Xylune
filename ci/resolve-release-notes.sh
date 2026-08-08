#!/usr/bin/env bash
set -euo pipefail

version="${1:?Usage: resolve-release-notes.sh VERSION}"
english_specific="docs/releases/RELEASE_NOTES_${version}.md"
turkish="docs/releases/tr/RELEASE_NOTES_${version}.md"
english="$english_specific"

if [[ ! -s "$english" ]]; then
  english="${RUNNER_TEMP:-/tmp}/xylune-release-notes-en-${version}.md"
  python3 - "$version" "$english" <<'PY'
from pathlib import Path
import re
import sys

version, output = sys.argv[1:]
text = Path("CHANGELOG.md").read_text(encoding="utf-8")
pattern = re.compile(
    rf"(?ms)^##\s+{re.escape(version)}(?:\s+—[^\n]*)?\n.*?(?=^##\s+|\Z)"
)
match = pattern.search(text)
if not match:
    raise SystemExit(
        f"No docs/releases/RELEASE_NOTES_{version}.md and no CHANGELOG section for {version}. "
        "Refusing to publish the entire changelog as one release."
    )
Path(output).write_text(match.group(0).rstrip() + "\n", encoding="utf-8")
PY
fi

if [[ ! -s "$turkish" ]]; then
  echo "Missing Turkish release notes for $version: $turkish" >&2
  exit 1
fi

output="${RUNNER_TEMP:-/tmp}/xylune-release-notes-${version}.md"
python3 - "$english" "$turkish" "$output" <<'PY'
from pathlib import Path
import re
import sys

en_path, tr_path, output_path = map(Path, sys.argv[1:])

def body(path: Path) -> str:
    text = path.read_text(encoding="utf-8").replace("\r\n", "\n").strip()
    # GitHub already displays the release title. Avoid repeating the version H1
    # inside each language section while preserving actual note headings.
    text = re.sub(r"^#\s+Xylune\s+[^\n]+\n+", "", text, count=1)
    return text.strip()

english = body(en_path)
turkish = body(tr_path)
if not english or not turkish:
    raise SystemExit("Both English and Turkish release notes must contain content")

output_path.write_text(
    "<!-- xylune-release-notes:en -->\n"
    "## English\n\n"
    f"{english}\n\n"
    "<!-- xylune-release-notes:tr -->\n"
    "## Türkçe\n\n"
    f"{turkish}\n",
    encoding="utf-8",
)
PY

printf '%s\n' "$output"
