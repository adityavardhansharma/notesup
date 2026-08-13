# 06 — Home (every box)

Phone, portrait. Edge-to-edge. Status bar icons: dark on paper, light on ink.

```
┌─────────────────────────────────────┐
│ status                               │
│  Notesup                    🔍  ☺    │  64 dp bar, no fill, no shadow
│  (All)(Pinned)(Recent)(Projects) ▦☰  │  48 dp
│                                      │
│  [pin][pin][pin]                     │  optional 168 dp strip
│                                      │
│  ┌────────┐  ┌────────┐              │
│  │ thumb  │  │        │              │  10 dp gap, 16 dp page
│  │ title  │  │ title  │              │
│  │ preview│  │ preview│              │
│  │ 3m · X │  │ 1h     │              │
│  └────────┘  └────────┘              │
│                                      │
│                           ( +  ∧ )   │  split plus
└─────────────────────────────────────┘
```

## App bar

- Height 64 + status inset.  
- Start 16: wordmark `Notesup` `titleLarge` 500. Tap wordmark → scroll to top.  
- End: search 48, then 8, avatar 48, then 8.  
- Avatar: Coil circle 32 in the 48 hole, or `account_circle`.  
- Offline: 16 `cloud_off` at avatar bottom-end. Connecting: 16 `sync` spinning. Healthy: nothing.  
- No bottom hairline. No elevation.

## Pills

- `ButtonGroup` connected, start 16. Height 40.  
- Four: All · Pinned · Recent · Projects.  
- Selected: `primaryContainer` / `onPrimaryContainer`.  
- Unselected: transparent / `onSurface`.  
- End 16: grid 40 + list 40. Selected view fill 1.  
- Recent **forces list**. View toggle disables (alpha 38%) on Recent.

## Pin strip (All only, if pins > 0)

- Horizontal `LazyRow`, 16 start, 8 gap.  
- Card width 148, height 152, radius 24, `tertiaryContainer`.  
- Same innards as grid card, tighter preview 1 line.  
- After 6: text button `See all` `labelLarge` primary — selects Pinned pill.  
- No heading “Pinned”. The strip *is* the heading.

## Grid card (the object)

- Fill `surfaceContainerLow` (or tint 8%, or `tertiaryContainer` if pinned in the main grid).  
- Radius 24. **No border. No shadow.**  
- Width: `(W - 32 - 10) / 2`.  
- Thumb if image/ink: full width, height min(120, width * 10/16), crop, top radius 24, bottom 0.  
- Pad 14. Title `titleMedium` 2 lines. Preview `bodySmall` 2 lines. Meta `labelSmall`: relative time · project if any.  
- Badges 16 dp, 8 from top-end: lock then pin, 4 dp apart.  
- Press: fill `surfaceContainerHighest`.  
- Long-press: `LONG_PRESS` + menu or select.

**Never** date-group the grid.

## List row

- Min 72. Pad 16 horizontal.  
- Leading 40×40 thumb radius 12, or 40×40 tonal `surfaceContainer` with first letter `titleSmall`.  
- Title 1 line, preview 1 line, time end `labelSmall`.  
- Divider: none. Air is the divider (4 dp between).  
- Swipe start pin (`tertiaryContainer` + `keep`). End delete (`errorContainer` + `delete`). Threshold 96.

## Recent

Same list. Sticky headers `labelSmall` `onSurfaceVariant`, 32 dp, start 16: Today / Yesterday / This week / Older.

## Projects

- Row 1: Inbox `inbox` + `Inbox` + count.  
- Then projects: 10 dp hue dot + optional emoji 18 + name + count. Height 56.  
- FAB on this filter is **still the capture plus**, not “new project”.  
- New project: app bar `add` **only while Projects is selected** (`New project`). Capture SplitButton stays capture. Different job.

## Split plus

- Bottom end, 16 + nav inset.  
- Primary 56×56 (or Expressive split height), `primary`, `add`, no label.  
- Trailing 40–48, `keyboard_arrow_up`, spins on open (component).  
- Menu above: Text note, Checklist, Drawing, Image. 48 h, radius 16, `surfaceContainerHigh`, icons 24 start 16.  
- Primary tap: new text note Inbox, caret in body.  
- Drag plus onto a project row: ghost + drop, create there.

## Empty All

Center in the remaining canvas (not including plus).  
Wordmark `displaySmall`. 8 dp. `Write anything.` `bodyLarge` `onSurfaceVariant`.  
No button. The plus is the button.

Every other empty (Pinned, Recent, Projects, project interior, search, trash): [ui/20-EMPTY-STATES.md](20-EMPTY-STATES.md).

## Select mode

Bar: `close` | `{n} selected` `titleMedium` | `keep` | `drive_file_move` | `delete`.  
Plus hides 160 ms. Cards show a 22 dp check ring top-start.

## Large screen

Rail 80: All `notes`, Pinned `keep`, Recent `schedule`, Projects `layers`, Settings `settings`.  
Content: same cards, 3–4 columns. Plus still bottom of the content pane, not over the rail.
