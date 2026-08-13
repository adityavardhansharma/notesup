# 03 — Type

Font default: **Roboto Flex** (variable), fallback `SansSerif`.  
User may pick one of 12 OFL faces (FEATURES-NOW-LOCKED). UI chrome **always** Roboto Flex so settings don’t break.

## Scale (use these names in code)

| Token | Size / line | Weight | Tracking | Where |
|---|---|---|---:|---|
| `displaySmall` | 36 / 44 | 500 | -0.25 | Empty home wordmark only |
| `headlineMedium` | 28 / 36 | 400 | 0 | **Note title** |
| `titleLarge` | 22 / 28 | 500 | 0 | Home wordmark; H1 |
| `titleMedium` | 16 / 24 | 500 | 0.15 | Card title; project name; H2 |
| `titleSmall` | 14 / 20 | 500 | 0.1 | Sheet titles; H3 |
| `bodyLarge` | 16 / 24 | 400 | 0.15 | Settings, dialogs, account |
| `bodyNote` | **18 / 28** | 400 | 0.15 | **Editor paragraph / quote** |
| `bodyMedium` | 14 / 20 | 400 | 0.25 | List preview |
| `bodySmall` | 12 / 16 | 400 | 0.4 | Card preview |
| `labelLarge` | 14 / 20 | 500 | 0.1 | Pills, buttons, snackbar |
| `labelMedium` | 12 / 16 | 500 | 0.5 | Chips, language |
| `labelSmall` | 11 / 16 | 500 | 0.5 | Time, widget meta |
| `code` | 14 / 20 | 400 | 0 | Code block — JetBrains Mono / Plex Mono |

H1/H2/H3, editor title, card titles, selected pills: **M3 emphasized** type roles (not hand-picked 600).

## Chrome strings (exact style)

| String | Style | Color |
|---|---|---|
| `Notesup` (home) | `titleLarge` 500 | `onSurface` |
| `Write anything.` | `bodyLarge` | `onSurfaceVariant` |
| Pills | `labelLarge` | selected `onPrimaryContainer` / rest `onSurface` |
| Card title | `titleMedium` maxLines 2 | `onSurface` |
| Card preview | `bodySmall` maxLines 2 | `onSurfaceVariant` |
| Card time | `labelSmall` | `onSurfaceVariant` |
| List title | `titleMedium` 1 line | `onSurface` |
| Search hint | `bodyLarge` | `onSurfaceVariant` |
| Editor title placeholder `Title` | `headlineMedium` | `onSurfaceVariant` @ 55% |
| Editor title filled | `headlineMedium` | `onSurface` |
| Snackbar | `labelLarge` | `inverseOnSurface` on `inverseSurface` |
| Settings rows | `bodyLarge` | `onSurface` |
| Dialog title | `titleSmall` | `onSurface` |
| Dialog body | `bodyMedium` | `onSurfaceVariant` |

No small caps. No ALL CAPS buttons. No exclamation marks.

## Typing

- Caret: 2 dp wide, `primary`, system blink.  
- Editor padding: 20 dp side, 8 dp under title, 12 dp between blocks.  
- Max measure tablet: **640 dp** centered.  
- `imePadding()` on the list; toolbar sits `8` dp above IME.  
- Do not animate letter-spacing or font size while typing.
- `liga` on for prose; off for `Code`. `tnum` on times and numbered markers.
- Title optical margin: **1.5 dp** start vs body.
- `opsz` axis follows `fontSize` on bundled Roboto Flex.  
- Autocorrect / capitalization: sentence, default keyboard `textCapSentences`.  
- Title field: `textCapWords`? **No** — `textCapSentences` so “iOS notes” isn’t forced.  
- Password: never in this app except Clerk.

## Focus type

- Sentence mode: inactive paragraphs `onSurface` @ **62%** (≈4.5:1). Not 35%. High-contrast / TalkBack → 100%.  
- Typewriter: caret at 38% of viewport.  
- Selection pauses both.

## Font picker UI

Sheet: 12 rows, each row set in **that** font, the word `Notesup` + `The quick brown fox`. Radio on the right. Size S/M/L as a ButtonGroup under the list (maps bodyNote 16/18/20).
