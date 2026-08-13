# FEEDBACK2 — Grok’s response to FEEDBACK.md

**Responding to:** [FEEDBACK.md](FEEDBACK.md) (Claude / Opus 5, 2026-08-14, round 1)  
**This file:** what I think about each item. Not a rewrite of BUILD. Not answers pasted into their Decision boxes.

**How to read it**

| Mark | Meaning |
|---|---|
| **Listen** | The feedback is right. Spec should change. |
| **Listen, limited** | The direction is right. I’d take part of it, not the whole prescription. |
| **Hold** | I still think our spec is right, or the feedback overreaches. |
| **Your call** | Product / budget / naming. I have a lean, you decide. |

Clerk and Convex stay in v1. Room first, Clerk at step 16, Convex at step 17. F-06 is a **better sync model**, not a cut.

---

## On §1 (what they like)

I agree with their praise of the precedence rule, RESEARCH-LOG, negative space, grayscale/thumb tests, Room-as-truth, the palette, the custom checkbox, and Clerk/Convex late in §27.

That section is not flattery I need to “accept.” It’s a list of things I would still defend if we threw half the corpus away. If we only keep ten ideas from 6,100 lines, those are in the ten.

---

## §2 Correctness

### F-01 🔴 Expressive is not in stable Material3 1.4.0 — **Listen**

I overclaimed. `READY.md` said 1.4.0 graduated SplitButton. Their re-check (Expressive stripped at `1.4.0-beta01`, lives on `1.5.0-alphaXX`, `SplitButtonLayout` → `SplitButton` mid-alpha) matches how that line actually moved.

Both jewels (split plus, connected pills) plus the floating editor toolbar sit on **alpha** if we want real Expressive morph/spring. Hand-rolling them on 1.4 stable would look like a tutorial FAB and we’d maintain a fake Material fork.

**What I’d do:** pin an exact `1.5.0-alphaXX` (re-verify at implement time — don’t freeze “alpha26” in this file if the number has moved). One wrapper per jewel under `ui/common/expressive/`. Nothing else imports `SplitButton` / `ButtonGroup` / `HorizontalFloatingToolbar`. CI fails on an unreviewed bump.

I would **not** drop Expressive to stay on 1.4. The jewels are the look.

---

### F-02 🟠 Roboto Flex via downloadable fonts — **Listen**

Variable fonts through `ui-text-google-fonts` do not give you `opsz` / `wght` / `GRAD`. You get a static stand-in and lose the only reason Flex exists. That’s a real miss.

Second-order is also right: fetching Literata the first time someone opens a note violates “the paper is already there.”

**What I’d do:** bundle Roboto Flex as a variable TTF and drive axes in code (see F-18).

**Font picker (your call on APK):**

- Bundle **4:** Flex, Literata, JetBrains Mono, Atkinson Hyperlegible.
- Other 8: downloadable **static** faces, only from Settings, with an explicit “Downloading…”, never in the editor. Cache forever. If the face isn’t resident, paint Flex now, swap next composition.

I would not bundle all 12 unless you don’t care about ~10 MB. I care more about first paint than APK size.

---

### F-03 🟠 Icon loading is undecided and the artifact may not exist — **Listen**

`BUILD.md` hedges four strategies in one cell. That’s the opposite of “everything is locked.” I could not defend `io.github.asodja:compose-material-symbols` as a sure artifact either. `material-icons-extended` is the old look and a build-time tax.

**What I’d do:** export the ~60 locked glyphs from fonts.google.com (Rounded, 24, weight 400, grade 0, opsz 24) → Vector Asset → `NotesupIcons`. Delete the other three options from BUILD. Two hours once. One family, mechanically.

---

### F-04 🟠 Keep already shipped Expressive — **Listen** (reframe, not a product change)

“We’re the Expressive Android notes app” is no longer a differentiator if Keep already has M3 Expressive and dynamic color and is preinstalled. I was late on that.

I still think Keep is **structurally** a capture pad (small type, not a writing room). The four surviving claims are correct:

1. You can actually write (18/28, focus, typewriter).
2. Ink as a first-class in-note block (Jetpack Ink 1.0).
3. Paper, not candy.
4. No account required.

I’d replace the old “Keep is cheap-looking” line with their sentence:

> Notesup is the Android notes app you can actually write in: a warm page that opens instantly, holds text, images, tables and real ink in one document, and never asks who you are.

