# Turp 0.24.23

- Fixed Turp reporting or reasoning from the wrong app version by reading the version name and code from Android's actually installed package metadata, with generated build constants only as a fallback.
- About/build information, repository update comparisons, update-check User-Agent metadata, and portable archive metadata now use the installed package version.
- The AI runtime context now receives the actual installed Turp app version and explicitly distinguishes it from the separate core-prompt revision, preventing the prompt revision from being mistaken for the app version.
- Reworked Add Provider into an adaptive Material bottom sheet instead of an oversized alert dialog.
- The provider form now scrolls independently while Add and Cancel remain reachable, respects the keyboard and navigation bars, and stacks its actions on very narrow screens.
- Added regression coverage for runtime-version wiring and small-screen provider setup behavior.
