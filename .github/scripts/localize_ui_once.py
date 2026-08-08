from __future__ import annotations

import ast
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
UI_ROOT = ROOT / "app/src/main/java/app/xylune/chat/ui"
PLAIN_TEXT_IMPORT = "import androidx.compose.material3.Text\n"
ALIASED_TEXT_IMPORT = "import androidx.compose.material3.Text as MaterialText\n"


def is_direct_ui_package(source: str) -> bool:
    return source.startswith("package app.xylune.chat.ui\n")


def alias_material_text_imports() -> list[str]:
    changed: list[str] = []
    for path in sorted(UI_ROOT.rglob("*.kt")):
        source = path.read_text(encoding="utf-8")
        if not is_direct_ui_package(source):
            continue
        if path.name == "LocalizedText.kt":
            continue
        if PLAIN_TEXT_IMPORT not in source:
            continue
        path.write_text(source.replace(PLAIN_TEXT_IMPORT, ALIASED_TEXT_IMPORT, 1), encoding="utf-8")
        changed.append(str(path.relative_to(ROOT)))
    return changed


def kotlin_string_literals(source: str) -> set[str]:
    # This is intentionally a review aid, not a Kotlin parser. It extracts
    # ordinary quoted literals and decodes common escapes so remaining UI copy
    # can be audited after the import migration.
    result: set[str] = set()
    for match in re.finditer(r'"((?:\\.|[^"\\])*)"', source):
        raw = match.group(1)
        try:
            value = ast.literal_eval('"' + raw.replace('"', '\\"') + '"')
        except Exception:
            value = raw
        value = value.strip()
        if len(value) < 2 or not any(ch.isalpha() for ch in value):
            continue
        if value.startswith(("http://", "https://")):
            continue
        if re.fullmatch(r"[A-Za-z0-9_.:/{}$<>?=&,+*\\-]+", value) and " " not in value:
            continue
        result.add(value)
    return result


def write_audit(changed: list[str]) -> None:
    literals: set[str] = set()
    for path in sorted(UI_ROOT.rglob("*.kt")):
        source = path.read_text(encoding="utf-8")
        if is_direct_ui_package(source):
            literals.update(kotlin_string_literals(source))

    output = ROOT / "tools/xylune-ui-literals.txt"
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(
        "# Temporary localization audit\n"
        "# Direct app.xylune.chat.ui files whose Material Text import was routed through LocalizedText.kt:\n"
        + "\n".join(f"# {path}" for path in changed)
        + "\n\n"
        + "\n".join(sorted(literals, key=lambda item: item.casefold()))
        + "\n",
        encoding="utf-8",
    )


if __name__ == "__main__":
    changed_files = alias_material_text_imports()
    write_audit(changed_files)
    print(f"Aliased Material Text in {len(changed_files)} direct UI files")
