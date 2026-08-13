# NOTESUP — COMPLETE BUILD SPEC

**This is the only file you need to build the app.**  
**How it looks:** [PANE.md](PANE.md) → every `ui/` file.  
If another research file disagrees with this one, **this file wins** on product. **PANE / `ui/` win on look** unless this file cites an official token.  
Every decision below is **locked**. Do not invent a second option. Do not ask the product owner.

Numbers and APIs below were corrected against official docs on 2026-08-14 and against [FEEDBACK.md](FEEDBACK.md) / [feedback2.md](feedback2.md) on 2026-08-14. Source table: [RESEARCH-LOG.md](RESEARCH-LOG.md). Do not “improve” a token back to a round number that is not in that log.

**Look:** [PANE.md](PANE.md) (`ui/00`–`ui/22`). **Feel:** [UX-PEOPLE-GO-TO-WAR-FOR.md](UX-PEOPLE-GO-TO-WAR-FOR.md).

No application code is in this repo yet. When you write code, you implement *this*, in the order at the bottom.

---

## 0. What you are building

**Name:** Notesup  
**One sentence:** The Android notes app you can actually write in — a warm page that opens instantly, holds text, images, tables and real ink in one document, and never asks who you are.

**Package:** `com.notesup.app`  
**Namespace:** `com.notesup.app`  
**Application class:** `com.notesup.app.NotesupApp`  
**Single Activity:** `com.notesup.app.MainActivity`  
**Launcher label:** `Notesup`  
**minSdk:** 26  
**targetSdk:** 36  
**compileSdk:** 36  
**JDK / Kotlin jvmTarget:** 17  
**Language:** Kotlin only. No Java sources.  
**UI:** Jetpack Compose only. No XML layouts except `AndroidManifest`, Glance XML-free Glance, and widget metadata.  
**Auth:** Clerk official Android SDK.  
**Sync:** Convex official Android client + `clerk-convex-kotlin`.  
**Local truth:** Room.  
**Design system:** Material 3 Expressive.

You will not add a fifth tab. You will not add an auth wall. You will not add Notion databases. You will not use `#FFFFFF` or `#000000` as surfaces.

**Feel (full argument: [UX-PEOPLE-GO-TO-WAR-FOR.md](UX-PEOPLE-GO-TO-WAR-FOR.md)):**

1. No skeleton loaders. No home spinner. No toast except `Note deleted` + Undo.
2. Caret starts in the **body**. Title is optional and never blocking.
3. Create never asks which project. **Drag the SplitButton onto a project row** to create there (Things Magic Plus).
4. Focus: Off / **Auto** (default, chrome fades after 2 s) / **Sentence** (other paragraphs **62%** opacity) / Typewriter (caret at 38% height). Selection pauses dim + typewriter. A11y/high-contrast: Sentence off.
5. Search: first keystroke filters, debounce ≤ 50 ms.
6. Checked items do **not** jump to the bottom unless Settings `Sort checked to end` is on (default off).
7. Healthy sync = no icon.
8. Empty states are one sentence (`Write anything.` / `Nothing matches.`). No Lottie, no carousel. First-run is the one-screen Welcome in [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md), then that empty.
9. No template picker. No notebook modal on create.

---

## 1. Locked product rules (non-negotiable)

1. Creating a note never waits on the network or on Clerk.
2. First home frame is painted from Room.
3. Auth is skippable forever. First-run is **Welcome** then optional custom Clerk — [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md). Not an auth wall. Not `AuthView`.
4. A note lives in Inbox or in exactly one Project.
5. Pins are an overlay, not a location.
6. The editor is a vertical list of blocks, not a canvas, not one giant EditText.
7. Default capture is a blank text note. Keyboard in ≤ 200 ms after the create animation starts.
8. Dynamic color is ON. Harmonize toward the seed so wallpaper neon cannot win.
9. Icons are Material Symbols Rounded only. Weight 400. Optical size 24. Grade 0. Fill 0 outlined / fill 1 selected.
10. Haptics: `HapticFeedbackConstants` by default. Never `createOneShot`. **Three** events may use `VibrationEffect` primitives if supported (new note, checkbox on, magic-plus drop); else the same constants.
11. Predictive back is ON (`android:enableOnBackInvokedCallback="true"`).
12. English UI strings in v1. `res/values/strings.xml` only. No hardcoded user-visible English in Kotlin except debug logs.

---

## 2. Module and file tree (create exactly this)

```
app/
  src/main/
    AndroidManifest.xml
    java/com/notesup/app/
      NotesupApp.kt
      MainActivity.kt
      NoteCaptureActivity.kt
      di/           AppModule.kt
      ui/
        theme/      Color.kt Type.kt Shape.kt Motion.kt Theme.kt PaperGrain.kt
        common/     NotesupIcons.kt NotesupHaptics.kt PredictiveBack.kt SharedNoteBounds.kt EmptyHome.kt
        common/expressive/  NotesupSplitCapture.kt NotesupFilterPills.kt NotesupEditorToolbar.kt
        home/       HomeScreen.kt HomeViewModel.kt HomeUiState.kt NoteCard.kt NoteListRow.kt
        search/     SearchScreen.kt SearchViewModel.kt
        editor/     EditorScreen.kt EditorViewModel.kt EditorUiState.kt BlockList.kt blocks/*.kt FormatToolbar.kt InkCanvas.kt
        account/    AccountSheet.kt AuthScreen.kt AuthCodeScreen.kt SettingsScreen.kt
                    AppearanceScreen.kt TypeSettingsScreen.kt PaperSettingsScreen.kt
                    FocusSettingsScreen.kt TrashScreen.kt ManageAccountScreen.kt
                    PrivacyScreen.kt AboutScreen.kt
        onboarding/ WelcomeScreen.kt
        project/    ProjectScreen.kt ProjectViewModel.kt ProjectEditSheet.kt
        media/      ImageSourceSheet.kt Lightbox.kt
        lock/       LockGateScreen.kt
        capture/    CaptureViewModel.kt
        navigation/ NotesupNav.kt Routes.kt
      domain/
        model/      Ids.kt Note.kt Block.kt Project.kt RichText.kt InkId.kt
        op/         NoteOps.kt SearchQuery.kt
      data/
        local/      NotesupDb.kt NoteDao.kt ProjectDao.kt MediaDao.kt SyncQueueDao.kt entities/*.kt
        remote/     ConvexApi.kt ConvexDto.kt ConvexMappers.kt
        auth/       AuthRepository.kt
        repo/       NoteRepository.kt ProjectRepository.kt MediaRepository.kt
        export/     MarkdownExport.kt PdfExport.kt
      widget/       NewNoteWidget.kt PinnedWidget.kt RecentWidget.kt ProjectWidget.kt
      work/         SyncWorker.kt UploadWorker.kt
    res/
      values/       strings.xml themes.xml
      mipmap-*/     ic_launcher, ic_launcher_round, ic_launcher_foreground
      xml/          file_paths.xml backup_rules.xml data_extraction_rules.xml shortcuts.xml
      drawable/     ic_*.xml — **only** hand-exported Material Symbols Rounded (see §3 icons)
      font/         roboto_flex_variable.ttf literata.ttf jetbrains_mono.ttf atkinson_hyperlegible.ttf
convex/             schema.ts notes.ts projects.ts users.ts media.ts ink.ts http.ts auth.config.ts
```

Extra modules in v1: `:baselineprofile`, `:macrobenchmark` (F-12). Not feature surface.

---

## 3. Gradle (locked)

**Root plugins:** Android application, Kotlin Android, Kotlin compose, Kotlin serialization, KSP, Hilt.

**App dependencies (exact roles, use current stable that satisfies these):**

| Lib | Why |
|---|---|
| Compose BOM | UI |
| **material3 `1.5.0-alphaXX`** (pin exact at implement; **not 1.4.0**) | Expressive lives only on the 1.5.0-alpha line. 1.4.0 stable is the non-Expressive baseline. Re-verify every bump. |
| material3-adaptive + adaptive-layout + adaptive-navigation | `NavigableListDetailPaneScaffold` ([ui/21](ui/21-LARGE-SCREEN.md)) |
| **No `material-icons-extended`.** No third-party Symbols artifact. | Export ~60 Rounded 24/400/0 SVGs → `res/drawable/ic_*` → `NotesupIcons`. Only path. |
| navigation3-ui **1.1.6** (latest 1.1.x stable) | Nav3, predictive back |
| lifecycle-viewmodel-compose, runtime-compose | |
| hilt-android + hilt-navigation-compose | |
| room-runtime + room-ktx + room-compiler (ksp) | |
| kotlinx-serialization-json | |
| kotlinx-coroutines-android | |
| coil-compose | |
| work-runtime-ktx + hilt-work | |
| biometric | |
| glance-appwidget + glance-material3 | |
| `dev.convex:android-convexmobile:0.8.0@aar` `{ isTransitive = true }` | |
| `androidx.ink:ink-authoring-compose:1.0.0` + brush + strokes + storage + rendering | **stable** drawing |
| `androidx.navigation3:navigation3-ui:1.1.6` (or latest 1.1.x stable) | Nav3, predictive back built-in |
| `androidx.datastore:datastore-preferences` | `onboarding_done`, `sync_paused`, `theme`, `installId` |
| `androidx.core:core-splashscreen` | Paper-coloured system splash only. No custom logo. |
| `com.clerk:clerk-android-api` **only** | custom Compose auth. **Do not** add `clerk-android-ui` / `AuthView` |
| `com.clerk:clerk-convex-kotlin` | `createClerkConvexClient` |
| compose-rich-editor (MohamedRejeb) **only inside Paragraph block** | |

**Manifest:**

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.USE_BIOMETRIC"/>
<application
    android:name=".NotesupApp"
    android:allowBackup="true"
    android:dataExtractionRules="@xml/data_extraction_rules"
    android:fullBackupContent="@xml/backup_rules"
    android:enableOnBackInvokedCallback="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/Theme.Notesup">
```

Photo access: **Android Photo Picker only.** No `READ_MEDIA_IMAGES`. Camera: `ActivityResultContracts.TakePicture` to app cache; no CAMERA permission if you use the system capture intent. Prefer Photo Picker + optional `TakePicture`.

Deep links:

- `notesup://new`
- `notesup://new?type=checklist|ink|image`
- `notesup://note/{uuid}`
- `notesup://project/{uuid}`
- `notesup://search?q=`

