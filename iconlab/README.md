# Icon Lab

Small standalone Android icon/splash test app module.

## What it accepts

- PNG
- SVG
- self-contained Android drawable XML (especially VectorDrawable XML)

XML that references app resources such as `@drawable/foo` cannot be resolved unless those resources are also part of Icon Lab.

## Runtime launcher behavior

Android does not allow an installed app to replace arbitrary manifest icon/name resources with a file at runtime. Icon Lab uses a pinned shortcut with the selected name and bitmap, and can hide its stock launcher alias after the pin succeeds. Applying again updates the same pinned shortcut ID.

## Splash behavior

Android 12+ system splash artwork is resource/theme based. Icon Lab makes that transition neutral and immediately shows a runtime splash using the selected icon and name before opening the editor.

Package: `app.turp.icontest`
