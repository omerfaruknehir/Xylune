from pathlib import Path


# Historical release notes are immutable history. The broad test rebrand pass must not
# rewrite a regression test which deliberately verifies the old 0.24.28 Xylune heading.
release_test = Path("app/src/test/java/app/xylune/chat/ReleaseVersionRegressionTest.kt")
text = release_test.read_text()
text = text.replace('english.startsWith("# Turp 0.24.28")', 'english.startsWith("# Xylune 0.24.28")')
text = text.replace('turkish.startsWith("# Turp 0.24.28")', 'turkish.startsWith("# Xylune 0.24.28")')
release_test.write_text(text)

# This is arbitrary content inside a synthetic XLSX fixture, not app-owned UI copy.
# Keeping it untouched also verifies the extractor does not transform user documents.
office_test = Path("app/src/test/java/app/xylune/chat/files/OfficeDocumentExtractorTest.kt")
text = office_test.read_text()
text = text.replace('<si><t>Turp</t></si>', '<si><t>Xylune</t></si>')
text = text.replace('text.contains("Name\\tTurp")', 'text.contains("Name\\tXylune")')
office_test.write_text(text)

# Theme.Xylune.* is a stable Android resource identifier, not user-facing brand copy.
launcher_test = Path("app/src/test/java/app/xylune/chat/settings/LauncherIconManagerTest.kt")
text = launcher_test.read_text()
text = text.replace('Theme.Turp.Launcher.System', 'Theme.Xylune.Launcher.System')
launcher_test.write_text(text)

# Android 12+ System palette must remain runtime-dynamic. The radish geometry changes,
# but v31 System resources continue to use @android:color/system_* instead of hardcoded hex.
def system_tint(source: str, leaf_start: str) -> str:
    mappings = {
        "#FF78BF43": leaf_start,
        "#FF80C442": leaf_start,
        "#FF3A8331": "@android:color/system_accent3_200",
        "#FF338331": "@android:color/system_accent3_200",
        "#FF24782D": "@android:color/system_accent3_200",
        "#FF367F31": "@android:color/system_accent3_200",
        "#FF5CA735": "@android:color/system_accent3_200",
        "#FF3F8E31": "@android:color/system_accent3_200",
        "#FFE51F47": "@android:color/system_accent2_200",
        "#FFFF385D": "@android:color/system_accent2_200",
        "#FFDB2449": "@android:color/system_accent2_200",
        "#FFC91A40": "@android:color/system_accent2_200",
        "#FFEA4668": "@android:color/system_accent2_200",
        "#FFFFFFFF": "@android:color/system_neutral1_10",
    }
    for old, new in mappings.items():
        source = source.replace(old, new)
    return source

base = Path("app/src/main/res/drawable/ic_xylune_foreground.xml").read_text()
v31 = Path("app/src/main/res/drawable-v31")
v31.joinpath("ic_xylune_foreground_system.xml").write_text(
    system_tint(base, "@android:color/system_accent1_200")
)
v31.joinpath("ic_xylune_mark_system.xml").write_text(
    system_tint(base, "@android:color/system_accent1_800")
)

assert '# Xylune 0.24.28' in release_test.read_text()
assert '<si><t>Xylune</t></si>' in office_test.read_text()
assert 'Name\\tXylune' in office_test.read_text()
assert 'Theme.Xylune.Launcher.System' in launcher_test.read_text()
foreground = v31.joinpath("ic_xylune_foreground_system.xml").read_text()
mark = v31.joinpath("ic_xylune_mark_system.xml").read_text()
assert '@android:color/system_accent1_200' in foreground
assert '@android:color/system_accent3_200' in foreground
assert '@android:color/system_accent1_800' in mark
assert '@android:color/system_accent3_200' in mark
assert 'M734,681' in foreground
assert 'M734,681' in mark
print("Historical fixture and Android System icon contracts fixed")
