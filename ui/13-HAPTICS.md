# 13 — Haptics

`View.performHapticFeedback`. No `VIBRATE` permission. Respect system off.

| Event | Constant |
|---|---|
| New note | `CONFIRM` |
| Split open | `CLOCK_TICK` |
| Split type | `CLOCK_TICK` |
| Magic-plus drop on project | `CONFIRM` |
| Pin | `CONFIRM` |
| Unpin | `CLOCK_TICK` |
| Checklist on | `CONFIRM` |
| Checklist off | `CLOCK_TICK` |
| Delete | `REJECT` |
| Export / sign-in success | `CONFIRM` |
| Export / sign-in / image fail | `REJECT` |
| Lock on | `CONFIRM` |
| Long-press | `LONG_PRESS` |
| Select extra tap | `CLOCK_TICK` |
| Swipe pin / delete commit | `CONFIRM` / `REJECT` |
| Ink width step | `CLOCK_TICK` |
| Block drag start/end | `GESTURE_START` / `END` (else tick / confirm) |
| Create project | `CONFIRM` |
| Search verb | `CONFIRM` |

**None** on: back, open note, type, scroll, ink stroke, pill change, theme, sync recover, undo, search open, Done on drawing.

**Richer render (only if primitives supported) for three rows:** new note, checklist on, magic-plus drop → `TICK` then `CLICK`. Same table, richer hardware. Fallback = the constant.

If you add a haptic not in this table, you are wrong.
