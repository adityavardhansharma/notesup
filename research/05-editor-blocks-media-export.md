# 05 — Editor, mixed media, export

The editor is the product. Home is a gallery of this.

## The question people actually asked

Apple Notes can do text, then an image, then more text, a list, a sketch. Notion can do that plus 40 block types. OneNote can put anything anywhere on a canvas. Bear is mostly beautiful text.

Which one is premium on a phone?

**Accepted answer:** Apple Notes + Craft. A **vertical block document**. Not a canvas. Not a database. Not a markdown source view as the default.

## Three editor architectures (rated)

| Architecture | Who | Phone feel | Score | Verdict |
|---|---|---|---:|---|
| **Monolithic rich text** (spans in one field) | Keep, Notally, Evernote classic | Fast, images become attachments or end-of-note | 6 | Reject as the long-term model |
| **Block document** | Craft, Notion, Apple Notes (internally), Medium | Predictable, insert anywhere, each block a type | 10 | **Accept** |
| **Infinite canvas** | OneNote, Goodnotes paper, AFFiNE edgeless | Glorious on tablet, miserable one-handed | 4 | Reject as default |
| **Markdown source** | Markor, iA, Obsidian | Writers love, civilians hate | 7 | Accept as *export + optional view*, not default |
| **Outliner** | Workflowy, Logseq, Roam | Powerful, alien | 5 | Indent inside a text block only |

## Accepted block set (v1 is small on purpose)

| Block | v1 | Notes |
|---|---|---|
| `Title` | yes | One per note, large type, placeholder "Title" |
| `Paragraph` | yes | Default. Inline: bold, italic, underline, strike, `code`, link |
| `Heading` | yes | H2 only in v1 (one level). H3 later |
| `Checklist` | yes | Tap circle, haptic, persist |
| `Bullet` / `Numbered` | yes | Convert from paragraph via toolbar |
| `Image` | yes | Full-bleed inside note, caption optional |
| `Ink` | yes | See file 06. A block with a height, not a new app |
| `Divider` | yes | Quiet |
| `Quote` | later | Easy |
| `Code fence` | later | Dev flavor, not identity |
| `Audio` | later | Notability path |
| `File / PDF page` | later | Samsung/Goodnotes path |
| `Table` | no | Notion disease |
| `Database` | no | |
| `Embed` | no | |
| `Math` | no | NeutriNote / Nebo later |

A note is `List<Block>` plus metadata (id, title, projectId, pinned, locked, tint, timestamps, revision).

## Why blocks beat one big EditText

Research (Compose community 2025–26, MohamedRejeb compose-rich-editor, "Notion-style editor in CMP" writeup):

- Inline images *inside* a single Compose `TextField` are still constrained.
- `contentReceiver` helps with keyboard GIFs, not with a document model.
- A `LazyColumn` of blocks scrolls huge notes, virtualizes images, and lets ink be a real composable.

**Accepted implementation shape (notes only, no app code yet):**

- `LazyColumn` of block UIs
- The focused text block is a `BasicTextField` / rich editor
- Unfocused text blocks are `Text` (cheaper)
- Insert image = insert a block at caret index
- Backspace at start of empty block merges / deletes

Library candidates (file 15 goes deeper):

| Lib | Role | Score |
|---|---|---:|
| MohamedRejeb **compose-rich-editor** | Inline styles, HTML/MD | 8 for *inside a paragraph* |
| Custom block list | Document | 10 |
| Markwon | Render md | 7 for preview/export |
| Native `EditText` interop | Last resort | 4 |

**Reject** shipping the whole note as one compose-rich-editor instance.

## Editor chrome (Expressive)

Apple Notes: top done + share, bottom format bar.
Craft: floating, minimal.
Bear: almost nothing in focus.
Pixel Recorder: honest Material.

**Accepted:**

```
Top:  ←   (title fades here when collapsed)    ⋯
      [share] is inside ⋯ unless exporting mid-write

Bottom (above IME): HorizontalFloatingToolbar
  [B I U s `] | [H • ☑] | [image ink ⋮]
