from __future__ import annotations

import re
from pathlib import Path

# One-shot branch migration. The follow-up push intentionally triggers the workflow.
ROOT = Path(__file__).resolve().parents[2]
FILES = [
    ROOT / "app/src/main/java/app/xylune/chat/ui/TurkishUiCopy.kt",
    ROOT / "app/src/main/java/app/xylune/chat/ui/TurkishUiCopyExtra.kt",
    ROOT / "app/src/main/java/app/xylune/chat/ui/TurkishUiCopyExtra2.kt",
]
ENTRY = re.compile(r'^(\s*)("(?:\\.|[^"\\])*")\s+to\s+("(?:\\.|[^"\\])*")\s*,?\s*$')
CHUNK_SIZE = 48


def split_catalog(path: Path) -> int:
    source = path.read_text(encoding="utf-8")
    marker = "    private val exact = mapOf(\n"
    start = source.find(marker)
    if start < 0:
        if "private fun exactLookup(text: String): String?" in source:
            return 0
        raise RuntimeError(f"exact map marker not found in {path}")

    body_start = start + len(marker)
    translate_at = source.find("\n    fun translate(text: String): String {", body_start)
    if translate_at < 0:
        raise RuntimeError(f"translate function not found in {path}")

    map_region = source[body_start:translate_at]
    close_at = map_region.rfind("    )")
    if close_at < 0:
        raise RuntimeError(f"exact map closing delimiter not found in {path}")
    entries_region = map_region[:close_at]

    entries: list[tuple[str, str]] = []
    for line in entries_region.splitlines():
        match = ENTRY.match(line)
        if match:
            entries.append((match.group(2), match.group(3)))
    if not entries:
        raise RuntimeError(f"no exact translation entries parsed from {path}")

    chunks = [entries[i:i + CHUNK_SIZE] for i in range(0, len(entries), CHUNK_SIZE)]
    functions: list[str] = []
    lookup_chain = " ?:\n            ".join(f"exact{index}(text)" for index in range(1, len(chunks) + 1))
    functions.append(
        "    private fun exactLookup(text: String): String? =\n"
        f"        {lookup_chain}\n"
    )
    for index, chunk in enumerate(chunks, start=1):
        lines = [f"    private fun exact{index}(text: String): String? = when (text) {{"]
        for key, value in chunk:
            lines.append(f"        {key} -> {value}")
        lines.append("        else -> null")
        lines.append("    }")
        functions.append("\n".join(lines) + "\n")

    transformed = source[:start] + "\n".join(functions) + source[translate_at:]
    transformed = transformed.replace(
        "        exact[text]?.let { return it }",
        "        exactLookup(text)?.let { return it }",
        1,
    )
    path.write_text(transformed, encoding="utf-8")
    return len(entries)


if __name__ == "__main__":
    total = 0
    for file in FILES:
        count = split_catalog(file)
        total += count
        print(f"{file.name}: split {count} exact translations")
    print(f"Split {total} translations total")
