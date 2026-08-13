# 21 — Large screen & landscape

Window width, not device marketing names.

| Width | Class | Chrome |
|---|---|---|
| `< 600` | compact | Phone. No rail. No two-pane. |
| `600–839` | medium | Rail 80. Two-pane **if** editor or settings-child is open. |
| `≥ 840` | expanded | Rail 80. Two-pane. Grid 4 cols when no detail. |

Height `< 480` (phone landscape): **no rail**, no two-pane. Editor is full width. Toolbar docks above IME. Same as compact. Do not invent landscape-only chrome.

Fold / hinge: treat as the current window size. Do not draw into the hinge. `material3-adaptive` `HingeInfo` — if a vertical hinge splits two usable regions, put list on the start region and detail on the end. If we cannot get a clean split, fall back to width-only.

Official scaffold: `NavigableListDetailPaneScaffold` (`adaptive` + `adaptive-layout` + `adaptive-navigation`). Already covered by `material3-adaptive` in BUILD.

---

## Rail (medium + expanded, portrait-ish)

80 dp start. Destinations **only**:

| Icon | Label (a11y) | Goes |
|---|---|---|
| `notes` | All | Home All |
| `keep` | Pinned | Home Pinned |
| `schedule` | Recent | Home Recent |
| `layers` | Projects | Home Projects |
| `settings` | Settings | `settings` |

Selected: `primaryContainer`. No FAB on the rail. Plus stays in the **list** pane, bottom-end, 16 above nav.

Avatar + search stay in the list pane app bar, not the rail.

---

## Two-pane

When a note / project interior / settings child / search is open **and** width ≥ 600 and height ≥ 480:

```
[ rail 80 ] [ list 360 ] [ detail fill ]
```

- List pane min 360, max 360. Detail gets the rest. Editor body still **max 640** centered in the detail pane.
- Shared-bounds **off**. Crossfade 160 ms. Reduce-motion: instant.
- Selected card in the list: `primaryContainer` @ 40% or `surfaceContainerHighest` — **Decision:** `surfaceContainerHighest` + 2 dp start `primary` bar 24 h. Not a loud fill.
- Back: `PopUntilContentChange` so note A → note B → back is A, then back again collapses detail on compact. On two-pane, collapsing detail leaves the list and an **empty detail**: `Select a note.` `bodyLarge` `onSurfaceVariant`, centered. Plus still on the list pane.
- Closing the last pane does **not** exit the app (system back from list = launcher).

Welcome / auth: **no rail, no two-pane**. Column max **400**, centered on `background`. Same as ui/14.

Account is still a sheet, max width 480, centered.

---

## Settings on large

Two-pane: root list 360 | child fill. Appearance grid can go 4 columns in the detail if width allows (card 72 stays).

---

## Capture activity

Floating: system freeform, our min 360×420 ([ui/17](17-CAPTURE-SHARE.md)). No rail inside capture. Lock-screen is always full-window, even on a tablet.

---

## Strings

```
cd_all=All
cd_pinned=Pinned
cd_recent=Recent
cd_projects=Projects
cd_settings=Settings
select_a_note=Select a note.
```
