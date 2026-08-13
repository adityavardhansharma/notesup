# 09 — Sheets, menus, dialogs, snackbar

## Sheet (account, insert, paper, type, move, image source)

- Max 85% height. Top radius **28**.  
- Handle 32×4, `outline`, 8 from top, centered.  
- Scrim `#000000` 40% light / 60% dark.  
- Predictive back: `offsetY = p * height`.  
- Title if needed: `titleSmall`, 16 top, 20 side.  
- Rows 56, icon 24, text `bodyLarge`, 20 side.

## Menu (overflow)

Expressive `DropdownMenu`. Radius 16. Item 48. Icon + 12 + text.  
Destructive `Delete` is `error` text, last after a 1 dp rule.

## Dialogs

Radius 28. Title `titleSmall`. Body `bodyMedium` `onSurfaceVariant`.  
Buttons: text button cancel, filled button confirm. Destructive confirm is `error` filled.  
Every confirm (sign out, delete account, delete n, delete project, empty trash, sync these, set a screen lock): [ui/22-SYSTEM-EDGES.md](22-SYSTEM-EDGES.md). Same radius / type / button rules.

## Snackbar

Only undo-class events.  
`inverseSurface` / `inverseOnSurface`. Action `primary` on that surface.  
16 side, 8 above plus **or** above nav if plus hidden. Radius 12.  
4 s. Swipe dismiss OK.

## Toasts / banners

**None.** No “Saved.” No “Synced.” No “Welcome back.”
