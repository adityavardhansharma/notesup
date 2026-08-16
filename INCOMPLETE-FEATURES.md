# Notesup — Incomplete & Missing Features

> **Purpose of this document.** This is a working spec of everything we discussed that is **not present** or **only partially present** in the app today. For each item it records: what exists now, what is missing, the intended behaviour, and a detailed UI / interaction / visual design so it can be built without re-deriving decisions.
>
> **Verification caveat.** The whole codebase has been changed and reviewed **statically only** — no build or emulator run has happened. Treat "done" items as "written and reasoned correct," not "seen working on a device."
>
> **Status legend**
> - 🔴 **Missing** — not implemented at all (stub, placeholder, or absent).
> - 🟡 **Partial** — works for the common path but has gaps, dead controls, or missing polish.
> - 🟢 **Done (this branch)** — implemented on `fix/audit-bugs-and-signin-crash`, listed only where it clarifies a related gap.

---

## Table of contents

1. [Sync: Clerk auth + Convex backend](#1-sync-clerk-auth--convex-backend)
2. [Home-screen widgets (Glance)](#2-home-screen-widgets-glance)
3. [Ink / handwriting canvas](#3-ink--handwriting-canvas)
4. [Tables](#4-tables)
5. [Rich-text formatting (bold / italic / links)](#5-rich-text-formatting-bold--italic--links)
6. [Image polish (captions, viewer, camera, multi-image share)](#6-image-polish-captions-viewer-camera-multi-image-share)
7. [Projects: inbox, move-to-project, edit & delete, colours](#7-projects-inbox-move-to-project-edit--delete-colours)
8. [Trash / recycle bin](#8-trash--recycle-bin)
9. [Settings screens that are still placeholders](#9-settings-screens-that-are-still-placeholders)
10. [Note lock: real encryption](#10-note-lock-real-encryption)
11. [Checklist behaviour: sort-checked, reorder, delete, enter-to-add](#11-checklist-behaviour-sort-checked-reorder-delete-enter-to-add)
12. [Focus mode / typewriter mode](#12-focus-mode--typewriter-mode)
13. [Share-into-app (received text/images)](#13-share-into-app-received-textimages)
14. [Account screen & onboarding polish](#14-account-screen--onboarding-polish)
15. [Cross-cutting design system notes](#15-cross-cutting-design-system-notes)
16. [Suggested build order](#16-suggested-build-order)

---

## 1. Sync: Clerk auth + Convex backend

**Status: 🔴 Missing (intentionally deferred until you provide the API key).**

### What exists now
- `AuthRepository` has stub methods (`signInGoogle`, `startEmail`, `verifyEmail`, `signInPasskey`) that all `error(...)` immediately.
- `AuthScreen` collects an email and moves to a 6-digit `AuthCodeScreen`, but the code is never verified — entering any 6 digits just finishes onboarding.
- `SyncCoordinator.start()` returns early (URL is a placeholder, `auth.current()` is null).
- `ConvexApi` is three empty declarations.
- `SyncQueueDao` + `sync_queue` table exist and the app enqueues one row per note change, but **nothing drains the queue**.
- The data model is already sync-ready: every `Note`/`Project` carries `remoteId`, `rev`, `writerId`, `baseRev`, `baseWriterId`.

### What is missing
- Real Clerk initialization + Google / email-OTP / passkey sign-in.
- A Convex client that pushes queued local changes and pulls remote changes.
- Conflict resolution (last-writer-wins using `rev` + `writerId`, or a merge).
- A sync status surfaced in the UI.

### Intended behaviour
- Sync is **optional and additive**. A signed-out user gets a 100%-working local app (this is already the case after this branch). Signing in only turns on background mirroring of the exact same local Room data.
- On sign-in: adopt the local notes into the account (upload everything with `remoteId == null`), then keep Room as the source of truth and mirror both directions.
- On sign-out: keep local data, stop mirroring, clear the auth user.

### UI / design

**Auth entry (`AuthScreen`)** — already laid out; wire the buttons.
- Header wordmark "Notesup", title *Sign in to sync*, body *Notes stay on this phone until you do.*
- **Continue with Google**: 56 dp outlined button, 16 dp corner radius, Google glyph (unspecified tint so it keeps brand colours) + label. On tap → Clerk Google flow → on success `finishOnboarding()`.
- Divider row with centered "or".
- Email `OutlinedTextField` (16 dp radius, email keyboard, IME action Go) + **Continue** stadium button, enabled when the value contains `@`.
- Bottom: *Continue without an account* text button, then legal caption (280 dp max width, centered, `labelSmall`, `onSurfaceVariant`).

**OTP screen (`AuthCodeScreen`)** — add real states:
- Title *Check your email*, subtitle *We sent a code to* + the email.
- 6-cell code field (current impl is a single numeric field; upgrade to 6 boxed digits, 12 dp gap, `titleLarge`, auto-advance, auto-submit on the 6th digit).
- States: **idle**, **verifying** (disable field, small circular progress), **error** (shake + red helper text *That code didn't match*), **resend** (text button *Resend code*, disabled with a 30 s countdown "Resend in 0:30").

**Sync status chip** (new, on Home top bar next to the account avatar):
- States and glyphs (all icons already exist in `NotesupIcons`): **Offline** = `Offline` (cloud-off) `onSurfaceVariant`; **Syncing** = `Sync` icon rotating 360° / 1200 ms `LinearEasing`; **Synced** = brief `Check` in `primary` for 1.5 s then hide; **Paused** = `Offline`, tappable to resume.
- Tapping it opens a small sheet: last-synced timestamp, a *Sync now* button, and a *Pause sync* switch bound to `prefs.syncPaused`.

**Account state in menus**: `AccountSheet` and `ManageAccountScreen` should show the avatar, email, and a *Signed in* row when authenticated (currently only sign-in/sign-out actions). Avatar = `AsyncImage(user.imageUrl)` clipped to a circle, 40 dp; fall back to the `Account` glyph.

---

## 2. Home-screen widgets (Glance)

**Status: 🟡 Partial — the "New note" widget works; the other three show hard-coded placeholder text.**

### What exists now
- Four `GlanceAppWidget`s + receivers registered in the manifest:
  - `NewNoteWidget` — a 3-cell row (Note / List / Ink) on a paper background; the whole row opens `MainActivity`. **Functional.**
  - `PinnedWidget` — renders the string *No pinned notes yet* always.
  - `RecentWidget` — renders the header "Recent" + the empty-home string always.
  - `ProjectWidget` — renders "Inbox" + empty-home string always.
- `ProjectWidgetConfigureActivity` lists projects to pick, but the chosen id is **discarded** (it just `setResult(RESULT_OK)` with no stored mapping).
- `WidgetUpdater.schedule()` debounces and calls `updateAll` on all four after any note change.

### What is missing
- Pinned/Recent/Project widgets don't read Room, so they never show real notes.
- No per-widget → project mapping is persisted, so the project widget can't know which project it is.
- Cells in the New-note widget aren't individually clickable (only the whole row is), so "List" and "Ink" don't deep-link to those note kinds.
- No deep links: tapping a note in a widget should open **that** note, not just the app.

### Intended behaviour
- **Recent**: newest N (~5) alive notes, title + relative time, tap opens the note.
- **Pinned**: pinned notes, same layout; empty state when none.
- **Project**: the notes of the configured project (or Inbox), with the project name as header.
- **New note**: each of the three cells launches straight into a new note of that kind (text / checklist / ink).

### Design

Data access inside Glance: add a Hilt `@EntryPoint` (`interface WidgetEntryPoint { fun noteDao(): NoteDao }`) fetched via `EntryPointAccessors.fromApplication(context, ...)` inside the `suspend provideGlance`. Query a small suspend helper (`noteDao.observeRecent().first().take(5)` etc.).

Deep links: reuse the existing `notesup://` scheme (already a `VIEW` intent-filter on `MainActivity`). Define `notesup://note/{id}`, `notesup://new?kind=text|checklist|ink`, `notesup://project/{id}`. `MainActivity` already receives `intent.dataString` as `initialDeepLink` into `NotesupNav`; add parsing there to push the right destination on start.

Visual spec (all widgets):
- Background `Color(0xFFF6F1EA)` (the warm paper) in light; provide a dark variant via `GlanceTheme` (`surface`). 12 dp padding, 16 dp rounded corners on the outer box.
- Header row: title `titleSmall`-equivalent (Glance `TextStyle` 14 sp, medium), plus a small wordmark on the right at 60% alpha.
- List rows: 44 dp min height, single-line title (ellipsize), trailing relative time in 11 sp `onSurfaceVariant`. Divider 1 dp `outlineVariant` at 40% alpha between rows.
- Empty state: centered `bodyMedium` grey line, e.g. *Nothing pinned yet*.
- Tap targets: each row `clickable(actionStartActivity(deep-link intent))`.
- **New-note widget**: keep the 3-cell split; make each cell its own `clickable`. Left cell is the "wax" accent (`Color(0xFF8B2942)`, white text); the other two are paper with `Color(0xFF1C1917)` text. 48 dp tall cells, 4 dp gaps.

`ProjectWidgetConfigureActivity`: on selection, persist `appWidgetId → projectId` (a small DataStore/prefs map or a Room table), then `updateAll`/`update` that widget and `setResult(RESULT_OK)`. Set `RESULT_CANCELED` up front so backing out doesn't leave a broken widget.

---

## 3. Ink / handwriting canvas

**Status: 🔴 Missing — `InkCanvas` is an empty grey box; ink blocks render as a 280 dp placeholder.**

### What exists now
- `Block.Ink(id, inkId, previewPath)` in the model; `InkEntity(id, noteId, strokeBlob, heightDp, updatedAt)` and `InkDao` for storage.
- `NoteKind.INK` starter block creates an `Ink` block.
- The Jetpack **Ink** libraries are already dependencies (`androidx.ink:ink-authoring-compose`, `ink-brush`, `ink-strokes`, `ink-storage`, `ink-rendering`, `ink-nativeloader`).
- `InkCanvas` composable is a stub (grey rounded box), and `EditorScreen` renders `Block.Ink` as a grey box.
- `NotesupEditorToolbar` (a pen/highlighter/eraser toolbar) exists but isn't wired to a live canvas.

### What is missing
- An actual drawing surface using `InProgressStrokesView` / the Ink authoring APIs.
- Brush selection (pen, highlighter, eraser), colour, and stroke width.
- Serialize strokes to `InkEntity.strokeBlob` (via `ink-storage`), render saved strokes back, and generate a raster `previewPath` for cards/exports.
- Undo/redo of strokes.

### Intended behaviour
- Tapping an ink note (or inserting an ink block) opens a canvas that captures stylus/finger strokes with low latency.
- Strokes persist per block and re-render when the note reopens.
- A thumbnail preview is shown on the note card.

### Design

**Canvas**
- Full-width, height from `InkEntity.heightDp` (default 280 dp), `surfaceContainerLow` background, `shapes.large` clip, subtle 1 dp `outlineVariant` border.
- Uses `InProgressStrokesView` for the wet layer; committed strokes rendered by `CanvasStrokeRenderer` on a `Canvas` behind it.
- Palm rejection: prefer `MotionEvent` tool-type stylus when a stylus is present; allow finger when none.

**Tool bar** (bottom, floating, `surfaceContainerHigh`, `shapes.extraLarge`, 52 dp tall — matches `FormatToolbar`)
- Buttons (icons already exist): `Pen`, `Highlighter`, `Eraser`, `Width`, `Palette`, then `Undo` / `Redo`.
- **Pen**: opaque round brush. **Highlighter**: 40% alpha, flat, wider default. **Eraser**: removes intersected strokes (whole-stroke erase is simplest, pixel-erase is nicer).
- **Width**: popover with 3–4 preset dots (2 / 4 / 8 / 14 px) shown at actual size.
- **Palette**: row of 8 swatches reusing `TintWashes`-style colours + black/white; selected swatch gets a ring.
- Selected tool highlighted with `primaryContainer` pill behind the icon.

**Persistence**
- On stroke commit (and debounced), serialize the stroke set with `ink-storage` into `strokeBlob`; save `heightDp`.
- On open, decode `strokeBlob` → render committed strokes.
- Generate `previewPath`: rasterize the strokes to a small PNG in `filesDir/ink/{inkId}.png`; store the path in `Block.Ink.previewPath`. `NoteCard` should show that preview (same deterministic-path approach as images), and `MarkdownExport` already references `media/{inkId}.png`.

**Undo/redo**: keep an in-memory stack of committed stroke ids; undo pops and re-renders. Reset the stack when leaving the note.

---

## 4. Tables

**Status: 🟡 Partial — a table can be inserted and is exported to Markdown, but in the editor it only renders a summary `▦ rows × cols` and cannot be edited.**

### What exists now
- `Block.Table(id, cols, rows, cells: List<String>, headerRow)` in the model.
- Insert menu offers "Table" → creates a 2×2 empty table.
- `MarkdownExport` renders a proper Markdown table.
- Editor shows only `▦ 2 × 2` text; no cell editing, no add/remove row/column.

### What is missing
- A real grid UI with editable cells.
- Add/remove row and column; toggle header row.

### Intended behaviour
- Inline editable grid; first row styled as header when `headerRow` is true.
- Controls to add/remove the last row/column and to toggle the header.

### Design
- Render as a `Column` of `Row`s; each cell is a `BasicTextField` in a bordered box: 1 dp `outlineVariant`, 8 dp inner padding, min 40 dp tall, cells share width evenly (`weight(1f)`), body uses `bodyNoteStyle`.
- Header row (when `headerRow`): `surfaceContainerHigh` fill, `titleSmall` weight.
- Around the table, a compact control strip (only visible while a cell in that table is focused): `+ Row`, `+ Col`, `– Row`, `– Col`, and a *Header* toggle chip.
- Cell text edits fold through the same `EditorViewModel` field model used for other blocks: key each cell `t:{blockId}:{index}`, fold into `cells` on commit/autosave. Add/remove operations reshape `cells`/`rows`/`cols` and commit.
- Horizontal scroll if the table is wider than the screen (`horizontalScroll`), with the first column optionally pinned later.

---

## 5. Rich-text formatting (bold / italic / links)

**Status: 🔴 Missing — the model supports spans but the UI has no formatting controls and renders plain text only.**

### What exists now
- `RichText(text, spans: List<RichSpan>)` with `RichSpan(start, end, style, href)` and `SpanStyleTag { BOLD, ITALIC, UNDERLINE, STRIKE, CODE, LINK }`.
- The dependency `com.mohamedrejeb.richeditor` is present but unused.
- The paragraph editor uses a plain `BasicTextField(TextFieldState)` — no styling, spans are never created or rendered.
- The old `FormatToolbar` had Bold/Italic/Underline buttons wired to empty lambdas (removed on this branch; only Insert remains).

### What is missing
- Applying/removing inline styles over a selection.
- Rendering spans (bold/italic/underline/strike/inline-code/link) in the editor and in previews/exports.
- Link entry + tap-to-open.

### Intended behaviour
- Select text → a formatting bar (or the bottom toolbar) toggles styles on the selection.
- Links prompt for a URL and become tappable.

### Design
- **Selection toolbar**: when there's a non-empty selection, show a floating pill above the selection (or reuse the bottom `FormatToolbar`) with `Bold`, `Italic`, `Underline`, `Strike`, `Code`, `Link` (all icons exist). Active styles for the current selection are highlighted (`primaryContainer` pill).
- **Link**: tapping `Link` opens a small dialog — *Link text* (prefilled from selection) + *URL* fields, **Add** / **Cancel**. Stored as a `RichSpan(style=LINK, href=...)`.
- **Rendering**: convert `RichText` → `AnnotatedString` (map each span tag to `SpanStyle`/`TextDecoration`; `CODE` uses `JetBrainsMono` + `surfaceContainer` background; `LINK` uses `primary` + underline and is clickable). This means moving paragraph editing to a `TextField` that supports styled `TextFieldValue`/`AnnotatedString`, or adopting the `richeditor-compose` state object.
- **Exports**: extend `MarkdownExport` to emit `**bold**`, `*italic*`, `` `code` ``, `[text](url)`, etc. from spans.

---

## 6. Image polish (captions, viewer, camera, multi-image share)

**Status: 🟢 Core done this branch (insert from picker/camera, inline + thumbnail display). 🟡 Polish missing.**

### What exists now (after this branch)
- Insert image via system photo picker or camera; imported + scaled by `MediaRepository` to `filesDir/media/{id}.jpg` (+ a `_t` thumbnail).
- Inline display in the editor and as the note-card cover, both from the deterministic file path.
- `Lightbox` composable exists (full-screen image + close button) but **is not wired** to anything.
- `ImageSourceSheet` (camera / gallery) is used by the editor.

### What is missing / partial
- **Captions**: `Block.Image.caption` is stored but there's no caption text field under the image.
- **Full-screen viewer**: tapping an inline image should open `Lightbox` with pinch-zoom; currently no tap handler and no zoom.
- **Delete / replace image**: no affordance to remove or swap an inserted image.
- **Camera permission edge cases**: if there's no camera app the launch is caught but there's no user feedback.
- **Multi-image share into the app** (`SEND_MULTIPLE`, and single `image/*` `SEND`) currently creates nothing (image-only shares are ignored to avoid blank notes).
- The `_t` thumbnail is generated but not used (cards load the full image).

### Design
- **Caption**: under each inline image, a single-line `BasicTextField` placeholder *Add caption…* in `bodySmall onSurfaceVariant`, centered; folds through the field model (`img:{blockId}` → `caption`).
- **Viewer**: tap image → `Lightbox` in a full-screen dialog; black 92% scrim, pinch-to-zoom + double-tap-to-zoom (`Modifier.graphicsLayer` + transformable state, clamp 1×–5×), swipe-down to dismiss, close button top-start.
- **Image context menu**: long-press an inline image → small menu *View*, *Replace*, *Remove*.
- **Cards** should prefer the `_t` thumbnail path for performance.
- **Share-in images**: for `image/*` `SEND` / `SEND_MULTIPLE`, create a note and import each uri as an `Image` block (reuse `MediaRepository.import`); see §13.

---

## 7. Projects: inbox, move-to-project, edit & delete, colours

**Status: 🟡 Partial.**

### What exists now
- Create a project (name, hue 0–7, optional emoji) from the Projects filter; project list; open a project screen with its notes.
- `ProjectRepository.delete` moves notes to Inbox and soft-deletes (rev bumped this branch).
- `ProjectEditSheet` supports an initial name/hue/emoji + a `title` override, so it's ready for "edit" reuse.

### What is missing
- **Inbox is not tappable** in the Projects list — you can't view no-project notes.
- **Move to project** — the bulk "Move" action was a dead button (removed); there's no move UI at all (multi-select on Home, or a per-note menu).
- **Edit / delete a project** — no entry point to rename, recolour, re-emoji, or delete an existing project.
- **Emoji picker** — `ProjectEditSheet` takes an emoji but there's no picker; the colour dot uses `TintWashes` but the little project bullet on Home is a fixed `FilledIconButton` that ignores the project hue.
- **Reorder projects** — `order` field exists, always 0, no drag handle.

### Design
- **Inbox row** (Projects list, already rendered): make it `clickable` → a project screen variant showing notes with `projectId == null` (add an `Inbox` destination, or `ProjectDest(null)`), header "Inbox" with the `Inbox` glyph.
- **Project row**: replace the fixed-colour dot with a 10–12 dp circle filled with `TintWashes[hue]`; show emoji (if any) before the name; trailing note-count in `labelSmall onSurfaceVariant`; overflow `More` button → *Edit*, *Delete*.
  - **Edit** opens `ProjectEditSheet` prefilled (title *Edit project*, button *Save*).
  - **Delete** → confirm dialog (reuse copy: *Notes in this project move to Inbox. They are not deleted.*).
- **Move to project**: in Home multi-select, restore a **Move** action → bottom sheet listing Inbox + all projects (each a row with colour dot + name); picking one sets `projectId` on all selected notes. Also add *Move to project* to the editor overflow menu.
- **Emoji picker**: a compact grid sheet of common emoji + a "None" option; selection stored on the project.
- **Reorder**: drag handle (`Drag` icon) on each project row in an edit mode; persist `order`.

---

## 8. Trash / recycle bin

**Status: 🟡 Partial — deletes are soft (30-day retention query exists) but the Trash screen is an empty placeholder.**

### What exists now
- `setDeleted` marks `deletedAt`; `observeTrash`, `purgeTrash(cutoff=30 days)`, and `emptyTrash` exist in the DAO/repo.
- Home multi-delete shows an **Undo** snackbar (this branch).
- `TrashScreen` renders only the empty sentence *Nothing in trash* regardless of contents.

### What is missing
- Trash doesn't list deleted notes.
- No **restore** or **delete-forever** per note; no **Empty trash**.
- Auto-purge (`purgeTrash`) is never scheduled.
- No "Deleted · N days left" countdown (strings `deleted_today` / `deleted_days` exist but are unused).

### Design
- **List**: reuse `NoteListRow`, one row per trashed note, sorted by `deletedAt` desc. Each row's supporting line shows the countdown (*Deleted · today* / *Deleted · %dd left*, from the existing strings, computed from `deletedAt + 30d`).
- **Row actions**: trailing overflow → *Restore* (`Undo` icon, clears `deletedAt`) and *Delete forever* (`Delete` icon, hard-delete after a confirm).
- **Top bar**: title "Trash" + an **Empty trash** text/menu action (confirm dialog → `emptyTrash()`), disabled when empty.
- **Empty state**: keep the current centered sentence.
- **Auto-purge**: run `purgeTrash()` on app start (in `SyncCoordinator.start()` or a periodic `WorkManager` job) so 30-day-old notes disappear.

---

## 9. Settings screens that are still placeholders

**Status: 🟡 Partial — the main Settings list now persists its toggles (this branch), but several sub-screens are stubs.**

### What exists now
- Main `SettingsScreen`: appearance/type/paper/focus/trash/account/privacy/about rows, plus **working** switches for home-view, sort-checked, lock-new, lock-screen-history (wired this branch).
- **Privacy / "What syncs"** and **About** screens are complete.
- Stubs:
  - `AppearanceScreen` → only the word "Wallpaper".
  - `TypeSettingsScreen` → only "Roboto Flex".
  - `PaperSettingsScreen` → only "blank".
  - `FocusSettingsScreen` → lists focus options as plain text, **not selectable**.
  - The "Default notes" row shows *Off* and does nothing.

### What is missing / design

- **AppearanceScreen** (`prefs.appTheme` + `prefs.theme`):
  - **App theme** section: selectable list — *Dynamic* (Material You, API 31+), *Dieci* (true-black OLED), *Ink/Midnight/Slate* (dark palettes), *Paper/Graphite/Noon/Fog/Legal/Kraft* (light palettes). Each row is a swatch preview (a small rounded rectangle showing the palette's surface + primary) + label + trailing `Check` when selected. These keys already switch real `ColorScheme`s in `Theme.kt`.
  - **Light/Dark mode** segmented control: *System* / *Light* / *Dark* → `prefs.theme`.

- **TypeSettingsScreen** (`prefs.defaultFont` + `prefs.bodySize`):
  - **Default font** list: Roboto Flex / Literata / JetBrains Mono / Atkinson Hyperlegible, each label rendered **in its own typeface** (families exist via `noteFontFamily`), trailing `Check`.
  - **Body size** segmented control S / M / L bound to `prefs.bodySize`, with a live preview paragraph above it using `bodyNoteStyle(size)`.

- **PaperSettingsScreen** (`prefs.defaultPaper`):
  - Selectable list Blank / Lines / Dots / Grid, each row showing a **mini preview** of the pattern (reuse the `paperBackground` drawing at ~64 dp) + label + `Check`. This is the default for new notes; per-note override already lives in the editor.

- **FocusSettingsScreen** (`prefs.focus`) — make the options actually selectable (radio-style rows with `Check`): *Off*, *Auto*, *Sentence*, *Typewriter*. See §12 for what they do.

- **Default notes** row: either wire it to a real preference (e.g. default new-note kind, or default-to-locked using `prefs.lockNew`) or remove it. If kept, make it a picker: *Text / Checklist / Ink*.

General settings visuals: `ListItem`s with `headlineContent`, optional `supportingContent`, trailing value or `Switch`/`Check`; section dividers (`HorizontalDivider`); 20 dp horizontal padding; titles in `titleLarge` in the top `Bar`.

---

## 10. Note lock: real encryption

**Status: 🟡 Partial — locking gates the UI with biometrics, but note contents are stored in plaintext.**

### What exists now (after this branch)
- Menu toggles `locked`; the editor gates behind `LockGateScreen` (BiometricPrompt, device-credential fallback) with a per-session unlock.
- Locked notes hide their title/preview on cards (*Locked note*) and are excluded from the FTS index (`toFts` returns null when locked).
- `Note.lockCipher: ByteArray?` exists but is always null; content is still stored as readable `blocksJson`/`plaintext`.

### What is missing
- Actual encryption of a locked note's body at rest (the point of `lockCipher`).
- A key tied to biometric/device credential (Android Keystore).

### Design
- On lock: derive/unlock an AES key from the Android **Keystore** (require user auth), encrypt the serialized blocks into `lockCipher`, and blank `blocksJson`/`plaintext` in the row. On unlock (BiometricPrompt success): decrypt into memory for the session; re-encrypt on save while locked.
- `LockGateScreen` already provides the biometric flow; hook its success to key retrieval rather than just flipping a boolean.
- Card/preview behaviour already correct (shows *Locked note*, kept out of search).
- Edge cases: device with no secure lock → the gate already shows *Set up a screen lock* with a settings deep link (version-guarded this branch). If biometrics are removed after locking, offer device-credential fallback (already allowed) and a clear error.

---

## 11. Checklist behaviour: sort-checked, reorder, delete, enter-to-add

**Status: 🟡 Partial — checklists are now editable (toggle, edit text, add item) but lack list-management niceties.**

### What exists now (after this branch)
- Editable items with a `Checkbox`, strike-through when checked, and an **Add item** button.
- `prefs.sortChecked` persists (Settings) but nothing consumes it.

### What is missing
- **Sort checked to bottom** when `sortChecked` is on.
- **Delete an item** (e.g. backspace on empty, or a trailing remove button).
- **Enter to add** the next item (currently only the explicit button).
- **Reorder** items (drag handle).
- **Indent / sub-items** (optional, nice-to-have).

### Design
- When `prefs.sortChecked` is true, render unchecked items first then checked (stable within each group); persist order or sort at render time.
- **Enter-to-add**: intercept the newline in the item `TextFieldState`; on Enter, insert a new empty `CheckItem` after the current one and move focus to it.
- **Backspace-to-merge/delete**: on Backspace at offset 0 of an empty item, remove it and focus the previous item's end.
- **Remove button**: optional trailing `Close` icon per row (appears on focus) → removes that item.
- **Reorder**: drag handle (`Drag` icon) on the left in an edit affordance; reorder `items`.
- Apply the same enter-to-add / delete affordances to **bullets** and **numbered** lists.

---

## 12. Focus mode / typewriter mode

**Status: 🔴 Missing — `prefs.focus` persists but has no effect; the setting screen isn't even selectable (see §9).**

### Intended behaviour (`prefs.focus` values)
- **Off**: normal editing.
- **Auto**: dim everything except the paragraph/line being edited.
- **Sentence**: dim everything except the current sentence.
- **Typewriter**: keep the caret line vertically centered as you type.

### Design
- **Sentence/Auto dimming**: non-active blocks/sentences render at ~35% alpha (`onSurface.copy(alpha=0.35f)`); the active one at full opacity. Determine "active" from the focused field + selection; for Sentence, split the focused paragraph on sentence boundaries and only the sentence containing the caret is full-opacity.
- **Typewriter**: as the caret moves, animate the `LazyColumn` scroll so the caret line sits near vertical center (track the focused block's position; `animateScrollBy`). Add generous top/bottom content padding (~40% of viewport) so the first/last lines can still center.
- Transitions: fade alpha over ~150 ms; keep it subtle so it doesn't feel jumpy.
- Wire the `FocusSettingsScreen` radio selection to `prefs.setFocus`, and read `prefs.focus` in `EditorScreen`.

---

## 13. Share-into-app (received text/images)

**Status: 🟡 Partial — plain-text shares create a note; image shares are ignored; the created note isn't opened.**

### What exists now (after this branch)
- `MainActivity.handleShare` handles `SEND`/`SEND_MULTIPLE`: for text it creates a note from subject+text; **image-only shares are dropped** (to avoid blank notes); the manifest also advertises `image/*` for both actions.
- After creating from a share, the app just shows Home (the note is at the top of the list) — it doesn't jump into the note.

### What is missing
- Import shared **images** into an `Image`-block note (single and multiple).
- **Open** the newly created note directly (deep link into the editor).
- Mixed shares (text + image) → one note with a paragraph + image blocks.

### Design
- Extend `handleShare`: collect `EXTRA_STREAM` uri(s) (`getParcelableExtra` for `SEND`, `getParcelableArrayListExtra` for `SEND_MULTIPLE`), plus text. Create one note: a paragraph for the text (if any) followed by one `Image` block per imported uri (via `MediaRepository.import`).
- After creation, set `initialDeepLink = notesup://note/{id}` so `NotesupNav` opens the editor for it (ties into the deep-link work in §2).
- Show a brief toast/snackbar *Saved to Notesup* if the app was launched only to capture.
- Grant read permission on incoming uris (they come with `FLAG_GRANT_READ_URI_PERMISSION` from the sender; read immediately since the grant is transient).

---

## 14. Account screen & onboarding polish

**Status: 🟡 Partial — flows exist but reflect the stubbed auth.**

### What exists now (after this branch)
- `ManageAccountScreen` shows Sign in **or** Sign out based on real auth state, and sign-out actually calls `AuthRepository.signOut()`.
- `AccountSheet` (from the Home avatar) offers Sign in / Settings / About.
- Onboarding: Welcome → (Start writing | Sign in) with the skip-crash fixed.

### What is missing / design
- **Signed-in header**: `ManageAccountScreen` and `AccountSheet` should show avatar + email + provider ("via Google") when signed in, not just action rows. Avatar `AsyncImage` 40–64 dp circle; email `titleMedium`; provider `bodySmall onSurfaceVariant`.
- **Delete account**: `AuthRepository.deleteAccount()` exists (currently just signs out) — add a destructive row *Delete account* with a typed-confirm dialog once Convex exists (must also delete remote data).
- **Passkey**: `signInPasskey` + a `Passkey` icon exist; add a *Use a passkey* option on `AuthScreen` once Clerk is wired.
- **Onboarding**: consider a 2–3 pane intro (writing / privacy / sync) before Welcome; optional. Keep the current single Welcome as the minimum.

---

## 15. Cross-cutting design system notes

These apply to everything above so new screens feel native to Notesup.

- **Type**: Roboto Flex variable via `flex(weight, opsz)`; note body uses `bodyNoteStyle(size, family)`. Serif = Literata, mono = JetBrains Mono, accessible = Atkinson. Titles `headlineMedium` (28 sp) for the note title, `titleLarge` (22 sp) for screen bars.
- **Colour**: `MaterialExpressiveTheme` with dynamic colour (API 31+) harmonized to `Seed`; per-note **tint** uses `TintWashes[1..7]` as a low-alpha page wash (0.06–0.08). Locked/pinned accents use `tertiaryContainer`.
- **Shape**: rounded, generous — cards 24 dp, sheets/large surfaces `shapes.large`, pills/stadium buttons via `Stadium`, toolbars `shapes.extraLarge`.
- **Motion**: `MotionScheme.expressive()`. Use it for sheet/menu transitions and the predictive-back scale already in the editor.
- **Haptics**: `rememberHaptics()` — `confirm()` for commits, `reject()` for destructive, `tick()` for toggles/selection, `longPress()` for entering multi-select. Wire these on every new interactive control.
- **Paper**: `PaperGrain` overlay for a subtle texture; the editor's `paperBackground(paper, color)` draws lines/dots/grid behind the page.
- **Icons**: single source of truth in `NotesupIcons` (already includes pen/highlighter/eraser/width/palette/drag/etc. for the ink and reorder work).
- **Empty states**: centered single-sentence `EmptySentence` with an optional wordmark (see existing home/pinned/recent/search/project/trash strings).
- **Lists**: `NoteCard` (grid) and `NoteListRow` (list) are the canonical note representations; reuse them in widgets, search, trash, and project screens.
- **Edit persistence**: any new editable block must fold through `EditorViewModel`'s field model (namespaced key + `fold()` case) so edits are never lost on back navigation — never write straight to Room from the composable.

---

## 16. Suggested build order

Ordered by user impact vs. effort, and by unblocking dependencies:

1. **Trash screen** (§8) — small, high value; data layer already exists.
2. **Settings sub-screens** (§9) — small, makes the app feel finished; all prefs already exist.
3. **Projects: inbox + move + edit/delete** (§7) — medium; core organization.
4. **Focus mode** (§12) — small–medium; pure UI over an existing pref.
5. **Rich-text formatting** (§5) — medium; the library is already a dependency.
6. **Image polish** (§6) — small–medium; core already done.
7. **Tables editing** (§4) — medium.
8. **Checklist niceties** (§11) — small–medium.
9. **Deep links + widgets real data** (§2) — medium; also unblocks share-open (§13).
10. **Share-into-app images/open** (§13) — small once deep links exist.
11. **Ink canvas** (§3) — large; native drawing.
12. **Note encryption** (§10) — medium; security-sensitive.
13. **Clerk + Convex sync** (§1) — large; do last, when the API key is available.

---

*Generated as a planning aid. Nothing here has been run on a device; treat implementation notes as the intended design, to be validated during a real build.*
