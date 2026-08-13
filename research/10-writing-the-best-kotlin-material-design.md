# 10 — Writing the Best Kotlin Material Design

**This is the memory file.** When we write the app, we open this. It is not a Kotlin textbook. It is the subset of Kotlin + Compose + Material that makes Notesup feel expensive and not like a tutorial.

Companion name requested: *Writing the Best Kotlin Material Design*.

## Why Kotlin is the right language for this product

Notesup is a state machine that happens to look like paper:

- A note is a sealed document (blocks)
- Sync is a sealed connection
- UI is a sealed screen
- Every user event is a sealed action

Kotlin's actual luxury features are **sealed hierarchies, null safety, data classes, coroutines/Flow, and extension functions**. If we write Java-shaped Kotlin (mutable `ArrayList`, callbacks, `!!`, god ViewModels), the UI will leak that shape.

Doc importance:

| Doc | Importance | Why |
|---|---:|---|
| kotlinlang.org sealed classes | 10 | UI state, blocks, sync |
| kotlinlang.org data classes / destructuring | 9 | models |
| kotlinlang.org null safety | 10 | never crash a note |
| kotlinx.coroutines + Flow | 10 | Room + Convex |
| developer.android.com architecture | 10 | UDF, Repository, ViewModel |
| developer.android.com Compose | 10 | the UI |
| developer.android.com Material 3 Compose | 10 | Expressive |
| Compose animation / predictive back | 9 | premium motion |
| Haptics principles | 9 | feel |
| Glance | 8 | widgets |
| WindowSizeClass | 8 | foldables |
| Kotlin serialization | 9 | Convex + Room JSON blocks |
| This file | 10 | the house style |

## Language rules (extreme, but only the ones we will feel)

### 1. Model the domain as sealed types

```kotlin
sealed interface Block {
    val id: BlockId
    data class Paragraph(override val id: BlockId, val text: RichText) : Block
    data class Heading(override val id: BlockId, val text: String) : Block
    data class Checklist(override val id: BlockId, val items: List<CheckItem>) : Block
    data class Image(override val id: BlockId, val uri: LocalUri, val remoteId: String?) : Block
    data class Ink(override val id: BlockId, val strokes: StrokeDoc, val preview: LocalUri?) : Block
    data class Divider(override val id: BlockId) : Block
}

sealed interface HomeFilter {
    data object All : HomeFilter
    data object Pinned : HomeFilter
    data object Recent : HomeFilter
    data object Projects : HomeFilter
}

sealed interface EditorUiState {
    data object Loading : EditorUiState
    data class Ready(val note: Note, val focused: BlockId?) : EditorUiState
    data class Locked(val noteId: NoteId) : EditorUiState
    data class Missing(val noteId: NoteId) : EditorUiState
}
```

`when` on these is exhaustive. That is how we avoid "else → show toast" design.

**Reject:** `String type = "image"` flags. **Reject:** `enum class` when the variants hold data.

### 2. Data classes are immutable snapshots

UI receives `Note`, never a mutable document it can corrupt. Edits are functions:

`fun Note.insert(index: Int, block: Block): Note`

Room stores a serialized form. In memory we stay immutable. Undo becomes trivial (stack of `Note`).

### 3. Null means absence, not error

- `projectId: ProjectId?` = Inbox
- `remoteId: String?` = not synced yet
- `preview: LocalUri?` = ink not snapshotted

Never `!!` in UI. Map to sealed state instead.

### 4. Value classes for IDs

`@JvmInline value class NoteId(val raw: String)`

Stops passing a project id into `open(noteId)`.

### 5. Coroutines are the sync spine

- `viewModelScope` for UI work
- `Dispatchers.Default` for JSON / ink snapshot
- `Dispatchers.IO` for disk
- Convex calls already suspend — call them from a repository, not a composable

**Reject:** launching coroutines inside random composables except `LaunchedEffect` for one-shot subscribe if the VM is not ready. Prefer VM.

### 6. Flow / StateFlow is the UI contract

```
Room (local truth) ──► Flow<List<NoteSummary>>
Convex subscribe   ──► reconcile into Room
ViewModel          ──► StateFlow<HomeUiState>
Compose            ──► collectAsStateWithLifecycle
```

Unidirectional. No Compose writing to Convex directly.

### 7. Extension functions for UI vocabulary

```kotlin
fun NoteSummary.previewLines(max: Int = 2): String
fun Note.isEmpty husk(): Boolean
```

Keep them on the domain, not a 400-line `Utils.kt`.

### 8. Named arguments, default arguments, no builder theater

`Note(title = "", blocks = listOf(Block.Paragraph(...)))`

### 9. No `Any`, no `Map<String, Any?>` at the UI boundary

Convex Android uses `Map<String, Any?>` on the wire. That map **dies in the repository**. UI sees `Note`.

Official caution from Convex Android docs we accept: number types, `_id` / `_creationTime` field mapping, reserved Kotlin keywords. Handle in DTOs, not models.

## Compose rules that read as premium

### Structure

```
Screen
  ViewModel (state + events)
    Repository
      LocalDataSource (Room)
      RemoteDataSource (ConvexClientWithAuth)
      Auth (Clerk)
```

Composables are **dumb** and **restart-safe**.

### Theme is a first-class composable

```kotlin
@Composable
fun NotesupTheme(
    dark: Boolean = isSystemInDarkTheme(),
    dynamic: Boolean = true,
    content: @Composable () -> Unit,
) {
    val scheme = rememberNotesupScheme(dark, dynamic)
    MaterialTheme(
        colorScheme = scheme,
        typography = NotesupType,
        shapes = NotesupShapes,
        // motionScheme when using Expressive theme APIs
        content = content,
    )
}
```

