# 10 — Search, account, settings

## Search

- Tap 🔍: sharedBounds to a stadium field, 56 h, 8 margin, `surfaceContainerHigh`.  
- Leading `search` 24, then hint `Search notes`, trailing `close` when query ≠ empty.  
- First keystroke filters (≤ 50 ms). No spinner.  
- Empty query: chips `Pinned` `Drawings` `Images` `Locked` — `labelLarge`, stadium, 8 gap.  
- Results: list rows like home list, 8 below field.  
- Miss: `Nothing matches.` `bodyLarge` `onSurfaceVariant`, 48 from field.  
- Back: reverse field to icon.

## Account sheet

Handle + 24 top.  
**Out:** 64 `account_circle`, 12, `Sign in to sync` `titleMedium`, 4, `Notes stay on this phone until you do.` `bodyMedium` `onSurfaceVariant`, 16, filled `Sign in`. Then rows Settings, About.  
**In:** 64 avatar, email `titleMedium`, `Synced` or `Sync paused` `bodySmall` `onSurfaceVariant`. Rows: Settings, About, Sign out (`logout`).  
**Sign in** closes this sheet and pushes route `auth` — the **custom** Clerk screen in [ui/14-ONBOARDING.md](14-ONBOARDING.md). Never `AuthView`. Never a Clerk sheet inside this sheet.

## Settings / About / privacy / trash / manage

**Full pixels:** [ui/15-SETTINGS.md](15-SETTINGS.md).  
This file keeps search + the account **sheet** only. Do not invent a second settings layout here.
