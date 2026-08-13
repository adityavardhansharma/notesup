# 15 — Open source, libraries, Android platform

Implementation inventory. Still no app code. This is what we will reach for when coding starts.

## Android-native notes we can read as code

| Project | Why open it | Steal | Don't steal | Score |
|---|---|---|---|---:|
| **Notally** (OmGodse) | Best small Material notes | Backup, labels, no-permission ethos | Visual ceiling | 9 |
| **NotallyX** | Maintained fork | Tasks, export | — | 9 |
| **Quillnote** | Compose + Material You + markdown | Theme, architecture | Editor power | 8 |
| **Fossify Notes** | Widgets, simplicity | Widget jobs | Too simple | 6 |
| **Omni Notes** | Attachments, maturity | File handling | XML UI | 6 |
| **Markor** | File-based md | Export honesty | Not a gallery | 7 |
| **NeutriNote** | Local power | — | Density | 5 |
| **Notesnook** | E2EE product | Encryption thinking | RN complexity | 6 |
| **Joplin Android** | Sync models | Conflict humility | UI | 6 |
| Dozens of "NoteApp Jetpack Compose" student repos | Room+VM wiring | Folder structure | Tutorial look | 4 |

GitHub `note-taking` topic: 3,504 repos. Stars do not equal phone UI. AppFlowy (75k), Memos (62k), Joplin (56k), SiYuan (46k), Logseq (44k), TriliumNext (37k) are mostly **desktop or web souls**. Read them for IA, not for Compose.

Other OSS worth a glance: FSNotes (Swift file-speed), Lorien (ink joy), Excalidraw/tldraw (stroke model ideas), MohamedRejeb compose-rich-editor, Foam/nb (files).

## Libraries accepted for v1 consideration

| Need | Library | Notes | Score |
|---|---|---|---:|
| UI | Compose + Material3 + Expressive opt-in | Platform | 10 |
| Images in UI | Coil 3 | Exact size, disk cache | 10 |
| Navigation | Navigation Compose (type-safe routes) | Few routes | 9 |
| DI | Hilt or Metro/Koin — pick one, stay | Hilt is default Android | 8 |
| DB | Room + ksp | Truth | 10 |
| JSON | kotlinx.serialization | Convex + blocks | 10 |
| Lifecycle collection | `collectAsStateWithLifecycle` | Never raw collect | 10 |
| Async | coroutines + Flow | | 10 |
| Rich paragraph | **compose-rich-editor** (MohamedRejeb) | Inside a block only | 8 |
| Markdown export | Custom serializer from blocks; Markwon if preview | Don't edit as md | 8 |
| PDF | Android `PdfDocument` / PrintedPdf or iText-ish | Keep deps thin; system print as fallback | 7 |
| Images load/save | Coil + our downsampler | | 9 |
| Ink | Jetpack Ink if minSdk/API ok; else Compose Canvas | Decide at implement | 8 |
| Auth | Clerk Android + clerk-convex-kotlin | Official | 10 |
| Sync | android-convexmobile | Official | 10 |
| Biometric | AndroidX Biometric | Lock | 9 |
| Widgets | Glance | | 10 |
| Window size | material3-adaptive / WindowSizeClass | Rail, two-pane | 9 |
| Accompanist | Only if something still not in platform | Prefer AndroidX | 5 |
| Logging | Timber debug only | Never note bodies in release | 7 |
| Crash | Later | | 5 |

## Libraries / approaches rejected

| Thing | Why |
|---|---|
| WebView editor (TinyMCE, Quill, ProseMirror in a WebView) | Not native, not Expressive, keyboard hell |
| Flutter modules | Wrong app |
| RN / Expo | Wrong app |
| Firebase Auth / Firestore | User chose Clerk + Convex |
| Convex Auth | User chose Clerk |
| Auth0 Android | Clerk |
| Realm as second DB | Room is enough |
| SQLDelight *and* Room | Pick Room |
| Orbit/MVI library required | Sealed StateFlow is enough |
| Compose Destinations code-gen if it fights us | Simple routes |
| Lottie everywhere | Cheap motion |
| Custom icon font | Material Symbols |
| LeakCanary in release | — |
| Google Play Instant / minigames | — |

## Platform docs rated (Android)

