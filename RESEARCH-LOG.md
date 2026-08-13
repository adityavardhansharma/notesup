# Research log — 2026-08-14

This is what was actually read, not inferred. Every BUILD.md change after this date must cite a row here.

## Pages opened and used

| Source | URL | What it forced |
|---|---|---|
| M3 duration tokens | https://m3.material.io/styles/motion/easing-and-duration/tokens-specs | short1=50, short2=100, short3=150, short4=200, medium1=250, medium2=300, medium3=350, medium4=400, long1=450, long2=500. Selection controls = 200ms Standard. FAB→sheet = 400ms Emphasized. |
| M3 easing | same + applying-easing page | emphasized.decelerate = cubic-bezier(0.05, 0.7, 0.1, 1.0). emphasized.accelerate = cubic-bezier(0.3, 0.0, 0.8, 0.15). Emphasized itself is path-based on Android; CSS fallback is Standard. |
| M3 Expressive intro | https://m3.material.io/blog/building-with-m3-expressive | New/updated components: button groups, split button, loading indicator, FAB menu, toolbars. |
| M3 toolbars | https://m3.material.io/components/toolbars/overview + 9to5Google 2025-05-18 | Docked = global. Floating = contextual. **Do not show toolbar and navigation bar together.** |
| M3 split button | https://m3.material.io/components/split-button/overview | Overflow control **spins and changes shape** when opened. Use the component, do not invent a chevron FAB. |
| M3 navigation bar | https://m3.material.io/components/navigation-bar | **Baseline navigation bar is no longer recommended.** Confirms no 5-tab bar. |
| Compose shared elements | https://developer.android.com/develop/ui/compose/animation/shared-elements | Card→editor is **sharedBounds** (container transform). sharedElement only if the pixels are the same (thumb). Keys: data class, not raw strings. Modifier order must match. Shape morph is **not** automatic. No shared-element through Dialog/ModalBottomSheet AndroidView. |
| Predictive back progress | https://developer.android.com/develop/ui/compose/system/predictive-back-progress | `PredictiveBackHandler` + `Flow<BackEventCompat>`: progress 0→1, touchX/Y. On cancel: catch `CancellationException`, reset, **rethrow**. Official sample scales `1 - progress`. |
| Predictive back setup | https://developer.android.com/guide/navigation/custom-back/predictive-back-gesture | `enableOnBackInvokedCallback=true`. Compose `PredictiveBackHandler`. Single-responsibility callbacks. Do not intercept IME. |
| Haptics principles | https://developer.android.com/develop/ui/views/haptics/haptics-principles | Less is more. Clear > rich > never buzzy for UI. No one-shot 300ms. 10–20ms clicks. Strength ∝ importance, ∝ 1/frequency. |
| Haptic feedback how-to | https://developer.android.com/develop/ui/views/haptics/haptic-feedback | **Preferred: `View.performHapticFeedback` + `HapticFeedbackConstants`.** No `VIBRATE` permission. CONFIRM vs REJECT. Do not use `createOneshot`. |
| Widgets overview | https://developer.android.com/develop/ui/views/appwidgets/overview | Types: information, collection, control, hybrid. Gestures: tap + vertical swipe only. |
| Widget quality TIER 1 | https://developer.android.com/docs/quality-guidelines/widget-quality | Fill all 4 grid edges. Support 2×2 / 4×1 / 4×2. **Use system (OEM) corner radius — do not set 24dp yourself.** Dynamic + dark/light. Unique name + description. Preview image. Header icon if scrolling. System config. System launch transition. 48dp targets. |
| Canonical widget layouts | https://developer.android.com/design/ui/mobile/guides/widgets/layouts | Map ours: New note = Toolbar. Pinned = Text+image. Recent/Project = Text+image list. Glance samples on android/platform-samples. |
| Glance | https://developer.android.com/develop/ui/compose/glance | Compose-runtime for widgets. **Not interoperable with normal Compose UI widgets.** |
| Material 3 in Compose | https://developer.android.com/develop/ui/compose/designsystems/material3 | Official type ramp. **bodyLarge is 16/24, not 18/28.** titleMedium 16/24. headlineMedium 28/36. displaySmall 36/44. extraLarge shape default 24dp. Dynamic color API 31+. Tonal elevation. Reply sample. Expressive is the current Compose M3 story. |
| Convex Android | https://docs.convex.dev/client/android/overview + quickstart | ConvexClient / subscribe Flow / mutation. Clerk via clerk-convex-kotlin. Flavor URLs. Client is `open`. Numbers/`_id` gotchas. |
| Convex + Clerk (backend) | https://docs.convex.dev/auth/clerk | **Required `convex/auth.config.ts`** with `domain: CLERK_JWT_ISSUER_DOMAIN` and **`applicationID: "convex"`**. Activate Clerk’s Convex integration (JWT template named `convex`). Dev vs prod issuer URLs. |
| clerk-convex-kotlin README | https://github.com/clerk/clerk-convex-kotlin | Factory is **`createClerkConvexClient(deploymentUrl, context)`**. Auth state on `ConvexClientWithAuth.authState`. Workout sample. |
| Clerk Android quickstart | https://clerk.com/docs/android/getting-started/quickstart | **Must enable Native API** in Clerk dashboard. Packages: `com.clerk:clerk-android-api` + optional `clerk-android-ui`. We take **api only**. `Clerk.initialize(this, pk)`. combine `isInitialized` + `userFlow`. Hosted / custom. Native API bypasses CAPTCHA. |
| Clerk Android README (custom UI) | https://github.com/clerk/clerk-android | **API-only** artifact is the official custom-UI path. `SignIn.create` strategies: EmailCode, Password, Passkey. `authenticateWithRedirect` / `signInWithOAuth(GOOGLE)`. `signInWithPasskey` + `setActive`. |
| Clerk custom email OTP / OAuth / passkey | https://clerk.com/docs/reference/native-mobile/auth.mdx + passkeys custom-flow | `signInWithOtp` / `verifyCode`. `signInWithOAuth(OAuthProvider.GOOGLE)`. `signInWithPasskey`. Combined sign-in/up via transfer. |
| Google Credential Manager SIWG | https://developer.android.com/identity/sign-in/credential-manager-siwg | Ship **both** the system account sheet **and** a distinct Continue with Google button. Never WebView. Never legacy Play Services sign-in. |
| DesignerUp 200 onboarding flows | https://designerup.co/blog/i-studied-the-ux-ui-of-over-200-onboarding-flows-heres-everything-i-learned/ | Onboarding ≠ login. First screen is “will this work for me,” not account fields. |
| NN/g + 2026 onboarding | skip-when-possible; always let users skip; guest-checkout logic | Carousels lose. Skip is the primary. Experience value before account. |
| Authgear 2026 login UX | https://www.authgear.com/post/login-signup-ux-guide/ | If already authenticated, skip login. Long-lived session. First-run must not re-ask. |
| Android 12 splash | https://developer.android.com/develop/ui/views/launch/splash-screen | System splash is mandatory. Design it as paper (`windowBackground` = `background`). Not a second logo screen. |
| ROLE_NOTES / CREATE_NOTE | https://developer.android.com/develop/ui/views/touch-and-input/stylus-input/create-a-note-taking-app | Lock = full screen + new note unless unlocked consent. Unlocked role = floating. `EXTRA_USE_STYLUS_MODE`. Content capture **only** in bubble + role held. `requestDismissKeyguard`. Multi-instance. |
| List-detail adaptive | https://developer.android.com/develop/adaptive-apps/guides/list-detail | `NavigableListDetailPaneScaffold`. We lock `PopUntilContentChange`. Two-pane at width ≥ 600 and height ≥ 480. |
| BiometricPrompt | https://developer.android.com/identity/sign-in/biometric-auth | `BIOMETRIC_STRONG or DEVICE_CREDENTIAL`. No negative button with device credential. `setConfirmationRequired(false)`. CryptoObject for AES-GCM. Enroll via `ACTION_BIOMETRIC_ENROLL`. |
| Glance previews | https://developer.android.com/develop/ui/compose/glance/generated-previews + create-app-widget | `previewImage` + `providePreview` (API 35+). `initialLayout` loading. Configure activity for project widget. |
| Photo Picker | Android developer Photo Picker | No `READ_MEDIA_IMAGES`. `PickVisualMedia` max 10. Camera via `TakePicture` cache, no CAMERA perm. |
| Play privacy / Data safety | Play Console + in-app policy practice | In-app “What syncs” is required premium honesty. Store listing still needs a hosted URL of the same text. |
| NoteApps.info 2026 | https://noteapps.info/best_note_taking_apps_2026 | Aesthetic cluster: Craft, Reflect, Obsidian, Milanote, Capacities. Feature-count is a trap. |
| NotallyX | https://github.com/Crustack/NotallyX | Real Android notes DNA: rich text, tasks, pin, color, list/grid, widget, biometric lock, undo/redo, almost no permissions. Reminders exist — we still keep them out of v1. |
| Samsung Notes 2026 forum | us.community.samsung.com grid-layout thread | Date-grouped grid created **negative space and user revolt**. Do not group the All-grid by date. Date headers belong on Recent **list** only. |
| Zapier / PCMag 2026 note roundups | zapier.com/blog/best-note-taking-apps, pcmag | Confirms Keep=speed, Apple=mixed, Obsidian=power, Craft=polish. |

