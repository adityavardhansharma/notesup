# 02 — Color

Seed `#8B2942`. Paper light `#F6F1EA`. Ink `#1C1917`. Dark desk `#161311`.  
Generate the full M3 role set in Material Theme Builder from the seed; **builder file wins** if tones drift. Harmonize dynamic color toward the seed.

## Roles — what they mean here

| Role | Light job | Dark job |
|---|---|---|
| `background` / `surface` | Desk | Night desk |
| `onSurface` | Ink | Cream ink |
| `onSurfaceVariant` | Meta, secondary icons | Meta |
| `surfaceContainerLowest` | — rarely | — |
| `surfaceContainerLow` | **Note card** | Card |
| `surfaceContainer` | Code block, table header | same |
| `surfaceContainerHigh` | Search field, toolbar, menu | same |
| `surfaceContainerHighest` | Pressed card | same |
| `primary` | Wax — plus, caret, selected | Lighter wax |
| `onPrimary` | White on plus | Dark on plus |
| `primaryContainer` | Selected pill, checked fill | same idea |
| `onPrimaryContainer` | Text on selected pill | same |
| `secondary` | Lock, quieter accent | same |
| `tertiary` / `tertiaryContainer` | **Pin only** | Pin |
| `error` / `errorContainer` | Delete swipe, destructive | same |
| `outline` | Slider, unselected ring | same |
| `outlineVariant` | Hairlines | same |
| `scrim` | `#000000` @ 40% light / 60% dark | |

## Forbidden

- Keep yellow/green/blue card fills as default  
- Green “synced” / red “offline” traffic lights  
- `primary` on large text blocks  
- Random hex in composables — tokens only  

## Note tint (optional, 8%, container only)

0 none · 1 wax · 2 clay · 3 moss · 4 tide · 5 ink-blue · 6 plum · 7 rust  
Never change `onSurface`. Never fill 100%.

## Note paper (underlay, not a theme)

`blank` `ruled` `ruled_narrow` `legal` `graph_sm` `graph_lg` `dots` `kraft`  
Line/grid color: `onSurface` @ **8%** (light) / **12%** (dark). Legal wash: `#F3E6B8` @ 40% over paper. Kraft wash: `#C4A574` @ 18%.

## App themes (12)

Each is a full `ColorScheme` + default paper. Cards in Settings are 72×88, radius 20, live preview of desk+card+wax dot.

`dynamic` `paper` `graphite` `noon` `fog` `legal` `ink` `midnight` `slate` `dieci` `kraft` `contrast`

`dieci` = true black **only** as this theme. Default dark remains warm `ink`.

## States

| State | Color |
|---|---|
| Icon rest | `onSurface` (bar) or `onSurfaceVariant` (secondary) |
| Icon selected | `onPrimaryContainer` on `primaryContainer` (pills/tools) |
| Unchecked checkbox ring | `#6E5F59` (~5.1:1), not `outline` |
| Table grid | `outline` (semantic), not `outlineVariant` |
| Icon disabled | `onSurface` @ 38% |
| Pressed card | `surfaceContainerHighest`, 120 ms |
| Ripple | `onSurface` @ 12% (Material3 default ripple, not custom color) |
| Caret | `primary` |
| Selection | `primary` @ 24% |
| Link | `primary` |
| Code chip | `onSurfaceVariant` on `surfaceContainerHigh` |
| Offline glyph | `onSurfaceVariant` — not red |
| Error text | `error` |

## Contrast checks (must pass)

- `onSurface` on `background`  
- `onSurface` on `surfaceContainerLow` (card)  
- `onPrimary` on `primary` (plus)  
- `onPrimaryContainer` on `primaryContainer` (pill)  
- Body on legal / kraft washes  
- Dynamic color: if any pair fails AA, snap to `paper` / `ink`
