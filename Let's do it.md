# Let's do it

Notesup research bible. **No application code in this pass.** This file is the map of everything researched, what it means, and where the decisions live.

Last updated: 2026-08-14. Parent look map: [PANE.md](PANE.md). First-run: [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md). Remaining surfaces: [ui/15](ui/15-SETTINGS.md)–[ui/22](ui/22-SYSTEM-EDGES.md).

## What this project is

Notesup is the Android notes app you can actually write in: a warm page that opens instantly, holds text, images, tables and real ink in one document, and never asks who you are. Kotlin, Compose, Material 3 Expressive (**1.5.0-alpha**, wrapped). Auth **Clerk** (optional). Sync **Convex** (after Room). Keep already shipped Expressive — we do not compete on “we have pills.” We compete on writing, ink, paper, and no account.

Premium here does **not** mean more features. Premium means:

- Instant capture (one thumb, one tap, writing in under a second)
- A home that feels like a designed object, not a settings dump
- An editor that can hold text, images, checklists, and ink without becoming Notion
- Motion, haptics, and shape that feel like Android 16 — not a 2019 Material card grid
- Offline that never lies
- Sync that appears only after the local product is already beautiful

## How to read this research

**To build the app, open [BUILD.md](BUILD.md).**  
**The look (every icon, color, motion, box):** [PANE.md](PANE.md).  
**First-run + custom Clerk screens:** [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md).  
**Settings / projects / capture / lock / media / empty / tablet / edges:** [ui/15-SETTINGS.md](ui/15-SETTINGS.md) … [ui/22-SYSTEM-EDGES.md](ui/22-SYSTEM-EDGES.md).  
The feeling people defend: [UX-PEOPLE-GO-TO-WAR-FOR.md](UX-PEOPLE-GO-TO-WAR-FOR.md).  
Themes, paper, tables, slash, fonts, code: [FEATURES-NOW-LOCKED.md](FEATURES-NOW-LOCKED.md).  
Drawing canvas (pencil/marker/highlighter/eraser): [DRAWING.md](DRAWING.md).  
Primary-source corrections and URLs: [RESEARCH-LOG.md](RESEARCH-LOG.md).  
Every pixel, millisecond, icon, string, schema field, and back-gesture frame is locked in BUILD. Research files are the argument. BUILD is the decision.

Fifteen files under `research/` if you want the argument:

| # | File | What it is |
|---|---|---|
| 01 | [research/01-note-taking-apps.md](research/01-note-taking-apps.md) | 120 note apps, public + GitHub, rated |
| 02 | [research/02-premium-apps-not-notes.md](research/02-premium-apps-not-notes.md) | 100 premium apps that are *not* notes |
| 03 | [research/03-material-expressive.md](research/03-material-expressive.md) | Material 3 Expressive, components, motion, color |
| 04 | [research/04-home-nav-grid-list.md](research/04-home-nav-grid-list.md) | Home, grid, list, nav pills, FAB |
| 05 | [research/05-editor-blocks-media-export.md](research/05-editor-blocks-media-export.md) | Editor, images, PDF/Markdown export |
| 06 | [research/06-ink-drawing-focus.md](research/06-ink-drawing-focus.md) | Drawing, stylus, focus mode |
| 07 | [research/07-motion-haptics-patterns.md](research/07-motion-haptics-patterns.md) | 100+ motion + haptic + interaction patterns |
| 08 | [research/08-color-type-components.md](research/08-color-type-components.md) | Color, type, icons, 100 inspirations per key component |
| 09 | [research/09-ia-projects-folders-pins-widgets.md](research/09-ia-projects-folders-pins-widgets.md) | Projects, folders, pins, Android widgets |
| 10 | [research/10-writing-the-best-kotlin-material-design.md](research/10-writing-the-best-kotlin-material-design.md) | Kotlin + Compose + Material, written to remember |
| 11 | [research/11-convex.md](research/11-convex.md) | Convex docs, Android client, ratings |
| 12 | [research/12-clerk.md](research/12-clerk.md) | Clerk docs, Android SDK, Convex glue |
| 13 | [research/13-accepted-rejected-ratings.md](research/13-accepted-rejected-ratings.md) | Every accept / reject, with scores |
| 14 | [research/14-THE-ACCEPTED-SYSTEM.md](research/14-THE-ACCEPTED-SYSTEM.md) | **The one file that is the product** |
| 15 | [research/15-open-source-android-platform.md](research/15-open-source-android-platform.md) | OSS editors, ink, widgets, Glance, Room |

If two files disagree, **[BUILD.md](BUILD.md) wins**. Then 14. 13 is the ledger of why.

## Method

Sources used:

