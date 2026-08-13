# 11 — Widgets (look)

Glance. System corner radius. Fill all 4 cell edges. Light + dark + dynamic. Unique name + description + preview asset.

## New note — Toolbar

Name: `New note`  
Description: `Start a text note, list, or drawing.`  
Sizes: 4×1 and 2×2.  
Look: three equal cells, 48 min touch. Icons `add` `checklist` `draw`, no labels on 4×1 if tight; labels `labelSmall` when 2×2. Desk color. No wax fill on all three — only the add cell uses `primary` / `onPrimary`. Others `surface` / `onSurface`.

## Pinned — Text + image

Name: `Pinned note`  
Description: `Glance your pinned note.`  
2×2 / 4×2. Title `titleMedium`, 3–4 lines `bodySmall`. Thumb if any. Empty: `Pin a note` `bodyMedium`. Header optional: 24 app mark + `Pinned` when 4×2.

## Recent — Text + image list

Name: `Recent notes`  
Description: `Your latest notes.`  
4×2 / 4×3. Header: mark + `Recent`. Rows 48, title 1 line. Empty: `Write anything.`

## Project — list + config

Name: `Project`  
Description: `Notes in one project.`  
System configure: pick project. Header = project name + hue dot. Else like Recent.

Tap → `notesup://…`. System launch transition.

**Picker previews + project configure list:** [ui/22-SYSTEM-EDGES.md](22-SYSTEM-EDGES.md) §13.
