# 17 — Capture & share-in

Official: [Create a note-taking app](https://developer.android.com/develop/ui/views/touch-and-input/stylus-input/create-a-note-taking-app).  
This is `NoteCaptureActivity` + share target + launcher shortcuts. **Not** `MainActivity`.

---

## Why a second activity

`ACTION_CREATE_NOTE` from the lock screen / stylus must be `showWhenLocked` + `turnScreenOn`. The main task must not leak the note list onto the keyguard. Official: from lock → **full screen**; unlocked via the notes role → **floating window**.

Manifest (already in BUILD) plus:

```
android:resizeableActivity="true"
android:excludeFromRecents="true"   <!-- lock-screen launches -->
android:taskAffinity=""
android:documentLaunchMode="always" <!-- unlocked floating multi-instance -->
```

When `KeyguardManager.isKeyguardLocked()`: keep `excludeFromRecents`. When unlocked floating: recents may show this document.

`supportsPictureInPicture`: **no**.

---

## Who opens it

| Entry | Extra | Note type |
|---|---|---|
| Lock-screen notes affordance | — | TEXT, unless stylus extra |
| Stylus / `EXTRA_USE_STYLUS_MODE == true` | stylus | INK, canvas focused |
| `EXTRA_USE_STYLUS_MODE == false` | keyboard | TEXT, IME |
| Launcher shortcut New note | — | TEXT |
| Shortcut checklist / drawing | type | CHECKLIST / INK |
| `notesup://new?type=` | type | same |
| Share `text/plain` | `EXTRA_TEXT` | TEXT, body = text |
| Share `image/*` | stream | TEXT + image block(s), max 10 |
| Widget cells | deep link | same as shortcuts |

Share and shortcuts may also land in **MainActivity** if the app is already unlocked and in the foreground. **Decision:** share + shortcuts + widgets → `MainActivity` editor (normal). `ACTION_CREATE_NOTE` and lock-screen / role floating → `NoteCaptureActivity` only.

Skip Welcome. Set `onboarding_done = true`.

---

## Lock-screen layout (keyguard locked)

Full screen. Paper + grain. **No** home, **no** search, **no** avatar, **no** pills, **no** list of old notes.

```
┌─────────────────────────────────────┐
│ status (lock-screen rules)           │
│  Done                           ⋮    │  64, no back-to-home
│                                      │
│  Title                               │
│  Body (or ink canvas if stylus)      │
│                                      │
│      [ toolbar if TEXT ]             │
└─────────────────────────────────────┘
```

- **Done** start: text `labelLarge` `primary` — saves (already autosaved) and `finish()`. Not a back arrow (back would look like “into the app”).
- `⋮`: Pin · Lock · Delete. No Move, Export, Share, Paper, Type (too much on a lock screen). Delete + undo snackbar still works; after 4 s and activity finished, delete sticks.
- IME / ink toolbar same tokens as the editor.
- `FLAG_SECURE` **on** this window when the keyguard is locked (shoulder + recents). Off after unlock if the note is not a locked note.
- History: **forbidden** unless Settings `Show notes on lock screen` is On **and** that toggle was set while unlocked. If Off: ignore any “open existing” extra; always `created=true`.
- To open an existing locked-history note: `requestDismissKeyguard` first; on success, if the note is app-locked, run the lock gate ([ui/18-LOCK-GATE.md](18-LOCK-GATE.md)).

Back / swipe: `finish()`. Predictive back to wallpaper. Does **not** open `MainActivity`.

---

## Floating window (unlocked + notes role)

System sizes the bubble/freeform. Our min: **360 × 420**. Paper. Same Done + ⋮ chrome.

**Content capture** (official `ACTION_LAUNCH_CAPTURE_CONTENT_ACTIVITY_FOR_NOTE`): show a 48 `crop_free` in the bar **only if**

- `isLaunchedFromBubble() == true`
- `RoleManager.isRoleHeld(ROLE_NOTES)`
- `DevicePolicyManager.getScreenCaptureDisabled(null) == false`
- Not keyguard-locked

Success (`CAPTURE_CONTENT_FOR_NOTE_SUCCESS`): insert image block from the returned URI (same downsample as picker). Cancel / failed / blocked: inline `Couldn’t capture.` No snackbar.

Do **not** put capture in the full-screen editor. Users already have the system screenshot.

---

## Share-in (MainActivity)

No confirm. No “Save to which notebook?”

1. New TEXT note in Inbox.
2. If `EXTRA_TEXT` / `EXTRA_SUBJECT`: body = subject line + blank + text (subject only if present). Caret at end.
3. If image streams: insert up to 10 image blocks, then any text as a paragraph after.
4. Open editor `created=true`. IME if there is text to continue; else if only images, no IME.
5. Unsupported type: `Couldn’t add that.` on a one-line home snackbar (this is fail-class, allowed like image fail). Do not create an empty note.

`ACTION_SEND_MULTIPLE`: same, cap 10 images.

---

## Launcher shortcuts (static)

`res/xml/shortcuts.xml` — three, in order:

| id | Label | Icon | Link |
|---|---|---|---|
| `new_note` | New note | `add` | `notesup://new` |
| `new_list` | New checklist | `checklist` | `notesup://new?type=checklist` |
| `new_ink` | New drawing | `draw` | `notesup://new?type=ink` |

Long-press launcher icon. No dynamic shortcuts in v1 (no “resume last note”).

---

## Consent copy (Settings)

See [ui/15-SETTINGS.md](15-SETTINGS.md) rows 8–9. Offer `ROLE_NOTES` **only** from Settings. After they become the role holder, do not toast.

---

## Strings

```
done=Done
capture_content=Capture what’s behind
capture_fail=Couldn’t capture.
share_fail=Couldn’t add that.
shortcut_note=New note
shortcut_list=New checklist
shortcut_ink=New drawing
```
