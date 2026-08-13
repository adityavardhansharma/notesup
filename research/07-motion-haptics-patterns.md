# 07 — 100+ patterns: motion, haptics, interaction

Android sources: [Haptics design principles](https://developer.android.com/develop/ui/views/haptics/haptics-principles), `HapticFeedbackConstants`, `VibrationEffect.Composition`, Haptic Sampler. Motion: M3 Expressive `MotionScheme`, Compose animation APIs, predictive back.

Google's own rule, which we adopt as law: **less is more**. If the user turns haptics off, we failed.

## Classifications we will use

| Class | Feel | When |
|---|---|---|
| **Clear** | 10–20ms crisp tick | Discrete events: tap confirm, toggle, pin |
| **Rich** | primitives composed | Rare delight: first note created, export done, pin-to-top settle |
| **Buzzy** | long, ringing | **Never** for UI. Notifications only if system demands |

Avoid `VibrationEffect.createOneShot` for UI. Prefer `HapticFeedbackConstants` so the device matches the rest of Android.

## Where to use animation (100, scored)

Score = should Notesup do this?

| # | Pattern | From | Score | Decision |
|--:|---|---|---:|---|
| 1 | Shared-element card → editor | Photos, Keep, Craft | 10 | **A** |
| 2 | Predictive back to home | Android 14+ | 10 | **A** |
| 3 | Container transform grid ↔ list | M3 | 8 | **A** |
| 4 | Pill selection slide | Expressive, Music | 9 | **A** |
| 5 | Shape-morph icon selected | Expressive buttons | 9 | **A** |
| 6 | FAB explode to split actions | M3 FAB menu | 8 | **A** |
| 7 | FAB hide on scroll down | Keep, many | 7 | **A** mild |
| 8 | App bar collapse | M3 | 8 | **A** |
| 9 | Focus chrome fade | Bear, iA | 9 | **A** |
| 10 | Keyboard IME coordinated toolbar | Apple Notes | 9 | **A** |
| 11 | Check circle fill | Things 3, Streaks | 10 | **A** |
| 12 | Pin fly to pinned strip | Mail, Keep | 8 | **A** |
| 13 | Delete shrink + fade | iOS mail, Files | 8 | **A** |
| 14 | Undo toast slide | Material snackbar | 8 | **A** |
| 15 | Swipe reveal actions | Gmail, Apple | 7 | **A** list only |
| 16 | Pull to refresh | Everywhere | 3 | **R** (local-first, no ritual) |
| 17 | Skeleton shimmer | Social | 4 | **R** on home (we have Room) |
| 18 | Full-screen splash logo | Banks | 2 | **R** |
| 19 | Onboarding carousel | SaaS | 2 | **R** |
| 20 | Confetti | Fintech, games | 3 | **R** |
| 21 | Parallax hero | Marketing | 2 | **R** |
| 22 | Page curl | Old iBooks | 2 | **R** |
| 23 | Ink stroke spring | Procreate | 8 | **A** if cheap |
| 24 | Eraser dissolve | Apple Notes | 7 | **A** |
| 25 | Image expand to lightbox | Photos | 9 | **A** |
| 26 | Block insert slide | Craft | 8 | **A** |
| 27 | Block drag reorder | Craft, Apple | 8 | **A** v1.1 |
| 28 | Sheet morph from FAB | M3 | 7 | **A** insert sheet |
| 29 | Menu gap / stagger | Expressive menus 2025 | 7 | **A** |
| 30 | Search field expand from icon | Keep, iOS | 9 | **A** |
| 31 | Search results stagger 20ms | Linear | 6 | **F** short lists only |
| 32 | Empty-state illustration loop | Lottie farms | 3 | **R** |
| 33 | Sync spinner in avatar | Drive | 7 | **A** tiny |
| 34 | Offline slash fade | Slack | 7 | **A** |
| 35 | Conflict badge pulse | Git tools | 4 | **R** pulse; static badge |
| 36 | Typing caret blink only | OS | 10 | **A** system |
| 37 | Text style apply flash | Docs | 5 | **R** |
| 38 | Markdown live morph | Typora | 6 | **F** later |
| 39 | Typewriter scroll | iA | 8 | **A** option |
| 40 | Line highlight | Code editors | 3 | **R** |
| 41 | Night theme crossfade | Instagram | 7 | **A** 200ms |
| 42 | Dynamic color change | Wallpaper | 6 | **A** if OS does |
| 43 | Widget flip | Old Android | 3 | **R** |
| 44 | Widget content fade | Clock | 7 | **A** |
| 45 | Lock icon lock-motion | Wallet | 8 | **A** |
| 46 | Password sheet | System | 8 | **A** |
| 47 | Clerk hosted auth browser | Clerk | 7 | **A** (theirs) |
| 48 | Passkey prompt | OS | 10 | **A** system |
| 49 | Success check draw | Stripe | 7 | **A** export |
| 50 | Error shake field | Forms | 6 | **F** auth only |
| 51 | Overscroll stretch | iOS | 5 | Use Android stretch |
| 52 | Nested scroll connection | Compose | 8 | **A** |
| 53 | Bottom sheet snap | Material | 8 | **A** |
| 54 | Dialog scale in | M3 Expressive | 8 | **A** |
| 55 | Snackbar with undo | Gmail | 9 | **A** delete |
| 56 | Multi-select app bar morph | Photos | 9 | **A** |
| 57 | Count-up selected | Photos | 8 | **A** |
| 58 | Drag to pin strip | Folders iOS | 6 | **F** later |
| 59 | Folder open burst | iOS | 4 | **R** we have no springboard folders |
| 60 | Project header collapse | Things | 8 | **A** |
| 61 | Sticky time headers | Mail | 8 | **A** |
| 62 | Fast scroller letter | Contacts | 5 | **F** if 500+ notes |
| 63 | Magnifier on text | OS | 10 | **A** system |
| 64 | Selection handles | OS | 10 | **A** |
| 65 | Link preview chip | iMessage | 5 | **F** later |
| 66 | Image upload progress bar | Drive | 7 | **A** quiet |
| 67 | Skeleton image | Web | 5 | Placeholder tone |
| 68 | Shared element image | Photos | 9 | **A** |
| 69 | PDF generate morph | Print | 6 | **F** |
| 70 | Share sheet | OS | 10 | **A** |
| 71 | Staggered grid appear | Pinterest | 4 | **R** every launch |
| 72 | First-run one card demo | — | 3 | **R** |
| 73 | Cursor color accent | iA | 7 | **A** |
| 74 | Quote bar grow | Medium | 6 | **F** |
| 75 | Checklist collapse done | Things | 8 | **A** optional |
| 76 | Swipe complete | Todoist | 6 | **F** |
| 77 | Force-press peek | 3D Touch dead | 2 | **R** |
| 78 | Long-press preview | Android 12+ | 7 | **A** |
| 79 | Drag handle on sheet | Material | 8 | **A** |
| 80 | Rail expand | Large screen | 8 | **A** |
| 81 | Two-pane list/detail | Foldables | 9 | **A** |
| 82 | Width-adaptive grid columns | WindowSizeClass | 10 | **A** |
| 83 | IME insets animation | Compose | 10 | **A** |
| 84 | Edge-to-edge content | Android 15 | 10 | **A** |
| 85 | Contrast when over image | Photos | 8 | **A** |
| 86 | Status bar icon swap | Themes | 8 | **A** |
| 87 | Nav bar transparent | Edge-to-edge | 8 | **A** |
| 88 | Gesture exclusion on swipe | Compose | 9 | **A** |
| 89 | Scroll-to-top on tab reselect | Twitter | 7 | **A** on logo tap |
| 90 | Double-tap like | Instagram | 1 | **R** |
| 91 | Heart burst | Social | 1 | **R** |
| 92 | Story ring | IG | 1 | **R** |
| 93 | Shimmer CTA | Growth | 1 | **R** |
| 94 | Badge bounce | Games | 2 | **R** |
| 95 | Number ticker | Finance | 5 | **F** |
| 96 | Clock analog widget motion | Google Clock | 6 | widgets only |
| 97 | Weather morph | Pixel | 5 | **F** |
| 98 | Map camera | Flighty | 4 | **R** |
| 99 | Haptic+lottie combo | Copilot | 6 | only if meaningful |
| 100 | Zero animation create-note | Drafts | 10 | **A** after first 200ms transform |

## Where to use haptics (100, scored)

Use `view.performHapticFeedback(constant)` from Compose via `LocalView` or the newer Compose haptics APIs. Respect `ViewConfiguration` / user disable.

| # | Event | Constant (intent) | Score | Decision |
|--:|---|---|---:|---|
| 1 | New note created | `CONFIRM` | 10 | **A** |
| 2 | Split FAB open | `GESTURE_START` / tick | 7 | **A** light |
| 3 | Split FAB select type | `CLOCK_TICK` | 7 | **A** |
| 4 | Pin | `CONFIRM` | 10 | **A** |
| 5 | Unpin | `REJECT` light or tick | 7 | **A** |
| 6 | Delete | `REJECT` | 8 | **A** |
| 7 | Undo | tick | 6 | **F** |
| 8 | Checklist on | `CONFIRM` | 10 | **A** |
| 9 | Checklist off | tick | 8 | **A** |
| 10 | Toggle grid/list | tick | 6 | **A** very light |
| 11 | Pill change | tick | 5 | **F** (too frequent) |
| 12 | Open note | none | 9 | **A** none |
| 13 | Close note | none | 8 | **A** |
| 14 | Long-press card | `LONG_PRESS` | 10 | **A** system |
| 15 | Enter select mode | `LONG_PRESS` | 10 | **A** |
| 16 | Multi-select tap | tick | 7 | **A** |
| 17 | Swipe action commit | `CONFIRM` / `REJECT` | 8 | **A** |
| 18 | Swipe not far enough | none | 8 | **A** |
| 19 | Search open | none | 8 | **A** |
| 20 | Search no results | none | 8 | **A** |
| 21 | Export success | `CONFIRM` rich | 8 | **A** |
| 22 | Export fail | `REJECT` | 8 | **A** |
| 23 | Image added | tick | 6 | **F** |
| 24 | Image failed | `REJECT` | 8 | **A** |
| 25 | Ink tool change | tick | 7 | **A** |
| 26 | Ink stroke | **none** | 10 | **A** none |
| 27 | Ink undo | tick | 6 | **F** |
| 28 | Slider tick (thickness) | `CLOCK_TICK` / segment | 8 | **A** on steps |
| 29 | Snap slider ends | richer tick | 7 | **A** |
| 30 | Keyboard keys | OS | 10 | **A** do not override |
| 31 | Error on save | `REJECT` | 8 | **A** |
| 32 | Offline try-share live link | `REJECT` | 6 | **F** |
| 33 | Sync recovered | none | 7 | **A** silent |
| 34 | Conflict detected | `REJECT` once | 7 | **A** once |
| 35 | Lock note | `CONFIRM` | 8 | **A** |
| 36 | Unlock success | `CONFIRM` | 8 | **A** |
| 37 | Unlock fail (biometrics) | OS | 10 | **A** OS |
| 38 | Sign in success | `CONFIRM` | 7 | **A** |
| 39 | Sign in fail | `REJECT` | 7 | **A** |
| 40 | Sign out | none | 7 | **A** |
| 41 | Text style bold apply | none | 8 | **A** none (high frequency) |
| 42 | Heading convert | tick | 5 | **F** |
| 43 | Drag block start | `GESTURE_START` | 8 | **A** |
| 44 | Drag block drop | `GESTURE_END` | 8 | **A** |
| 45 | Reorder over slot | `SEGMENT_TICK` | 7 | **A** |
| 46 | Widget tap | OS | 8 | **A** |
| 47 | Widget resize | OS | 8 | **A** |
| 48 | Back gesture | OS | 10 | **A** |
| 49 | Pull refresh | **don't exist** | 10 | **A** |
| 50 | Scroll | **none** | 10 | **A** none |
| 51 | Fast fling | none | 10 | **A** |
| 52 | Overscroll | OS optional | 6 | **F** |
| 53 | Tab reselect | none | 7 | **A** |
| 54 | Empty FAB hint | none | 8 | **A** |
| 55 | First launch | none | 10 | **A** |
| 56 | Theme switch | tick | 5 | **F** |
| 57 | Dynamic color change | none | 8 | **A** |
| 58 | Clipboard paste image | tick | 6 | **F** |
| 59 | Share opened | none | 8 | **A** |
| 60 | Permission denied | `REJECT` | 6 | **F** |
| 61 | Camera capture | OS shutter | 10 | **A** OS |
| 62 | Crop confirm | `CONFIRM` | 7 | **A** |
| 63 | Dialog appear | none | 8 | **A** |
| 64 | Dialog confirm destructive | `REJECT` then confirm? | 6 | one `CONFIRM` on yes |
| 65 | Snackbar appear | none | 8 | **A** |
| 66 | Time picker | OS ticks | 10 | **A** if we ever date |
| 67 | Color select | tick | 6 | **A** light |
| 68 | Project move | `CONFIRM` | 7 | **A** |
| 69 | Create project | `CONFIRM` | 8 | **A** |
| 70 | Rename | none | 8 | **A** |
| 71 | Search verb run (`pin`) | `CONFIRM` | 8 | **A** |
| 72 | Command palette select | tick | 7 | **A** |
| 73 | Rail destination | tick | 4 | **R** frequent |
| 74 | Large screen pane focus | none | 7 | **A** |
| 75 | Fold / unfold recreate | none | 8 | **A** |
| 76 | Battery saver (reduce) | respect | 10 | **A** |
| 77 | Accessibility disable haptic | respect | 10 | **A** |
| 78 | TalkBack | no extra haptic spam | 10 | **A** |
| 79 | Game-like rumble | never | 10 | **R** |
| 80 | Notification of reminder | system channel | 8 | **A** later |
| 81 | Incoming (N/A) | — | — | — |
| 82 | Clock alarm | N/A | — | — |
| 83 | Fingerprint | OS | 10 | **A** |
| 84 | Error buzz 300ms | legacy | 10 | **R** |
| 85 | One-shot 50ms | legacy | 8 | **R** prefer constants |
| 86 | Rich "expand" on first note | Sampler expand | 6 | **F** once-ever maybe |
| 87 | Rich "bounce" | Sampler | 3 | **R** |
| 88 | Rich "wobble" | Sampler | 2 | **R** |
| 89 | Resist on over-delete | Sampler resist | 5 | **F** |
| 90 | Keyboard haptic conflict | — | 10 | never double |
| 91 | Stylus button | manufacturer | 7 | **A** if present |
| 92 | Palm reject no haptic | — | 10 | **A** |
| 93 | Multi-touch accident | none | 8 | **A** |
| 94 | Autofill | OS | 10 | **A** |
| 95 | Clipboard overlay | OS | 10 | **A** |
| 96 | Screenshot | OS | 10 | **A** |
| 97 | Screen recording | none extra | 8 | **A** |
| 98 | Low storage save fail | `REJECT` | 8 | **A** |
| 99 | Encryption lock (if any) | `CONFIRM` | 7 | **A** |
| 100 | Anything on every keystroke | — | 10 | **R** absolutely |

## Design guidelines we accept from Google (rated 10)

1. Favor clear/rich over buzzy.
2. Correlate strength with importance and inverse frequency.
3. Be consistent with the system constants.
4. Co-design visual + haptic. Out-of-sync feels broken.
5. Never legacy one-shots for clicks.

## Where to use which component (quick map)

| Need | Component | File |
|---|---|---|
| Filter home | Connected `ButtonGroup` / `ToggleButton` | 03, 04 |
| Create | `SplitButton` or FAB menu | 03, 04 |
| Editor tools | `HorizontalFloatingToolbar` | 03, 05 |
| Overflow | Expressive `DropdownMenu` | 03 |
| Search | Full-screen search + `SearchBar` | 04 |
| Destructive | `AlertDialog` expressive | 03 |
| Undo | Snackbar | 07 |
| Settings | List + switch, not a tab | 04 |
| Large screen | `NavigationRail` + two pane | 04 |
| Widget | Glance | 09 |

## Color × meaning (preview; file 08 owns the palette)

| Meaning | Color role | Haptic |
|---|---|---|
| Pin | Tertiary container | CONFIRM |
| Destructive | Error | REJECT |
| Sync ok | On-surface variant, not green party | none |
| Offline | Outline | none |
| Lock | Secondary | CONFIRM |
| Ink default | On-surface (true ink) | — |

## Accept / reject

**Accept:** shared element, predictive back, checkbox joy, pin, delete+undo, search expand, IME-linked toolbar, clear haptics on rare confirms, no haptic on scroll/type/stroke/open.

**Reject:** pull-to-refresh as ritual, splash, carousel, confetti, rumble, per-keystroke, per-stroke, shimmer home, story rings.
