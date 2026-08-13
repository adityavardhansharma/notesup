# 12 — States

## Pressed

| Control | Pressed look |
|---|---|
| Card | `surfaceContainerHighest` |
| Icon button | Ripple only |
| Pill | Ripple + already-selected fill |
| Plus | Component |
| Checkbox | 200 ms fill |
| List swipe | Reveal color as specified |
| Theme card | 2 dp primary ring when selected; press ripple |

## Focused (a11y / keyboard)

3 dp `primary` ring, 2 dp offset, on the focused control. Editor field: caret is enough (no extra ring on the page).

## Disabled

38% alpha. No ripple.

## Empty

One sentence. No art. Complete table: [ui/20-EMPTY-STATES.md](20-EMPTY-STATES.md). Welcome is 14, not an empty.

## Loading

**Home: none.**  
Clerk init: treat as signed out for chrome; do not block list.  
Image bind: `surfaceContainer` placeholder, no shimmer.  
Export: disable the menu item and show a 2 dp `primary` bar under the app bar, indeterminate, 2 s max then snackbar fail.

## Error

Snackbar or inline `error` text. No full-screen error clown.  
Image fail: `REJECT` + replace block with `Couldn’t add image` `bodySmall` `error` + tap to retry.

## Offline

`cloud_off` on avatar. Sheet line `Sync paused`. Writing works.

## Locked

Card preview is `Locked note`. Open → gate + `BiometricPrompt`. Full pixels: [ui/18-LOCK-GATE.md](18-LOCK-GATE.md).  
`FLAG_SECURE` on the gate and the decrypted editor. Recents never shows the body.

## Select

See 06. Count in bar. Rings on cards.

## Conflict

Second note `… (conflict)`. Snackbar `Kept both versions`. No modal.

## Reduce motion

Fades only. Checkbox still 150 ms fill (or instant if OS asks).

## RTL

`supportsRtl=true`. Pills and plus mirror. Back arrow auto-mirrors. Swipe start/end follow layout direction.
