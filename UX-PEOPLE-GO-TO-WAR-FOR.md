# UX people will go to war for

Parent map (UI + UX + BUILD): **[PANE.md](PANE.md)**.

This is not a feature list. It is the feeling that makes someone say, on Reddit, in a year: *I tried everything else. I came back.*

Sources that actually said that, or said the opposite loudly enough to learn from:

| Who | What people defend | What they abandon |
|---|---|---|
| **Things 3** | “Never feels messy.” “Fun to put stuff into.” Magic Plus. Checkboxes as objects. Paper transform. Quick Find as you type. | Feature race with Todoist |
| **Bear** | “No spinners, no skeleton screens, no toasts. Always ready.” Typography. Focus. Get out of the way. | Becoming Notion |
| **Drafts** | Open = already writing. Content first, action later. | Treating it like a filing cabinet |
| **iA Writer** | Sentence/paragraph dim. Typewriter. The current line is the only bright thing. | Toolbars |
| **Craft** | Documents that look like objects you would share | **Decision fatigue, too many clicks, lag on iOS** |
| **Apple Notes** | Mixed media without thinking. Quick Note from anywhere. | Power-user religion |
| **Keep** | One-tap, widgets, grid of faces | Looking cheap |
| **Copilot Money** | Haptic as recognition. Native, not a dashboard. People pay $95/year for *opening it daily*. | Spreadsheet apps |
| **Linear** | Empty states with one sentence. Keyboard as the product. | Jira |
| **NotallyX** | Honest Android, lock, grid/list, no permission theater | Being too small |
| **Samsung Notes 2026** | Ink | Date-grouped grid that *users revolted against* |

Craig Mod on Things: *pure craft. Tactile. Each animation purposeful. Mainly, it is fun. A fun app to be in.*

WIRED on Things: *a clean, crisp piece of paper, ready whenever you need it.*

Bear writeup: *You feel like you are using an Apple computer again. No spinners, no skeleton loading screens, no toast messages.*

r/thingsapp (still, years later): *Why I can’t fully let go: the UI is unbeatable, friction for quick capture is practically zero.*

r/bearapp on Craft: *too much clicking, decision fatigue, laggy on iOS. Bear is fast and minimal.*

That last sentence is the war. **Fast and minimal beats beautiful-and-clicky.**

---

## The feeling, in one paragraph

You open Notesup. Your notes are already there. No shimmer. No “syncing…”. You tap the plus. The keyboard is up before you have finished the gesture. You type. The chrome leaves. The current sentence stays bright. You check a box and the circle fills like a real object. You drag the plus onto a project and a note is born there. You swipe back and the note shrinks into the card it came from. You never created an account. You never named a folder. You never picked a template. The note was saved before you thought to save.

If any of that waits, we lost.

---

## Twelve laws (non-negotiable)

### 1. The paper is always already there
Bear’s law. Local cache is the UI. Room paints home. Convex is a ghost.

**Forbidden:** skeleton cards, circular progress on home, “Loading notes…”, pull-to-refresh ritual, splash logo.

**Allowed toast:** only `Note deleted` + Undo. Nothing else pops from the bottom to congratulate you.

### 2. Capture does not ask a question
Drafts + Things Magic Plus + Keep FAB.

- Tap plus → blank note, Inbox, caret in body (or title if you prefer title-first; **locked: caret in body**, title stays optional above).
- Chevron → checklist / ink / image. Still no “which notebook?”
- **Drag the plus onto a project row** → create *in that project* (Things Magic Plus). This is the one gesture power users will show their friends.
- Widget cells do the same three creates. Zero app chrome.

Never a template picker. Never a folder dialog. Never “Name this note” as a blocking field.

### 3. The checkbox is a designed object
Things spent a year on this circle. People mention it in reviews.

- 22 dp ring, 2 dp stroke
- Fill + check in **200 ms** (`short4`)
- `CONFIRM` on, `CLOCK_TICK` off
- The row does **not** jump to the bottom when checked (NotallyX default is jumpy). Optional setting `Sort checked to end`, **default OFF**.

If the checkbox feels like a Material `CheckBox` from a form, we failed.

### 4. Writing removes the app
Bear focus + iA sentence dim + our chrome fade.

Three levels, Settings → Focus (default **Auto**):

| Mode | What happens |
|---|---|
| Off | Chrome stays |
| Auto (default) | After 2 s of typing, app bar + toolbar fade (240 ms). Tap restores. |
| Sentence | Auto **plus** non-active paragraphs at **62% opacity** (AA). Active 100%. A11y/high-contrast: off. |
| Typewriter | Caret stays at **38%** of viewport height. No dim. Default ON automatically when note is long (≥4 blocks or ≥400 chars), off for short notes. |

iA’s own warning: sentence/typewriter can fight selection. **While a range is selected, dim and typewriter pause.**

### 5. Search is as fast as a thought
Things Quick Find: the moment you hit a key, results exist.

- Search field appears; **first character** filters. No “press search” debounce over 50 ms.
- Empty query: verb chips only (`Pinned`, `Drawings`, `Images`, `Locked`).
- No spinner. Empty results: one line `Nothing matches.` Not an illustration.

### 6. It is fun to rearrange
Things list editing. Craig Mod: *fun to put stuff into, to rearrange.*

- Long-press card → lift 2 dp, 90% scale, drag among grid with 8 dp gap animation
- List: drag handle appears in select mode; items gather under the finger
- Block handle in editor (v1.1 already planned) — **ship a simple up/down in v1 overflow**, drag in v1.1
- Swipe pin / delete must feel like Mail, not like a fight with back gesture

