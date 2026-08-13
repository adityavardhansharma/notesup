# 22 — System edges & confirmations

Everything a user can hit that is not a “place” in 06–21.  
Dialogs inherit [ui/09-SHEETS-DIALOGS.md](09-SHEETS-DIALOGS.md): radius 28, title `titleSmall`, body `bodyMedium` `onSurfaceVariant`, text cancel + filled confirm. Destructive confirm = `error` filled.

---

## 1. Sign out

Title `Sign out?`  
Body `Notes stay on this phone. Sync stops.`  
`Cancel` / `Sign out` (not error — they can sign in again).  
On confirm: Clerk signOut, `sync_paused` ignored, avatar resets, pop to home. No snackbar.

---

## 2. Delete account

Title `Delete account?`  
Body `This removes your sign-in and the cloud copy. Notes on this phone stay.`  
Field 56, hint `Type DELETE`. Confirm enabled only when the field is exactly `DELETE`.  
`Cancel` / `Delete` error.  
On confirm: Clerk delete + Convex purge mutation, then local session cleared. Haptic `REJECT`. Stay on home, signed out.

---

## 3. Delete note (single)

No dialog. `deletedAt = now`. Snackbar `Note deleted` + `Undo` 4 s. Haptic `REJECT`.

---

## 4. Multi-select delete

Title `Delete {n} notes?`  
Body `They go to Trash for 30 days.`  
`Cancel` / `Delete` error. Then snackbar ` {n} notes deleted` + `Undo` (restores the set).

---

## 5. Delete project

[ui/16-PROJECTS.md](16-PROJECTS.md).

---

## 6. Empty Trash

[ui/15-SETTINGS.md](15-SETTINGS.md).

---

## 7. Sync these notes?

[ui/14-ONBOARDING.md](14-ONBOARDING.md). After any first link of locals, including Manage account `Sync now` if `remoteId` still empty.

---

## 8. Camera / picker / capture failure

No toast.  
Editor: ghost block `Couldn’t add image` + tap.  
Share-in: snackbar `Couldn’t add that.` (fail-class, like export).  
ROLE_NOTES capture: `Couldn’t capture.` inline in the capture bar.

User denies nothing we requested (Photo Picker / system camera need no runtime perm). If they back out of the picker: do nothing.

---

## 9. Export failure

2 dp bar dies at 2 s. Snackbar `Couldn’t export`. Haptic `REJECT`.

---

## 10. Missing / deleted deep link

`notesup://note/{id}` and the note is gone (unknown id or purged):

Full paper. Bar back. Center `This note isn’t here.` `bodyLarge` `onSurfaceVariant`.  
Back → home. No plus required; plus still OK if this is MainActivity.

---

## 11. Session dead

Clerk `userFlow` goes null while they were signed in (revoked, deleted on another device, token unrestorable):

- Treat as signed out. **No modal.**
- Avatar → `account_circle`.
- If they had been syncing: `cloud_off` after the usual 4 s rule.
- Local notes stay.
- Next sign-in is the normal `auth` screen. If locals still have `remoteId`s, they re-attach after `setActive`; if the cloud user is gone, they are just local again. No second onboarding.

---

## 12. Auth leftovers (same paper as ui/14)

### Password (only if Clerk requires it)

Bar back. Title `Enter your password`. Field 56 radius 16, `PasswordVisualTransformation`, ImeAction.Go. Filled Continue 56 stadium.  
`Use a code instead` if email_code is also available.  
IME: `WindowInsets.ime + navigationBars` pad the button. Never hide Continue under the keyboard.

### Authenticator

Same 6 cells as email code. Title `Enter your authenticator code`.  
`Use a backup code` → one field 56, filled Continue.

### Hosted fallback

Custom Tab. Our screen stays with a 20 dp spinner on the Google button until the tab returns. Cancel = re-enable.

### Landscape Welcome / Auth

Same column, pad 32, max 400. Buttons still 24 + nav from the bottom. Wordmark may sit closer to the top (weight 1 still). No side-by-side form.

### Pressed / loading (all auth + Welcome buttons)

- Pressed: ripple only. No extra fill, no scale.
- Loading: 20 dp `CircularProgressIndicator` `onPrimary` (filled) or `primary` (Google/outlined), centered in the button, label gone, all actions disabled.
- Elevation: **0** everywhere.

---

## 13. Widget picker previews

Glance: `previewImage` (≤ API 31) **and** `providePreview` on API 35+. `initialLayout` = Glance default loading, paper-colored.

Draw the static `previewImage` as a **real screenshot of the empty/default state**, paper `#F6F1EA`, system radius not painted (the picker clips):

| Widget | Preview content |
|---|---|
| New note | Three cells: wax `add` · outline `checklist` · outline `draw` |
| Pinned | Title `Pin a note` on cream |
| Recent | Header mark + `Recent`, one row `Write anything.` |
| Project | Header hue + `Inbox`, one row `Write anything.` |

Project configure: **system** `AppWidget` configure activity. Our UI: a list of projects + Inbox, rows 56, tap binds `projectId` and `RESULT_OK`. Empty projects: Inbox only. Title `Choose a project`.

---

## 14. Recents / task

`TaskDescription`: color `background`, label `Notesup`, icon = launcher.  
Locked / keyguard capture: `FLAG_SECURE` so recents is empty/solid paper, not the body.

System splash: `core-splashscreen`, `windowSplashScreenBackground` = `background`, icon = launcher adaptive. Keep on screen until first Room emission. No extra logo.

---

## 15. Status / nav bar

`enableEdgeToEdge`. Icons: dark on paper, light on ink (already).  
Nav bar: transparent, contrast enforced by the system. Do not paint a fake bar.

---

## 16. Confirmations we will **not** add

- Sign-in success / “Welcome back”
- Saved / Synced
- Rate the app
- What’s new
- Leave-without-saving (autosave is truth)
- Overwrite conflict (we make a copy)
- Notification permission
- Analytics / crash opt-in

---

## Strings

```
sign_out_q=Sign out?
sign_out_body=Notes stay on this phone. Sync stops.
delete_account_q=Delete account?
delete_account_body=This removes your sign-in and the cloud copy. Notes on this phone stay.
type_delete=Type DELETE
delete_n=Delete %d notes?
delete_n_body=They go to Trash for 30 days.
deleted_n=%d notes deleted
export_fail=Couldn’t export
empty_missing=This note isn’t here.
choose_project=Choose a project
```
