# 15 — Settings (every row, every sub-screen)

`ui/10` names the sheet. **This file is the product.**  
Route: `settings` and the children below.  
Bar: 64, `arrow_back` 48, title `titleLarge` 500, no extra actions unless named.

---

## Root list

`background`. No grouped-card joke. Air is the divider.

Rows **56** h, pad 20 side. Leading icon 24 `onSurfaceVariant`, 16 gap, label `bodyLarge` `onSurface`, trailing `labelSmall` `onSurfaceVariant` or switch.  
Section gaps: **8** (not a heading) except where a 1 dp `outlineVariant` hairline is listed.

**Exact order (replaces the vague list in `ui/10`):**

| # | Label | Trailing | Opens |
|--:|---|---|---|
| 1 | Appearance | theme name | `settings/appearance` |
| 2 | Type | font · S/M/L | `settings/type` |
| 3 | Paper | paper name | `settings/paper` |
| 4 | Home view | Grid / List | **inline** `ButtonGroup` 2, no push |
| 5 | Focus | Auto / Off / … | `settings/focus` |
| 6 | Sort checked to end | switch, default Off | — |
| — | hairline | | |
| 7 | Lock new notes | switch, default Off | — |
| 8 | Show notes on lock screen | switch, default **Off** | — |
| 9 | Default notes app | On / Off | system role dialog |
| — | hairline | | |
| 10 | Trash | count or empty | `settings/trash` |
| — | hairline | | |
| 11 | Account | email or Sign in | `settings/account` or `auth` |
| 12 | What syncs | — | `settings/privacy` |
| 13 | About | version | `settings/about` |

**Show notes on lock screen:** the official ROLE_NOTES consent. Must be toggled **while unlocked**. Default Off. When Off, `NoteCaptureActivity` from the keyguard **always** creates a new note and never lists history.

**Default notes app:** `RoleManager.isRoleAvailable(ROLE_NOTES)` then `createRequestRoleIntent`. If role APIs missing (API < 34): hide the row. Value `On` only if `isRoleHeld`. Subtitle under the row, `bodySmall` `onSurfaceVariant`, 2 lines max: `Write from the lock screen and the stylus shortcut.` Never ask this on first launch.

**Lock new notes:** if no strong biometric and no device credential (`BiometricManager.canAuthenticate` fails `NONE_ENROLLED` / no hardware): switch disabled, tap → dialog `Set a screen lock` / `Open settings` (`ACTION_BIOMETRIC_ENROLL` with `BIOMETRIC_STRONG or DEVICE_CREDENTIAL`).

Switches: Material3, no custom thumb. Haptic none.

---

## Appearance — `settings/appearance`

Title `Appearance`.

Pad 16. **3 × 4** grid, gap 10.

Card **72 × 88**, radius **20**.  
Fill = that theme’s `surface`.  
Inside: 8 pad, a 40×28 fake note card radius 8 in that theme’s `surfaceContainerLow`, 4 dp wax dot `primary` top-end.  
Name under, `labelSmall`, center, 6 dp, 1 line.  
Selected: **2 dp** `primary` ring, 2 dp offset (not a check).  
`dynamic` card: live wallpaper-harmonized scheme; label `Wallpaper`.

Tap applies **immediately**. Crossfade scheme inherit (`ui/05`). No Save. No haptic.

`dieci` is the only true-black theme. Do not preview other darks as `#000`.

---

## Type — `settings/type`

Title `Type`.

**Size** first: connected `ButtonGroup` `S` `M` `L`, height 40, start 16, 16 below bar.  
S = bodyNote **16/25**, M = **18/28**, L = **20/31**. Line-height stay 1.55. Default **M**.

Then list, 72 min rows:

- Leading 40×40, first letter of the face in that face, `titleMedium`, fill `surfaceContainer`.
- Name `bodyLarge` in **that typeface**.
- Trailing: `Bundled` `labelSmall` `onSurfaceVariant` for Flex, Literata, JetBrains Mono, Atkinson. Others blank until downloaded, then nothing.
- Selected: `primaryContainer` fill on the row, 12 radius, 8 horizontal inset.

On-demand (8 faces): first tap starts download. 2 dp `primary` bar under the name, indeterminate. Success: apply + haptic `CONFIRM`. Fail: inline `Couldn’t add this type.` + `Try again`. **Never** fetch a font to paint the editor of an open note until the file is on disk.

Preview pinned under the size group (not a second screen): `Write anything.` in the selected face at the selected size, `onSurface`, 20 side, 16 vertical. Paper grain behind that band only.

Same list is reused from editor overflow → Type, as a **sheet** (max 85%). Settings is the full screen (download lives here). Overflow sheet **only** offers already-on-disk faces + size.

---

## Paper — `settings/paper`

Title `Paper`.

2 columns, gap 10, pad 16. Eight cards:

Card width fill, height **112**, radius 20. Real underlay at 100% (ruled/graph/dots/legal/kraft washes from `ui/02`). Name `labelSmall` under, 6 dp.  
Selected: 2 dp `primary` ring.

Tap sets the **default for new notes**. Existing notes keep their own paper.

Editor overflow → Paper is the **same grid in a sheet**.

---

## Focus — `settings/focus`

Title `Focus`. Radio list. One selected. Tap applies.

| Value | Title | One-line |
|---|---|---|
| `off` | Off | Chrome stays. |
| `auto` | Auto | Chrome fades after you stop typing. **Default.** |
| `sentence` | Sentence | Other paragraphs dim. Off when TalkBack or high contrast is on. |
| `typewriter` | Typewriter | The line you type stays mid-page. |