## Official numbers that replace invented ones

### Motion (M3 tokens — use these names in code)

| Token | ms | Notesup use |
|---|---:|---|
| short1 | 50 | press/ripple only |
| short2 | 100 | icon fill start |
| short3 | 150 | check, snackbar enter, search field collapse commit |
| short4 | 200 | **selection controls**, create-note IME path, sheet exit, back *commit remainder*, pill, FAB-menu items |
| medium1 | 250 | hardware-back full play (was 240) |
| medium2 | 300 | search expand |
| medium3 | 350 | unused unless a large shared-bounds needs it |
| medium4 | 400 | **FAB/split → any sheet**, container transform card↔editor |
| long1 | 450 | unused in v1 |
| long2 | 500 | unused in v1 |

Easing locked to official:

- On-screen / shared bounds: `MotionScheme` emphasized (path) — do not hand-roll `cubic-bezier(0.2,0,0,1)` if `MaterialTheme.motionScheme` exists.
- Enter (sheet): emphasized.decelerate `(0.05, 0.7, 0.1, 1.0)`
- Exit (sheet): emphasized.accelerate `(0.3, 0.0, 0.8, 0.15)`

### Type (M3 ramp — then one product override)

| Role | Official | We use |
|---|---|---|
| displaySmall | 36/44 | empty wordmark |
| headlineMedium | 28/**36** | editor title (was 28/34 — **wrong**) |
| titleLarge | 22/28 | app bar |
| titleMedium | 16/**24** | card title (was 16/22 — **wrong**) |
| titleSmall | 14/20 | sheets |
| bodyLarge | **16/24** | UI chrome only |
| **bodyNote** | *not in M3* | **18/28** — product override for the editor paragraph only. Do not rename it bodyLarge. |
| bodyMedium | 14/20 | list preview |
| bodySmall | 12/16 | card preview |
| labelLarge | 14/20 | pills |
| labelMedium | 12/16 | |
| labelSmall | 11/16 | meta (was 11/14) |

### Shape

Use M3 `Shapes`: extraSmall 4, small 8, medium 12, large 16, extraLarge **24**. Cards = `extraLarge`. Do not invent a parallel shape table.

### Widgets — corrections

Previous BUILD said radius 24 to match cards. **TIER 1 forbids custom widget corner radius.** Use `androidx.glance.appwidget` / `GlanceModifier.cornerRadius` from **system** (`RoundedCornerCompat` / OEM provided). Fill all 4 edges. Unique descriptions. Preview drawables required.

### Back — corrections

Do not invent a second animation system that fights shared bounds.

**Locked implementation:**

1. `SharedTransitionLayout` at Activity root.
2. Card and editor root: `Modifier.sharedBounds(rememberSharedContentState(NoteBoundsKey(noteId)))` with **identical modifier order**: clip/background **after** sharedBounds on both sides? Official: size-affecting modifiers after sharedBounds, padding **before** must match. Match exactly.
3. Thumb image (if any): `sharedElement(NoteImageKey(noteId))`.
4. `PredictiveBackHandler` on editor:
   - collect `backEvent.progress` into `backProgress`
   - drive the shared transition seek / overlay scale with `progress` (official sample: scale = 1 - progress for a leaving surface; we **seek** the sharedBounds instead of a fake 0.92 scale if Navigation/SharedTransition supports seeking; if not, apply official sample scale on the editor overlay **in addition to** sharedBounds — never instead of it)
   - completion: pop
   - cancel: reset `backProgress=0`, rethrow `CancellationException`
5. No haptic.
6. IME: do not register a handler; let system hide it first.

Commit threshold stays 0.32 or fast flick — that part is ours; M3 does not specify a notes-app threshold.

### Clerk / Convex — corrections

Previous BUILD missed required dashboard + backend files:

1. Clerk Dashboard → **enable Native API**.
2. Clerk Dashboard → **activate Convex integration** (JWT template `convex`).
3. Hosted auth: register **namespace + applicationId/package** `com.notesup.app`.
4. File `convex/auth.config.ts` with `applicationID: "convex"` and `domain: process.env.CLERK_JWT_ISSUER_DOMAIN`.
5. Android deps: `com.clerk:clerk-android-api`, `com.clerk:clerk-android-ui`, `com.clerk:clerk-convex-kotlin`.
6. Client factory: **`createClerkConvexClient(deploymentUrl, context)`** (current README).
7. Do **not** block first frame on `Clerk.isInitialized`. Home paints from Room. Auth state only affects avatar + sync worker.

### What we will not change despite research

- No navigation bar (now officially aligned with Expressive).
- No auth wall (Clerk quickstart shows a wall; we override for product).
- Editor body 18/28 (M3 default 16/24 is too small for a writing app; Bear/iA/Apple all go larger). Named `bodyNote`.
- Projects not folders (Things 3 / Craft spaces still win).
- No date-grouped All-grid (Samsung users documented this as a regression).

## Apps / UIs that still stand after this pass

Craft cards, Apple mixed blocks, Bear/iA type, Drafts capture, Things projects, Keep speed (structure), NotallyX honesty + lock + widget, INKredible ink, reMarkable restraint, Expressive split/toolbar/pills, Clock/Drafts widgets, Copilot haptic discipline.

Samsung Notes ink depth is a ceiling, not a chrome model. Notion remains a reject on phone.
