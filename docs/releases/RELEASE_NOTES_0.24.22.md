# Turp 0.24.22

- Fixed the source-pill soft lock at its root by removing the full-window focusable source preview popup that could retain input focus while rendering invisibly.
- Source pills now reuse the normal anchored link/source preview with reliable Back and outside-tap dismissal.
- Added an app language control with System default, English, and Turkish choices using Android per-app locales.
- Added Turkish resources and broad Turkish UI coverage across setup, chats and projects, settings, providers and models, search, image generation/editing, backup and cloud flows, local execution, previews, usage, legal/about, errors, and status surfaces.
- Kept user/model output, code, URLs, file names, provider names, and model identifiers out of UI translation.
- Added regression coverage for source-preview dismissal and app-locale wiring.
