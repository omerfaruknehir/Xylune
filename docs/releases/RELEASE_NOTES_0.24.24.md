# Xylune 0.24.24

- Moved app language into the normal Settings hierarchy instead of showing it as a floating top-right globe action.
- Added **Settings → Personalization → App language**, with the current selection shown directly in the Settings list.
- Added a dedicated language page with **System default**, **English**, and **Türkçe** choices and normal predictive-back navigation.
- Removed the old Settings-only language overlay from `MainActivity`.
- Fixed Settings route titles so they follow Xylune's selected app language rather than relying on the process/system locale.
- Expanded Turkish coverage for Search & web routing, native-provider search, Xylune search engines, credentials, and related descriptions.
- Added regression coverage so the floating language overlay cannot return and the proper Settings destination remains wired.
