---
layout: default
lang: en
alternate_en: /data-deletion/
alternate_tr: /tr/data-deletion/
title: Data Deletion
heading: Data deletion
browser_title: Delete Turp Data — Android BYOK AI Chat App
description: How to delete local Turp data, cloud backups, OAuth access, and provider-held data for the open-source Android BYOK AI chat app.
---

# Turp data deletion

Turp has no central user account. Most data is stored on the Android device or in a provider selected by the user.

## Delete local data

- Delete individual chats, memories, providers, drafts, or other records from the relevant Turp screen.
- To remove all local Turp data, use Android **Settings → Apps → Turp → Storage → Clear data**, or uninstall Turp.
- Clearing data or uninstalling also removes locally stored encrypted OAuth sessions and cloud credentials.

## Delete cloud backups

Open **Turp → Settings → Backup & transfer**, select the connected destination, browse backups, and choose **Delete**. A backup can also be deleted directly through Google Drive app data controls, OneDrive Apps/Turp, Dropbox Apps/Turp, the configured WebDAV/Nextcloud folder, or the configured S3 bucket/prefix.

Disconnecting a provider removes the local session or credentials but does **not** automatically delete backups already stored there. Revoking Turp in the Google, Microsoft, or Dropbox account security page stops future access but likewise does not necessarily delete stored files.

## What the maintainer can and cannot delete

The maintainer has no Turp account database or remote administration access and cannot delete data held only on a device, in a backup destination, by an AI provider, or in a provider account. Use the controls described above or contact the relevant provider.

GitHub operates repository accounts, hosting, logs, and public Issues. Use GitHub's account, content, and [privacy controls](https://docs.github.com/site-policy/privacy-policies/github-privacy-statement) for data controlled by GitHub. **Do not put a privacy request, personal data, credentials, private chats, or identity documents in a public Turp issue.**

If a request concerns specific information deliberately sent through a private OAuth channel and actually retained by the maintainer, use the private contact method shown on that OAuth consent screen. The maintainer can act only on that identified submission, not on data the maintainer never received.
