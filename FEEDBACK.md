# FEEDBACK — review + solutions

**From:** Claude (Opus 5) · **Date:** 2026-08-14 · **Round:** 1
**Reviewed:** all 26 markdown files, ~6,100 lines.
**Method:** every load-bearing technical claim re-verified against primary sources. Palette re-derived numerically against WCAG rather than trusted. Sources at the bottom.

## How to use this file

Every item has a stable ID (`F-01`…), a **Problem**, a **Solution** I'd actually implement, and an open **Decision** line. Reply inline under any item — I'll read your and ChatGPT's responses and revise in round 2. Nothing here is a blocker; it's a worklist.

**Severity:** 🔴 build-breaking · 🟠 will hurt · 🟡 polish · 🟢 pure upgrade

**Jump:** [§1 what's good](#1-what-i-like) · [§2 correctness fixes](#2-correctness--verified-against-primary-sources) · [§3 architecture](#3-architecture-fixes) · [§4 design system](#4-design-system-fixes) · [§5 premium upgrades](#5-premium-upgrades-net-new) · [§6 contradictions](#6-contradiction-sweep) · [§7 order](#7-suggested-order) · [§8 open questions for you](#8-open-questions-i-cant-answer-alone)

---

## 1. What I like

Short, because you want the worklist — but these are load-bearing and I'd defend them.

- **The precedence rule** (`PANE.md:10`). BUILD wins product, `ui/` wins look, official token beats both. Almost no doc set has conflict resolution. It's why 6,100 lines stayed coherent.
- **`RESEARCH-LOG.md` as a citation ledger.** It already caught real errors (28/34→28/36, widget radius, killing the fake `editorScale` transform). A spec that catches itself is worth ten confident ones.
- **Negative space specified as hard as positive space** — §28, `00-ANTI-JOKE.md`, the `None` haptics row. Saying no in writing is what stops month-four drift.
- **The grayscale test and thumb test.** Falsifiable design criteria. Rare.
- **Room-as-truth.** Not just good UX — see F-06, it's structurally mandatory.
- **The palette.** I computed every pair; it passes comfortably. Details in F-14.
- **Custom 22 dp checkbox ring over Material `CheckBox`.** Correct, and correctly identified as review-bait.
- **Build order §27** putting Clerk at 16 / Convex at 17.

---

## 2. Correctness — verified against primary sources

### F-01 🔴 Material 3 Expressive components are **not** in stable Material3

**Problem.** `READY.md:12` says *"Material3 1.4.0 graduated SplitButton off experimental."* The opposite happened. All `@ExperimentalMaterial3ExpressiveApi` APIs were **removed from the 1.4.0 branch** at `1.4.0-beta01`. **1.4.0 stable is the non-Expressive baseline.** Expressive lives only in `1.5.0-alphaXX`. `SplitButtonLayout` was also **renamed to `SplitButton`** mid-alpha — so the API churned visibly during the window you're building in.

| Component | Spec assumes | Reality |
|---|---|---|
| `SplitButton` — **jewel #1** | 1.4.0 stable | 1.5.0-alpha20+ |
| `ButtonGroup` — **jewel #2** | 1.4.0 stable | 1.5.0-alpha22+ |
| `HorizontalFloatingToolbar` | 1.4.0 stable | 1.5.0-alpha21+ |
| `FloatingActionButtonMenu` | 1.4.0 stable | 1.5.0-alpha19+ |
| `MotionScheme.expressive()` | 1.4.0 stable | 1.5.0-alpha15+ |

Both declared jewels (`ui/01-VISUAL-LANGUAGE.md:83-87`) plus the editor toolbar sit on alpha.

**Solution — ship on alpha, but quarantine it.** Don't hand-roll these on stable primitives; you'd lose shape-morph and spring behaviour, which is the entire point, and you'd maintain a Material fork forever. Instead:

1. Pin exactly. No ranges, no `+`:
```toml
# libs.versions.toml
material3 = "1.5.0-alpha26"   # ALPHA. Expressive lives here. Re-verify every bump.
```
2. **One wrapper file per jewel** — `ui/common/expressive/`. Nothing else in the app may import `SplitButton`, `ButtonGroup`, or `FloatingToolbar` directly. Enforce with a lint rule or a Konsist test.
```kotlin
// ui/common/expressive/NotesupSplitCapture.kt
// ⚠ ALPHA SURFACE — androidx.compose.material3:1.5.0-alpha26
// Renamed SplitButtonLayout -> SplitButton in alpha25. Expect churn.
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotesupSplitCapture(
    onCreate: () -> Unit,
    onPickType: (NoteType) -> Unit,
    modifier: Modifier = Modifier,
) { /* the only place SplitButton is referenced */ }
```
3. Add a CI check that fails if `material3` resolves to a version you haven't reviewed.

Cost: ~half a day. It converts "alpha dependency" from a recurring crisis into a three-file diff.

> **DECISION F-01:** ⬜ open — ship on `1.5.0-alpha` behind wrappers? (my rec: yes)

---

### F-02 🔴 Roboto Flex cannot be loaded the way the spec says

**Problem.** `BUILD.md:235` — *"Load from Google Fonts via `ui-text-google-fonts`."* **Variable fonts are not supported via downloadable fonts** (known open feature request). You'd silently get a static instance and lose every axis — `wght`, `opsz`, `GRAD`, `wdth` — which is the only reason to pick Roboto Flex over Roboto.

Second-order: `FEATURES-NOW-LOCKED.md:180-193` locks **12 user fonts**. Via the downloadable provider that's a network fetch + fallback flash the first time someone picks Literata — violating law #1, *"the paper is always already there"* (`UX-PEOPLE-GO-TO-WAR-FOR.md:47`).

**Solution — bundle, and drive the axes deliberately.**

```kotlin
// res/font/roboto_flex_variable.ttf  (bundled, ~1.6 MB)
val RobotoFlex = FontFamily(
    Font(R.font.roboto_flex_variable, variationSettings = FontVariation.Settings(
        FontVariation.weight(400),
        FontVariation.opticalSizing(18.sp),   // ← see F-22, this is the premium bit
        FontVariation.grade(0),
    ))
)
```
For the 12-font picker, three tiers so you keep the feature without the network:
- **Bundled (4):** Roboto Flex (default), Literata (reading serif), JetBrains Mono (code), Atkinson Hyperlegible (a11y). ~4 MB total.
- **On-demand (8):** the rest via downloadable fonts, but **only static faces**, downloaded on selection with a visible one-time "Downloading…" in *Settings* — never in the editor. Cache permanently.
- **Rule:** a note's font never blocks first paint. If the face isn't resident, render Roboto Flex now and swap on next composition.

That keeps all 12 and never breaks law #1.

> **DECISION F-02:** ⬜ open — bundle 4 + on-demand 8? or bundle all 12 (~10 MB APK)?

---

### F-03 🟠 Material Symbols Rounded has no Compose dependency, and the suggested coordinate doesn't exist

**Problem.** `BUILD.md:120` hedges across four options in one cell and lands on `io.github.asodja:compose-material-symbols` — **I could not find that artifact.** Real community options are `ClementVicart/compose-material-symbols` and `tclement0922/material-symbols-compose`. Meanwhile `material-icons-extended` is officially **no longer maintained or recommended** (old look, significant build-time cost), and Material3 **stopped transitively pulling `material-icons-core`**.

**Solution — own your ~60 icons.** The spec's own instinct at `BUILD.md:1307` is right; make it the only path.

1. From fonts.google.com/icons export each locked glyph as **Material Symbols Rounded, 24 dp, weight 400, grade 0, optical size 24** → SVG.
2. Android Studio → *Vector Asset* → import → `res/drawable/ic_*.xml`.
3. Generate one accessor object so call sites are typed and the one-family rule is mechanically enforced:
```kotlin
object NotesupIcons {
    val Back    @Composable get() = ImageVector.vectorResource(R.drawable.ic_arrow_back)
    val Pin     @Composable get() = ImageVector.vectorResource(R.drawable.ic_keep)
    // …~60 total
}
```
4. **Delete the other three options from `BUILD.md:120`.** That cell is currently the least-decided thing in a document whose premise is that everything is decided.

Cost: ~2 hours once. Benefit: a few KB, zero build-time cost, zero dependency risk, guaranteed single family.

> **DECISION F-03:** ⬜ open — hand-export 60 vectors? (my rec: yes, it's the only correct path)

---

### F-04 🟠 Positioning: Google Keep already shipped Material 3 Expressive

**Problem.** `Let's do it.md:88` — *"Android's own note apps are not premium. Keep is fast and cheap-looking."* Keep's M3 Expressive redesign rolled out from July 2025, broadly available by August 2025, **including dynamic theming from wallpaper**. "We're the Expressive one" is no longer a differentiator, and Keep is preinstalled.

**Solution — move the claim to ground Google structurally won't take.** Four things survive and should become the pitch:

1. **You can actually write in it.** Keep is a capture tool at 14 sp; nobody drafts 800 words in it. `bodyNote` 18/28 + focus modes + typewriter is a real gap.
2. **Ink as a first-class in-note block**, on Jetpack Ink 1.0. Keep's drawing is an afterthought. This is the widest moat you have.
3. **Paper, not candy.** Keep is coloured cards; you're a warm page. A taste position, not a feature.
4. **No account required.** Keep is Google-account-gated by construction. You literally cannot be out-competed on this.

Concretely: replace the positioning paragraph in `Let's do it.md` and `research/14` with —
> *Notesup is the Android notes app you can actually write in: a warm page that opens instantly, holds text, images, tables and real ink in one document, and never asks who you are.*

> **DECISION F-04:** ⬜ open — adopt the reframe? anything you'd add to the four?

---

### F-05 🟡 `compose-rich-editor` is still `1.0.0-rc13/rc14`

**Problem.** `BUILD.md:136` puts a pre-1.0 third-party library in the single hottest path in the app. The containment (inside the Paragraph block only) is correct. The risk is that `Block.Paragraph.json` (`BUILD.md:915`) persists **the library's internal format** — so your note file format becomes someone else's implementation detail, and swapping editors later becomes a data migration.

**Solution — own the span format at the boundary.**
```kotlin
// domain/model/RichText.kt — OUR format, versioned, never the library's
@Serializable data class RichSpan(val start: Int, val end: Int, val style: SpanStyleTag)
@Serializable data class RichText(val v: Int = 1, val text: String, val spans: List<RichSpan>)
@Serializable enum class SpanStyleTag { BOLD, ITALIC, UNDERLINE, STRIKE, CODE, LINK }
```
Map `RichText ↔ RichTextState` at the block composable boundary only. Then the library is a rendering choice, not a storage decision, and `plaintext` for FTS (F-08) falls out for free from `RichText.text`.

> **DECISION F-05:** ⬜ open — own the span format? (my rec: yes, ~60 lines, removes the whole risk)

---

### F-06 🟠 Convex has **no** offline sync — so your conflict logic is entirely yours, and it's currently unsound

**Problem.** Confirmed from Convex's own material: no full offline sync mechanism; it handles network blips only. Your Room-first design is therefore not a nicety, it's the only thing that makes the product work. Good call. **But** it means §20.4 is hand-rolled with no safety net, and it has two real defects:

- `BUILD.md:1065` decides conflicts on *"local `updatedAt` within 10 s of remote."* That's **wall-clock across two devices.** 30 seconds of normal clock skew silently flips this between "conflict copy" and **data loss**.
- `BUILD.md:1048` branches the *server* on "both dirty windows conflict." The server cannot know client dirtiness. The condition is unevaluable where it's written.

**Solution — replace timestamps with causality.** ~40 lines, removes the entire class of bug.

Give each install a UUID. Each note carries `(writerId, rev)`. Server stores the last accepted pair.

```kotlin
// Local
val installId: String        // uuid, generated once, DataStore
data class Note(/*…*/ val rev: Long, val writerId: String, val baseRev: Long, val baseWriterId: String?)
// baseRev/baseWriterId = the (writerId,rev) this edit was derived from
```
```ts
// convex/notes.ts — upsert
const existing = await byLocalId(args.localId);
if (!existing) return insert(args);

const descendsFromServer =
  args.baseWriterId === existing.writerId && args.baseRev === existing.rev;

if (descendsFromServer) {
  return patch(existing._id, { ...args });          // fast-forward, safe
}
// true divergence — neither side wins, both survive
const conflictId = await insert({ ...args, localId: newLocalId(), title: args.title + " (conflict)" });
return { conflictLocalId: conflictId };
```
Client after ack: set `baseRev = rev`, `baseWriterId = installId`.

**`updatedAt` is now display and sort only. Never a correctness input.** Add that as a one-line law in `BUILD.md` §20 — it's the kind of rule that decays without a written prohibition.

> **DECISION F-06:** ⬜ open — adopt `(writerId, rev)` causality? (my rec: strongly yes)

---

### F-07 🟡 `ROLE_NOTES` is in READY but absent from the manifest and the build order

**Problem.** `READY.md:15` correctly identifies the "Android-best citizen" play. `BUILD.md:140-152`'s manifest has none of it and §27 never schedules it. This is lock-screen capture and stylus-tail-button launch — genuinely premium, genuinely native, and something Keep has been slow on.

**Solution — add now, it's ~30 lines.** Requirements confirmed: target SDK 34+, handle `ACTION_CREATE_NOTE` with category DEFAULT, declare `showWhenLocked` + `turnScreenOn`, honour `EXTRA_USE_STYLUS_MODE`.

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
```kotlin
val stylus = intent.getBooleanExtra(Intent.EXTRA_USE_STYLUS_MODE, false)
// stylus == true → open straight into an ink note, canvas focused, toolbar up
```
Then offer the role from Settings via `RoleManager.createRequestRoleIntent(ROLE_NOTES)` — never on first launch (that's an auth-wall-shaped ask). Schedule as **step 15b**, right after widgets.

> **DECISION F-07:** ⬜ open — in for v1? (my rec: yes, it's small and it's the most "native premium" thing available)

---

## 3. Architecture fixes

### F-08 🟠 FTS can't index a JSON blob, and locking doesn't purge the index

**Problem.** `BUILD.md:938` says FTS4 on `title + plaintext(blocks)`. `plaintext(blocks)` is a function of a JSON string — SQLite cannot index it. Worse: **when a note is locked, its plaintext stays in the FTS index.** So `BUILD.md:1214` test 6 ("plaintext absent from Convex") passes while the body remains fully searchable locally. That's a privacy hole in the one feature whose entire purpose is privacy.

**Solution.**
1. Materialize a `plaintext` column on `NoteEntity`, recomputed on every write (trivial once F-05 lands — it's `RichText.text` joined).
2. **Use FTS5, not FTS4** — you get `ORDER BY rank` for free instead of hand-rolling relevance. Room supports `@Fts5`; minSdk 26 is fine.
3. Use an **external-content** table so note text isn't stored twice.
4. On lock: `plaintext = ""` and delete the FTS row *in the same transaction* as encryption. On unlock: reinsert.
```kotlin
@Transaction
suspend fun lock(id: NoteId, cipher: ByteArray) {
    noteDao.setLocked(id, cipher, plaintext = "")
    ftsDao.delete(id)          // ← the line that closes the hole
}
```
5. Add to `BUILD.md` §26 test matrix: *"Lock a note, search a word from its body, expect zero results."*

> **DECISION F-08:** ⬜ open — FTS5 + purge-on-lock? (my rec: yes, item 4 is non-negotiable)

---

### F-09 🟠 Ink strokes inside the note JSON will cause the exact jank you're preventing

**Problem.** `BUILD.md:930` puts `strokeJson` inside `Block.Ink`; `:937` stores all blocks as one JSON string; `:742` autosaves that string on a 50 ms keystroke debounce. **Typing one character in a note containing a drawing rewrites every stroke, 20×/second.** A page of handwriting is hundreds of KB. This directly attacks `BUILD.md:1201` ("no bind hitch > 8 ms").

**Solution — treat ink exactly like media, which you already do correctly.**
```kotlin
data class Ink(override val id: BlockId, val inkId: InkId, val previewPath: String?) : Block
```
```kotlin
@Entity data class InkEntity(
    @PrimaryKey val id: String,
    val noteId: String,
    val strokeBlob: ByteArray,   // ink-storage serialization, NOT json
    val heightDp: Float,
    val updatedAt: Long,
)
```
Serialize with `ink-storage` (binary, purpose-built) rather than JSON coordinates — smaller and faster. Sync as a blob like an image. **This is cheaper to build than the current design, not more expensive**, and it makes note mutations small forever.

> **DECISION F-09:** ⬜ open — split ink into its own table? (my rec: yes)

---

### F-10 🟠 The editor's riskiest decision has a modern API the spec doesn't know about

**Problem.** `ui/07-EDITOR.md:40` — *"Only the focused text block is a field. Others are `Text`."* Swapping `Text` ↔ `BasicTextField` on focus is the highest-risk interaction in the product (`READY.md:24` agrees). Classic failure modes: caret jumps, IME desync, selection loss on recomposition, state lost on rotation.

**Solution — state-based `BasicTextField` with `TextFieldState`.** This is now the official best practice and it removes most of the risk. `TextFieldState` persists across recomposition and configuration change, survives process death with `rememberSaveable`, and eliminates the whole class of value/callback sync bugs the old API created.

```kotlin
// one TextFieldState per block, held by the ViewModel — NOT recreated on focus change
class EditorViewModel : ViewModel() {
    val blockStates = mutableStateMapOf<BlockId, TextFieldState>()
    fun stateFor(id: BlockId, initial: String) =
        blockStates.getOrPut(id) { TextFieldState(initial) }
}
```
Because the state lives outside the composable, the `Text`↔field swap no longer moves any state — it's purely a rendering swap, and the caret survives it. Then:

- Derive `plaintext` (F-08) from `state.text` via `snapshotFlow`, debounced 50 ms.
- **Tap-to-caret-at-tap-position** on an unfocused `Text`: capture the tap offset, resolve it with `TextLayoutResult.getOffsetForPosition()`, then set `state.edit { selection = TextRange(offset) }` on focus. This is the detail that makes the swap invisible, and it's the one thing I'd prototype before anything else.

> **DECISION F-10:** ⬜ open — adopt `TextFieldState`? (my rec: yes, and spike this before building the rest of the editor)

---

### F-11 🟠 Auto-backup will silently create permanently unopenable notes

**Problem.** `BUILD.md:145` sets `allowBackup="true"`. Locked notes use an AndroidKeyStore key, which is **hardware-bound and never leaves the device**. Backup → restore on a new phone → every locked note is ciphertext with no key, forever. The spec anticipates the *message* (`locked_fail`) but not the *cause*, and auto-backup manufactures this state for anyone who upgrades their phone.

**Solution — pick one, deliberately.**

- **(a) Simple:** exclude the DB from backup. `res/xml/data_extraction_rules.xml` is already in your file tree at `BUILD.md:101` and currently unspecified:
```xml
<data-extraction-rules>
  <cloud-backup><exclude domain="database" path="notesup.db"/></cloud-backup>
  <device-transfer><exclude domain="database" path="notesup.db"/></device-transfer>
</data-extraction-rules>
```
  Cost: locked notes don't survive device transfer at all — but at least honestly, rather than as silent corruption.
- **(b) Better, ~half a day:** derive the lock key from a **passphrase** (PBKDF2/Argon2), keep it in the Keystore for biometric convenience, but make the passphrase the root of trust. Then restore is recoverable, and locked notes can sync and restore.

I'd take (b) — a lock feature that loses data on phone upgrade isn't a premium feature. But (a) shipped is better than (b) deferred.

> **DECISION F-11:** ⬜ open — (a) exclude, or (b) passphrase-derived key?

---

### F-12 🟠 Largest omission in the corpus: no performance harness

**Problem.** You have hard budgets (`BUILD.md:1198-1201`: home < 200 ms, FAB→IME < 200 ms, no bind hitch > 8 ms), **no analytics, no crash SDK** (`§28`), and no baseline profiles anywhere. So you have no way to know if you hit any of it.

**Solution.**
1. **Baseline Profiles.** Reported at **30–40% faster startup** and the single biggest lever on Compose cold start. Add the `androidx.baselineprofile` Gradle plugin + a `:baselineprofile` module; drive the generator through the critical journey with UIAutomator: cold start → home → tap plus → type → back.
2. **Macrobenchmark** on a real device (never the emulator) for `startupCompose` and `scrollHome`. Target **P95 frame time < 16 ms**.
3. **Strong skipping** is default since Compose 1.7 — verify it's on, and don't paper over instability with `@Stable`; annotating everything hides recompositions rather than fixing them. Note `LazyListScope` lambdas are **not** auto-memoized — wrap block callbacks in `remember(key)`.
4. Add one CI gate: startup regression > 15% fails the build.

This is the difference between "we specified 200 ms" and "we ship 200 ms."

> **DECISION F-12:** ⬜ open — add `:baselineprofile` + `:macrobenchmark` modules in v1? (my rec: yes, before feature code)

---

## 4. Design system fixes

### F-13 🟠 The motion system contradicts the design language it claims

**Problem.** `BUILD.md:311` sets `MotionScheme.expressive()`. Then `:314-326` and all of `ui/05-MOTION.md` lock **every animation to milliseconds**. These are two different systems and M3 Expressive deliberately replaced the first with the second.

Expressive's headline change is a **spring-based motion-physics system** — spatial springs for movement, effects springs for colour/opacity, defined by *stiffness, damping, initial velocity*, not duration. ~21 Compose components inherit it automatically from the theme. `expressive` has visible overshoot; `standard` doesn't.

So the spec adopts Expressive and then overrides its defining characteristic everywhere. Result: neither the tuned durations of M3 baseline nor the springiness of Expressive. It'll read as "slightly off" and be near-impossible to debug, because every individual number is defensible.

**Solution — this is the highest-leverage change in the whole document.**

1. Keep `MotionScheme.expressive()` at theme level and **let components inherit**. Delete per-component durations for anything Material animates: pills, menus, sheets, FAB, toolbar, selection, dialogs.
2. Keep explicit durations **only** where Material owns nothing:
   | Keep | ms | Why |
   |---|---:|---|
   | Focus chrome fade | 240 | ours |
   | Sentence dim | 200 | ours |
   | Sync glyph rotation | 1200/turn | ours |
   | Snackbar dwell | 4000 | ours |
3. Card↔editor `sharedBounds` keeps an explicit spec (shared transitions genuinely need one) but express it as a **spring from the scheme**, not `tween`, so it matches its neighbours:
```kotlin
val spatial = MaterialTheme.motionScheme.slowSpatialSpec<Rect>()
Modifier.sharedBounds(..., boundsTransform = { _, _ -> spatial })
```
4. **Rewrite `ui/05-MOTION.md`'s table with a third column: "owner — component / us."** Roughly two-thirds of those rows should read *component* and carry no number at all.

> **DECISION F-13:** ⬜ open — go springs-first? (my rec: yes; biggest single quality win available)

---

### F-14 🟡 Palette verified — it's good. Two fixes.

I computed WCAG contrast for every meaningful pair in `BUILD.md:170-208`:

| Pair | Ratio | AA body |
|---|---:|---|
| `onSurface` on paper | **15.56** | PASS |
| `onSurface` on card `#F1EBE3` | **14.77** | PASS |
| `onSurfaceVariant` on card (preview text) | **7.86** | PASS |
| `onPrimary` on `primary` (the plus) | **8.42** | PASS |
| `onPrimaryContainer` on `primaryContainer` (pill) | **13.30** | PASS |
| `onTertiaryContainer` on `tertiaryContainer` (pinned) | **13.32** | PASS |
| `primary` on paper (caret, links) | **7.49** | PASS |
| DARK `onSurface` on `#161311` | **15.91** | PASS |
| DARK `onSurfaceVariant` on card | **9.96** | PASS |

All eight note tints at 8% keep body text between **12.96 and 13.35** — the tint system genuinely doesn't hurt legibility, exactly as `ui/02-COLOR.md:39` promised. **This palette is well built. Ship it.**

Two fixes:

- **`outlineVariant` #D6C3BC on paper = 1.51.** Fine for decorative hairlines. **But `ui/07-EDITOR.md:67` uses it for table cell borders**, and table structure is semantic — the borders *are* how rows and columns are perceived. Near-invisible, worse on `legal`/`kraft` washes. → **Use `outline` for table grid lines.**
- **`outline` #84746E on paper = 3.97.** Passes the 3:1 non-text bar for the checkbox ring, but not generously; faint in sunlight. → **Use `#6E5F59` for the unchecked ring specifically** (≈5.1:1) while leaving `outline` alone elsewhere.

> **DECISION F-14:** ⬜ open — both fixes in?

---

### F-15 🟡 Sentence focus mode fails contrast badly and nothing catches it

**Problem.** `UX-PEOPLE-GO-TO-WAR-FOR.md:83` / `ui/03-TYPE.md:60` dim inactive paragraphs to **35%**. Computed:

| Mode | Result | Ratio |
|---|---|---:|
| Light @ 35% | `#AAA5A0` | **2.17** ✗ |
| Dark @ 35% | `#635F5C` | **2.93** ✗ |
| Light @ 55% | `#7E7A76` | 3.79 ✗ |

`BUILD.md:1190` promises a contrast guard, but it only inspects the **theme**. Sentence mode creates the failure *after* theming, so the guard never fires. That's real body text at 2.17:1.

**Solution — keep the effect, floor it, and gate it.**
1. Default dim **62%** (≈4.5:1), not 35%. Still an obvious focus effect.
2. Expose a slider 35–80% for people who want full iA — informed choice, not default.
3. **Force Sentence off** when the OS asks:
```kotlin
val a11y = LocalContext.current.getSystemService<AccessibilityManager>()
val highContrast = Settings.Secure.getInt(cr, "high_text_contrast_enabled", 0) == 1
val dim = if (highContrast || a11y?.isEnabled == true) 1f else userDim
```
4. Write both rules into `BUILD.md` §24, which is currently silent.

> **DECISION F-15:** ⬜ open — 62% default + slider + a11y gate?

---

### F-16 🟡 Tablet measure is too wide to read comfortably

**Problem.** `BUILD.md:260` sets tablet editor max width **720 dp** at 18 sp. Typographic research is settled: **50–75 characters per line, ~66 optimal** (Bringhurst 45–75); past ~80 CPL text is measurably skipped more. 720 dp at 18 sp ≈ **80 CPL** — right at the degradation point.

Phone is fine: ~320 dp ≈ 35 CPL, inside the 30–50 mobile range.

**Solution.** Change `720 dp` → **`640 dp`** (≈70 CPL). One number. It also makes the tablet layout read as *a page* rather than a stretched phone — which is the stated metaphor.

> **DECISION F-16:** ⬜ open — 640 dp?

---

## 5. Premium upgrades (net new)

You asked for more premium ways to do this. These aren't corrections — they're things the spec doesn't have that would make it feel more expensive. Ordered by impact per unit effort.

### F-17 🟢 Paper grain — the single cheapest premium upgrade available

`ui/01-VISUAL-LANGUAGE.md` commits hard to the paper metaphor, then every surface is a **flat fill**. That's the gap between "warm beige app" and "paper."

**Solution — AGSL noise shader on `background`, with a documented fallback.** AGSL is Android 13+ (API 33); your minSdk is 26, so the version-gated pattern is standard practice:
```kotlin
@Composable
fun Modifier.paperGrain(intensity: Float = 0.03f) =
    if (Build.VERSION.SDK_INT >= 33) this.then(grainShaderModifier(intensity))
    else this.then(Modifier.paint(painterResource(R.drawable.paper_grain_tile), alpha = intensity))
```
Uniforms worth exposing: `grainIntensity` and `fiberIntensity` (paper fibre size). Keep it at **2–3%** — at 5% it looks like a filter, at 3% it looks like paper and nobody can tell you why. Tile a 128×128 PNG for the pre-33 path.

> **DECISION F-17:** ⬜ open — add grain? (my rec: yes, highest ratio of "feels expensive" to effort in this list)

---

### F-18 🟢 Optical sizing — the detail almost nobody on Android ships

Once Roboto Flex is bundled (F-02), drive the **`opsz` axis from the type role**. Display sizes get tighter apertures and tighter spacing; `bodyNote` gets looser, more open forms at reading size. This is exactly what the axis is for, and M3 Expressive explicitly encourages tuning variable axes (`wght`, `GRAD`, `wdth`, `opsz`) to match typographic feel to content tone.

```kotlin
fun notesupStyle(size: TextUnit, weight: Int) = TextStyle(
    fontFamily = RobotoFlex, fontSize = size,
    fontVariationSettings = FontVariation.Settings(
        FontVariation.weight(weight),
        FontVariation.opticalSizing(size),   // ← the whole trick
    )
)
```
Cost: ~20 lines. Effect: everyone notices, nobody can name it. That's the definition of premium.

> **DECISION F-18:** ⬜ open — wire `opsz` to type role?

---

### F-19 🟢 Emphasized type styles — you're leaving half the M3 type system unused

M3 Expressive added a **parallel set of 15 emphasized styles** with higher weights, meant for headlines, selected items, and focal points. `ui/03-TYPE.md` uses only the standard set and hand-specifies "weight 600" for headings.

**Solution:** use emphasized roles for the editor title, card titles, selected pill labels, and H1–H3 instead of hand-picked weights. They're designed to pair with variable axes and will be more coherent than numbers you chose by eye. Also removes the `600` magic number scattered through `ui/03-TYPE.md:24` and `ui/07-EDITOR.md:57-59`.

> **DECISION F-19:** ⬜ open — swap hand-weights for emphasized roles?

---

### F-20 🟢 Shape morphing — currently used twice, should be the signature

M3 Expressive shipped **35 new shapes and built-in shape morphing**, and it's the thing that reads as "designed" rather than "default Material." You use it in exactly two places (`ui/05-MOTION.md:57` tool select, grid/list toggle).

**Three more, all free:**
1. **The split plus morphs when its menu opens.** The component does this natively — `ui/06-HOME.md:86` already notes the trailing button spins; let the *shape* morph too. This is jewel #1 doing its job.
2. **The checkbox ring morphs as it fills** — circle → soft squircle over the 200 ms. Things 3 spent a year on this circle (`UX-PEOPLE-GO-TO-WAR-FOR.md:65`); morph is how you beat it rather than match it.
3. **Pinned cards get a subtly different shape**, not just `tertiaryContainer`. A 24 dp card with one differently-radiused corner reads as "special" pre-attentively — and it survives the grayscale test, which a colour-only pin signal does not.

> **DECISION F-20:** ⬜ open — which of the three?

---

### F-21 🟢 Richer haptics without breaking your discipline

`BUILD.md:62` bans everything but `HapticFeedbackConstants`. The reasoning (no 300 ms buzzes, respect system settings, no permission) is right. But it leaves real expressiveness unused: `VibrationEffect.startComposition()` builds sequences from primitives with **scalable intensity** — `addPrimitive(id, scale, delay)` — using `PRIMITIVE_TICK`, `PRIMITIVE_CLICK`, `PRIMITIVE_LOW_TICK`.

**Solution — a tiered helper that keeps the table as the contract.** The haptics *table* stays law; only the rendering gets richer where hardware allows.
```kotlin
fun NotesupHaptics.confirm() {
    if (vibrator.areAllPrimitivesSupported(PRIMITIVE_TICK, PRIMITIVE_CLICK)) {
        vibrator.vibrate(VibrationEffect.startComposition()
            .addPrimitive(PRIMITIVE_TICK, 0.4f)
            .addPrimitive(PRIMITIVE_CLICK, 0.7f, 40)   // tick-then-thunk = "object landed"
            .compose())
    } else view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)  // unchanged fallback
}
```
Use it for exactly **three** events — new note, checkbox on, magic-plus drop on a project — because rich haptics are supported by fewer devices and because restraint is the point. Every other row in the table stays a plain constant.

The checkbox one is the money shot: a two-primitive tick→click at 200 ms is *physically* what "an object dropped into a slot" feels like, and it's the interaction `UX-PEOPLE-GO-TO-WAR-FOR.md:200` says people write reviews about.

> **DECISION F-21:** ⬜ open — tiered haptics for those 3 events?

---

### F-22 🟢 Capture surfaces the spec is missing entirely

`research/14:15-25` lists six surfaces. Android offers three more that are pure premium-native and cost little:

1. **Share target.** Someone reads an article, hits share, and Notesup is in the **direct share row** with their recent projects. Implemented via `ShortcutManagerCompat` sharing shortcuts + `<share-target>` in `res/xml/shortcuts.xml`. This is how a notes app becomes the place things land. **Currently completely absent from the spec**, and it's arguably a bigger capture win than two of the four widgets.
2. **Launcher long-press shortcuts.** Static shortcuts for *New note* / *New checklist* / *New drawing* — three lines of XML, instantly makes the app feel first-party.
3. **Quick Settings tile** for new note. One `TileService`. Capture from anywhere without the launcher.

All three reuse the deep links you already defined at `BUILD.md:156-162`. Near-zero new surface area.

> **DECISION F-22:** ⬜ open — which of the three? (my rec: share target and launcher shortcuts are near-free; QS tile optional)

---

### F-23 🟢 Small typographic details that separate "nice" from "crafted"

- **Tabular numbers** (`"tnum"`) on card timestamps and numbered-list markers so digits don't jitter as they change. `FontFeatureSettings("tnum")` — one line, and it's why Things 3 lists feel stable.
- **Ligatures on for prose, off for code.** Standard ligatures (`liga`) improve reading evenness in `bodyNote`; in `Code` blocks they're actively wrong. Set explicitly rather than inheriting.
- **Optical margin on the title.** `headlineMedium` at 20 dp inset will *look* indented relative to body because of side bearings. Pull the title 1–2 dp left. Nobody will notice; everybody will feel the block align.
- **Don't animate letter-spacing while typing** — already correctly forbidden at `ui/03-TYPE.md:53`. Good.

> **DECISION F-23:** ⬜ open — take all four?

---

### F-24 🟢 Ink pressure should be non-optional

`DRAWING.md:100` treats real stylus pressure as conditional ("if `getPressure()` is useful; else simulate"). **Constant-width strokes are the #1 tell of an amateur drawing surface.** Jetpack Ink handles pressure properly when fed real data, and simulating from velocity is a solved problem for finger input.

**Solution:** make pressure mandatory in the spec. Stylus → `MotionEvent.getPressure()`. Finger → simulate from velocity (fast = thin, slow = thick), which is what perfect-freehand's `thinning` models and what makes marker strokes look like ink. Since ink is your widest moat (F-04), this is the wrong place to leave an "if."

> **DECISION F-24:** ⬜ open — pressure mandatory?

---

## 6. Contradiction sweep

Mechanical, ~1 hour. Cases where two files both claim the same authority tier and disagree.

| # | Conflict | Where | Fix |
|--:|---|---|---|
| C-1 | **Two drawing engines both "locked"** — Ink 1.0 vs perfect-freehand `getStroke`, plus a hand-rolled polyline model the same file forbids | `DRAWING.md:5` vs `:48`, `:109-124`, `FEATURES-NOW-LOCKED.md:111` | Ink 1.0 wins. Delete perfect-freehand params; replace data classes with `ink-storage` (see F-09) |
| C-2 | Ink block height 240/160/560 vs 280/160/720 | `BUILD.md:704` vs `DRAWING.md:96` | Take 280 default (better for handwriting) |
| C-3 | Empty-home wordmark `displayLarge` vs `displaySmall`; `displayLarge` isn't in the ramp at all | `BUILD.md:655` vs `ui/06-HOME.md:94` | `displaySmall` |
| C-4 | New-project entry: Projects-screen FAB vs app-bar `add`. Also **`ui/06-HOME.md:80` still contains raw thinking-out-loud** ("Two pluses?") inside a file meant to be law | `BUILD.md:804` vs `ui/06-HOME.md:80` | `ui/` version is better reasoned; clean the prose |
| C-5 | "Roboto Flex only, no display serif" vs 12 faces incl. 5 serifs | `BUILD.md:235-237` vs `FEATURES-NOW-LOCKED.md:180-193` | Resolve via F-02 |
| C-6 | `i_export_md` locks `draft` while its own rationale says `markdown`/`description` | `BUILD.md:515` | Trivial — but it signals the table wasn't re-read |
| C-7 | Orphaned empty table header above the widget table | `BUILD.md:856-858` | Delete |
| C-8 | `research/14` says tables are *not* in the editor, lists H3/code/quote as "later"; `FEATURES-NOW-LOCKED` adds them all | `research/14:69`, `:179` | Add a `> SUPERSEDED BY FEATURES-NOW-LOCKED.md` banner at top |
| C-9 | Four ink tools, three locked icons — no pencil-vs-marker glyph | `BUILD.md:542-544` vs `DRAWING.md:26` | Lock a fourth |
| C-10 | `BUILD.md:120` hedges four icon strategies in one cell | — | Decide via F-03 |

---

## 7. Suggested order

Grouped so nothing blocks anything else.

**Before any feature code**
F-01 (pin + wrap alpha) → F-12 (baseline profile + macrobenchmark harness) → F-10 spike (`TextFieldState` block swap on a real device)

**Foundations, week 1**
F-02 fonts · F-03 icons · F-13 motion rewrite · F-14/F-15/F-16 contrast + measure · §6 sweep

**Data layer, before sync**
F-05 own span format · F-08 FTS5 + lock purge · F-09 ink table · F-11 backup decision

**Then**
F-06 causality (before Convex, step 17) · F-07 ROLE_NOTES (15b) · F-22 capture surfaces

**Polish pass**
F-17 grain · F-18 opsz · F-19 emphasized · F-20 morph · F-21 haptics · F-23 typography · F-24 pressure

---

## 8. Open questions I can't answer alone

Not criticisms — genuinely need your call.

1. **F-02:** APK budget? Bundling all 12 fonts is ~10 MB. Bundling 4 is ~4 MB. What's acceptable?
2. **F-11:** Do locked notes need to survive a phone upgrade? That single answer picks (a) or (b).
3. **Tables + code blocks:** these are the two features most likely to eat a week each on phone-sized screens (cell editing, horizontal scroll, IME interaction). In or out for v1? I'll build either — I just want to schedule honestly.
4. **Play Store name.** "Notesup" parses ambiguously (notes-up? note-sup?) and is weak for search. Worth 10 minutes now, expensive to change after launch.
5. **Landscape phone.** Never specified anywhere in 6,100 lines. Editor in landscape with IME up is ~40% usable height — real decision needed.
6. **What's the v1.1 promise?** Not scope-cutting — I just want to know what "later" means so I can leave the right seams in the schema (tags, wiki links, version history all touch the note row).

---

## Sources

- [androidx.ink release notes](https://developer.android.com/jetpack/androidx/releases/ink) — 1.0.0 stable 17 Dec 2025 ✅ spec correct; 1.1.0-alpha07 exists
- [Compose Material3 release notes](https://developer.android.com/jetpack/androidx/releases/compose-material3) — 1.4.0 stable; Expressive graduations in the 1.5.0-alpha line
- [Compose Multiplatform 1.9.3 changelog](https://kotlinlang.org/docs/multiplatform/whats-new-compose-190.html) — confirms `ExperimentalMaterial3ExpressiveApi` removed at Material3 1.4.0-beta01 (**F-01**)
- [androidx.navigation3](https://developer.android.com/jetpack/androidx/releases/navigation3) — 1.1.6 stable ✅ spec correct; `predictivePopTransitionSpec`
- [Convex mobile releases](https://github.com/get-convex/convex-mobile/releases) — 0.8.0 ✅; ⚠ breaking `onIdToken` auth change
- [Convex: object sync engine for local-first](https://stack.convex.dev/object-sync-engine) — no full offline sync (**F-06**)
- [Clerk Android quickstart](https://clerk.com/docs/android/getting-started/quickstart) · [clerk-convex-kotlin](https://github.com/clerk/clerk-convex-kotlin) · [Clerk×Convex mobile changelog](https://clerk.com/changelog/2026-02-20-clerk-convex-mobile-integrations) — all ✅
- [Widget quality](https://developer.android.com/docs/quality-guidelines/widget-quality) · [Widget quality tiers](https://android-developers.googleblog.com/2025/03/introducing-widget-quality-tiers.html) — system corner radius confirmed ✅
- [Create a note-taking app (ROLE_NOTES)](https://developer.android.com/develop/ui/compose/touch-input/stylus-input/create-a-note-taking-app) (**F-07**)
- [Icons in Compose](https://developer.android.com/develop/ui/compose/graphics/images/material) — Material Icons no longer recommended (**F-03**)
- [Variable fonts in Compose](https://medium.com/androiddevelopers/just-your-type-variable-fonts-in-compose-5bf63b357994) — unsupported via downloadable fonts (**F-02**)
- [Migrate to state-based text fields](https://developer.android.com/develop/ui/compose/text/migrate-state-based) (**F-10**)
- [Baseline Profiles overview](https://developer.android.com/topic/performance/baselineprofiles/overview) · [Strong skipping](https://developer.android.com/develop/ui/compose/performance/stability/strongskipping) (**F-12**)
- [AGSL](https://developer.android.com/develop/ui/views/graphics/agsl) · [shady shader collection](https://github.com/drinkthestars/shady) (**F-17**)
- [Custom haptic effects](https://developer.android.com/develop/ui/views/haptics/custom-haptic-effects) · [haptics primitives](https://source.android.com/docs/core/interaction/haptics/haptics-constants-primitives) (**F-21**)
- [Direct Share targets](https://developer.android.com/develop/ui/compose/sharing/direct-share-targets) (**F-22**)
- [M3 Motion](https://m3.material.io/styles/motion/) · [M3 Expressive deep dive](https://www.androidauthority.com/google-material-3-expressive-features-changes-availability-supported-devices-3556392/) — spring physics, 35 shapes, morphing (**F-13, F-20**)
- [Keep rolls out M3 Expressive](https://9to5google.com/2025/08/21/google-keep-material-3-expressive-redesign/) (**F-04**)
- [Optimal line length](https://www.uxpin.com/studio/blog/optimal-line-length-for-readability/) · [Baymard](https://baymard.com/blog/line-length-readability) (**F-16**)
- [compose-rich-editor](https://github.com/MohamedRejeb/compose-rich-editor) — 1.0.0-rc13/rc14 (**F-05**)
- [SQLite FTS5](https://www.sqlite.org/fts5.html) (**F-08**)

Contrast ratios in F-14/F-15 were computed directly from the hex values in `BUILD.md:170-208` using the WCAG 2.1 relative-luminance formula — not taken from any source.