```

When IME hides, toolbar becomes a compact pill or docks under the app bar. Never a 48dp graveyard of 20 icons (Samsung Notes).

Focus mode (Bear / iA): chrome fades after 2s of typing. Tap or caret move brings it back. **Accepted.**

## Images

**Accepted**

- Insert from gallery, camera, or share-sheet *into Notesup*
- Downsample to a max long-edge (e.g. 2560) for the working copy; keep original in files store if user wants lossless later
- Crop / rotate in a small sheet
- Drag block handle to reorder (v1.1). v1: move up/down in overflow
- Alt/caption field, collapsed
- Multi-image insert creates N image blocks, not a gallery widget

**Rejected**

- Stickers shop
- GIF keyboard as a feature
- Auto-upload before the note exists locally
- Full-bleed images that break list virtualization (always bounded height, tap to expand)

## Export

Demand from the user prompt: **PDF** and **Markdown**. Correct.

| Format | Who does it well | Our rule |
|---|---|---|
| Markdown | Bear, Joplin, Obsidian, Markor, Craft | Canonical interchange. Images as `./media/<id>.jpg`. Checklists as `- [ ]`. Ink as PNG. |
| PDF | Craft, Bear, Apple, Samsung | Print-like: type, images, ink rasters. Share sheet. |
| Plain text | everyone | Fallback |
| HTML | Bear, Craft | Later |
| .docx | Evernote | No |
| ENEX | Evernote | Import later, not export pride |
| JSON blocks | us | Internal backup, hidden |

Export lives in the `⋯` menu, not a tab. After export, system share sheet. Haptic `CONFIRM` on success.

Import Markdown / images in v1.1. Evernote later.

## Link / share of a live note

v1: export files only. Live link requires Convex + auth and a web reader we are **not** building yet.

## Undo

System text undo is not enough for blocks. We need a small command stack: insert block, delete block, move, style. Android keyboard undo should hook it. **Accepted** for v1, even if the stack is short (50).

## Editor performance = UX

- Only one `TextField` composed as editable
- Images: Coil, disk cache, exact size
- Ink block: snapshot bitmap for scroll, live canvas only when focused
- Opening editor: show local blocks synchronously; reconcile sync in background

## 100 editor-chrome / insert inspirations (compressed)

Apple Notes toolbar 9, Craft slash 9, Notion slash 7, Medium highlights 8, Google Docs mobile 5, Word mobile 3, Keep 5, Samsung 4, OneNote 3, Bear 10, iA 10, Ulysses 8, Typora 8, Obsidian 5, Logseq 6, Roam 4, Workflowy 7, Dynalist 6, RemNote 4, Tana 4, Capacities 7, Anytype 6, AppFlowy 5, AFFiNE 6, SiYuan 5, Amplenote 4, Evernote 4, Joplin 5, Standard Notes 6, Notesnook 6, Notally 8, Quillnote 7, Markor 7, NeutriNote 5, FSNotes 8, Drafts 9, 1Writer 7, Taio 7, iA syntax 9, Ulysses export 8, Day One 7, Flomo 6, Memos 6, Slack compose 5, iMessage 6, WhatsApp 4, Gmail compose 5, Superhuman 8, Linear describe 8, GitHub md 6, Notion mobile + 4, Coda 4, Dropbox Paper 7, Dropbox Paper is dead 7, Confluence 2, Google Keep voice 6, Recorder 8, Pixel Screenshots 7, Instagram compose 4, VSCO 6, Darkroom 6, Halide 5, Snapseed 5, Photos edit 6, Samsung gallery 4, Files 3, Drive 3, Xodo 4, LiquidText 5, Goodnotes insert 7, Notability 7, Nebo 8, Noteshelf 5, Penly 7, Notein 7, Squid 5, Concepts 6, INKredible 8, reMarkable 10, Kindle Scribe 8, Remarkable layers 7, Procreate (insert? no) 6, Concepts 6, FigJam 5, Miro 3, Milanote 7, Heptabase 6, Whimsical 5, Excalidraw 7, Lorien 7, Microsoft Whiteboard 3, Samsung Notes PDF 7, Adobe Acrobat 3, Reader 7, Instapaper 6, Matter 7, Highlight 7, Kindle notes 5, Readwise 8.

**Winning chrome DNA:** Bear quiet + Apple mixed insert + Craft block handles + Expressive floating toolbar.

## Accept / reject

**Accept:** block document; small block palette; floating expressive toolbar; focus mode; MD + PDF export; local-first images; one live TextField.

**Reject:** canvas default; Notion slash with 30 types; tables/databases; sticker economy; live web embed; markdown-source as default; desktop ribbon.
