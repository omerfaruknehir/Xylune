# Turp 0.24.29

## Complete Turp identity

- Completes the hard Turp rebrand across the Android package and namespace, Kotlin/resource/class paths, native library names, storage and backup identifiers, deep links, MIME types, CI variables, documentation, Pages paths, and repository-facing URLs.
- Changes the Android application ID to `app.turp.chat`, making Turp a distinct application identity rather than retaining the previous compatibility package.
- Intentionally does not migrate or preserve legacy install, preference, database, backup, deep-link, or other previous-app identifiers. Existing legacy installs and backups are not treated as Turp data.
- Adds a repository-wide identity regression test plus CI verification that rejects legacy product names in checked-in paths or raw file bytes, including native binaries.
- Renames release/build artifacts and source metadata consistently to Turp and validates the new package through unit tests, lint, release assembly, signature checks, and emulator smoke tests.