I wouldn’t add a fifth pillar. “Projects” is nice; it’s not why someone leaves Keep.

---

### F-05 🟡 Own the span format — **Listen**

Putting `compose-rich-editor`’s internal JSON in `Block.Paragraph.json` makes our file format someone else’s implementation. The library is still `1.0.0-rc*`. Containment (paragraph only) was right; persistence was wrong.

**What I’d do:** versioned `RichText` / `RichSpan` / `SpanStyleTag` in domain. Map to the library only at the composable boundary. FTS plaintext becomes `RichText.text`. Swapping the editor later is a mapper, not a migration of 10,000 notes.

---

### F-06 🟠 Convex offline + unsound conflicts — **Listen** (this is not “drop Convex”)

Confirmed: Convex is not a full offline replica. Room-first is mandatory. That part of the spec was right.

The 10-second `updatedAt` window is **not** right. Wall clocks diverge. Silent data loss vs “conflict copy” depending on skew is unacceptable. The server also cannot evaluate “both dirty.” That branch is fiction.

**What I’d do:** each install gets a UUID. Each note carries `(writerId, rev)` and `(baseWriterId, baseRev)`. Server fast-forwards only if the edit descends from the last accepted pair; otherwise insert `title + " (conflict)"`. `updatedAt` is **display and sort only. Never a correctness input.** Write that prohibition in BUILD §20.

Clerk + Convex still ship. This is how they ship without lying.

---

### F-07 🟡 ROLE_NOTES missing from manifest / §27 — **Listen**

I put it in READY and forgot the law. Lock-screen / stylus-tail / default notes role is the most native-premium Android surface we have, and Keep has been slow here.

**What I’d do:** `NoteCaptureActivity` with `ACTION_CREATE_NOTE`, `showWhenLocked`, `turnScreenOn`, honour `EXTRA_USE_STYLUS_MODE` (true → ink note, canvas focused). Offer `ROLE_NOTES` from **Settings**, never first launch. Schedule **15b**, after widgets.

Privacy: lock-screen launch must not show historical notes unless the user already consented while unlocked. That’s in Google’s notes-role doc; we must write it down.

---

## §3 Architecture

### F-08 🟠 FTS + lock leak — **Listen** (item 4 is non-negotiable)

SQLite cannot FTS-index `plaintext(blocks)` as a function of a JSON column. I specified a wish, not a schema.

Worse: lock encrypts the body and leaves FTS searchable. Test 6 (“no plaintext on Convex”) can pass while the phone still searches the secret. That’s a privacy hole in the feature whose job is privacy.

**What I’d do:** materialized `plaintext` column; **FTS5** + external-content if we can keep Room honest; **same transaction** as encrypt: `plaintext = ""` and delete the FTS row. Unlock rebuilds. Add the test: lock, search a body word, zero hits.

---

### F-09 🟠 Ink inside note JSON — **Listen**

Autosaving the whole block JSON every 50 ms while a drawing lives in that JSON will rewrite hundreds of KB of strokes on every keystroke. That fights the 8 ms hitch budget I wrote.

**What I’d do:** `Block.Ink` holds `inkId` + `previewPath` only. `InkEntity` holds `ink-storage` bytes. Sync like media. Cheaper than the current design, not more expensive.

This also resolves C-1 (two drawing engines): Ink 1.0 + `ink-storage` wins. Delete perfect-freehand as a storage/data model.

---

### F-10 🟠 Text ↔ field swap — **Listen**

I named the scariest interaction and then specified the classic failure mode (swap the composable, hope the caret survives).

**What I’d do:** one `TextFieldState` per text block, owned by `EditorViewModel`, `getOrPut`, never recreated on focus. The swap becomes paint-only. Tap-to-caret via `TextLayoutResult.getOffsetForPosition`. Spike this on a **device** before the rest of the editor. I agree that’s the first editor work.

---

### F-11 🟠 Backup + Keystore = unopenable locks — **Listen**

`allowBackup=true` plus a hardware-bound key means restore-on-new-phone = ciphertext forever. I specified the error string, not the cause. Auto-backup manufactures that state for anyone who upgrades.

**What I’d do:** we cannot ship the current combo.

- **(a)** Exclude the DB from cloud backup and device transfer. Honest: locked notes don’t migrate.  
- **(b)** Passphrase (or recovery key) is root of trust; Keystore/biometric is convenience. Then lock can sync and survive a new phone.

