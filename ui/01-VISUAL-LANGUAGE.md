# 01 — Visual language

Notesup is a **notebook**, photographed in warm indoor light. Not a productivity OS. Not a social app. Not a component demo.

## The object

Imagine a cream page on a wooden desk. A drop of sealing wax at the lower right — that is create. The page has a title, then writing. If you pinned it, the wax is a little warmer at the top. That is the whole metaphor. We never illustrate the desk. We *are* the page.

## Materials

| Material | Where | Look |
|---|---|---|
| **Paper** | Background, editor, cards | Warm, slightly yellow, never bleached |
| **Ink** | Type, icons at rest | Near-black warm, never cool #111 |
| **Wax** | Plus, selected pill, caret, checked ring | `#8B2942` / dynamic primary |
| **Graphite** | Meta, secondary icons | `onSurfaceVariant` |
| **Glass** | None | — |
| **Metal** | None | — |

## Grain (F-17)

`background` and editor page: **2–3%** paper grain. AGSL noise on API 33+; 128×128 tile PNG below. Never 5%. Not on buttons or the plus.

## Light

Light theme is the hero. Dark is a **warm room at night**, not a cinema.

- Light: paper `#F6F1EA`, cards `surfaceContainerLow` a half-step darker than the desk.
- Dark: `#161311` desk, cards `#1E1B18`. Text `#F3EDE6`.
- Contrast: body on paper must pass WCAG AA. If dynamic color fails, fall back to seed Paper theme.

## Density

Closer to **Things** than to Gmail.

| Zone | Feeling |
|---|---|
| Home grid | Objects with air (10 dp gap, 16 dp page) |
| Home list | 72 dp rows, not 48 |
| Editor | Generous: 20 dp side, 18/28 type |
| Settings | Android Settings density (tighter is OK) |
| Drawing toolbar | Instrument: 48 dp tools, not 32 |

Do not pack home like a file manager. Do not pad the editor like a landing page.

## Depth

1. Desk (`background`)  
2. Card / sheet (`surfaceContainerLow` / `surfaceContainerHigh`)  
3. Jewel (primary container: plus, selected pill)  
4. Scrim 40% when a sheet is up  

No z-fighting. No floating cards with 8 dp elevation *and* a border.

## Alignment

- Wordmark, pills, and content share a **16 dp** start edge (phone).  
- Icons in the app bar sit on a **48 dp** optical square, 8 dp from the end.  
- Card text inset **14 dp**. Title and preview share the same start.  
- Editor title and body share **20 dp** side inset. Never 16 in the editor (home is 16; editor is slightly more “page”).  
- Split plus: 16 dp from end, 16 dp above gesture inset.

## Radius language

| Thing | Radius | Why |
|---|---:|---|
| Note card | 24 (`extraLarge`) | Object |
| Image/ink inside card | 16 top, 0 bottom if flush | Nested |
| List thumb | 12 | Smaller object |
| Pill | Stadium | Jewel |
| Split plus | Expressive defaults | Jewel |
| Sheet | 28 top | Arrival |
| Dialog | 28 | Arrival |
| Menu | 16 | Transient |
| Icon button | 20 (full circle hit, icon 24) | Quiet |
| Checkbox | Circle 22 | Object |
| Text field (search) | 28 | Stadium |
| Code / table / image block | 16 | Nested in page |
| Widget | **system OEM** | Android citizen |

## Stroke / hairline

We almost never draw a 1 dp border around cards.  
Hairlines only: toolbar group separators (1 dp `outlineVariant`), table grid, divider block, drawing canvas handle.

## The two jewels

1. **Connected filter pills** under the wordmark.  
2. **Split plus** at the bottom end.

If you add a third jewel (vibrant toolbar, wavy loader, neon), the UI becomes a catalog. Stop.

## Large screens

When width ≥ 600 dp: rail 80 dp, same icons as pills, content on the right. Editor may sit beside the list. The paper metaphor holds — two pages, not a dashboard.
