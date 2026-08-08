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
    for path in sorted(UI_ROOT.rglob("*.kt")):
        source = path.read_text(encoding="utf-8")
        if not is_direct_ui_package(source) or path.name == "LocalizedText.kt":
            continue
        if PLAIN_TEXT_IMPORT in source:
            path.write_text(source.replace(PLAIN_TEXT_IMPORT, ALIASED_TEXT_IMPORT, 1), encoding="utf-8")

    return [
        str(path.relative_to(ROOT))
        for path in sorted(UI_ROOT.rglob("*.kt"))
        if path.name != "LocalizedText.kt"
        and is_direct_ui_package(path.read_text(encoding="utf-8"))
        and ALIASED_TEXT_IMPORT in path.read_text(encoding="utf-8")
    ]


def kotlin_string_literals(source: str) -> set[str]:
    # Review aid only. Triple-quoted regex/code payloads are intentionally
    # removed first; ordinary literals are kept on a single physical line.
    source = re.sub(r'""".*?"""', '', source, flags=re.DOTALL)
    result: set[str] = set()
    for match in re.finditer(r'"((?:\\.|[^"\\\n])*)"', source):
        raw = match.group(1)
        try:
            value = ast.literal_eval('"' + raw + '"')
        except Exception:
            value = raw
        value = value.strip()
        if len(value) < 2 or len(value) > 700 or not any(ch.isalpha() for ch in value):
            continue
        if value.startswith(("http://", "https://")):
            continue
        if re.fullmatch(r"[A-Za-z0-9_.:/{}$<>?=&,+*\\-]+", value) and " " not in value:
            continue
        if any(fragment in value for fragment in (
            "Modifier.", "SyntaxKind.", "drawPath(", "drawLine(", "drawCircle(",
            " val ", " fun ", "remember(", "setOf(", "Regex(", "return@",
        )):
            continue
        result.add(value)
    return result


def write_audit(localized_files: list[str]) -> None:
    literals: set[str] = set()
    for path in sorted(UI_ROOT.rglob("*.kt")):
        source = path.read_text(encoding="utf-8")
        if is_direct_ui_package(source) and path.name != "LocalizedText.kt":
            literals.update(kotlin_string_literals(source))

    output = ROOT / "tools/xylune-ui-literals.txt"
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(
        "# Temporary localization audit\n"
        "# Direct app.xylune.chat.ui files routed through LocalizedText.kt:\n"
        + "\n".join(f"# {path}" for path in localized_files)
        + "\n\n"
        + "\n".join(sorted(literals, key=lambda item: item.casefold()))
        + "\n",
        encoding="utf-8",
    )


if __name__ == "__main__":
    localized_files = alias_material_text_imports()
    write_audit(localized_files)
    print(f"Localized Text facade active in {len(localized_files)} direct UI files")