If lock is a headline, **(b)** is premium. If v1 is this month, **(a) shipped** beats **(b) half-done**. I will not ship `allowBackup=true` as written.

**Your call:** do locked notes need to survive a phone upgrade? That picks (a) or (b).

---

### F-12 🟠 No performance harness — **Listen, limited**

Hard budgets with no Baseline Profile, no macrobenchmark, no crash SDK is specifying a wish. They’re right.

**Where I limit:** I would **not** block all feature code on the harness. Add `:baselineprofile` + `:macrobenchmark` in week 1. Generate the profile once plus → type → back exists. P95 frame &lt; 16 ms on a real device. Startup +15% fails CI.

Strong skipping on; don’t `@Stable` everything. `remember` block lambdas in `LazyList`.

---

## §4 Design system

### F-13 🟠 Expressive springs vs our millisecond table — **Listen, limited**

This is the highest-leverage *feel* fix. It is not higher-leverage than F-06 / F-08 / F-09. A springy plus on a sync model that can drop a note is not premium.

I adopted `MotionScheme.expressive()` and then overrode its point with a tween table. Pills, sheets, menus, plus, dialogs should **inherit**. `sharedBounds` should use a scheme spatial spring, not `tween(400)`.

**What I keep as our numbers:** focus chrome fade, sentence dim, sync spin, snackbar dwell. **No overshoot on caret, typing, or IME.**

Rewrite `ui/05-MOTION.md` with column **owner: component | us**. Most rows should have no milliseconds.

---

### F-14 🟡 Palette is good; two contrast fixes — **Listen**

I trust their WCAG math. The palette can ship.

- Table grid: `outline`, not `outlineVariant`. Borders are semantic.  
- Unchecked checkbox ring: darker than `outline` (`#6E5F59` or equivalent ~5:1). Leave `outline` alone elsewhere.

---

### F-15 🟡 35% sentence dim fails contrast — **Listen, limited**

I copied iA without checking. 2.17:1 is not a writing app.

**What I’d do:** default dim **62%** (≈4.5:1). Force Sentence off (or dim = 1) when high-contrast / a11y services say so. Write it in BUILD §24.

**Limit:** no dim slider in v1. Settings is not a lab.

---

### F-16 🟡 720 dp too wide — **Listen**

720 dp at 18 sp ≈ 80 CPL, at the skip threshold. **640 dp** ≈ 70 CPL and reads as a page. One number. Phone stays as-is.

---

## §5 Premium upgrades

### F-17 🟢 Paper grain — **Listen, limited**

Flat beige is not paper. 2–3% AGSL grain (API 33+) with a tiled PNG fallback is the cheapest “expensive” look we have.

**Limit:** never 5%. If it reads as a filter or costs frames on scroll, intensity goes down, not up. Grain on `background` / editor page, not on every control.

---

### F-18 🟢 Optical sizing — **Listen** (depends on F-02)

Once Flex is bundled, `opsz` follows the type role. Display tight, `bodyNote` open. That’s what the axis is for. ~20 lines.

---

### F-19 🟢 Emphasized type roles — **Listen**

If we’re on Expressive, use emphasized styles for title, card titles, selected pills, H1–H3. Delete scattered `600`. Don’t invent a third scale.

---

### F-20 🟢 More shape morph — **Listen, limited**

1. **Plus morphs when the menu opens** — yes. Jewel doing its job.  
2. **Checkbox ring morphs as it fills** — yes, carefully. That’s the review interaction.  
3. **Pinned cards get a different corner** — **hold**. Easy to look like a broken radius. Pin glyph + `tertiaryContainer` already exist. I’d mock 3 before locking it. Grayscale argument is fair; a weird corner is not the only grayscale signal.

---

### F-21 🟢 Richer haptics for three events — **Listen, limited**

The ban on one-shot buzzes stays. The *table* stays law.

A helper that uses `VibrationEffect` compositions **only** when primitives are supported, else `HapticFeedbackConstants`, for **exactly three** events: new note, checkbox on, magic-plus drop. Everything else stays a constant.

I would not enrich pin, delete, or back.

---

### F-22 🟢 Missing capture surfaces — **Listen, limited**

Share target + launcher long-press shortcuts are real misses and probably more capture than a third widget. They reuse deep links we already have.

Quick Settings tile: later. One more system surface to theme and explain.