| Doc | Importance | Use |
|---|---:|---|
| Design & Plan / Design your UI | 8 | Process |
| Compose conceptual | 10 | How we build |
| Material 3 in Compose | 10 | Theme |
| Expressive samples (ButtonGroup, SplitButton, FloatingToolbar) | 10 | Home + editor |
| Architecture (UI layer, data layer, VM) | 10 | File 10 |
| UDF | 10 | |
| Haptics principles + feedback + APIs | 9 | File 07 |
| App widgets overview | 9 | File 09 |
| Glance | 9 | Widgets |
| Edge-to-edge / insets | 9 | Scaffold |
| Predictive back | 8 | Editor |
| Shared element / bounds in Compose | 9 | Card open |
| WindowSizeClass / canonical layouts | 8 | Foldables |
| Biometric | 8 | Lock |
| DataStore | 7 | Settings flags |
| CameraX / photo picker | 8 | Image insert (prefer system photo picker) |
| ContentReceiver / rich content IME | 6 | GIFs later, not v1 pride |
| Android Ink / stylus | 8 | File 06 |
| Accessibility | 8 | |
| WorkManager | 7 | Mutation queue / uploads |
| Security / EncryptedFile | 8 | Locked notes at rest |
| Play feature delivery | 2 | No |

## Kotlin language docs rated

| Topic | Importance | Notesup use |
|---|---:|---|
| Sealed classes / interfaces | 10 | Blocks, UI, sync |
| Data classes | 9 | Note snapshots |
| Null safety | 10 | |
| Value / inline classes | 8 | IDs |
| Coroutines basics | 10 | |
| Flow / StateFlow / SharedFlow | 10 | |
| Channels | 4 | Rare |
| Serialization guide | 9 | |
| Coding conventions | 8 | |
| Collections / buildList | 8 | Block edits |
| Context parameters | 4 | Optional later |
| Multiplatform | 3 | Android only |
| Compose compiler notes | 7 | Stability of Note |

## Clerk + Convex platform notes

See files 11 and 12. Combined stack we accept:

```
Application.onCreate
  Clerk.initialize
  ClerkConvexAuthProvider → ConvexClientWithAuth
  Room
  WorkManager (queue)

debug/release resValue convex_url
INTERNET permission
minSdk 26
```

## Ink / editor OSS specifically

| Project | Role | Score |
|---|---|---:|
| MohamedRejeb/compose-rich-editor | Inline styles, HTML/MD | 8 |
| Compose `BasicTextField` v2 | Caret control | 9 |
| Markwon | Render only | 7 |
| Android Ink / androidx ink | Strokes | 9 if usable |
| Perfect-freehand ports | Nice curves | 7 |
| Excalidraw scene format | Too much | 4 |
| Lorien | Desktop ink | 5 |
| MyScript / Nebo SDKs | Convert later, likely paid | 5 |
| Samsung S-Pen SDK | Optional | 5 |

**Accepted plan:** custom block list + rich-editor paragraph + our canvas/Ink block. Do not wait on a mythical "Compose Notion editor" package.

## Widget implementation notes (Glance)

- Separate `glance` source set / module if the app grows
- Sizes: 2x1, 2x2, 4x2, 4x3, 4x4
- Configure project widget with a project id
- Taps deep-link: `notesup://new`, `notesup://note/{id}`
- Update on Room write via Glance `update` — do not wait for Convex

## Permissions (premium is stingy)

| Permission | When |
|---|---|
| INTERNET | Sync / auth |
| USE_BIOMETRIC | Lock |
| Notifications | Only if reminders ever ship |
| Camera | Only when user picks camera |
| Photos | Photo picker (no broad READ_MEDIA if we can avoid) |
| Vibration | Haptics use feedback APIs, not VIBRATE if possible |

Notally's "almost no permissions" is a trust signal. Copy the ethic.

## Performance budget (UX)

- Home first frame < 200ms on a mid Pixel-class device with 200 notes
- Open note < 250ms local
- Scroll grid 120Hz-capable devices without bind hitch
- Image bind uses precomputed thumbs
- Ink unfocused = bitmap
- Convex must never be on the first-frame path

## What to read the week we start coding (in order)

1. File 14 (this product)
2. File 10 (Kotlin/Compose house style)
3. Compose M3 + Expressive samples
4. Convex Android overview
5. Clerk Android + clerk-convex-kotlin
6. NotallyX + Quillnote source, one evening each
7. Haptics principles
8. Glance getting started
9. Shared element + predictive back samples
10. Photo picker + PdfDocument

## Accept / reject

**Accept:** Room, Compose, Expressive, Coil, Hilt, Glance, Clerk official, Convex official, compose-rich-editor as a paragraph engine, WorkManager queue, system photo picker.

**Reject:** WebView editors, second cloud, folder of experimental UI kits, permission maximalism, tutorial architecture left in the project.
