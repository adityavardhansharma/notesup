# 03 — Material 3 Expressive

Primary sources: [Building with M3 Expressive](https://m3.material.io/blog/building-with-m3-expressive) (May 13, 2025), Android / Wear OS launch post, 9to5Google component follow-ups (menus Nov 2025), Compose `ExperimentalMaterial3ExpressiveApi`, composables.com Material3 catalog.

Doc importance for Notesup: **10/10**. This is the body of the app. If we ignore it we will look like a 2022 Keep clone on Android 16.

## What Expressive actually is

Material 3 Expressive is not a new product name. It is an evolution of M3:

- More **shape** (asymmetric, animated, connected)
- More **motion physics** (a `MotionScheme` on the theme, not random tweens)
- More **emotion** in components that used to be rectangles with 24dp corners
- Explicit research claim from Google: expressive UI can make common actions **feel faster** (Gmail demo: ~4× perceived speed on some actions)

It is **not** iOS Liquid Glass. Do not blur every surface. Do not put a frosted tab bar over the notes. Android premium in 2026 is **color, shape, and motion**, not refraction.

## Component inventory (official + Compose)

### New in the Expressive wave (I/O 2025)

| Component | What it is | Notesup | Score |
|---|---|---|---:|
| **ButtonGroup / connected toggle buttons** | Segmented, shared stroke, leading/middle/trailing shapes | Home view toggle (grid/list), editor align, ink tools | 10 |
| **SplitButton** | Primary action + overflow chevron | **New note** + types (note / list / ink / image) | 10 |
| **FAB menu** | FAB that fans actions | Alternate to SplitButton on home | 9 |
| **LoadingIndicator** (expressive) | Morphing, not a circle of death | Sync, export, search | 8 |
| **Toolbar / Floating toolbar** | Contextual, can sit over content | **Editor formatting + insert** | 10 |
| **HorizontalFloatingToolbar** | Pill toolbar, optional attached FAB | Editor home-away-from-top-app-bar | 10 |

### Updated components

| Component | Expressive change | Notesup | Score |
|---|---|---|---:|
| **Navigation bar** | Larger active indicator, more shape, better motion | We mostly **do not use a 3–5 dest nav bar** | 6 |
| **Navigation rail** | Same language for tablets / unfolded | Foldables, large screens | 8 |
| **Short navigation bar items** | Configurable colors | If we ever need a bar | 6 |
| **App bars** | Flexible, collapse with motion | Home title + search | 9 |
| **Common buttons** | `ButtonDefaults.shapes()` animated shapes | Primary CTAs | 9 |
| **Icon buttons** | Shape morph selected/unselected | Pin, more, view toggle | 9 |
| **FAB / Extended FAB** | Shape + color emphasis | Capture | 9 |
| **Sliders** | Expressive track | Ink size, image crop | 8 |
| **Progress** | Wavy / expressive | Export PDF | 7 |
| **Menus** (late 2025) | Vertical menus, new shapes, selection, submenu motion | Note overflow, editor insert | 9 |
| **Carousel** | Emphasis on hero item | Cover picker, paper, ink brush | 8 |

### Theme tokens we will actually set

```
MaterialTheme {
  colorScheme   // dynamic + Notesup seed
  typography    // see file 08
  shapes        // extra-round for pills, sharper for editor sheets
  motionScheme  // standard() or expressive()
}
```

Compose already exposes `MaterialTheme.Values` with `motionScheme`. **Accepted:** set an expressive motion scheme app-wide and only opt out in the editor caret path (typing must not bounce).

## Shape strategy for Notesup

| Surface | Shape | Why |
|---|---|---|
| Home note card | Extra-large rounded, 20–28dp | Object, not list row |
| Pinned card | Same + tonal container | Status without a badge farm |
| Filter pills | Full stadium | Expressive, Airbnb/Music familiar |
| Split FAB | Stadium + attached menu | The product's signature control |
| Editor sheet / insert | Large rounded top only | Standard Android sheet |
| Ink canvas | Almost square, 12dp | Tool, not sticker |
| Dialogs | Extra-large | Expressive dialog spec |
| Widgets | 24dp, match cards | Home screen kinship |

**Rejected:** squircle-everything (iOS), 4dp sharp Material 2, random blob shapes that don't come from the M3 shape library.

## Color strategy (Expressive + You)

Accepted:

1. **Dynamic color ON by default** (Material You). The app belongs on *this* phone.
2. A **Notesup seed** (`Primary` a warm ink-black/cream in light, a paper-warm dark in dark) used when dynamic color is off or as the fallback harmonization source.
3. **Tonal containers** for pinned, locked, synced, conflict — not rainbow labels like Keep.
4. User **note tint** is optional and *quiet* (one hue, container-level). Keep's 12 loud chips are rejected as the default grid.

Rejected:

- Neon accent on every FAB
- Pure black OLED with #FFFFFF text as the only dark mode (use tonal dark)
- Glassmorphism overlays
- Per-notebook Instagram gradients

## Motion strategy

Google's Expressive motion is physics-based. Rules for us:

| Motion | Use | Do not use |
|---|---|---|
| Shared element note card → editor | Open/close note | Between unrelated screens |
| Container transform | Grid ↔ list | Every filter change |
| Shape morph | Selected icon buttons, split FAB open | While typing |
| Enter/exit fade+scale | Sheets, menus | Full-screen fades on every nav |
| Predictive back | Editor → home, home → exit | Disable it |
| Scroll-linked collapse | Top app bar on home | Parallax hero that eats space |

**Perceived speed:** make the **new note** path 0 animation beyond a 180–220ms container transform. Expressive is allowed to be theatrical on *opening a cover picker*, not on *starting to type*.

## Navigation: what Expressive wants vs what Notesup needs

Expressive refresh of NavigationBar is good for apps with 3–5 peer destinations (Phone, Gmail, YouTube).

**A notes app does not have 3–5 peer destinations.** Destinations are:

- Home (the product)
- Search (a mode)
- Editor (a surface)
- Settings (a basement)

Putting Home / Search / Archive / Profile / Settings on a nav bar is how notes apps start looking like Instagram.

**Accepted home chrome (Expressive, not a nav bar):**

1. Collapsing app bar: wordmark + search icon + profile/settings avatar
2. **Connected ButtonGroup pills:** All · Pinned · Recent · Projects
3. Trailing **view toggle** (grid/list) as icon buttons with shape morph
4. **SplitButton FAB** or **HorizontalFloatingToolbar + FAB** docked bottom-end, above system gesture inset

This is the "premium nav pills" request, expressed in actual Expressive components.

## Docs rated

| Doc | Importance | Notes |
|---|---:|---|
| m3.material.io Expressive announcement | 10 | Source of component list |
| Android Design & Plan | 8 | Architecture adjacent |
| Compose Material 3 guide | 10 | How we implement |
| ExperimentalMaterial3ExpressiveApi samples (ButtonGroup, FloatingToolbar) | 10 | Copy shapes, not colors |
| Wear OS Expressive post | 6 | Motion philosophy only |
| 9to5Google menus article | 7 | Confirms menus caught up Nov 2025 |
| Liquid Glass / iOS 26 writeups | 4 | Know the competitor language; do not port |

## Accept / reject

**Accept**

- Expressive as the system language
- SplitButton / FAB menu for compose types
- Floating toolbar in the editor
- Connected button group for filters and view
- MotionScheme
- Dynamic color + seed
- Navigation rail on large screens
- Expressive menus for overflow

**Reject**

- NavigationBar with 4+ peers
- Liquid Glass cosplay
- Expressive motion on keystrokes
- Using every new component on one screen (catalog-app disease)
- LoadingIndicator as a personality (keep it quiet)

## One sentence

Expressive gives us the **pills, the split capture button, the floating editor toolbar, and the motion physics**. It does not give us a product. The product is still a notebook.