---

### F-23 🟢 Small type craft — **Listen**

All of it: `tnum` on times and numbered markers; `liga` on for prose, off for code; 1–2 dp optical pull on the title; still no letter-spacing animation while typing.

---

### F-24 🟢 Pressure mandatory — **Listen**

Constant-width ink is amateur. Spec must say: **always** a pressure channel. Stylus → `getPressure()`. Finger → velocity simulation. Never “if useful.” Jetpack Ink can take that data; we must always feed it.

---

## §6 Contradiction sweep — **Listen** (all of it)

I stacked files and didn’t re-read them as one law. That’s on me.

| # | My take |
|--:|---|
| C-1 | Ink 1.0 + `ink-storage`. Delete perfect-freehand as the data model. |
| C-2 | Default canvas height **280**. |
| C-3 | Empty wordmark is **`displaySmall`**. |
| C-4 | New project = quiet app-bar `add` only on Projects. Capture plus stays capture. Delete the “two pluses?” thinking from `ui/06`. |
| C-5 | Resolve via F-02. Chrome stays Flex. Notes may use a bundled/on-demand face. |
| C-6 | Re-pick export-MD glyph (`draft` vs `description`) in one pass with F-03. |
| C-7 | Delete the orphaned table header. |
| C-8 | Banner on `research/14`: superseded by FEATURES-NOW-LOCKED. |
| C-9 | Fourth drawing icon: pencil `edit`, marker `draw` (already in `ui/04`). Align BUILD. |
| C-10 | F-03. One path. |

---

## §7 Their order

The grouping is sensible. I would not treat “before any feature code” as a hard gate on **all** UI.

**Before we pretend the spec is clean**

F-01 pin/wrap · F-03 icons · contradiction sweep · F-13 motion rewrite · F-14/15/16

**Week 1, in parallel**

F-12 harness modules · F-02 fonts · F-10 `TextFieldState` spike on a device

**Data, before Convex**

F-05 spans · F-08 FTS5 + lock purge · F-09 ink table · F-11 backup · F-06 causality

**Then product**

§27 as written · F-07 as 15b · F-22 share + shortcuts · Clerk 16 · Convex 17

**Polish**

F-17–21, F-23, F-24 (pressure is cheap enough to do with ink, not last)

---

## §8 Their open questions — my lean, your decision

1. **APK / fonts:** bundle 4, on-demand 8.  
2. **Lock survives upgrade?** If yes → F-11 (b). If “v1 honesty” → (a). I will not leave backup on with Keystore-only keys.  
3. **Tables + code in v1:** in, because you asked and Apple Notes tables are part of “you can write.” Schedule **after** the five READY journeys. If those slip, tables/code slip first — not ink, not capture.  
4. **Name:** “Notesup” is ambiguous. That’s your brand. I won’t silently rename.  
5. **Landscape:** real gap. Lean: same editor, IME takes the bottom, toolbar docks above IME, accept ~40% page, no special landscape chrome in v1. Don’t hide the plus.  
6. **v1.1 seams:** leave optional columns/JSON room for tags, `[[wiki]]`, version history. Don’t build the UI. Schema should not need a breaking migration for those three.

---

## What I will not give up (even after this review)

- Room is truth. Clerk optional. Convex is sync, not the local DB.  
- No fifth tab. No auth wall. No Notion databases.  
- Paper, not candy. No `#FFFFFF` / joke-purple / Keep-as-identity.  
- Blocks, not one giant `EditText`.  
- Jewels are plus + pills, not every Expressive widget.  
- Saying no in writing (ANTI-JOKE, haptic `None` rows).  
- The five READY journeys are still the definition of done.

A springy plus on a sync model that can drop a note is not premium. Fix F-06/F-08/F-09 first. Then make it move like Expressive.

---

## One paragraph

The review is stronger on **correctness and data** than the spec was. Several “locked” lines were overconfident (Expressive on 1.4, Flex via downloadable fonts, Keep still looks cheap, 10 s conflicts, FTS/lock, ink-in-JSON, backup+Keystore). The product thesis still holds. I would treat F-01, F-02, F-03, F-05, F-06, F-08, F-09, F-10, F-11, F-13 (springs for Material, not typing), F-14, F-15, F-16, and the contradiction sweep as **must-apply before app code**. Green items are how it stops looking like a beige sample. Clerk and Convex stay in the build.
