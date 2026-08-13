# PANE — the parent

This is the map of **how Notesup looks and feels**.  
Do not write application code from this file. Follow the links.

**Build the product from:** [BUILD.md](BUILD.md)  
**Feel / laws from:** [UX-PEOPLE-GO-TO-WAR-FOR.md](UX-PEOPLE-GO-TO-WAR-FOR.md)  
**Look / every pixel from:** the `ui/` files below.

If BUILD and a `ui/` file disagree on a measurement, **BUILD wins for product rules; `ui/` wins for how it looks** — unless BUILD cites an official token (motion ms, M3 type ramp). Then the token wins.

Decisions from review: [feedback2.md](feedback2.md) (applied). Source review: [FEEDBACK.md](FEEDBACK.md).

---

## Read in this order

| # | File | What it locks |
|--:|---|---|
| 0 | [ui/00-ANTI-JOKE.md](ui/00-ANTI-JOKE.md) | Why most “Material notes” look cheap, and the look we refuse |
| 1 | [ui/01-VISUAL-LANGUAGE.md](ui/01-VISUAL-LANGUAGE.md) | The object: paper + wax. Density, light, luxury rules |
| 2 | [ui/02-COLOR.md](ui/02-COLOR.md) | Every color role, every state, every theme |
| 3 | [ui/03-TYPE.md](ui/03-TYPE.md) | Every text style, every string of chrome |
| 4 | [ui/04-ICONS.md](ui/04-ICONS.md) | Every icon, size, weight, fill, description |
| 5 | [ui/05-MOTION.md](ui/05-MOTION.md) | Every animation, every duration, every back frame |
| 6 | [ui/06-HOME.md](ui/06-HOME.md) | Home: bar, pills, cards, list, FAB, empty |
| 7 | [ui/07-EDITOR.md](ui/07-EDITOR.md) | Editor: title, typing, blocks, toolbar, slash |
| 8 | [ui/08-DRAWING-CHROME.md](ui/08-DRAWING-CHROME.md) | Drawing toolbar, tools, popovers (engine in DRAWING.md) |
| 9 | [ui/09-SHEETS-DIALOGS.md](ui/09-SHEETS-DIALOGS.md) | Sheets, menus, dialogs, snackbar |
| 10 | [ui/10-SEARCH-ACCOUNT-SETTINGS.md](ui/10-SEARCH-ACCOUNT-SETTINGS.md) | Search, account, settings, appearance |
| 11 | [ui/11-WIDGETS.md](ui/11-WIDGETS.md) | Four widgets, visually |
| 12 | [ui/12-STATES.md](ui/12-STATES.md) | Pressed, focused, empty, error, offline, locked, select |
| 13 | [ui/13-HAPTICS.md](ui/13-HAPTICS.md) | Every haptic, none elsewhere |
| 14 | [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md) | First-run Welcome + custom Clerk (not AuthView) |
| 15 | [ui/15-SETTINGS.md](ui/15-SETTINGS.md) | Settings root, appearance, type, paper, focus, trash, account, privacy |
| 16 | [ui/16-PROJECTS.md](ui/16-PROJECTS.md) | Create, open, rename, delete, move, empty project |
| 17 | [ui/17-CAPTURE-SHARE.md](ui/17-CAPTURE-SHARE.md) | Lock-screen capture, ROLE_NOTES float, share-in, shortcuts |
| 18 | [ui/18-LOCK-GATE.md](ui/18-LOCK-GATE.md) | Locked note envelope + BiometricPrompt |
| 19 | [ui/19-MEDIA.md](ui/19-MEDIA.md) | Image sheet, caption, lightbox |
| 20 | [ui/20-EMPTY-STATES.md](ui/20-EMPTY-STATES.md) | Every empty sentence |
| 21 | [ui/21-LARGE-SCREEN.md](ui/21-LARGE-SCREEN.md) | Rail, two-pane, landscape |
| 22 | [ui/22-SYSTEM-EDGES.md](ui/22-SYSTEM-EDGES.md) | Confirmations, session death, widget previews, auth leftovers |

Also in force:

- [FEATURES-NOW-LOCKED.md](FEATURES-NOW-LOCKED.md) — themes, paper, tables, fonts, slash
- [DRAWING.md](DRAWING.md) — ink engine + tools
- [READY.md](READY.md) — go/no-go
- [research/](research/) — argument, not law

---

## The one-sentence UI

A **warm sheet of paper** with a **wax seal** (the split plus). Everything else is quiet. If a screen looks like a dashboard, a settings app, or a Compose tutorial, it is wrong.

---

## How to use this when coding

1. Open the screen file (`06`–`11`, or `14`–`22` for first-run, settings, capture, lock, media, empty, large, edges).  
2. Check tokens in `02` `03` `04` `05`.  
3. Check state in `12` and haptic in `13`.  
4. If you want to add a shadow, a spinner, or a fifth destination, read `00` first and stop.

**Applied 2026-08-14:** Expressive on 1.5.0-alpha behind wrappers; Flex bundled + opsz; 60 hand icons; own `RichText`; causality sync; FTS5 + lock purge; ink table; TextFieldState; backup excludes DB; springs inherit; 62% sentence dim; 640 dp; grain 2–3%; three rich haptics; share + shortcuts; ROLE_NOTES at 15b; pressure always. First-run `ui/14`. **Settings, projects, capture, lock gate, media, every empty, large screen, and system edges: `ui/15`–`ui/22`.**
