# Ready to build

**Yes. I am ready** — after applying [feedback2.md](feedback2.md) into BUILD / PANE / ui.

Clerk and Convex stay (steps 16–17). Sync uses causality, not clocks.

Not because every pixel of the universe was catalogued. Because the product is decided, the APIs that make it *smooth* are now **stable and official**, and the remaining risk is implementation craft — which only shows up when we write the app.

## What I just verified (so this is not a vibe)

| Risk | Status |
|---|---|
| Drawing feel | **Jetpack Ink 1.0.0 is stable** (2025-12-17). Compose authoring module. Stock highlighter. This is Google’s note-app ink path. |
| Expressive capture | Material3 **1.5.0-alpha** (not 1.4.0). Pin exact alpha. Wrap jewels. |
| Open / back | Official **sharedBounds** + **PredictiveBackHandler**. **Navigation 3 is stable** (1.1.6 as of 2026-08-12). All Nav3 versions support predictive back. |
| Rich text | compose-rich-editor still shipping (Maven 2026). **Inline images inside one field still weak** — our **block list** is the correct architecture, not a guess. |
| Android-best citizen | Official `ROLE_NOTES`, `ACTION_CREATE_NOTE`, lock-screen note, stylus extra. We implement after the editor exists (same week as widgets). |
| Clerk + Convex | Official Android SDK + `createClerkConvexClient` + `auth.config.ts`. Local Room first so auth cannot block writing. |

## What “world-class” will actually be won or lost on

Not more research. These five, in order, when we code:

1. **Plus → keyboard ≤ 200 ms**, Room write, no spinner  
2. **Ink 1.0** marker/pencil/highlighter that looks like ink  
3. **sharedBounds + predictive back** card ↔ editor  
4. **One live TextField** in a `LazyColumn` of blocks (IME + toolbar)  
5. **No skeleton, no auth wall, no bounce on type**

If those five land, the app is already in the class people defend. Themes, paper, tables, slash, fonts sit on top.

## Residual risk (honest, not blockers)

- IME + floating toolbar will take real device time. Spec is right; fingers decide.  
- Clerk UI is **custom** ([ui/14-ONBOARDING.md](ui/14-ONBOARDING.md)). `AuthView` is banned. Residual risk is Credential Manager + email-code edge cases, not “does AuthView look like paper.”  
- Settings, projects, capture, lock gate, media, empties, tablet, confirmations are specified in `ui/15`–`ui/22`. Residual risk is device craft (IME, keyguard, Glance picker), not missing screens.  
- Convex mobile is smaller than web — we own offline.  
- I never generated the seed palette in Material Theme Builder yet — first theme task when coding starts.

None of that is a research hole. It is build work.

## What we will not reopen

Notion databases. 5-tab bar. Auth wall. Webcam. WebView editors. Folder trees. Slash menus of 30.

## Go

Implement from **[BUILD.md](BUILD.md)** in §27 order.  
Feel: **[UX-PEOPLE-GO-TO-WAR-FOR.md](UX-PEOPLE-GO-TO-WAR-FOR.md)**.  
Look: **[PANE.md](PANE.md)** — `ui/00`–`ui/22`. First-run [ui/14](ui/14-ONBOARDING.md). Settings through edges [ui/15](ui/15-SETTINGS.md)–[ui/22](ui/22-SYSTEM-EDGES.md).  
Ink: **[DRAWING.md](DRAWING.md)** with **Ink 1.0.0**.  
Features: **[FEATURES-NOW-LOCKED.md](FEATURES-NOW-LOCKED.md)**.

I am ready. Say go and we write the app.
