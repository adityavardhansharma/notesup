# Features now locked (reopened and decided)

You asked: themes, paper, tables, drawing, headings, mobile slash, title, fonts, code, and “what else did you miss.”

I searched Apple Notes (tables, lines & grids), Bear (themes, type), Craft (blocks, click tax), Notion `/` (desktop-first), Samsung/Goodnotes paper templates, iA Writer fonts, Steve Ruiz **perfect-freehand** / tldraw (the drawing library the industry actually uses — Canva, Excalidraw, draw.io), plus screen/webcam *overlay* drawers (annotation-overlay, desktop-ink, webcam-overlay). Those overlay apps are for presenting over a camera. They are **not** a notes editor. We take their *stroke feel*, not a webcam layer.

**Yes we add these.** We add them the Apple Notes / Bear way, not the Notion-on-a-phone way.

---

## 1. Themes — yes, curated

People go to war for Bear’s *complete* themes (everything changes at once). They leave Craft when every surface is a separate picker.

**App theme** = chrome + default paper color + accent + default type. One tap. Not 12 color sliders.

### Locked set (12)

| Id | Name | Kind | Feel |
|---|---|---|---|
| `dynamic` | Wallpaper | system | Material You, harmonized to seed |
| `paper` | Paper | light | Default. Cream `#F6F1EA`, wax `#8B2942` |
| `graphite` | Graphite | light | Cool gray paper, charcoal ink |
| `noon` | High Noon | light | Warmer, sunned page |
| `fog` | Fog | light | Blue-gray, quiet |
| `legal` | Legal | light | Soft yellow pad chrome (OneNote/legal energy) |
| `ink` | Ink | dark | Warm dark default |
| `midnight` | Midnight | dark | Cool navy (Copilot energy, not OLED) |
| `slate` | Slate | dark | Neutral |
| `dieci` | Dieci | dark | True black, Bear OLED fans |
| `kraft` | Kraft | light | Brown fiber, notebook shop |
| `contrast` | High contrast | auto | A11y, Atkinson + hard ink |

Settings → Appearance: a **3×4 grid of theme cards** (preview swatch + name). Dynamic is first. No per-token editor in v1. Pixels: [ui/15-SETTINGS.md](ui/15-SETTINGS.md).

Theme does **not** wipe a note’s own paper or font if the user set those.

---

## 2. Note paper / background — yes

Apple Notes “Lines & Grids.” Samsung/Goodnotes templates. Users *hate* when typed text pushes the lines down (Apple bug, Reddit).

**Paper is a fixed underlay** that scrolls with the note, aligned to `bodyNote` line-height. Title sits in a **clear band** (no lines through the title).

### Locked papers (8)

| Id | What it is | Who we stole from |
|---|---|---|
| `blank` | Tinted paper, no marks | Bear, Craft |
| `ruled` | College rule, 7 mm / matches 28 sp line | Apple lined, OneNote |
| `ruled_narrow` | 6 mm | legal pads |
| `legal` | Yellow + ruled | classic pad, OneNote |
| `graph_sm` | 5 mm graph | Apple grid, engineering |
| `graph_lg` | 10 mm graph | sketch |
| `dots` | 5 mm dots | bullet journal, Goodnotes |
| `kraft` | fiber wash, no lines | craft notebook |

Per **note**, in overflow → **Paper**. Default = theme’s paper (usually `blank`). Picker pixels: [ui/15-SETTINGS.md](ui/15-SETTINGS.md).

Ink/drawing sits *on* the paper. Graph/ruled help handwriting. Typing does not shift the grid.

---

## 3. Tables — yes, visual, not Markdown

Apple Notes: toolbar → table → **2×2**, then `⋯` on row/column to add/delete.

We go one step better for “I want N×M now” without becoming Excel.

### Insert

1. Toolbar **+** or slash `table`  
2. **Size pad** (like iOS table picker / Google Docs): grid you drag, 1–8 columns × 1–12 rows, live preview `3 × 4`  
3. Tap to drop. Default if they just tap Table: **2×2** (Apple).

### Edit (phone)

- Tap cell → keyboard, one line in v1 (Shift/Alt-enter later)
- Select row: tap the **left handle** (`⋯`) → Add row above / below / Delete row  
- Select column: tap the **top handle** → Add column left / right / Delete column  
- Long-press handle → drag reorder row/col (v1.1; v1 is add/delete only)
- Wide tables **scroll horizontally** inside the note. Do not shrink type below 14 sp.
- Caps: **8 columns, 50 rows**
- First row optional **header** (bold + surfaceContainer)

**Not** a Notion database. No types, no filters, no formulas.

Markdown export: GitHub-flavored pipe table. PDF: real table.

Domain:

```kotlin
data class Table(
    override val id: BlockId,
    val cols: Int,
    val rows: Int,
    val cells: List<String>, // row-major
    val headerRow: Boolean,
) : Block
```

---

## 4. Drawing — Apple Notes / Samsung Notes (full spec: [DRAWING.md](DRAWING.md))

A canvas **in the note**. Markup, not a second app.

**Tools:** Pencil, Marker, Highlighter, Eraser (stroke or area).  
**Per tool:** width + opacity. Color well. Undo / Redo (50).  
**Engine:** Jetpack **Ink 1.0.0** + `ink-storage`. Pressure always. Blobs in `InkEntity`, not note JSON.

---

## 5. Headings — H1, H2, H3

One heading was too thin. Notion’s six levels is desktop religion.

| Level | Role | Size |
|---|---|---|
| Title | note name, not a heading | `headlineMedium` 28/36 |
| H1 | emphasized `titleLarge` |
| H2 | emphasized `titleMedium` |
| H3 | emphasized `titleSmall`, `onSurfaceVariant` |