Every color in the app comes from `MaterialTheme.colorScheme` or a semantic extension (`pinContainer`). No `Color(0xFF...)` in screens.

### Expressive opt-in is allowed, catalog-app disease is not

```kotlin
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
```

Use it for: `ButtonGroup`, `SplitButton`, `HorizontalFloatingToolbar`, animated `ButtonDefaults.shapes()`, expressive menus.

Do not use it to sprinkle 14 new widgets on home.

### Lists

- `LazyVerticalGrid` / `LazyColumn` with `key = { it.id.raw }`
- `remember` nothing heavy per item
- One `AsyncImage` size, exact

### Text

- Editor title: `BasicTextField` + `TextStyle` from theme
- Body: one focused field at a time (file 05)
- Never `Text(fontSize = 14.sp)` — theme roles only

### Motion

- `Modifier.sharedBounds` / shared element for card → editor
- `AnimatedContent` for filter changes
- `animateFloatAsState` sparingly
- Predictive back on editor
- IME insets: `WindowInsets.ime` animation, toolbar sits above

### Haptics

```kotlin
val view = LocalView.current
view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
```

One helper: `NotesupHaptics.confirm(view)` so we can no-op when the user disabled it.

### Edge to edge

`enableEdgeToEdge()` in Activity. Scaffolds consume insets. FAB sits above gesture nav. No fake bottom bar spacer.

### Window size

`WindowSizeClass`: phone compact = pills + FAB. Medium/expanded = `NavigationRail` + optional two-pane (list | editor).

### Accessibility

- All icon buttons have `contentDescription`
- Contrast on tinted cards
- Don't rely on color for pin (glyph + container)
- Minimum 48dp touch on FAB / pills
- Reduce motion: skip shared element if `AccessibilityManager` asks

## Material Expressive — how we write it in Kotlin

Patterns we will literally use (from official samples, adapted):

1. **Connected filter pills** — `ToggleButton` + `ButtonGroupDefaults.connectedLeading/Middle/TrailingButtonShapes()`
2. **Split capture** — `SplitButton` primary `{ createNote() }` secondary `{ showTypes = true }`
3. **Editor toolbar** — `HorizontalFloatingToolbar(expanded, floatingActionButton = null)`
4. **Animated button shapes** — `Button(shapes = ButtonDefaults.shapes())` for primary CTAs only
5. **Vibrant toolbar colors** only if they still look like paper. Prefer standard tonal.

## Clerk + Convex in Kotlin (the glue we will write)

From official docs (files 11–12):

```kotlin
class NotesupApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Clerk.initialize(this, BuildConfig.CLERK_PK)
        // ClerkConvexAuthProvider → ConvexClientWithAuth
    }
}
```

UI:

```kotlin
val ui by viewModel.state.collectAsStateWithLifecycle()
when (ui.auth) {
    Auth.Loading -> NotesupMark()          // not a spinner forever
    Auth.SignedOut -> optional // local still works
    Auth.SignedIn -> /* avatar */
}
```

**Product rule in code form:** creating a note never waits on `SignedIn`. Room first. Convex when a session exists.

## Room as source of truth

- `NoteEntity` + `Block` payload (JSON via kotlinx.serialization)
- DAOs expose `Flow`
- Writes are transactional
- Remote IDs are columns, not the primary key
- Local UUID primary keys so offline create works

## Error handling

```kotlin
sealed interface WriteResult {
    data object Ok : WriteResult
    data class Convex(val error: ConvexError) : WriteResult
    data object OfflineQueued : WriteResult
    data class Disk(val reason: String) : WriteResult
}
```

UI maps these to quiet banners, not crash dialogs.

## Testing (we will care later)

- Domain functions: unit tests, no Android
- ViewModel: Turbine on StateFlow
- `ConvexClient` is `open` — fake it (official guidance)
- Compose: screenshot tests for card, pills, editor chrome — this is how we keep premium from regressing

## Anti-patterns (seen in 90% of GitHub Kotlin notes apps)

| Anti-pattern | Why it looks cheap |
|---|---|
| One Activity, all UI in `MainActivity.kt` | Can't theme, can't test |
| `viewModel.insert(title: String, content: String)` | Not a document |
| `Color.Blue` FAB | Not Material You |
| `LazyColumn` of `Card` with 16.dp everywhere | Tutorial Material |
| Navigation of 5 destinations | Fake product |
| `!!` on `note.title` | Crash on empty |
| LiveData in 2026 | Use Flow |
| `GlobalScope` | Leaks notes |
| Images loaded on Main | Jank |
| Custom canvas theme that ignores `MaterialTheme` | Port |

## Kotlin style we accept

- Official Kotlin coding conventions
- Trailing commas
- Expression functions when they're clearer
- `copy()` over mutation
- `buildList { }` for block edits
- Context receivers / context parameters: **only** if the team is fluent; not required for v1

## What "best Kotlin Material Design" means in one paragraph

Every screen is a function of a sealed state. Every color is a token. Every motion is a `MotionScheme` or a shared element. Every haptic is a platform constant. Every note is an immutable `List<Block>` with a local id. Compose never talks to the network. Expressive components appear where they earn the thumb: pills, split create, floating editor toolbar. The rest is paper.

## Remember

If a Kotlin trick does not make the note more honest, faster to open, or harder to crash, it does not belong in Notesup.
