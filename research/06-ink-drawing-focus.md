# 06 — Ink, drawing, focus

User ask: "You know how the Notes app has a drawing thing." Yes. And there are a hundred of them. Most will ruin a phone app if they become the product.

## What drawing is in a notes app

Three different products hide under "drawing":

| Kind | Example | Phone fit | Verdict |
|---|---|---|---|
| **Sketch block** inside a text note | Apple Notes sketch, Craft drawing, Samsung insert | Excellent | **Accept v1** |
| **Paper notebook** | Goodnotes, Noteshelf, Penly | Tablet-first | Accept as a *note type*, not home |
| **Infinite whiteboard** | Concepts, OneNote, AFFiNE, Lorien, Milanote | Weak one-handed | Reject as default |
| **Handwriting-as-text** | Nebo, Apple Scribble, Samsung convert | Hard, later | v2 |
| **PDF markup** | Xodo, Drawboard, LiquidText | Adjacent product | Later |

## Accepted ink model

An `Ink` block:

- Has a fixed default height (e.g. 240dp) that the user can drag taller
- Stores strokes, not only a PNG (so we can recolor / undo later)
- Renders a PNG snapshot when the block is not focused (scroll performance)
- When focused, becomes a live canvas with a compact ink toolbar

A **Sketch note** is just a note whose first block is a tall ink canvas. Same editor. No second app.

## Ink toolbar (focused)

Expressive connected buttons, not a painter's attic:

- Pen / highlighter / eraser
- Thickness (slider, expressive)
- Color (current + 6, more in a sheet)
- Undo / redo
- Done (defocus)

**Rejected on the ink toolbar:** stickers, tape, washi, rulers, 40 brushes, shape recognition in v1, lasso in v1 (v1.1), layers in v1.

Nebo/Goodnotes shape snap is loved. Put **shape recognition** on the later list. Do not block v1 on it.

## Platform reality (Android)

| API / lib | Role | Score | Notes |
|---|---|---:|---|
| **Android Ink / Jetpack Ink** (newer stroke APIs) | Best long-term stroke model | 9 | Prefer if minSdk allows; research at implement time |
| Compose `Canvas` + custom strokes | Full control | 8 | Fine for v1 if Ink API is awkward |
| `android.graphics` + `MotionEvent` in AndroidView | Predictable stylus | 7 | Use if Compose latency is visible |
| Samsung S-Pen SDK | Best on Samsung | 6 | Optional later, never required |
| Square / stroke libraries, squiggles | Fast start | 6 | Check license |
| Lorien / Excalidraw ports | Wrong container | 4 | |
| WebView drawing | No | 1 | |

Latency is UX. If a stylus trail lags, the app is not premium. **Finger ink must feel good too** — most Android users do not own an S-Pen.

Haptics: a *tiny* tick on stylus button / eraser switch. **No** haptic per stroke (numb, battery, cheap).

## Focus mode (writing)

Different from ink focus.

| Source | Idea | Score |
|---|---|---:|
| iA Writer | Typewriter scroll, syntax fade | 10 |
| Bear | Chrome retreat, red accent stays | 10 |
| Ulysses | Typewriter, dark rooms | 8 |
| reMarkable | Almost no UI | 10 |
| Medium | Highlight-only chrome | 8 |
| Google Docs | Never focuses | 2 |
| Samsung Notes | Never focuses | 2 |

**Accepted focus mode**

- After ~2 seconds of typing, app bar + floating toolbar ease out
- Status/nav still follow system; we do not fight edge-to-edge
- Caret stays optically near 40% from the top (typewriter option, default ON for long notes, OFF for short)
- Tap, selection, or insert button restores chrome
- A lock icon in `⋯` can *force* focus (distraction-free)

## 100 ink / paper / focus inspirations (compressed)

Apple Notes sketch 9, Apple Notes Math Notes 6, Freeform 6, Goodnotes 8, Notability 7, Noteshelf 6, Nebo 9, CollaNote 4, Noteful 6, Freenotes 2, Concepts 8, Procreate 7 (latency), Procreate Dreams 4, Adobe Fresco 5, Infinite Painter 6, ibis Paint 3, Autodesk Sketchbook 6, Samsung Notes 8, Penly 8, Notein 7, Squid 7, INKredible 9, LectureNotes 4, FiiNote 5, LiveBoard 4, Explain Everything 4, Microsoft Whiteboard 3, OneNote ink 6, Evernote sketch 4, Zoho card sketch 6, Craft drawing 8, Notion ink (weak) 3, Obsidian ink plugins 5, Logseq draw 4, Excalidraw 8, tldraw 8, Lorien 8, AFFiNE edgeless 6, Milanote 7, Heptabase 6, FigJam 5, Miro 3, Whimsical 5, Lucidspark 3, Jamboard (dead) 4, Google Keep doodle 5, ColorNote 1, Easy Notes 3, Notally 2 (no ink), Quillnote 2, Markor 1, reMarkable 10, Supernote 9, Kindle Scribe 8, Boox 6, Ratta 8, Daylight DC1 7, Paperlike marketing 5, Pencil (FiftyThree, dead) 8, Linea Sketch 8, Tayasui Sketches 7, Flow by Moleskine 6, Bamboo Paper 5, Notes Plus 5, UPAD 4, MyScript 9, Gboard handwriting 6, Samsung convert 8, Apple Scribble 8, Windows ink 5, ChromeOS stylus 5, Pixel Pencil (if any) 6, S-Pen air actions 5, OCR later 6, Math convert 5, Music ink 2, PDF annotate Goodnotes 8, Xodo 5, Kami 4, LiquidText 6, MarginNote 5, Drawboard 5, Highlights 6, Reader markup 6, Kindle highlights 5, iA focus 10, Bear focus 10, Ulysses 8, Typora 7, Obsidian zen 7, VS Code zen 6, Writeroom 9, Dark Room 8, OmmWriter 6, FocusWriter 6, iA syntax colors 8, Markdown preview split 5, typewriter scroll 9, caret highlight 7, line numbers 2, vim 1.

**Winning ink DNA:** INKredible feel + Apple insert-anywhere + reMarkable restraint + Samsung only as a capability ceiling.

## Accept / reject

**Accept:** ink as a block; sketch note as a tall ink block; tiny toolbar; stroke storage + PNG snapshot; finger-first; focus mode for text.

**Reject:** Goodnotes as the whole app; sticker shop; layers v1; canvas home; haptic-per-stroke; S-Pen exclusive features; shape pack v1; whiteboard community.
