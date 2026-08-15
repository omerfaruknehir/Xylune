from pathlib import Path


def replace_all(path: str, replacements: dict[str, str]) -> None:
    p = Path(path)
    text = p.read_text()
    for old, new in replacements.items():
        if old not in text:
            raise SystemExit(f"{path}: expected release/CI token missing: {old}")
        text = text.replace(old, new)
    p.write_text(text)


# Keep XYLUNE_* secrets/env/config names as compatibility/deployment identifiers.
# Rebrand only names that users see/download.
replace_all(
    ".github/workflows/android.yml",
    {
        "xylune-public-release-build": "turp-public-release-build",
        "xylune-android-failure-reports": "turp-android-failure-reports",
        "xylune-protected-signed-release": "turp-protected-signed-release",
        "Xylune-${XYLUNE_VERSION}-release.apk": "Turp-${XYLUNE_VERSION}-release.apk",
        "Xylune-${XYLUNE_VERSION}-release.aab": "Turp-${XYLUNE_VERSION}-release.aab",
        "Xylune-${XYLUNE_VERSION}-source.zip": "Turp-${XYLUNE_VERSION}-source.zip",
        "Xylune-${XYLUNE_VERSION}-source.tar.gz": "Turp-${XYLUNE_VERSION}-source.tar.gz",
        "Xylune-${XYLUNE_VERSION}-release.json": "Turp-${XYLUNE_VERSION}-release.json",
        "Xylune-${XYLUNE_VERSION}-SHA256.txt": "Turp-${XYLUNE_VERSION}-SHA256.txt",
        "--prefix=\"Xylune-${XYLUNE_VERSION}/\"": "--prefix=\"Turp-${XYLUNE_VERSION}/\"",
        "--title \"Xylune ${XYLUNE_VERSION}\"": "--title \"Turp ${XYLUNE_VERSION}\"",
    },
)

replace_all(
    ".github/workflows/wip-debug-apk.yml",
    {
        "Xylune-PR-${pr}-${short_sha}-debug.apk": "Turp-PR-${pr}-${short_sha}-debug.apk",
        "xylune-pr-${{ github.event.pull_request.number || 'manual' }}-debug-apk": "turp-pr-${{ github.event.pull_request.number || 'manual' }}-debug-apk",
    },
)

replace_all(
    ".github/workflows/wip-android-qa.yml",
    {
        "xylune-pr-${{ github.event.pull_request.number || 'manual' }}-android-qa": "turp-pr-${{ github.event.pull_request.number || 'manual' }}-android-qa",
    },
)

android = Path(".github/workflows/android.yml").read_text()
assert 'apk="Turp-${XYLUNE_VERSION}-release.apk"' in android
assert '--title "Turp ${XYLUNE_VERSION}"' in android
assert 'XYLUNE_KEYSTORE_PASSWORD' in android
assert 'XYLUNE_SOURCE_REPOSITORY' in android
assert 'name: turp-public-release-build' in android
assert 'name: turp-protected-signed-release' in android

wip = Path(".github/workflows/wip-debug-apk.yml").read_text()
assert 'apk="Turp-PR-${pr}-${short_sha}-debug.apk"' in wip
assert 'name: turp-pr-${{ github.event.pull_request.number || \'manual\' }}-debug-apk' in wip

qa = Path(".github/workflows/wip-android-qa.yml").read_text()
assert 'name: turp-pr-${{ github.event.pull_request.number || \'manual\' }}-android-qa' in qa

print("Turp release and CI artifact names applied")