**`data_extraction_rules.xml` / `backup_rules.xml` (v1 — F-11a):** exclude `notesup.db` from cloud backup **and** device transfer. Keystore-bound lock keys cannot restore. Honest: locked notes do not migrate in v1. Passphrase-derived lock keys = v1.1.

**Share target + launcher shortcuts (F-22):** `res/xml/shortcuts.xml` — static shortcuts New note / New checklist / New drawing. `<share-target>` so share-sheet can land a note (text/plain, image/*). Reuse the deep links above. No Quick Settings tile in v1.

**`NoteCaptureActivity` (F-07, step 15b):**

```xml
<activity android:name=".NoteCaptureActivity"
    android:exported="true"
    android:showWhenLocked="true"
    android:turnScreenOn="true"
    android:excludeFromRecents="true"
    android:theme="@style/Theme.Notesup">
    <intent-filter>
        <action android:name="android.intent.action.CREATE_NOTE"/>
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
</activity>
```

`EXTRA_USE_STYLUS_MODE == true` → new ink note, canvas focused. Lock-screen launch: **new note only** unless user already consented (while unlocked) to show history. Offer `ROLE_NOTES` from Settings, never first launch. Full chrome: [ui/17-CAPTURE-SHARE.md](ui/17-CAPTURE-SHARE.md). Share-in: new Inbox note, no confirm. Shortcuts: New note / checklist / drawing.

---

## 4. Design tokens — exact

### 4.1 Seed and surfaces (when dynamic color is off, and as harmonization source)

Generate the **full role set** from seed `#8B2942` in [Material Theme Builder](https://material.io/material-theme-builder) and commit the exported `Color.kt`. The table below is the target; if the builder differs by a few tones, **the builder file wins** (official Compose M3 path). Hand-hex is only the seed + paper/ink extras.

| Token | Light hex | Dark hex |
|---|---|---|
| `seed` | `#8B2942` | `#8B2942` |
| `primary` | `#8B2942` | `#FFB2C0` |
| `onPrimary` | `#FFFFFF` | `#55101F` |
| `primaryContainer` | `#FFD9DE` | `#6F1D31` |
| `onPrimaryContainer` | `#3F0013` | `#FFD9DE` |
| `secondary` | `#75565B` | `#E4BDC2` |
| `onSecondary` | `#FFFFFF` | `#43292E` |
| `secondaryContainer` | `#FFD9DE` | `#5C3F44` |
| `onSecondaryContainer` | `#2B1519` | `#FFD9DE` |
| `tertiary` | `#765A00` | `#F0C230` |  /* pin meaning */
| `onTertiary` | `#FFFFFF` | `#3E2E00` |
| `tertiaryContainer` | `#FFE08C` | `#594400` |
| `onTertiaryContainer` | `#241A00` | `#FFE08C` |
| `error` | `#BA1A1A` | `#FFB4AB` |
| `onError` | `#FFFFFF` | `#690005` |
| `errorContainer` | `#FFDAD6` | `#93000A` |
| `onErrorContainer` | `#410002` | `#FFDAD6` |
| `background` | `#F6F1EA` | `#161311` |
| `onBackground` | `#1C1917` | `#F3EDE6` |
| `surface` | `#F6F1EA` | `#161311` |
| `onSurface` | `#1C1917` | `#F3EDE6` |
| `surfaceVariant` | `#EDE6DD` | `#2A2623` |
| `onSurfaceVariant` | `#52443F` | `#D0C3BB` |
| `outline` | `#84746E` | `#998E87` |
| `outlineVariant` | `#D6C3BC` | `#52443F` |
| `scrim` | `#000000` | `#000000` |
| `inverseSurface` | `#312E2B` | `#E8E1DA` |
| `inverseOnSurface` | `#F6F1EA` | `#312E2B` |
| `inversePrimary` | `#FFB2C0` | `#8B2942` |
| `surfaceTint` | `#8B2942` | `#FFB2C0` |
| `surfaceContainerLowest` | `#FFFFFF` @ 40% over paper → treat as `#FBF8F4` | `#110E0C` |
| `surfaceContainerLow` | `#F1EBE3` | `#1E1B18` |
| `surfaceContainer` | `#EBE4DB` | `#231F1C` |
| `surfaceContainerHigh` | `#E5DDD4` | `#2A2623` |
| `surfaceContainerHighest` | `#DFD7CE` | `#322E2A` |

**Dynamic color:** `dynamicLightColorScheme(context)` / `dynamicDarkColorScheme(context)`, then **harmonize** every color toward `seed` using Material Color Utilities `Blend.harmonize(color, seed)`. Never skip harmonize.

**Follow system dark.** No in-app theme override in v1 except a Settings row that writes DataStore `theme = system|light|dark` (default `system`).

**DataStore keys (same Preferences store):** `theme`, `installId`, `onboarding_done` (default false), `sync_paused` (default false, only when signed in). Welcome / skip / session rules: [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md).

**Forbidden:** Keep yellow `#FFF475`, `#FFFFFF` full-bleed, `#000000` OLED, green/red sync dots, rainbow default card tints.

### 4.2 Note tint (optional, user-applied)

Eight hues only. Applied as `surfaceContainer` overlay at **8%** over paper. Never a full candy card.

| Id | Light wash | Meaning |
|---|---|---|
| 0 none | — | default |
| 1 | `#8B2942` @ 8% | seed |
| 2 | `#7A4F2A` @ 8% | clay |
| 3 | `#4E6B3A` @ 8% | moss |
| 4 | `#2F5E63` @ 8% | tide |
| 5 | `#3D4C7A` @ 8% | ink-blue |
| 6 | `#5A4270` @ 8% | plum |
| 7 | `#6B3A3A` @ 8% | rust |

Tint never changes type color. `onSurface` stays.

### 4.3 Type (exact)

Font family: **Roboto Flex variable, bundled** in `res/font/`. Downloadable fonts **cannot** drive variable axes. Fallback: `FontFamily.SansSerif`.

Drive `FontVariation.weight` + `FontVariation.opticalSizing(size)` per role (F-18).

**Bundled (4):** Roboto Flex, Literata, JetBrains Mono, Atkinson Hyperlegible.  
**On-demand (8 static):** remaining FEATURES faces, downloaded **only from Settings**, never in the editor. If missing, paint Flex now.

Do **not** ship Inter. Chrome UI is always Flex even if the note body uses another face.

Title, card titles, selected pills, H1–H3 use **M3 emphasized** type roles when the Expressive theme provides them. Do not hand-set `FontWeight.W600`.

Official M3 ramp ([Compose M3 typography table](https://developer.android.com/develop/ui/compose/designsystems/material3)) plus **one** product override.

| Role | size / line | weight | tracking | Use |
|---|---|---|---:|---|
| `displaySmall` | **36 / 44** (official) | 500 | -0.25 | empty home wordmark |
| `headlineMedium` | **28 / 36** (official; was wrongly 28/34) | 400 | 0 | editor title |
| `titleLarge` | 22 / 28 | 500 | 0 | home app bar |
| `titleMedium` | **16 / 24** (official; was 16/22) | 500 | 0.15 | card title, project name |
| `titleSmall` | 14 / 20 | 500 | 0.1 | sheet titles |
| `bodyLarge` | **16 / 24** official | 400 | 0.15 | chrome, settings, dialogs — **not the editor** |
| `bodyNote` | **18 / 28** | 400 | 0.15 | **editor paragraph only.** Not an M3 role. Do not put this size on cards. |
| `bodyMedium` | 14 / 20 | 400 | 0.25 | list preview |
| `bodySmall` | 12 / 16 | 400 | 0.4 | card preview |
| `labelLarge` | 14 / 20 | 500 | 0.1 | pills, buttons |
| `labelMedium` | 12 / 16 | 500 | 0.5 | badges |
| `labelSmall` | **11 / 16** (official; was 11/14) | 500 | 0.5 | meta, widgets |

Editor title placeholder: `Title` in `headlineMedium`, color `onSurfaceVariant` @ 0.55 alpha.  
Caret color: `primary`.  
Selection color: `primary` @ 0.24 alpha.

Tablet editor max width: **640 dp**, centered (~70 CPL at 18 sp). Phone: full width, horizontal padding **20 dp**.

`bodyNote` / title: `FontFeatureSettings("liga")`. Code: `"liga" off`, `"tnum"` on. Card times and numbered markers: `"tnum"`. Title optical margin: pull **1.5 dp** start vs body.

### 4.4 Shape (exact dp)

Use M3 `Shapes` from Compose (Reply sample defaults) and map:

| M3 role | Value | Notesup use |
|---|---:|---|
| extraSmall | 4 | chips if any |
| small | 8 | tiny thumbs |
| medium | 12 | list leading 40×40, ink inner |
| large | 16 | menus, ink block, image block |
| extraLarge | **24** | **note cards** |
| CircleShape | 50% | pills, checklist ring, avatar |

Sheet / dialog top: **28 dp** (Expressive dialog, not in the 5-step scale — keep).  
SplitButton / FAB: **`SplitButtonDefaults` / `FloatingActionButtonDefaults` shapes only.**  
**Widgets: do not use 24 dp.** Use the **system/OEM corner radius** (widget quality TIER 1 WS-2).

### 4.5 Elevation / shadow

Use **tonal elevation only**. No drop shadows on cards. Cards sit on `surface` with `surfaceContainerLow` fill. FAB uses Expressive default shadow (system). Sheets use scrim `#000000` @ **40%** light / **60%** dark.

### 4.6 Spacing (exact)

| Name | dp |
|---|---:|
| pageX phone | 16 |
| pageX tablet | 24 |
| pageTop under status | 0 (edge-to-edge, app bar handles) |
| gridGap | 10 |
| listRowMinHeight | 72 |
| cardPad | 14 |
| cardThumbMaxH | 120 |
| pinStripH | 168 |
| pillH | 40 |
| pillGap | 0 (connected group — use `ButtonGroupDefaults.ConnectedSpaceBetween`) |
| appBarH | 64 |
| filterRowH | 48 |
| viewToggleSize | 40 |
| fabMargin | 16 |
| fabAboveGesture | 16 + nav inset |
| toolbarH | 52 |
| imeGap | 8 |
| icon | 24 |
| iconTouch | 48 |
| avatar | 32 |
| projectDot | 10 |

### 4.7 Motion (exact — official M3 tokens only)

`MotionScheme.expressive()` on the theme. **Material-owned motion inherits springs. Do not tween pills, sheets, menus, SplitButton, toolbar, dialogs.** Those have **no milliseconds in our spec.**

Keep explicit timings **only** where Material owns nothing:

| Ours | ms | Why |
|---|---:|---|
| Focus chrome fade | 240 | ours |
| Sentence dim | 200 | ours |
| Sync glyph rotation | 1200 / turn | ours |
| Snackbar dwell | 4000 | ours |
| Focus delay | 2000 idle | ours |

Card ↔ editor `sharedBounds`: `boundsTransform` uses `MaterialTheme.motionScheme.slowSpatialSpec<Rect>()` — a **scheme spring**, not `tween(400)`.

**No overshoot on caret, typing, or IME.**

**Reduce motion:** skip sharedBounds; fade only. IME and system back-to-home stay system.

See [ui/05-MOTION.md](ui/05-MOTION.md).

---

## 5. Predictive back — every frame (locked)

This is the “one microsecond we press back” section. Implement it exactly.

### 5.1 Enablement

- Manifest flag `true` (already set).
- Single Activity. Navigation is Compose back stack.
- Never use `onBackPressed()`.
- Never consume `KEYCODE_BACK`.

### 5.2 Callback stack (innermost wins)

Order, innermost first:

1. Open dialog → dismiss dialog  
2. Open dropdown menu → dismiss menu  
3. Open modal sheet → dismiss sheet (progress-linked)  
4. IME visible → **do not register a handler** (system hides IME)  
5. Ink canvas focused → defocus ink  
6. Multi-select → exit select  
7. Search open → close search  
8. Editor open → **predictive shared-bounds back to the card**  
9. Nested project screen → pop to home  
10. Home → **no handler** (system back-to-home)

### 5.3 Editor → Home (the important one)

Implement with the **official Compose APIs**, not a handmade scale that fights them.

1. Wrap `NavHost` in `SharedTransitionLayout`.
2. Card root **and** editor root both use:

```kotlin
Modifier.sharedBounds(
    rememberSharedContentState(key = NoteBoundsKey(noteId)),
    animatedVisibilityScope = this,
    enter = fadeIn(tween(200, easing = FastOutSlowInEasing)),
    exit = fadeOut(tween(200, easing = FastOutSlowInEasing)),
    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
)
```

`NoteBoundsKey` is a `data class` (official: do not use bare strings). If the card has a thumb, also `sharedElement(NoteImageKey(noteId))` on both thumb and editor hero image.

**Modifier order must be identical** on card and editor (official warning: mismatched padding before `sharedBounds` causes a jump). Place clip/background **after** `sharedBounds`. Shape morph is **not** automatic — if corners must animate, use `sharedBounds` + `animateEnterExit`, not `sharedElement`.

3. `PredictiveBackHandler(enabled = true)` on the editor (official progress API):

```kotlin
PredictiveBackHandler(true) { progress: Flow<BackEventCompat> ->
    try {
        progress.collect { ev ->
            backProgress = ev.progress          // 0 = start, 1 = completed
            // first event (p≈0) still applies — do not gate on p > 0.05
            // seek/lerp the sharedBounds overlay with backProgress
            // IME: do not consume; system hides it
        }
        nav.pop()                               // completion
    } catch (e: CancellationException) {
        backProgress = 0f                       // official: reset then rethrow
        throw e
    }
}
```

**While dragging:** `homeScrimAlpha = 0.40f * (1f - backProgress)`. No haptic. Optional 6% `translationX` follow from `touchX` **only if** it does not fight `sharedBounds` (if it jitters, delete it).

**Commit:** if last `progress >= 0.32` or Δp/Δt > 1.8/s, finish. Remaining interpolation uses **short4 = 200 ms** emphasized. Pop at the end. Save is already continuous.

**Cancel:** `CancellationException` path. Spring `backProgress → 0` (damping 0.9, stiffness 800). Restore IME only if it was open at gesture start and a text block is focused.

**Do not** implement a second “editorScale 1.00→0.92 / homeScale 0.94→1.00” system *instead of* sharedBounds. That was a guess. SharedBounds **is** the container transform.

### 5.4 Home → launcher

No app handler. System predictive back-to-home: surface scales to ~90%, 28–32 dp corners, wallpaper / launcher visible. Do not draw a custom splash on top.

### 5.5 Search close

Search field width interpolates `full → 40.dp` (the search icon well) over `p`, alpha of results `1 → 0`. Commit 160 ms. Cancel spring 200 ms.

### 5.6 Sheet close

Sheet `offsetY = p * sheetHeight`. Scrim alpha `0.40 * (1-p)`. Commit 200 ms accel. Cancel 220 ms spring.

### 5.7 Ink defocus (not a pop)

Toolbar height 52 → 0, 160 ms. Canvas stays. No shared element.

### 5.8 Hardware back / 3-button nav

Same handlers. No drag: play `p=0→1` in **medium1 = 250 ms** emphasized. Same end state.

---

## 6. Haptics — complete table (locked)

Helper: `NotesupHaptics`. Default = `performHapticFeedback`. If primitives supported: **new note, checkbox on, magic-plus drop** use `PRIMITIVE_TICK` + `PRIMITIVE_CLICK`. Else those three still use `CONFIRM`. Never `createOneShot`. Never request `VIBRATE` unless compositions require it — prefer View path when possible.

| Event | Constant | Else |
|---|---|---|
| New note | `CONFIRM` | |
| Split chevron open | `CLOCK_TICK` | |
| Split type chosen | `CLOCK_TICK` | |
| Pin | `CONFIRM` | |
| Unpin | `CLOCK_TICK` | |
| Checklist on | `CONFIRM` | |
| Checklist off | `CLOCK_TICK` | |
| Delete commit | `REJECT` | |
| Export success | `CONFIRM` | |
| Export fail | `REJECT` | |
| Image load fail | `REJECT` | |
| Unlock fail | OS biometric | |
| Lock on | `CONFIRM` | |
| Long-press card | `LONG_PRESS` | |
| Select-mode extra taps | `CLOCK_TICK` | |
| Swipe action commit pin | `CONFIRM` | |
| Swipe action commit delete | `REJECT` | |
| Slider ink thickness step | `CLOCK_TICK` | |
| Drag block start | `GESTURE_START` | API 34+ else `CLOCK_TICK` |
| Drag block drop | `GESTURE_END` | API 34+ else `CONFIRM` |
| Sign-in success | `CONFIRM` | |
| Sign-in fail | `REJECT` | |
| Create project | `CONFIRM` | |
| Search verb run | `CONFIRM` | |
| Everything else | **none** | including back, open note, type, scroll, ink stroke, pill change, theme change, sync recover |

---

## 7. Icon system — 100-source pool, then every icon locked

### 7.1 The 100 inspirations used for every icon

Every icon in §7.2 was scored against this same pool. We did **not** pick a random Material name. We asked: what do these 100 systems do for this *job*, then we locked one Material Symbol Rounded glyph so the set is one family (premium = one hand, not a collage).

**Libraries (1–44):** Material Symbols Outlined, Rounded, Sharp; Material Icons Filled / Outlined / Round / Sharp / Two-Tone; Pictogrammers MDI; SF Symbols default / Hierarchical / Palette; Phosphor Light / Regular / Bold / Fill / Duotone; Lucide; Feather; Tabler; Heroicons outline / solid; Hugeicons; Remix; Bootstrap Icons; Font Awesome Regular / Solid; Ionicons; Eva; Ant Design; Fluent Regular / Filled; IBM Carbon; Streamline; Iconoir; Solar Linear / Bold; IconPark; Radix; Simple Icons; css.gg.

**OS / OEM (45–52):** Pixel / Material You Expressive, Samsung One UI 6–7, HyperOS, ColorOS, Nothing OS / glyph, iOS Settings + SF, Android Settings, Windows Fluent.

**Note / write apps (53–72):** Apple Notes, Google Keep, Samsung Notes, Craft, Bear, UpNote, Things 3, Drafts, iA Writer, Ulysses, Notion, Obsidian, Standard Notes, Joplin, Notesnook, Notally, NotallyX, Quillnote, Markor, Fossify Notes, Goodnotes, Penly, INKredible, reMarkable.

**Premium non-note (73–100):** Linear, Superhuman, Raycast, Cron/Notion Calendar, Copilot Money, Cash App, Mercury, Stripe, Halide, Darkroom, VSCO, Flighty, Hello Weather, Ivory, Apollo, 1Password, Wallet, Apple Music, Overcast, Streaks, Gentler Streak, Niagara, Google Clock, Pixel Recorder, Google Photos, Gmail, Phone/Contacts, Fantastical.

**Global reject for the whole set:** duo-tone, colorized glyphs, emoji-as-icon, SF Symbols drawn on Android, custom 1.5px stroke set, filled icons at rest, 20 dp icons, 28 dp icons in toolbars.

**Global lock:** Material Symbols **Rounded**, 24 dp, weight 400, grade 0, optical size 24. Fill 0 at rest. Fill 1 only when the control is the selected state (grid vs list, pin on, check on, ink tool selected). Color: `onSurface` in app bars, `onSurfaceVariant` for secondary, `primary` never except the Split FAB add glyph on `onPrimary`.

### 7.2 Every product icon (locked)

| ID | Job | 100-pool winner pattern | **LOCKED glyph** (`name`) | Fill rest / on | Content description string |
|---|---|---|---|---|---|
| `i_back` | Up / close editor | Most Android apps: `arrow_back`. iOS chevron rejected (wrong OS). `close` rejected (not hierarchical). | **`arrow_back`** | 0 / 0 | Back |
| `i_search` | Open search | 90/100 use magnifier. `manage_search` too busy. `pageview` rejected. | **`search`** | 0 / 1 when search open | Search |
| `i_account` | Avatar fallback if no photo | `account_circle` won over `person`, `face`, `person_outline`. | **`account_circle`** | 0 / 0 | Account |
| `i_grid` | Grid view | `grid_view` beat `apps`, `dashboard`, `window`. | **`grid_view`** | 0 / 1 if grid | Grid view |
| `i_list` | List view | `view_agenda` beat `list`, `reorder`, `view_list` (agenda matches card rows). | **`view_agenda`** | 0 / 1 if list | List view |
| `i_pin` | Pin | `keep` (Material’s pin) beat `push_pin`, `star`, `bookmark`, `flag`. Star rejected (rating). | **`keep`** | 0 / 1 if pinned | Pin |
| `i_pinned_badge` | Tiny on card | same `keep` 16 dp | **`keep`** | 1 / 1 | Pinned |
| `i_add` | New note on FAB | `add` beat `edit`, `create`, `note_add` (note_add is the *menu* item). | **`add`** | 0 / 0 | New note |
| `i_split` | FAB overflow | `keyboard_arrow_up` when closed (menu opens up), `expand_more` rejected. | **`keyboard_arrow_up`** | 0 / 0 | More note types |
| `i_note` | Type: text | `notes` beat `article`, `description`, `sticky_note_2`, `edit_note`. | **`notes`** | 0 / 0 | Text note |
| `i_checklist` | Type: checklist | `checklist` beat `check_box`, `task_alt`, `fact_check`. | **`checklist`** | 0 / 0 | Checklist |
| `i_ink` | Type: ink | `draw` beat `brush`, `gesture`, `edit`, `ink_pen`, `mode`. | **`draw`** | 0 / 1 tool | Drawing |
| `i_image` | Insert image | `image` beat `photo`, `add_photo_alternate`, `photo_library`. | **`image`** | 0 / 0 | Image |
| `i_camera` | Capture | `photo_camera` beat `camera_alt`, `camera`. | **`photo_camera`** | 0 / 0 | Camera |
| `i_gallery` | Picker | `photo_library` | **`photo_library`** | 0 / 0 | Gallery |
| `i_more` | Overflow | `more_vert` beat `more_horiz` (Android app bar standard). | **`more_vert`** | 0 / 0 | More |
| `i_share` | Share | `share` beat `ios_share` (wrong OS). | **`share`** | 0 / 0 | Share |
| `i_export_md` | Markdown | `description` (not `draft`) | **`description`** | 0 / 0 | Export Markdown |
| `i_export_pdf` | PDF | `picture_as_pdf` | **`picture_as_pdf`** | 0 / 0 | Export PDF |
| `i_lock` | Lock note | `lock` beat `lock_outline`, `https`, `vpn_key`. | **`lock`** | 0 / 1 locked | Lock |
| `i_unlock` | Unlock | `lock_open` | **`lock_open`** | 0 / 0 | Unlock |
| `i_delete` | Delete | `delete` beat `delete_outline`, `close`, `backspace`. | **`delete`** | 0 / 0 | Delete |
| `i_undo` | Undo | `undo` | **`undo`** | 0 / 0 | Undo |
| `i_redo` | Redo | `redo` | **`redo`** | 0 / 0 | Redo |
| `i_project` | Project | `folder` rejected (we rejected folders). `inbox` for Inbox. Projects: `label` rejected. **`dashboard_customize` too much.** Lock **`layers`** as “bucket” without being a file folder. | **`layers`** | 0 / 0 | Project |
| `i_inbox` | Inbox | `inbox` | **`inbox`** | 0 / 0 | Inbox |
| `i_settings` | Settings | `settings` beat `tune`, `manage_accounts`. | **`settings`** | 0 / 0 | Settings |
| `i_sync` | Syncing | `sync` (spin 1 turn / 1.2 s while CONNECTING) | **`sync`** | 0 / 0 | Syncing |
| `i_sync_ok` | Connected, idle | **no icon** (absence is premium) | — | — | — |
| `i_offline` | Offline | `cloud_off` beat `wifi_off` (it’s sync, not radio). | **`cloud_off`** | 0 / 0 | Offline |
| `i_conflict` | Conflict copy | `error_outline` | **`error_outline`** | 0 / 0 | Sync conflict |
| `i_check` | Generic done | `check` | **`check`** | 0 / 0 | Done |
| `i_close` | Close search / sheet | `close` | **`close`** | 0 / 0 | Close |
| `i_bold` | Bold | `format_bold` | **`format_bold`** | 0 / 1 | Bold |
| `i_italic` | Italic | `format_italic` | **`format_italic`** | 0 / 1 | Italic |
| `i_underline` | Underline | `format_underlined` | **`format_underlined`** | 0 / 1 | Underline |
| `i_strike` | Strike | `format_strikethrough` | **`format_strikethrough`** | 0 / 1 | Strikethrough |
| `i_code` | Inline code | `code` | **`code`** | 0 / 1 | Code |
| `i_link` | Link | `link` | **`link`** | 0 / 1 | Link |
| `i_heading` | Heading | `title` beat `format_size`, `h_mobiledata`. | **`title`** | 0 / 1 | Heading |
| `i_bullet` | Bullets | `format_list_bulleted` | **`format_list_bulleted`** | 0 / 1 | Bulleted list |
| `i_number` | Numbers | `format_list_numbered` | **`format_list_numbered`** | 0 / 1 | Numbered list |
| `i_divider` | Divider | `horizontal_rule` | **`horizontal_rule`** | 0 / 0 | Divider |
| `i_check_item` | Checklist circle | Custom 22 dp ring, 2 dp stroke `outline`. **Not** Material `check_box`. Things 3 won. | **custom canvas** | fill primary when on | (state in TalkBack) |
| `i_pen` | Ink pen | `edit` beat `create`, `ink_pen` if available use `edit` for consistency. | **`edit`** | 0 / 1 | Pen |
| `i_highlighter` | Highlighter | `highlight` | **`highlight`** | 0 / 1 | Highlighter |
| `i_eraser` | Eraser | `ink_eraser` if in font else `auto_fix_off` — lock **`ink_eraser`** fallback **`block`**. | **`ink_eraser`** | 0 / 1 | Eraser |
| `i_thickness` | Stroke width | `line_weight` | **`line_weight`** | 0 / 0 | Thickness |
| `i_color` | Ink color | `palette` | **`palette`** | 0 / 0 | Color |
| `i_move_project` | Move | `drive_file_move` | **`drive_file_move`** | 0 / 0 | Move to project |
| `i_tint` | Tint | `colors` / `format_color_fill` — lock **`format_color_fill`** | **`format_color_fill`** | 0 / 0 | Note color |
| `i_select_all` | Select | `select_all` | **`select_all`** | 0 / 0 | Select |
| `i_drag` | Block handle | `drag_indicator` | **`drag_indicator`** | 0 / 0 | Reorder |
| `i_info` | About | `info` | **`info`** | 0 / 0 | About |
| `i_sign_out` | Sign out | `logout` | **`logout`** | 0 / 0 | Sign out |
| `i_sign_in` | Sign in | `login` | **`login`** | 0 / 0 | Sign in |
| `i_passkey` | Passkey | `key` | **`key`** | 0 / 0 | Passkey |
| `i_google` | Google | Use official Google G drawable (brand rules), **not** a Symbol | **brand asset** | — | Continue with Google |
| `i_widget_add` | Widget | same `add` | **`add`** | 0 / 0 | New note |
| `i_empty` | empty home | **no icon** | — | — | — |

Launcher icon is **not** a Material Symbol. See §8.

---

## 8. Launcher icon and wordmark (locked)

**Mark:** a rounded-rectangle sheet of paper (`24` radius in 108 dp adaptive safe zone) in `surface` cream `#F6F1EA`, with a single sealing-wax **wedge** (a 6-o’clock notch of `#8B2942`) at the top-right like a closed wax seal — 18% of the canvas. No letter N. No fountain pen. No folded dog-ear (Keep clone). No yellow pad.

**Adaptive:**  
- Foreground: the paper + seal, 72 dp padded in 108.  
- Background: `#1C1917` (ink).  
- Monochrome (themed icons): white paper silhouette + seal cutout.

**Wordmark in-app:** the word `Notesup` in `titleLarge`, weight 500, `onSurface`. No logo next to it on phone (mark is the launcher only). Do not set it in small caps. Do not add a trademark.

---

## 9. Navigation (locked)

**Routes:**

```
welcome
auth
auth/code
home
search
editor/{noteId}?created={bool}
project/{projectId}
settings
settings/appearance
settings/type
settings/paper
settings/focus
settings/trash
settings/account
settings/privacy
settings/about
```

`account` is a **ModalBottomSheet**, not a route.  
Lightbox is an overlay, not a route.  
`NoteCaptureActivity` is a second activity, not a nav route ([ui/17-CAPTURE-SHARE.md](ui/17-CAPTURE-SHARE.md)).  
`welcome` / `auth` / `auth/code` are full-screen **our** Compose. Never Clerk `AuthView`. Pixel spec: [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md).  
Settings children: [ui/15-SETTINGS.md](ui/15-SETTINGS.md). Project: [ui/16-PROJECTS.md](ui/16-PROJECTS.md). Lock: [ui/18-LOCK-GATE.md](ui/18-LOCK-GATE.md).

**Start destination:** `welcome` if DataStore `onboarding_done == false` **and** `Clerk.user == null`. Else `home`.

Skip Welcome (set `onboarding_done = true`, go to the deep-link destination): widgets, shortcuts, share target, `ACTION_CREATE_NOTE`, `notesup://…`, existing Clerk session.

**Large screen:** [ui/21-LARGE-SCREEN.md](ui/21-LARGE-SCREEN.md). Rail at width ≥ 600 and height ≥ 480. Two-pane list **360** | detail fill. `NavigableListDetailPaneScaffold`, back `PopUntilContentChange`. Shared-bounds off on two-pane. Compact / phone landscape: no rail, no two-pane.

**Phone:** no rail, no nav bar.

---

## 10. Home screen — pixel spec

```
[status inset]
[App bar 64]
   start: wordmark "Notesup"  16 dp from start
   end:   search 48  |  avatar 48   (8 dp gap, 8 dp from end)
[Filter row 48]
   start 16: ButtonGroup All | Pinned | Recent | Projects
   end 16:   grid 40 | list 40
[content]
   All: pin strip if pins>0 (horizontal, 16 dp pad, 8 dp gap, card width 148, height 152)
        then grid or list
   Pinned: grid/list of pins only
   Recent: list forced (ignore toggle), sticky headers
   Projects: list of project rows + Inbox first
[Split FAB] bottom-end, 16 + nav insets
```

**Grid:** 2 columns phone (`maxWidth < 600`), 3 if `600–839`, 4 if `>= 840` and no editor pane.  
`LazyVerticalGrid` `Adaptive` **not used**. Fixed columns as above. Cell aspect: wrap content, **min height 148**, **max thumb 120**.  
**Never group the All-grid by date.** Samsung Notes 2026 did this; users reported a ruined dense grid and useless negative space. Date headers exist only on the **Recent list**.

**Card (grid):**

- Fill `surfaceContainerLow`
- Radius 24
- If tint: overlay 8%
- If pinned: `tertiaryContainer` instead of surfaceContainerLow
- Thumb (if image or ink preview): top, width fill, height min(120, 16:10 of width), crop `ContentScale.Crop`, bottom corners 0, top 24
- Then pad 14: title `titleMedium` maxLines 2 ellipsis; preview `bodySmall` `onSurfaceVariant` maxLines 2; meta row: relative time `labelSmall` · project name if any
- Pin glyph 16 dp top-end overlay if pinned and not already on pin strip
- Locked: `lock` 16 dp next to pin

**List row:** 72 min. Leading 40×40 thumb radius 12. Title 1 line. Preview 1 line. Time end-aligned `labelSmall`.

**Swipe list (All / Pinned / Recent only):**  
start → pin (tertiaryContainer)  
end → delete (errorContainer)  
Threshold 96 dp. Haptic on commit.

**Split FAB:**

- Use Material3 Expressive `SplitButtonLayout` / `SplitButton`.
- Primary 56 h, `primary` container, `add` + no label.
- Secondary 56×40, `keyboard_arrow_up`.
- Menu anchored above, items: Text note `notes`, Checklist `checklist`, Drawing `draw`, Image `image`. Each 48 h, radius 16, `surfaceContainerHigh`.
- Primary tap: create `Note` type TEXT in Inbox, navigate `editor/{id}?created=true`. Caret in **first paragraph**, not the title.
- **Magic Plus:** dragging the SplitButton (or a drag shadow of it) onto a project row in the Projects filter, or onto the pin-strip, creates the note **there** and opens the editor. Haptic `CONFIRM`. This is a v1 gesture, not a later treat.

**Empty All:** vertical center, wordmark `displaySmall`, 8 dp, then `bodyLarge` `onSurfaceVariant` string `empty_home` = `Write anything.` FAB still visible. **No illustration. No button besides FAB.**

**First-run:** **not** this empty. New install opens **Welcome** ([ui/14-ONBOARDING.md](ui/14-ONBOARDING.md)). After Start writing / Continue without / successful sign-in, *this* empty is home. No carousel. No permissions dialog. No `ROLE_NOTES` on first launch.

**Select mode:** long-press. App bar becomes: `close` | `{count} selected` | `keep` | `drive_file_move` | `delete`. FAB hidden 160 ms.

---

## 11. Search — pixel spec

- Tap search: shared-bounds from the 48 dp icon to a full-width field under the status bar (height 56, margin 8, radius 28, fill `surfaceContainerHigh`).
- Hint: `Search notes`.
- Keyboard: `ImeAction.Search`.
- Results: list rows, 8 dp below field.
- Leading suggestion chips (only if query empty): `Pinned`, `Drawings`, `Images`, `Locked` — these write the verbs `pin`, `ink`, `image`, `locked` into the field.
- Query parse:
  - tokens case-insensitive
  - `project:foo` filters project name contains foo
  - remaining text: FTS on title + body plaintext
- Tap result: same shared-element open as home.

---

## 12. Editor — pixel spec

**Top bar 64:**  
`arrow_back` | spacer | `more_vert`  
Title is **not** in the app bar. Title is the first block.

**Body:** `LazyColumn` contentPadding top 8, bottom 96 + ime.

**Title block:** `BasicTextField`, `headlineMedium`, placeholder `Title`, no enter-to-new-block (enter moves to first body block). Empty title allowed forever. Home card title fallback = first 48 chars of first paragraph or `Untitled`.

**Paragraph:** MohamedRejeb rich editor, **`bodyNote` 18/28**. Enter splits block. Backspace at index 0 merges with previous text block or deletes empty block. `/` at the start of an empty paragraph opens the **insert sheet** (diet slash — [FEATURES-NOW-LOCKED](FEATURES-NOW-LOCKED.md)).

**Headings:** H1 `titleLarge` 22/28 w600 · H2 `titleMedium` 16/24 w600 · H3 `titleSmall` 14/20 w600. Markdown `#` `##` `###` at line start converts and hides hashes.

**Table:** size pad 1–8 × 1–12 (default tap = 2×2). Edge `⋯` adds/deletes row/col. Cells plain text. Max 8×50. Horizontal scroll. Header row optional.

**Code:** mono, language chip, copy. Languages: plain, kotlin, js, py, json, sh, xml.

**Quote:** 3 dp primary bar, `bodyNote`.

**Paper:** underlay behind the `LazyColumn` (not through the title band). See FEATURES-NOW-LOCKED.

**Checklist item:** 22 dp custom circle + 12 dp gap + text field. Enter adds item. Backspace on empty item deletes item; if last item, block becomes paragraph.

**Image block:** width fill minus 20 dp pad, radius 16, max height 360, `ContentScale.Fit`. Caption optional, 1 line `bodySmall` under, default hidden until tap. Tap image: full-screen pager, scrim 0.92, pinch zoom, back closes (predictive scale).

**Ink block:** default height **280 dp**, min 160, max 720, drag handle bottom-center 32×4 dp `outline` pill to resize. Unfocused: `Image(previewPng)`. Focused: live canvas + toolbar. Strokes live in `InkEntity` (`ink-storage`), not in note JSON.

**Divider:** 1 dp `outlineVariant`, vertical margin 16.

**Floating toolbar** (`HorizontalFloatingToolbar`):  
height 52, margin above IME 8, horizontal 16, fill `surfaceContainerHigh`, tonal.  
Group A (styles): bold italic underline strike code link  
1 dp vertical hairline  
Group B (convert): H1 H2 H3 · bullets · numbers · checklist  
1 dp  
Group C (insert): **+** opens the insert sheet (table, image, draw, code, quote, divider)  

When IME hidden: toolbar docks to bottom above nav inset, same content.

**Focus (see UX file):** Settings default `Auto`.

- **Auto:** 2000 ms after last text input, app bar + toolbar alpha 0 in 240 ms. Tap or selection restores in 160 ms.
- **Sentence:** Auto plus non-active paragraphs at **62%** opacity. Active paragraph 100%. A11y → off.
- **Typewriter:** caret at 38% of viewport. Auto-enable when block count ≥ 4 or plaintext ≥ 400 chars; off for short notes. Can be forced on/off in Settings.
- **While a text range is selected:** pause dim and typewriter (iA: otherwise the screen jumps).

**Overflow menu order (exact):**

1. Pin / Unpin  
2. Move to project  
3. Note color  
4. Paper  
5. Type  
6. Lock / Unlock  
7. —  
8. Export Markdown  
9. Export PDF  
10. Share  
11. —  
12. Delete  

**Create animation:** if `created=true`, skip shared element; editor fades in 120 ms, title or body focused, IME requested immediately in `LaunchedEffect`.

**Autosave:** every local edit writes Room **immediately** (debounce 50 ms to coalesce keystrokes). Convex mutation debounce **700 ms** after last edit, or on `onStop`, or on back commit.

---

## 13. Drawing — exact

Full tool spec: **[DRAWING.md](DRAWING.md)**. That file is the source of truth for ink.

**Tools:** Pencil · Marker · Highlighter · Eraser (stroke default / area).  
**Tap selected tool again:** width + opacity popover (Apple Notes).  
**Undo / Redo** on the toolbar, stack 50.  
**Color:** six swatches + sheet.  
**Canvas:** in-note block, resize handle, paper shows through.  
**Stroke:** Jetpack Ink **1.0.0** (`InProgressStrokes` / Compose authoring). PNG snapshot on Done. See DRAWING.md.

No fountain/calligraphy pack in v1. No lasso until v1.1. No second app.

**No** shape snap, lasso, layers, stickers, undo beyond stroke undo/redo (50).

---

## 14. Images — exact

- Picker: `PickVisualMedia(ImageOnly)` allow multiple up to **10**. Sheet + lightbox: [ui/19-MEDIA.md](ui/19-MEDIA.md).
- Camera: `TakePicture` to `cache/capture_{uuid}.jpg`.
- On insert: copy to `files/media/{uuid}.jpg`, downsample so long edge ≤ **2560**, JPEG quality **88**, strip unnecessary EXIF except orientation (apply then strip).
- Room `MediaEntity`. Convex upload after local insert via WorkManager, not on UI thread.
- Failed upload: note still shows local. Retry exponential 15s × 2^n, cap 30 min.

---

## 15. Export — exact

**Markdown:**

```
# {title or Untitled}

{paragraph as markdown}
## {heading}
- [ ] / - [x]
- bullets
1. numbered
![caption](media/{id}.jpg)
![drawing](media/{id}.png)
---
```

Zip if any media: `{title}.zip` with `note.md` + `media/`. If no media: `{title}.md`.

**PDF:** US Letter if locale US, else A4. Margin 56 pt. Title `headline` 18 pt. Body 11 pt. Images max width content. Ink PNG. Page numbers footer `labelSmall`. Share `application/pdf`.

Both go through `Intent.ACTION_SEND` chooser. No custom share UI.

---

## 16. Projects, pins, trash — exact

**Inbox** is `projectId == null`. Not a real row.

**Project fields:** `id`, `name` (1–32 chars), `hue` (0–7 same as tint table), `emoji` (optional single emoji, default none), `order` (int), timestamps.

**Create project:** while Projects filter is selected, a quiet app-bar `add` (`New project`). **Not** the capture SplitButton. Pixels: [ui/16-PROJECTS.md](ui/16-PROJECTS.md). Sheet: name, 8 hue dots, optional emoji. Primary `Create`. Rename / delete / empty / Inbox-is-not-a-destination: same file.

**Project row:** 10 dp hue dot | emoji if any | name `titleMedium` | count `labelSmall` | chevron none (whole row clicks).

**Move sheet:** radio Inbox + projects. Confirm.

**Pins:** boolean on note. Pin strip max **6** then “See all” which selects Pinned pill. No cap on total pins.

**Delete:** set `deletedAt = now`. Snackbar `Note deleted` + `Undo` 4 s. Undo clears `deletedAt`. Purge worker daily: `deletedAt < now-30d`. Home never shows deleted.

**No archive in v1.**

---

## 17. Account sheet and settings — exact

Sheet height wrap, max 85%. Handle 32×4.

**Signed out:**  
`account_circle` 64 | `Sign in to sync` `titleMedium` | `Notes stay on this phone until you do.` `bodyMedium` | button `Sign in` (`login`) | list: Settings, About.

**Signed in:**  
Coil avatar 64 or `account_circle` | email `titleMedium` | sync line: `Synced` / `Sync paused` + `cloud_off` | `Sign out` | Settings | About.

**Sign in UI:** **custom** Compose on route `auth` — Google (Credential Manager + paper button), email code, optional passkey, `Continue without an account`. Combined sign-in/up. **No `AuthView`. No `clerk-android-ui`.** Hosted Custom Tab only if Play Services is missing and they tapped Google. Full pixels: [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md).

Account sheet **Sign in** pushes `auth` (closes the sheet). Same screen as first-run.

After `setActive`: if local notes > 0 and none have `remoteId`, dialog:

Title `Sync these notes?`  
Body `{n} notes on this phone will upload to your account.`  
Buttons `Not now` (signed in, `sync_paused = true`) / `Sync` (default, upload).  
0 notes: no dialog; sync is on.

**Settings rows (exact order):** [ui/15-SETTINGS.md](ui/15-SETTINGS.md) — Appearance, Type, Paper, Home view (inline), Focus, Sort checked, Lock new notes, Show notes on lock screen, Default notes app (`ROLE_NOTES`), Trash, Account, What syncs, About.

**About / privacy / manage / trash / theme / type / paper / focus:** pixels in ui/15. Delete account + sign out dialogs: [ui/22-SYSTEM-EDGES.md](ui/22-SYSTEM-EDGES.md).

---

## 18. Widgets — exact

| Widget | class | minResize | target | Layout |
|---|---|---|---|---|
Map to Android **canonical layouts** + **TIER 1 widget quality**.

| Widget | Glance class | Canonical | Sizes that MUST work | Layout |
|---|---|---|---|---|
| New note | `NewNoteWidget` | **Toolbar** | **4×1** and 2×2 | Brand `add` + `checklist` + `draw`. Hits all 4 edges. 48 dp targets. |
| Pinned | `PinnedWidget` | **Text and image** | **2×2**, 4×2 | Title + 4 preview lines + optional thumb. Empty: `Pin a note` (intentional empty, WT-1). |
| Recent | `RecentWidget` | **Text and image list** | **4×2**, 4×3 | Header: app icon + “Recent”. 3–8 rows. |
| Project | `ProjectWidget` | **Text and image list** | **4×2** | System configuration picker for `projectId` (WS-4). Header = project name + icon. |

**Required for every widget (or it is low quality):**

- Fill **all 4 edges** of the launcher cell (WL-1.1). 4×1 search-like toolbar may hit 2 edges only.
- **System corner radius**, not 24 dp (WS-2).
- Light + dark + dynamic color (WC-1, WC-2).
- Unique name + unique description in `appwidget-provider` (WD-4.4, WD-4.5).
- Static **preview** drawable that looks like the real widget (WD-4).
- Update after in-app write (WT-3.2) — Room write → Glance update 300 ms debounce.
- System launch transition into the note (WS-5).
- Glance **only** — do not put `androidx.compose.material3` composables in widget code (not interoperable).

Update Glance on every Room write of summaries (debounce 300 ms).  
Tap row → `notesup://note/{id}`.  
Tap new → `notesup://new`.

---

## 19. Domain model (locked)

```kotlin
@JvmInline value class NoteId(val raw: String)      // uuid v4
@JvmInline value class ProjectId(val raw: String)
@JvmInline value class BlockId(val raw: String)
@JvmInline value class MediaId(val raw: String)
@JvmInline value class UserId(val raw: String)      // clerk id when known

data class Note(
    val id: NoteId,
    val remoteId: String?,
    val projectId: ProjectId?,
    val title: String,
    val blocks: List<Block>,
    val pinned: Boolean,
    val locked: Boolean,
    val lockCipher: ByteArray?,
    val tint: Int,
    val paper: String,
    val font: String?,
    val createdAt: Instant,
    val updatedAt: Instant,          // DISPLAY AND SORT ONLY. Never a correctness input.
    val deletedAt: Instant?,
    val rev: Long,
    val writerId: String,            // this install's UUID
    val baseRev: Long,
    val baseWriterId: String?,
)

@Serializable data class RichSpan(val start: Int, val end: Int, val style: SpanStyleTag)
@Serializable data class RichText(val v: Int = 1, val text: String, val spans: List<RichSpan> = emptyList())
@Serializable enum class SpanStyleTag { BOLD, ITALIC, UNDERLINE, STRIKE, CODE, LINK }

sealed interface Block {
    val id: BlockId
    data class Paragraph(override val id: BlockId, val rich: RichText) : Block // OUR format, not the library's
    data class Heading(override val id: BlockId, val level: Int, val text: String) : Block // 1..3
    data class Table(
        override val id: BlockId,
        val cols: Int,
        val rows: Int,
        val cells: List<String>,
        val headerRow: Boolean,
    ) : Block
    data class Code(override val id: BlockId, val language: String, val text: String) : Block
    data class Quote(override val id: BlockId, val text: String) : Block
    data class Checklist(override val id: BlockId, val items: List<CheckItem>) : Block
    data class Bullets(override val id: BlockId, val items: List<String>) : Block
    data class Numbered(override val id: BlockId, val items: List<String>) : Block
    data class Image(override val id: BlockId, val mediaId: MediaId, val caption: String) : Block
    data class Ink(override val id: BlockId, val inkId: InkId, val previewPath: String?) : Block
    data class Divider(override val id: BlockId) : Block
}

@JvmInline value class InkId(val raw: String)
data class CheckItem(val id: String, val text: String, val checked: Boolean)

@Entity
data class InkEntity(
    @PrimaryKey val id: String,
    val noteId: String,
    val strokeBlob: ByteArray,   // androidx.ink.storage — not JSON
    val heightDp: Float,
    val updatedAt: Long,
)
```

Room: `blocks` JSON of **our** types. Materialized `plaintext` column on `NoteEntity` (title + `RichText.text` + headings + cells + code). **FTS5** (not FTS4) on `plaintext`. On lock, **same transaction**: encrypt, `plaintext = ""`, delete FTS row. Unlock rebuilds FTS.

Editor: one `TextFieldState` per text block, owned by `EditorViewModel` (`getOrPut`). Focus swap is paint-only. Tap-to-caret via `TextLayoutResult.getOffsetForPosition`.

`installId`: UUID once in DataStore. Every local write sets `writerId = installId` and increments `rev`. After Convex ack: `baseRev = rev`, `baseWriterId = installId`.

---

## 20. Convex (every backend thing, locked)

**Deployment:** one `npx convex dev` project.  
**Env (Convex dashboard + local):** `CLERK_JWT_ISSUER_DOMAIN` (Frontend API URL, e.g. `https://verb-noun-00.clerk.accounts.dev`), `CLERK_WEBHOOK_SECRET`.  
**Android:** `resValue` `convex_url` debug vs release from `local.properties` keys `CONVEX_URL_DEV` / `CONVEX_URL_PROD`. Never commit URLs.

### 20.1 Auth (this is required or sync silently fails)

Clerk Dashboard:

1. **Enable Native API** (Clerk Android quickstart — required).
2. **Activate the Convex integration** so a JWT template named `convex` exists.
3. For hosted auth / production: register namespace + package `com.notesup.app`.

Repo file `convex/auth.config.ts` (Convex Clerk docs — not optional):

```ts
import { AuthConfig } from "convex/server";
export default {
  providers: [
    { domain: process.env.CLERK_JWT_ISSUER_DOMAIN!, applicationID: "convex" },
  ],
} satisfies AuthConfig;
```

Then `npx convex dev` so the backend picks it up.

Android client (clerk-convex-kotlin README):

```kotlin
val convex = createClerkConvexClient(
    deploymentUrl = context.getString(R.string.convex_url),
    context = applicationContext,
)
```

Do **not** invent a second JWT header path.  
Every **sync** query/mutation starts with:

```
const identity = await ctx.auth.getUserIdentity();
if (!identity) throw new ConvexError("UNAUTHENTICATED");
const clerkUserId = identity.subject;
```

Webhook HTTP route `POST /clerk-users-webhook` (official Svix sample). Upsert `users` by `clerkUserId`. Mutations **must not** require the webhook row; they key off JWT `subject`. Webhook is for deletion fan-out only.

### 20.2 Schema (exact)

```ts
users: defineTable({
  clerkUserId: v.string(),
  createdAt: v.number(),
}).index("by_clerk", ["clerkUserId"]),

projects: defineTable({
  clerkUserId: v.string(),
  localId: v.string(),
  name: v.string(),
  hue: v.number(),
  emoji: v.optional(v.string()),
  order: v.number(),
  updatedAt: v.number(),
  deletedAt: v.optional(v.number()),
}).index("by_user", ["clerkUserId", "order"])
  .index("by_user_local", ["clerkUserId", "localId"]),

notes: defineTable({
  clerkUserId: v.string(),
  localId: v.string(),
  projectLocalId: v.optional(v.string()),
  title: v.string(),
  blocks: v.any(),
  pinned: v.boolean(),
  locked: v.boolean(),
  ciphertext: v.optional(v.string()), // if locked, blocks is [] and ciphertext holds body
  tint: v.number(),
  paper: v.optional(v.string()),
  font: v.optional(v.string()),
  updatedAt: v.number(),
  createdAt: v.number(),
  deletedAt: v.optional(v.number()),
  rev: v.number(),
  writerId: v.string(),
  baseRev: v.number(),
  baseWriterId: v.optional(v.string()),
}).index("by_user_updated", ["clerkUserId", "updatedAt"])
  .index("by_user_local", ["clerkUserId", "localId"])
  .index("by_user_pinned", ["clerkUserId", "pinned", "updatedAt"])
  .index("by_user_project", ["clerkUserId", "projectLocalId", "updatedAt"]),

media: defineTable({
  clerkUserId: v.string(),
  localId: v.string(),
  noteLocalId: v.string(),
  storageId: v.id("_storage"),
  kind: v.union(v.literal("image"), v.literal("ink")),
  mime: v.string(),
  width: v.optional(v.number()),
  height: v.optional(v.number()),
  updatedAt: v.number(),
}).index("by_user_local", ["clerkUserId", "localId"])
  .index("by_note", ["clerkUserId", "noteLocalId"]),
```

### 20.3 Functions (exact names)

| Name | Kind | Args | Behavior |
|---|---|---|---|
| `notes:list` | query | `{ since?: number }` | notes where deletedAt empty, by updatedAt desc, limit 200 then paginate `paginationOpts` |
| `notes:get` | query | `{ localId }` | one note or null |
| `notes:upsert` | mutation | full note + `writerId/rev/baseRev/baseWriterId` | if no row, insert. If `baseWriterId/baseRev` equals stored `(writerId, rev)` → patch (fast-forward). Else insert conflict copy `title + " (conflict)"`, return `{ conflictLocalId }`. **Never use `updatedAt` for this.** |
| `notes:tombstone` | mutation | `{ localId, deletedAt }` | set deletedAt |
| `projects:list` | query | — | not deleted |
| `projects:upsert` | mutation | project fields | |
| `projects:tombstone` | mutation | `{ localId }` | |
| `media:generateUploadUrl` | mutation | — | `ctx.storage.generateUploadUrl()` |
| `media:complete` | mutation | media fields + storageId | |
| `users:purgeMe` | mutation | — | delete all rows + storage for JWT subject |
| `http.clerkWebhook` | httpAction | Svix | upsert/delete users; on user.deleted schedule purge |

No `action` in v1 except if upload must be action — prefer mutation + client PUT to upload URL.

### 20.4 Sync algorithm (client)

**Law: `updatedAt` is display and sort only. Never a correctness input.**

1. Room is readable always.  
2. When Clerk user present and socket CONNECTED: subscribe `notes:list` + `projects:list`.  
3. Push via `notes:upsert` with `(writerId, rev, baseRev, baseWriterId)`.  
4. On `{ conflictLocalId }`: insert the extra local note; snackbar `Kept both versions`.  
5. Pull: if remote `(writerId, rev)` equals local base → already in sync. If remote is a descendant we don't have → apply remote and set base to remote pair.  
6. Outbox `SyncQueue`. Ink blobs upload like media (`ink.ts` + storage).  
7. `webSocketStateFlow == CONNECTING` > 4 s → `cloud_off` on avatar, no toast.  
8. `initConvexLogging()` debug only.

### 20.5 Conflict copy (exact)

New local note, `title = originalTitle + " (conflict)"`, same project, `pinned = false`, incoming body. Snackbar `Kept both versions`. No haptic.

---

## 21. Clerk (every setting, locked)

**Application:** native Android.  
**Dashboard first:** enable **Native API**. Enable **Convex** integration.  
**Sign-in options ON:** Email code, Google, Passkeys.  
**Sign-in options OFF:** Facebook, Apple, GitHub, phone, username. Password ON only as fallback.  
**Organizations:** OFF.  
**User profile:** email, image.  
**Sessions:** default lifetime.  
**Webhooks:** `user.created`, `user.updated`, `user.deleted` → Convex HTTP.  
**Publishable key:** `BuildConfig` from `local.properties` `CLERK_PK`. Never in git.  
**Dependencies:** `com.clerk:clerk-android-api`, `com.clerk:clerk-convex-kotlin`. **Not** `clerk-android-ui`.  
**UI:** our Welcome + `AuthScreen` + `AuthCodeScreen` — [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md). Combined sign-in/up.  
**Initialize:** `Clerk.initialize(this, pk)` in `NotesupApp.onCreate` **before** `createClerkConvexClient`.  
**minSdk:** Clerk 24, we ship 26.  
**Native API note:** it bypasses browser CAPTCHA. Keep Google + passkeys + email code. Do not add SMS in v1.

Auth does not block `MainActivity.setContent`. First Compose frame is Welcome **or** home. Writing never waits on Clerk.

---

## 22. Lock / crypto (locked)

- Lock uses AndroidX Biometric **BIOMETRIC_STRONG or device credential**. Gate chrome: [ui/18-LOCK-GATE.md](ui/18-LOCK-GATE.md).  
- On lock: serialize blocks → AES-256-GCM with Keystore key `NotesupLock_{noteId}`, `lockCipher` set, blocks emptied, `plaintext = ""`, **FTS row deleted in the same transaction**.  
- Convex: `locked=true`, `ciphertext`, `blocks=[]`. Never plaintext.  
- Unlock: biometric → decrypt → restore blocks → rebuild FTS.  
- **v1 backup:** database excluded from cloud backup and device transfer (F-11a). Locked notes do **not** survive a new phone in v1. Honest.  
- **v1.1:** passphrase (Argon2/PBKDF2) as root of trust; Keystore is convenience. Then lock can restore.  
- If keystore lost on this device: `locked_fail`. User may delete.

---

## 23. Strings (complete v1 English)

```
app_name=Notesup
empty_home=Write anything.
search_hint=Search notes
untitled=Untitled
filter_all=All
filter_pinned=Pinned
filter_recent=Recent
filter_projects=Projects
inbox=Inbox
cd_back=Back
cd_search=Search
cd_account=Account
cd_grid=Grid view
cd_list=List view
cd_new=New note
cd_more_types=More note types
type_text=Text note
type_list=Checklist
type_ink=Drawing
type_image=Image
title_hint=Title
menu_pin=Pin
menu_unpin=Unpin
menu_move=Move to project
menu_color=Note color
menu_lock=Lock
menu_unlock=Unlock
menu_md=Export Markdown
menu_pdf=Export PDF
menu_share=Share
menu_delete=Delete
deleted=Note deleted
undo=Undo
welcome_line=Write anything.
welcome_body=Notes live on this phone. Sign in later if you want them everywhere.
start_writing=Start writing
sign_in=Sign in
sign_in_to_sync=Sign in to sync
sign_in_body=Notes stay on this phone until you do.
continue_google=Continue with Google
continue_email=Continue
continue_without=Continue without an account
email_hint=Email
use_passkey=Use a passkey
or=or
auth_legal=By signing in, notes you choose to sync are stored in your account.
check_email=Check your email
code_sent=We sent a 6-digit code to
code_cd=Verification code
code_wrong=That code wasn’t right.
code_expired=That code expired. Resend it.
resend_in=Resend in 0:%02d
resend=Resend code
auth_fail=Couldn’t sign in.
auth_timeout=Couldn’t reach sign-in.
try_again=Try again
password_title=Enter your password
password_hint=Password
use_code_instead=Use a code instead
totp_title=Enter your authenticator code
backup_code=Use a backup code
appearance=Appearance
type=Type
paper=Paper
home_view=Home view
focus=Focus
sort_checked=Sort checked to end
lock_new=Lock new notes
lock_screen_history=Show notes on lock screen
default_notes=Default notes app
default_notes_body=Write from the lock screen and the stylus shortcut.
what_syncs=What syncs
wallpaper=Wallpaper
bundled=Bundled
type_fail=Couldn’t add this type.
empty_trash=Empty
empty_trash_q=Empty Trash?
empty_trash_body=Notes older than 30 days are already gone. This removes the rest on this phone.
empty_trash_action=Empty
nothing_trash=Nothing in Trash.
deleted_today=Deleted · today
deleted_days=Deleted · %dd left
restore=Restore
sync_now=Sync now
set_lock=Set a screen lock
set_lock_body=Locked notes need a fingerprint, face, or device PIN.
open_settings=Open settings
licenses=Open-source licenses
note_color=Note color
size_s=S
size_m=M
size_l=L
focus_off=Off
focus_off_body=Chrome stays.
focus_auto=Auto
focus_auto_body=Chrome fades after you stop typing.
focus_sentence=Sentence
focus_sentence_body=Other paragraphs dim. Off when TalkBack or high contrast is on.
focus_typewriter=Typewriter
focus_typewriter_body=The line you type stays mid-page.
new_project=New project
rename=Rename
color=Color
add_emoji=Add emoji
create=Create
save=Save
delete_project=Delete %s?
delete_project_body=Notes in this project move to Inbox. They are not deleted.
empty_project=Nothing in %s yet.
move_to=Move to
move=Move
move_n=%d notes
done=Done
capture_content=Capture what’s behind
capture_fail=Couldn’t capture.
share_fail=Couldn’t add that.
shortcut_note=New note
shortcut_list=New checklist
shortcut_ink=New drawing
unlock=Unlock
unlock_title=Unlock note
unlock_fail=Couldn’t unlock.
unlock_need_lock=Set a screen lock to open locked notes.
add_image=Add image
take_photo=Take photo
choose_gallery=Choose from gallery
caption=Caption
replace=Replace
cd_close_image=Close image
empty_pinned=Nothing pinned.
empty_recent=Nothing yet.
empty_projects=A project is a place.
empty_search=Nothing matches.
empty_missing=This note isn’t here.
select_a_note=Select a note.
sign_out_q=Sign out?
sign_out_body=Notes stay on this phone. Sync stops.
delete_account_q=Delete account?
delete_account_body=This removes your sign-in and the cloud copy. Notes on this phone stay.
type_delete=Type DELETE
delete_n=Delete %d notes?
delete_n_body=They go to Trash for 30 days.
deleted_n=%d notes deleted
choose_project=Choose a project
sync_these=Sync these notes?
sync_these_body=%d notes on this phone will upload to your account.
not_now=Not now
sync=Sync
synced=Synced
sync_paused=Sync paused
offline=Offline
sign_out=Sign out
settings=Settings
theme=Theme
theme_system=System
theme_light=Light
theme_dark=Dark
typewriter=Typewriter scrolling
dynamic_color=Dynamic color
trash=Trash
about=About
delete_account=Delete account
conflict_kept=Kept both versions
locked_note=Locked note
locked_fail=This locked note can’t be opened on this device.
create_project=New project
project_name=Name
see_all=See all
today=Today
yesterday=Yesterday
this_week=This week
older=Older
export_fail=Couldn’t export
image_fail=Couldn’t add image
pin_widget_empty=Pin a note
cd_pen=Pen
cd_highlighter=Highlighter
cd_eraser=Eraser
```

Every `cd_*` is a content description. Do not ship without them.

---

## 24. Accessibility (locked)

- All icon buttons 48 dp, described.
- Pin is never color-only (`keep` glyph always).
- Checklist TalkBack: `Checked, {text}` / `Not checked, {text}`.
- Contrast: if dynamic color fails WCAG AA for body on paper, fall back to seed scheme. Test both.
- **Sentence dim default 62%** (≈4.5:1), not 35%. If `high_text_contrast_enabled` or TalkBack/a11y enabled, dim = 100% (Sentence off).
- Unchecked checkbox ring: `#6E5F59` (≈5.1:1), not `outline`.
- Table grid lines: `outline`, not `outlineVariant`.
- `semantics { heading() }` on editor title and project names.
- Don’t announce sync spinner every frame.
- Landscape phone: same editor; toolbar docks above IME; no extra landscape chrome.

---

## 25. Performance budgets (locked)

- Home first frame with 200 notes: **< 200 ms** on Pixel 7-class after process start (exclude process create).
- Open existing note: **< 250 ms** to first text.
- New note to IME: **< 200 ms** from FAB tap.
- Grid scroll: no bind hitch > 8 ms on 120 Hz.
- Coil thumbs: generate 400 px wide JPEG on insert, store `thumbPath`, bind that.
- Only one `BasicTextField` **focused**; all text blocks hold `TextFieldState` in the VM.
- Baseline Profile for: cold start → home → plus → type → back.
- Macrobenchmark on a **device**: P95 frame time < 16 ms for home scroll. Startup +15% fails CI.

---

## 26. Test matrix (you will write these)

1. Create note offline, kill process, note still there.  
2. Sign in, sync, second device sees note < 2 s.  
3. Edit both devices on same note → conflict copy, no data loss.  
4. Predictive back from editor: cancel restores IME if it was open.  
5. Back from home goes to launcher with system animation.  
6. Lock note, plaintext absent from Convex **and** local FTS (search a body word → zero hits).  
6b. Two devices edit the same note without a common base → conflict copy, no silent overwrite.  
7. Delete + undo within 4 s restores.  
8. Widget new note opens editor with IME.  
9. Dynamic color + seed off path both render AA contrast.  
10. Reduce-motion: no shared element, fade only.

---

## 27. Build order (do this in one go, in this order)

0. Pin material3 **1.5.0-alpha** + expressive wrappers + `:baselineprofile` / `:macrobenchmark` modules. Spike `TextFieldState` block swap on a **device**.  
1. Gradle + package + bundled fonts + `NotesupTheme` (expressive motion inherit) + paper grain + empty `MainActivity` edge-to-edge.  
2. Domain (`RichText`, causality fields) + Room + **FTS5** + `InkEntity` + DAOs.  
3. `NoteRepository` local only (no Convex yet).  
4. Home UI + `NotesupFilterPills` + `NotesupSplitCapture` + empty state.  
5. Create note + editor title/paragraph (`TextFieldState`) + autosave.  
6. Shared-bounds (scheme spring) + **PredictiveBackHandler**.  
7. H1–H3, lists, checklist (morph + ring `#6E5F59`), quote, divider. **Then** table + code.  
8. Image picker + Coil + lightbox.  
9. Ink 1.0 canvas + `ink-storage` + preview PNG. Pressure always.  
10. Format toolbar + focus (62% dim, a11y gate).  
11. Overflow: pin, tint, paper, type, delete+undo, project move.  
12. Projects + Inbox. New project = app-bar `add` on Projects only.  
13. Search + FTS5 + verbs.  
14. Export MD + PDF.  
15. Widgets.  
15b. `ROLE_NOTES` + share target + launcher shortcuts + `NoteCaptureActivity` chrome ([ui/17](ui/17-CAPTURE-SHARE.md)).  
16. Clerk init (`clerk-android-api` only) + Welcome + custom `AuthScreen` / `AuthCodeScreen` + skippable forever. **No AuthView.** Settings children + privacy + trash ([ui/15](ui/15-SETTINGS.md)). Lock gate ([ui/18](ui/18-LOCK-GATE.md)).  
17. Convex + causality upsert + webhook + outbox.  
18. Lock/biometric + FTS purge + backup exclude.  
19. Large-screen rail + two-pane ([ui/21](ui/21-LARGE-SCREEN.md)). Image sheet + lightbox ([ui/19](ui/19-MEDIA.md)). Empty table ([ui/20](ui/20-EMPTY-STATES.md)). Edges ([ui/22](ui/22-SYSTEM-EDGES.md)).  
20. Polish: grain, opsz, emphasized type, three rich haptics, reduce-motion, contrast.

Do not start step 16 before step 5 works offline. That is the product.

**v1.1 seams (schema only, no UI):** optional tags, wiki links, version history, passphrase lock. Do not break the note row for those.

---

## 28. What you will not build (if you add one of these you are not following this spec)

A nav bar, a drawer of destinations, onboarding carousel, custom splash logo (system splash is paper-coloured only), pull-to-refresh, masonry grid, Keep candy default, folder trees, tags UI, graph, live collaboration, AI, stickers, canvas home, auth wall, password-only login, Clerk `AuthView` / `clerk-android-ui`, Clerk organizations, Firebase, Auth0, WebView editor, Inter font, OLED pure black, haptic on keystroke, haptic on back drag, confetti, Lottie empty state, ads, analytics in v1, crash SDK in v1 (optional later), fifth widget, reminders, OCR.

---

## 29. Definition of done

A mid-range Android 15+ phone:

- Cold start → home paper, wordmark, pills, FAB.  
- Thumb on FAB → keyboard, empty title, blinking caret, CONFIRM haptic.  
- Type two sentences, insert a photo, draw a line, tick a checkbox.  
- Back gesture: editor shrinks to the card you opened (or the new card), corners 28 dp, home rises, **no buzz**.  
- Kill the app. Note still there.  
- Sign in. Second device shows it.  
- Home screen widget creates another note.

If any of those fail, the app is not Notesup yet.

---

---

## 30. Icon appendix — candidates considered per job

For each job, the same 100-source pool in §7.1 was applied. Below: the shortlist that actually came up, and the lock. Anything not listed lost (wrong metaphor, wrong OS, too cute, too heavy, or breaks the one-family rule).

**Back:** arrow_back, arrow_back_ios, chevron_left, west, keyboard_backspace, close, keyboard_arrow_left, caret-left (Phosphor/Lucide/Feather/Tabler), chevron.backward (SF), ic_ab_back (Samsung). **Lock `arrow_back`.** iOS chevron is a port tell.

**Search:** search, manage_search, pageview, youtube_searched_for, content_paste_search, person_search, magnifying-glass (all outline sets), magnifyingglass (SF). **Lock `search`.**

**Account:** account_circle, account_box, person, person_outline, face, face_6, manage_accounts, person.crop.circle (SF). **Lock `account_circle`.** Photo from Clerk replaces it when present.

**Grid / list:** grid_view, apps, dashboard, window, view_module, layout-grid; view_agenda, view_list, list, reorder, format_list_bulleted, view_headline. **Lock `grid_view` + `view_agenda`.** Agenda matches our card rows better than a thin list glyph.

**Pin:** keep, keep_off, push_pin, push_pin_outlined, star, star_border, bookmark, bookmark_border, flag, sell, label, unarchive. **Lock `keep`.** Star is rating. Bookmark is reading-list. `push_pin` is more literal but noisier at 24 dp Rounded.

**Add / types:** add, add_circle, edit, create, note_add, post_add, edit_note, sticky_note_2, article, description, notes, checklist, check_box, task_alt, draw, brush, gesture, ink_pen, image, add_photo_alternate, photo_camera. **Lock `add` on FAB, `notes` / `checklist` / `draw` / `image` in the menu.**

**Overflow:** more_vert, more_horiz, menu, hamburger, settings (misuse), ellipsis (SF). **Lock `more_vert`.**

**Lock note:** lock, lock_outline, lock_person, https, vpn_key, password, enhanced_encryption. **Lock `lock` / `lock_open`.**

**Delete:** delete, delete_outline, delete_forever, close, backspace, remove_circle. **Lock `delete`.** Forever is for empty trash only (not v1 UI).

**Project / inbox:** folder, folder_open, topic, category, dashboard, layers, inbox, inventory_2, bookmark, label, workspaces. **Lock `layers` + `inbox`.** Folder is the IA we rejected.

**Sync:** sync, cloud, cloud_done, cloud_off, cloud_sync, wifi_off, autorenew, cycle. **Lock `sync` (motion only while connecting) and `cloud_off`. No icon when healthy.**

**Format:** format_bold/italic/underlined/strikethrough, code, link, title, format_size, format_list_bulleted, format_list_numbered, horizontal_rule. **Lock those exact format_* names.** No custom B/I/U letters.

**Ink tools:** edit, create, brush, draw, highlight, ink_highlighter, ink_eraser, auto_fix_off, block, line_weight, palette, colorize, format_paint. **Lock `edit` / `highlight` / `ink_eraser` / `line_weight` / `palette`.**

**Checklist control:** Material check_box was rejected in 80+ premium to-do references (Things, Streaks, Apple Reminders circles). **Lock a custom 22 dp ring**, 2 dp `outline`, fill `primary` + `check` 14 dp `onPrimary` when on. 160 ms.

**Launcher:** letter-N, fountain pen, folded dog-ear, yellow pad, Notion-cube, Keep-bulb, generic note, seal-on-paper. **Lock cream sheet + sealing-wax wedge on ink background.**

If a Symbol name is missing from the font on an older compose icons artifact, export that exact Material Symbol Rounded 24 dp SVG from fonts.google.com/icons into `NotesupIcons.kt`. Do not substitute a different metaphor.

---

*End of spec. Implement this. Do not reopen research to re-decide.*
