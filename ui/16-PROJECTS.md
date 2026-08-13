# 16 — Projects (the place)

Projects are Things 3 areas, not Evernote notebooks.  
Home filter is in [ui/06-HOME.md](06-HOME.md). **This file is create, open, rename, delete, empty, move.**

Route: `project/{projectId}`. Inbox is **not** a project (`projectId == null`).

---

## Projects filter (home)

Already in 06. Locked extras:

- Inbox first, always. `inbox` 24 + `Inbox` + count.
- Then user projects in `order`.
- Long-press a **user** project (not Inbox): menu `Rename` · `Color` · `Delete`. Haptic `LONG_PRESS`.
- Long-press Inbox: nothing.
- Tap row → `project/{id}` (Inbox tap selects… **no.** Inbox is not a destination. Tap Inbox stays on the Projects list? Wait.)

**Decision:** Inbox is only a **move target** and the default bucket. Tapping Inbox on the Projects filter does **not** open a screen. It does nothing extra — Inbox notes already live in All. The row exists so Magic Plus and Move have a place.

Tap a user project → `project/{id}`.

---

## Project screen

Same home chrome, with:

- Wordmark **replaced** by the project name `titleLarge` 500, 1 line ellipsis. Leading: hue 10 dp + emoji 18 if any, 8 before the name.
- End: search 48, then `more_vert` 48. **No avatar** on this screen (account stays on All).
- **No pills. No pin strip.** Grid/list toggle stays.
- Plus is still capture, and new notes land **in this project**.
- `more_vert`: Rename · Color · Delete.

Cards: same as home, no project-name meta (you are inside it).

Back: home with Projects filter selected, or All if they arrived from search.

---

## Empty project

Centered. No wordmark.  
`Nothing in {name} yet.` `bodyLarge` `onSurfaceVariant`.  
Plus still visible. No second button.

---

## Create — sheet

Opened from home Projects filter, app-bar `add` only.

Max 85%. Handle. Title `New project` `titleSmall`.

1. Field 56, radius 16, hint `Name`, 1–32 chars, ImeAction.Done. Counter `labelSmall` `{n}/32` end when n ≥ 24.
2. 16 dp.
3. **Hue:** 8 dots, 40 dp, gap 12. Same 8 tints as note color (ui/02). Default `0` (none = `outline` ring). Selected: 2 dp `onSurface` ring.
4. 16 dp.
5. **Emoji:** text button `Add emoji` → system / IME emoji. One grapheme. Trailing `close` 48 clears. Optional. Default none.
6. 24 dp.
7. Filled `Create` 56 stadium. Disabled while name blank or only whitespace. Haptic `CONFIRM`. Inserts at end of `order`. Dismiss. Stay on Projects filter, new row visible.

No color-only project without a name.

---

## Rename — sheet

Same as Create, title `Rename`. Field prefilled, select-all. Primary `Save`. Hue + emoji editable here too (Color menu item opens the **same** sheet scrolled to hue).

---

## Color — shortcut

`Color` in the long-press menu opens the same sheet with name read-only (or just the hue row in a smaller sheet). **Decision:** open Rename sheet. One place.

---

## Delete project — dialog

Title `Delete {name}?`  
Body `Notes in this project move to Inbox. They are not deleted.`  
`Cancel` / `Delete` error filled.

On confirm: `projectId = null` on those notes, delete project row, haptic `REJECT` (destructive). Snackbar **none** (not undo-class for a project — restoring a project + re-file is a v1.1). Stay on Projects filter.

Cannot delete Inbox.

---

## Move sheet (from overflow or select)

Title `Move to`. Radio. First row Inbox. Then projects. Selected = current.  
Confirm filled `Move`. Disabled if unchanged.  
Select-mode with n > 1: same sheet, body `{n} notes`.

---

## Magic Plus (already law)

Drag SplitButton onto a project row or pin-strip → create there, open editor, haptic `CONFIRM`. Ghost is the plus at 56, 38% scrim under finger.

---

## Strings

```
new_project=New project
rename=Rename
color=Color
project_name=Name
add_emoji=Add emoji
create=Create
save=Save
delete_project=Delete %s?
delete_project_body=Notes in this project move to Inbox. They are not deleted.
empty_project=Nothing in %s yet.
move_to=Move to
move=Move
move_n=%d notes
```
