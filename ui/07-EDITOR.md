# 07 — Editor (every box + typing)

The editor **is** the page. Home is a gallery of pages.

```
┌─────────────────────────────────────┐
│ ←                              ⋮    │  64, transparent
│                                     │
│  Title                              │  20 side, headlineMedium
│                                     │
│  The body types here at 18/28.      │
│                                     │
│  ┌─ image ─────────────────────┐    │
│  └─────────────────────────────┘    │
│                                     │
│  more text                          │
│                                     │
│      [ B I U s ` 🔗 | H • ☑ | + ]   │  floating toolbar
└─────────────────────────────────────┘
```

## App bar

Transparent. `arrow_back` start 4 (48 hole). `more_vert` end 4.  
No title in the bar. No share icon (it’s in `⋯`).  
On scroll, bar stays transparent; status icons stay correct via `enableEdgeToEdge`.

## Title

- First field. `headlineMedium` 28/36.  
- Placeholder `Title` at 55% `onSurfaceVariant`.  
- Pad 20 side, 12 top under bar, 16 below before body.  
- Paper lines **do not** run through this band.  
- Enter → focus first body block. Backspace at 0 in empty first body → stay (don’t delete title).  
- New note: **do not** focus title. Focus body.

## Body list

`LazyColumn`, 20 dp side, 12 dp between blocks, bottom 96 + IME.  
Every text block has a `TextFieldState` in the ViewModel. Unfocused paints as `Text`; focused is `BasicTextField` — **state does not move**.  
Tap: `TextLayoutResult.getOffsetForPosition` → selection on that state, then focus.

Tablet measure: **640 dp** max.

## Typing (premium or nothing)

- `bodyNote` 18/28, `onSurface`, caret `primary` 2 dp.  
- Keyboard: `textCapSentences`, autocorrect on, no suggestions bar theming.  
- **No** letter animation, no bounce, no spring on the caret.  
- Enter splits paragraph. Backspace at 0 merges.  
- `/` at start of empty paragraph → insert sheet (diet).  
- `# ` `## ` `### ` at start → heading, hashes hide.  
- `bodyNote` never changes size while typing.

## Blocks (look)

| Block | Look |
|---|---|
| Paragraph | `bodyNote`, no box |
| H1 | `titleLarge` 600, 8 extra top |
| H2 | `titleMedium` 600, 6 extra top |
| H3 | `titleSmall` 600 `onSurfaceVariant` |
| Checklist | 22 ring + 12 + field. Ring align to first line cap-height |
| Bullets | 6 dp disc `onSurface` + 12 + field |
| Numbers | `labelLarge` tabular + 12 + field |
| Quote | 3× full-height `primary` bar, 12 pad, `bodyNote` |
| Divider | 1 dp `outlineVariant`, 16 vertical |
| Image | radius 16, max h 360, fit. Caption `bodySmall` after tap |
| Table | radius 16, **`outline` 1 dp** cells (not `outlineVariant`), header `surfaceContainer`. Handles ⋯ outside |
| Code | radius 16, `surfaceContainer`, pad 12, mono 14/20, chip + copy top-end |
| Drawing | radius 16, paper shows through, bottom handle 32×4 |

Table size pad (insert): 8×12 ghost cells, 28 dp each, `primary` filled for hover size, label `3 × 4` under.

## Floating toolbar

`HorizontalFloatingToolbar`, height 52, 16 side, 8 above IME.  
`surfaceContainerHigh`, no vibrant colors (vibrant = joke).  
Icon 24, item 40. Groups with 1 dp `outlineVariant`.

**A** bold italic underline strike code link  
**B** H1 H2 H3 chips (`labelMedium`) · bullets · numbers · checklist  
**C** `add` → insert sheet  

Selected style: fill 1 + `primaryContainer`.  
When IME hides: dock above nav inset, same bar.

## Insert sheet (also `/`)

Half height, 28 top radius, handle 32×4.  
Search field 48, stadium.  
Rows 48: icon 24 + label `bodyLarge`.  
H1/H2/H3 one row, three stadium chips.  
Bullets/Numbers one row, two chips.  
Order: Text, Headings, Checklist, Lists, Table, Image, Drawing, Code, Quote, Divider.

## Overflow `⋯`

Menu 16 radius, `surfaceContainerHigh`. Items 48, icon + `bodyLarge`. Order in BUILD.

## Focus

After 2000 ms idle type: bar + toolbar α 0 in 240.  
Tap / select restores 160.  
Sentence: other blocks **62%**. A11y → off.  
Typewriter: caret 38% down. Pause both while selecting.

## Lightbox

Full pixels: [ui/19-MEDIA.md](19-MEDIA.md). Scrim 0.92, sharedBounds, pinch, predictive back to the block.
