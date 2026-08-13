# Drawing — Apple Notes / Samsung Notes class

You asked for a **drawing canvas on the note**, with marker, pencil, highlighter, width, boldness, eraser, undo, redo. Like Apple Notes Markup and Samsung Notes. That is what this file is.

I looked for a site named Grox by a SpaceX employee with a canvas demo. I did not find a public repo/site that matches that name. The spec below is taken from **Apple Notes drawing** ([support](https://support.apple.com/en-us/108919)) and **Samsung Notes pen toolbar** (fountain / pen / pencil / highlighter, thickness, opacity, stroke vs area eraser, undo/redo). **Engine (locked 2026-08-14):** Jetpack **Ink 1.0.0 stable** (`androidx.ink:ink-authoring-compose`, `ink-brush`, `ink-strokes`, `ink-storage`, `ink-rendering`). Official low-latency stylus library. `StockBrushes` includes highlighter. Pencil/marker are `Brush` / `BrushFamily` configs. Serialize with `ink-storage`. Compose module exists.

Do not roll a homemade polyline. Do not use Ink 1.1 alpha in production. 1.0.0 is the smoothness path.

---

## What drawing is in Notesup

A **drawing block** lives in the note, between text, images, tables.  
Tap Draw (insert sheet / slash / split FAB) → an ink canvas opens **in the note**.  
Done (checkmark) returns to typing. The sketch stays as a block.

Same idea as Apple: Markup in the note, not a separate app.

---

## Toolbar (when the canvas is focused)

Horizontal, above the IME / above the nav inset. One row. Apple Markup energy.

```
[ Pencil ] [ Marker ] [ Highlighter ] [ Eraser ] | [ Width ] [ Opacity ] [ Color ] | [ Undo ] [ Redo ] | [ Done ]
```

- Selected tool: fill 1, primary container, shape-morph.
- Tap the **already selected** tool again → popover for width + opacity (Apple: “tap the tool again to customize thickness and opacity”).
- Undo / Redo: `undo` / `redo` icons, disabled when the stack is empty.
- Done: `check`. Defocuses canvas, snapshots PNG, keeps stroke data.

No 40-brush attic. Samsung has fountain + calligraphy + brush — those wait until v1.1.

---

## Tools (locked)

| Tool | Feel | Blend | Pressure | Default width | Default opacity |
|---|---|---|---|---:|---:|
| **Pencil** | Dry, grainy, edges slightly broken | SrcOver | Yes (or simulated) | 2.5 dp | 100% |
| **Marker** | Solid ink, rounded cap | SrcOver | Yes — width breathes | 4.0 dp | 100% |
| **Highlighter** | Fat translucent bar | Multiply (or SrcOver @ 0.35 if multiply is ugly on paper) | No (fixed width) | 16 dp | 35% |
| **Eraser** | Removes ink only, not paper | DestOut | No | 20 dp (area) | — |

**Pencil texture:** light grain on the stroke outline (noise along the path). Not a photo of graphite.  
**Marker:** Jetpack Ink pressure-sensitive `Brush` (solid, rounded).  
**Highlighter:** rectangle-ish stroke, thinning 0, goes *under* marker/pencil visually (draw highlighter on a lower layer).

### Width (“how thick”)

Five chips **and** a slider. Chips: 1 · 2 · 4 · 8 · 16 dp (pencil/marker). Highlighter chips: 8 · 12 · 16 · 24 · 32.  
Slider continuous inside the tool popover.  
Remember last width **per tool**.

### Boldness / opacity (“how strong”)

Not a second pen. It is **opacity**.

Chips: 20 · 40 · 60 · 80 · 100%. Slider in the same popover.  
Highlighter default 35% (between 20 and 40).  
Pencil/marker default 100%.

Samsung calls this transparency. Apple calls it opacity. We label the popover **Opacity**.

### Color

Six swatches on the toolbar (last used first):

`#1C1917` `#8B2942` `#765A00` `#2F5E63` `#3D4C7A` `#E24B4A`

Tap the color well → sheet with those six + 12 extras + eyedropper later.  
Highlighter uses the same hues at the tool’s opacity.

### Eraser

Tap eraser once = **stroke** (tap a stroke, whole stroke gone) — Samsung “stroke eraser.”  
Tap eraser again to toggle **area** (paint to erase) with size slider — Samsung “area eraser.”  
Default: **stroke**. Safer on a phone.

Does not erase typed text or images. Only ink on this block.

### Undo / redo

Per drawing block, stack of **50**.  
Undo = last stroke / erase / lasso-move.  
Icons always visible while drawing.  
Haptic: none on undo (too frequent).  
System keyboard undo does not steal this while the canvas is focused.

---

## Canvas

- Default height **280 dp**, min 160, max 720. (BUILD agrees.)
- Drag the **bottom handle** (Apple’s yellow resize line, we use a 32×4 `outline` pill) to grow/shrink.
- Paper (ruled/graph/dots) shows **through** the canvas.
- Pinch to zoom **inside the focused canvas only** (Samsung). Two-finger pan when zoomed.
- **Pressure is mandatory.** Stylus → `MotionEvent.getPressure()`. Finger → simulate from velocity (fast = thin, slow = thick). Never constant-width.
- Palm: ignore `TOOL_TYPE_ERASER` and contacts wider than 30 mm.

**Lasso** (Apple has it): v1.1. v1 is draw + erase + undo. Do not block v1 on lasso.

---

## Data

Storage: `InkEntity.strokeBlob` via **`androidx.ink.storage`**, not JSON coordinates, not perfect-freehand params. Undo stack of 50 is in memory while focused. On Done: persist blob + raster `previewPath` (2× PNG). Scroll uses PNG. Focus uses Ink strokes.

---

## How you enter drawing

- Split FAB → Drawing → new note that is one tall canvas (optional) **or**
- Insert sheet / slash `draw` → insert a drawing block at the caret
- Toolbar + → Drawing

Same as Apple Markup in the note.

---

## What we will not ship in this tool

Fountain, calligraphy, brush packs, stickers, washi, ruler (v1.1), shape snap (v1.1), layers, infinite whiteboard as home, Image Wand, S-Pen-only features, any camera.

---

## Feel test

Finger, marker, one line. It should look like ink, not a polyline.  
Pencil should look dry. Highlighter should sit under ink.  
Undo brings the line back. Done, scroll the note, the sketch is still sharp.  
That is Samsung/Apple. That is the product.