If rearranging is a settings screen, we failed.

### 7. Sync is a ghost
Bear: iCloud without indicators.

- Avatar is the only sync surface
- Healthy = **no icon**
- Unhealthy = `cloud_off` after 4 s, no toast
- Conflict = a second note titled `… (conflict)` + one snackbar. Never a modal “resolve”

### 8. Empty is a sentence
Linear / Things, not Lottie.

- First launch: one Welcome paper (`Start writing` / `Sign in to sync`) — [ui/14-ONBOARDING.md](ui/14-ONBOARDING.md). Empty home is still wordmark + `Write anything.` + the plus. That is the product, not a lecture.
- Empty project: `Nothing in {name} yet.`
- Empty search: `Nothing matches.`
- Empty pin strip: the strip is **absent**, not a ghost box
- Empty widget: `Pin a note` / three create cells — value, not a lecture
- Full empty table: [ui/20-EMPTY-STATES.md](ui/20-EMPTY-STATES.md).

No mascot. No three-page carousel. No “Import from Evernote” on first frame.

### 9. The app looks like paper, not a startup
Craft’s cards without Craft’s menus. Bear’s type without 28 themes in v1.

- One seed, dynamic color, eight quiet tints
- `bodyNote` 18/28 in the editor
- Cards are objects (sharedBounds → paper)
- One accent (sealing-wax). Not Keep candy.

v1 ships **12 curated app themes** plus **8 note papers** and **12 fonts** (see FEATURES-NOW-LOCKED). Not 28 Bear Pro themes, not a 40-item slash menu. Complete themes, not 12 sliders.

### 10. Native Android, not a tasteful iOS port
Copilot’s lesson: people stay because it feels like the OS they own.

- Expressive pills, SplitButton (the official one that **spins**), floating toolbar
- Predictive back that seeks the card
- System widget radius, not our 24 dp
- Material Symbols, not SF Symbols copies
- Dynamic color from *this* wallpaper

If a reviewer says “it looks like an iPhone app,” we failed.

### 11. Thinking does not require an account
Apple Notes, Bear offline, Notally.

Home works. Plus works. Widgets work. Sign-in lives in the avatar and is a gift (“Sync these notes?”) not a wall.

### 12. Refuse the click
Craft users left because of *illogical submenus, click-three-times-to-select, decision fatigue.*

A feature that adds a decision at create-time is not a feature. It is a tax.

**Refuse in v1 even if requested in a meeting:** templates, notebook picker on create, “what kind of note is this?” wizard, graph, daily note as home, AI rewrite button on the first toolbar row, slash menu of 30.

---

## The gestures people will show their friends

These are the demo. If they are late, the app is not premium yet.

1. **Plus → keyboard** in one breath (≤200 ms).
2. **Drag plus onto a project.**
3. **Type, chrome vanishes, sentence stays bright.**
4. **Check a box.** The circle is the product.
5. **Back.** The note becomes the card. No buzz.
6. **Widget, lock screen of the phone, new note, still offline.**
7. **Kill the process. Note still there. No spinner on return.**

---

## Microcopy that is the UX

| Moment | Words | Never |
|---|---|---|
| Empty home | `Write anything.` | `Create your first note to get started!` |
| Delete | `Note deleted` + `Undo` | `Successfully deleted` |
| Sync ask | `Sync these notes?` / `{n} notes on this phone will upload to your account.` | `Enable cloud to continue` |
| Conflict | `Kept both versions` | `Merge conflict detected` |
| Search miss | `Nothing matches.` | `No results found. Try different keywords.` |
| Locked | `Locked note` | `This content is encrypted` |
| Offline | `Sync paused` in the sheet only | Banner on home |

Tone: adult, short, no exclamation marks except none.

---

## What “go to war” actually means in reviews

People do not write essays about your color tokens. They write:

- “I open it and I’m already writing.”
- “It never feels messy.”
- “I tried Notion / Craft / Samsung and came back because this one stays out of the way.”
- “The checkbox is stupidly satisfying.”
- “It feels like Android, but expensive.”
- “I didn’t have to make an account.”

And they write the kill shots we must never earn:

- “Pretty but laggy.” (Craft on iOS)
- “I have to click three times.” (Craft)
- “Why is there a workspace?” (Notion)
- “They grouped my grid by date and ruined it.” (Samsung 2026)
- “Why do I have to sign in?”
- “It bounces while I type.”

---

## Implementation order for *feel* (do these before more features)

From BUILD §27, these steps are the war:

1. Room-first home, no shimmer  
2. Plus → editor → IME  
3. SharedBounds + predictive back  
4. Checkbox object + haptic  
5. Focus Auto + Typewriter  
6. Sentence dim  
7. Drag-plus onto project  
8. Quick Find first keystroke  
9. Widgets  
10. Then Clerk/Convex as a ghost  

If you ship sync before the checkbox feels right, you shipped the wrong app.

---

## Locked additions to BUILD (also written into BUILD)

1. No skeletons, no home spinner, no toasts except undo.  
2. Caret starts in the **body**, title optional.  
3. Drag SplitButton onto a project row creates there.  
4. Focus modes: Off / Auto / Sentence / Typewriter as specified.  
5. Search debounce ≤ 50 ms.  
6. Sort-checked-to-end default **OFF**.  
7. Healthy sync: no icon.  
8. Empty states are one sentence.  
9. No template / notebook modal on create.  

That is the UX. Defend it like Bear people defend Bear.
