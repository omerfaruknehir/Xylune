# Xylune 0.24.26

## Structural UI cleanup

- Removes the duplicated Settings home and the 300 ms / 90 ms timing workaround; App language is now a normal Settings route owned by the existing navigation host.
- Replaces the provider-catalog 8-second escape timer with an explicit loading/ready/failed initialization state owned by application startup.
- Makes the navigation drawer respect Android's actual system Back gesture insets instead of reserving an invisible fixed-width Settings strip.
- Closes link previews when their exit transition actually finishes instead of sleeping for a hard-coded duration.
- Replaces the launcher's fixed 110 ms handoff with a first-draw lifecycle callback while keeping the separate One UI launcher-alias recovery path intact.

## Localization and regression coverage

- Moves exact Turkish interface copy into Android locale resources; the compatibility formatter is now used only for dynamic/interpolated text.
- Localizes Settings page titles through resource IDs, including App language.
- Replaces regression tests that explicitly required the old timing hacks with behavior and architectural-invariant coverage.
