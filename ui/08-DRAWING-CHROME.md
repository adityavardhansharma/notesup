# 08 — Drawing chrome

Engine and tools: [DRAWING.md](../DRAWING.md). This file is **how it looks**.

## In the note

Drawing block: radius 16, clip. Paper underlay visible.  
Unfocused: PNG, no toolbar.  
Focused: live Ink 1.0 wet strokes + toolbar.

Resize: 32×4 pill `outline`, center bottom, 8 dp below canvas. Drag grows height.

## Toolbar

One row, 56 tall, `surfaceContainerHigh`, 16 side, 8 above nav/IME. Radius 28 (stadium bar).

```
[pencil][marker][highl.][eraser]  |  [●]  |  [undo][redo]  |  [✓]
```

- Tools 48 hole, icon 24. Selected: `primaryContainer` circle 40.  
- Color: 28 dp circle, last color, 2 dp `outline` if cream-on-paper.  
- Undo/redo 48; disabled @ 38%.  
- Done: 40 circle `primary`, `check` `onPrimary`.

## Tool popover (second tap)

Anchor above the tool. Width 280, radius 20, `surfaceContainerHigh`, pad 16.  
Label `Thickness` `labelSmall`. Five chips 32 + slider.  
Label `Opacity`. Five chips + slider.  
No title bar.

## Color sheet

6 big 40 dp circles + 12 smaller 32. Selected: 2 dp `onSurface` ring.  
Standard 6: ink, wax, gold, tide, blue, coral.

## Cursor / stroke

Ink library wet preview. We do not draw a fake brush cursor.  
Eraser area: 40% `onSurface` circle preview under the finger.

## Don’t

Don’t put width sliders always visible (Samsung attic).  
Don’t use a rainbow ring as the whole toolbar.  
Don’t cover the canvas with a 96 dp graveyard of tools.