Row 72. Radio end. Title `bodyLarge`. Line `bodySmall` `onSurfaceVariant`.

---

## Trash — `settings/trash`

Title `Trash`. End: text `Empty` `labelLarge` `error` — hidden if 0.

List only (no grid). Rows like home list. Meta: `Deleted · {n}d left` (`labelSmall`). 0 days = `Deleted · today`. 1 = `1d left`.

- Tap → read-only editor: no IME, no toolbar, no plus. Bar: back | `Restore` filled 40 h stadium. Restore: `deletedAt = null`, haptic `CONFIRM`, pop to trash.
- Swipe start → restore (tertiaryContainer + `undo` meaning). End swipe **does not** exist (already deleted).
- `Empty` → dialog title `Empty Trash?` body `Notes older than 30 days are already gone. This removes the rest on this phone.` `Cancel` / `Empty` error filled.

Empty canvas: `Nothing in Trash.` `bodyLarge` `onSurfaceVariant`, centered. No art. No FAB.

Purge worker daily: `deletedAt < now-30d`. Home never shows these rows.

---

## Account — `settings/account`

**Signed out:** same copy as the account sheet. Filled `Sign in` 56 stadium → `auth`. No Clerk chrome.

**Signed in (Manage account — ours, never Clerk UserProfile):**

```
[ 64 avatar ]
email                  titleMedium
Google · Synced        bodySmall onSurfaceVariant
                       (or Email · Sync paused)

Sync now               text, only if paused
Sign out               text
```

- Avatar: Coil 64 circle, or letter on `primaryContainer`.
- Method line: `Google` if OAuth, else `Email`. Then `·` then `Synced` / `Sync paused`.
- `Sync now`: sets `sync_paused = false`, starts outbox. Hidden when already synced.
- `Sign out` → dialog in [ui/22-SYSTEM-EDGES.md](22-SYSTEM-EDGES.md). Local notes stay.

No email-change, no password-change, no connected-apps list in v1. Those live in Clerk Dashboard if we ever need them.

---

## What syncs — `settings/privacy`

Title `What syncs`. Scroll. This is the honesty page (Play Data safety + in-app). Not legal advice; do not invent extra collection.

`titleSmall` section + `bodyMedium` `onSurfaceVariant` 8 below. 20 side, 16 between sections.

**On this phone**  
Notes, drawings, images, and projects live in a local database. You can write with no account.

**If you sign in**  
We send your email (and Google account, if you use it) to Clerk, our sign-in provider. Notes you choose to sync go to Convex, our sync provider. Locked notes upload only as ciphertext. Search text of locked notes is not stored.

**If you don’t sign in**  
Nothing leaves this phone. There is no cloud copy.

**Backups**  
Android cloud backup does **not** include your notes database in v1. A new phone will not restore locked notes. Sign in and Sync if you want a copy.

**Delete**  
Sign out keeps local notes. Delete account removes the Clerk user and the cloud copy. Local notes stay until you delete them here.

**Licenses**  
Row at the bottom, 56, `Open-source licenses` → system / our list screen (font OFL + AndroidX). No website required in v1; Play Console still needs a privacy **URL** at store listing time (host this same text).

---

## About — `settings/about`

Centered. 96 from top of content.

Wordmark `titleLarge` 500. 8 dp. `Notesup` is the only brand line.  
`{versionName} ({versionCode})` `labelSmall` `onSurfaceVariant`.

No website. No social. No rate-the-app. No changelog carousel.

---

## Note color sheet (overflow, not Settings)

8 dots, 40 dp, gap 12, pad 20. Order: none (ring only) · wax · clay · moss · tide · ink-blue · plum · rust. Selected: 2 dp `onSurface` ring. Applies 8% overlay on the card only. Title `Note color` `titleSmall`.

---

## Strings (this file)

```
settings=Settings
appearance=Appearance
type=Type
paper=Paper
home_view=Home view
focus=Focus
sort_checked=Sort checked to end
lock_new=Lock new notes
lock_screen_history=Show notes on lock screen
default_notes=Default notes app
default_notes_body=Write from the lock screen and the stylus shortcut.
trash=Trash
account=Account
what_syncs=What syncs
about=About
wallpaper=Wallpaper
bundled=Bundled
type_fail=Couldn’t add this type.
empty_trash=Empty
empty_trash_q=Empty Trash?
empty_trash_body=Notes older than 30 days are already gone. This removes the rest on this phone.
empty_trash_action=Empty
nothing_trash=Nothing in Trash.
deleted_today=Deleted · today
deleted_days=Deleted · %dd left
restore=Restore
sync_now=Sync now
set_lock=Set a screen lock
set_lock_body=Locked notes need a fingerprint, face, or device PIN.
open_settings=Open settings
privacy_phone=On this phone
privacy_in=If you sign in
privacy_out=If you don’t sign in
privacy_backup=Backups
privacy_delete=Delete
licenses=Open-source licenses
note_color=Note color
size_s=S
size_m=M
size_l=L
focus_off=Off
focus_off_body=Chrome stays.
focus_auto=Auto
focus_auto_body=Chrome fades after you stop typing.
focus_sentence=Sentence
focus_sentence_body=Other paragraphs dim. Off when TalkBack or high contrast is on.
focus_typewriter=Typewriter
focus_typewriter_body=The line you type stays mid-page.
```