Convert via toolbar, slash (`/h1`), or markdown `#` `##` `###` at line start (then hide the hashes, Bear-style).

---

## 6. Mobile “slash” — yes, diet

Notion `/` on a phone is a scroll of 40 types. Craft died on “three clicks.”

**Two doors, one sheet:**

1. **+** on the floating toolbar → insert sheet  
2. Type `/` at the **start of an empty paragraph** → same sheet, filtered as they type

Sheet = half-height, search field, **exactly these rows** (order locked):

1. Text  
2. Heading 1 / 2 / 3 (one row, three chips)  
3. Checklist  
4. Bullets / Numbers (one row, two chips)  
5. Table  
6. Image  
7. Drawing  
8. Code  
9. Quote  
10. Divider  

That is the diet. No database, no embed, no 30 slash items.

---

## 7. Title / name of the note

Yes, thought through.

- Title is **always** the first field, `headlineMedium`, placeholder `Title`
- Never required. Card shows title, or first 48 characters of body, or `Untitled`
- Title does **not** live in the app bar (that’s iOS mail, not a notebook)
- Paper lines skip the title band (24 dp padding under title)
- Overflow does **not** have “Rename” — you rename by editing the title
- Search indexes title first
- Widget rows show title only

Caret on **new** note still starts in the **body** (capture). Tap title when you care. Drafts people get speed; Apple people get a name.

---

## 8. Typography — 12 fonts, not 20 random

Bear/iA/Ulysses: a **short curated list**, plus size/line. Twenty fonts is a costume trunk.

**Global default + per-note override** (overflow → Type). Settings download + size: [ui/15-SETTINGS.md](ui/15-SETTINGS.md).

**Bundled:** Roboto Flex, Literata, JetBrains Mono, Atkinson Hyperlegible.  
**On-demand from Settings only (static):** the other eight. Never fetch a font to paint the editor.

| # | Font (Google Fonts / OFL) | Job |
|--:|---|---|
| 1 | **Roboto Flex** | Default UI + body |
| 2 | Source Sans 3 | Clean sans |
| 3 | IBM Plex Sans | Editorial sans |
| 4 | Nunito | Soft, rounded |
| 5 | Atkinson Hyperlegible | Accessibility |
| 6 | Source Serif 4 | Reading |
| 7 | Literata | Long articles |
| 8 | Lora | Warm serif |
| 9 | Newsreader | News / journal |
| 10 | Bitter | Strong serif |
| 11 | IBM Plex Mono | Code + tech notes |
| 12 | JetBrains Mono | Code, default for code blocks |

Plus: size S / M / L (16 / **18** / 20 for bodyNote). Line height stay 1.55. Width stay full phone / **640 dp** tablet.

No Comic Sans. No handwritten display as UI. No paid iA fonts unless we license later.

---

## 9. Code blocks — yes

```kotlin
data class Code(
    override val id: BlockId,
    val language: String, // "plain" | "kotlin" | "js" | "py" | "json" | "sh" | "xml"
    val text: String,
) : Block
```

- Monospace (note’s mono or JetBrains Mono)
- `surfaceContainer` fill, radius 16, 12 dp pad
- Language chip top-end, tap to pick
- Wrap on by default on phone
- Copy icon
- Syntax colors **v1.1** (don’t block v1 on a highlighter)
- Export MD: fenced ` ```kotlin `

---

## 10. Quote (you didn’t name it; it belongs with headings)

One quote block: 3 dp primary bar, `bodyNote`, italic optional. Slash `quote`. Cheap, Bear/Medium energy.

---

## 11. What else I searched and **still refuse** in v1

| Thing | Why not now |
|---|---|
| Notion databases | Phone death |
| 6 heading levels | Noise |
| Columns / 2-up layout | Thumb |
| Math / LaTeX | NeutriNote later |
| Toggle/collapse | Useful, v1.1 |
| Callout boxes (5 colors) | Quote is enough |
| Embeds / bookmarks | Network in the editor |
| Slash menu of 30 | Craft tax |
| 28 Bear Pro themes | 12 is the diet |
| tldraw / Excalidraw WebView | Not native, keyboard hell |
| Webcam overlay drawing | Wrong product |
| Sticker / washi | Cheap |
| Audio block | Later |
| Scan to PDF | Later |
| Wiki `[[links]]` | Later |
| Version history UI | Later |

---

## Icons added

| Job | Symbol |
|---|---|
| Table | `table_chart` |
| Code | `terminal` (block) — inline stays `code` |
| Quote | `format_quote` |
| Paper | `grid_on` / `notes` for ruled |
| Theme | `palette` already — Appearance uses `contrast` |
| Font | `font_download` or `text_fields` → **`text_fields`** |
| H1/H2/H3 | chips, not three icons |

---

## Domain additions (BUILD must match)

`Note` gains:

- `paper: PaperId` (default `blank`)
- `font: FontId?` (null = app default)
- `themeOverride` — **no**. Theme is app-level only.

New blocks: `Heading(level: 1..3)`, `Table`, `Code`, `Quote`.

Convex `notes` document stores the same JSON.

---

## Where it lives in the UI

Overflow `⋯` now:

1. Pin  
2. Move to project  
3. Note color (tint)  
4. **Paper**  
5. **Type** (font + size)  
6. Lock  
7. —  
8. Export MD / PDF / Share  
9. —  
10. Delete  

Insert sheet / slash: the 10-item diet above.

Settings → Appearance: theme grid + default font + default paper + dynamic toggle.