- NoteApps.info 2026 catalog (40 apps, 357 features, 7,161 screenshots)
- Zapier, PCMag, Paperlike, Tool Finder, Storyflow 2026 roundups
- GitHub `note-taking` topic (3,504 repos) and `notes-app` + Kotlin filter
- Material 3 Expressive official: m3.material.io, Android Developers, Google I/O 2025
- Android haptics principles and Glance widget docs
- Convex Android client docs + Clerk official Android + `clerk-convex-kotlin`
- Kotlin language docs (sealed classes, coroutines, data classes)
- Reddit r/UXDesign, r/Android, r/ipad note-app threads
- Premium product reviews: Copilot Money, Things 3, Superhuman, Linear, Craft

Scoring (used in every file):

| Score | Meaning |
|------:|---------|
| 10 | Steal the whole idea |
| 9 | Accept, adapt to Android Expressive |
| 8 | Accept the pattern, not the chrome |
| 7 | Useful fragment |
| 6 | Situational |
| 5 | Neutral / do not lead with this |
| 4 | Reject as a primary pattern |
| 1–3 | Actively harmful to premium |

"Premium" was scored on **calm, speed, material honesty, and whether a note still feels like a note**. Feature count was scored separately and usually *against* the product.

## The one-sentence thesis

Notesup is a **local-first, block-bodied notebook** with a **Keep-speed home**, a **Craft-quality editor**, **Apple Notes mixed media**, **Things 3 project calm**, and an **Expressive Android body** — not a second brain, not a canvas, not a database.

## What I actually understood after all of this

1. **The winners of aesthetic excellence in 2026 are Craft, Reflect, Obsidian, Milanote, Capacities.** None of them are Android-native first. That is the gap.
2. **Android's own note apps are not premium.** Keep is fast and cheap-looking. Samsung Notes is powerful and visually dated. OneNote is a canvas, not a designed phone app. Easy Notes / ColorNote win Play Store volume and lose design.
3. **The best Android-native notes UIs are small OSS apps:** Notally / NotallyX, Fossify Notes, Omni Notes, Markor. They are honest Material. They are not expressive. They are the floor, not the ceiling.
4. **Premium is a subtraction problem.** Superhuman, Things 3, Halide, Copilot Money, Linear, Flighty, Cash App — they feel expensive because they refuse destinations. A 5-tab notes app is already not premium.
5. **Home is a gallery, not a file manager.** Grid of living note cards (color, first image, first line). List is the power view. Toggle lives in a pill, not a hamburger.
6. **The editor is a vertical stack of blocks**, not a rich-text soup and not an infinite canvas. Text, image, checklist, ink, divider. That is enough to feel like Apple Notes / Craft and still ship.
7. **Drawing is a first-class block, not a mode that takes over the product.** Ink lives inside a note. A dedicated sketch note type exists, but it is not the home.
8. **Nav on home is not a NavigationBar of 5 icons.** It is an **Expressive pill row** (All / Pinned / Recent / Projects) plus a **split FAB** (Note / Checklist / Ink / Image). Search is a top action, not a tab.
9. **Projects yes. Deep folder trees no.** Things 3 / Craft Areas beat Evernote notebooks. Pins are sacred. Tags are optional metadata, not navigation.
10. **Widgets are not optional for an Android notes app that wants to feel native.** Glance: new-note control, pinned-note information, recent-notes collection.
11. **Clerk + Convex is a real, documented Android path**, not a web-only fantasy. Official `clerk-convex-kotlin`, `ConvexClientWithAuth`, **custom Compose auth** (`clerk-android-api` only — no `AuthView`), passkeys, Google via Credential Manager.
12. **Local Room is still the source of truth.** Convex is the sync plane. The UI never waits on the network to create a note.
13. **Material Expressive is the correct body language** — floating toolbars, connected button groups, animated shapes, motion schemes, vibrant toolbars. Liquid Glass is iOS. Do not fake it.
14. **Haptics: less, clearer, mapped to meaning.** Confirm create / pin / check / delete. Never buzz on scroll. Never one-shot 300ms vibrations.
15. **AI is not the product.** Capture, write, find, draw. Anything else waits.

## Locked product constraints (from you)

- Android only
- Kotlin
- No code until this research is done
- Clerk for auth
- Convex for cloud
- Material Expressive
- Premium UI/UX as the primary competitive claim

## What happens after these files

When you say go, implementation starts from `research/14-THE-ACCEPTED-SYSTEM.md` only. These files stay as the memory of why.

## Sources worth reopening later

- https://noteapps.info/best_note_taking_apps_2026
- https://m3.material.io/blog/building-with-m3-expressive
- https://developer.android.com/develop/ui/compose/designsystems/material3
- https://developer.android.com/develop/ui/views/haptics/haptics-principles
- https://developer.android.com/develop/ui/views/appwidgets/overview
- https://docs.convex.dev/client/android/overview
- https://docs.convex.dev/quickstart/android
- https://clerk.com/docs (Android SDK, Convex native integration)
- https://github.com/clerk/clerk-android
- https://github.com/clerk/clerk-convex-kotlin
- https://github.com/topics/note-taking
- https://kotlinlang.org/docs/sealed-classes.html
