# 04 — Home, grid, list, navbar

This is the first screen. If home is wrong, no editor can save the app.

## Jobs of home

1. Prove this is a premium object in 400ms.
2. Get the user writing in one tap.
3. Let a person with 400 notes find one by face (grid) or by title (list).
4. Surface pins without a separate app.
5. Never require a folder decision before writing.

## Competitive home map

| App | Home | Nav | Create | Score for us |
|---|---|---|---|---:|
| Keep | Color grid, labels as chips | Drawer + search | FAB / assistant | 8 structure / 3 look |
| Apple Notes | Folder list → note list | Folders as OS | Compose top-right | 7 |
| Samsung Notes | Grid of covers | Tabs + folders | FAB | 6 |
| Craft | Doc cards, spaces | Sidebar (desktop) | + | 9 cards |
| Notally | Compact list, labels | Almost none | FAB | 8 restraint |
| Notion | Workspace / recents | Tab bar | + page | 3 |
| Obsidian | Vault file tree or last note | Sidebar | New md | 4 |
| Things 3 | Grouped lists | Sidebar concepts | + / quick find | 9 grouping |
| Zoho Notebook | Cover grid | Notebooks | + | 8 covers |
| Easy Notes | Category tabs, ads | Bottom-ish | + | 3 |
| Memos | Chronological stream | Almost none | Composer | 7 stream |
| Drafts | Inbox of drafts | Lists | Instant composer | 9 capture |

## Accepted home architecture

```
┌─────────────────────────────────────┐
│  Notesup          🔍        (avatar) │  collapsing app bar
│  All  Pinned  Recent  Projects      │  connected pill ButtonGroup
│                              ▦ ☰    │  view toggle (icon buttons)
├─────────────────────────────────────┤
│  PINNED (if any)                    │  1–3 featured cards, horizontal
│  [card] [card] [card]               │
├─────────────────────────────────────┤
│  GRID or LIST of the rest           │
│                                     │
│                          [ ✚ ▾ ]    │  SplitButton FAB
└─────────────────────────────────────┘
```

Search is **not a tab**. Search is a mode that replaces the grid with a focused field + results (Raycast/Keep hybrid).

Avatar opens a small account sheet (Clerk user, sync state, settings). Not a fifth tab.

## Grid view

**Accepted**

- 2 columns on phone, 3 on unfolded/tablet compact, 4 on rail layouts
- Card shows, in order of richness:
  1. First image or ink thumbnail if present (16:10 crop)
  2. Else tinted container
  3. Title (max 2 lines, `titleSmall` / `titleMedium`)
  4. First text line (`bodySmall`, 2 lines, fade)
  5. Quiet meta: relative time · project name if any
- Pinned: tonal container + tiny pin glyph, not a gold star
- Aspect: variable height (masonry-lite) **only if** we can keep scroll performance. Default is **fixed-height cards** (Keep-like) for v1. Masonry is v1.1 if Frame pacing holds.
- Long-press: Expressive menu (pin, move to project, lock, share, delete)

**Rejected**

- Pinterest true masonry as v1 (jank kills premium)
- Keep's loud 12-color chips as the card identity
- Notebook cover art that hides the content (Goodnotes on a phone)
- Checkboxes rendered as a second app on the card (one checkbox preview max)

### 100 grid-card inspirations (compressed, rated)

Keep color tiles 6, Apple gallery 8, Photos years 7, Craft cards 10, Notion gallery 6, Zoho covers 8, Pinterest 5, Google Photos 7, Instagram grid 3, VSCO grid 8, Darkroom filmstrip 7, Wallet stacks 8, Linear issue rows (not grid) 0, Things headings 7, Milanote boards 6, Capacities objects 7, Anytype sets 6, Goodnotes shelves 6, Samsung covers 6, OneNote sections 3, Evernote snippets 4, Notally list 8, Quillnote 7, Memos cards 8, Flomo 8, Day One photos 8, Readwise 7, YouTube library 4, Play Store 3, Files 4, Drive 4, Bear notes list 8, Ulysses sheets 8, iA library 7, Obsidian file 3, Logseq journal 5, Heptabase cards 7, AFFiNE edgeless 5, AppFlowy 5, SiYuan 4, Notesnook 6, Joplin 4, Standard Notes 5, Simplenote 6, Drafts inbox 8, Niagara 9 (subtraction), Nothing gallery 7, Pixel Screenshots 8, Clock world 7, Weather tiles 7, Copilot categories 8, Cash App tiles 6, Mercury 6, Stripe 7, Halide rolls 8, Airbnb cats 7, Apple Music recents 8, Spotify cards 4, Kindle library 6, Audiobook covers 6, Streaks 8, Oura 7, Fitness rings 4, Flighty legs 7, Structured blocks 8, Todoist 5, TickTick 3, Slack pins 3, Discord 2, Telegram 3, iMessage 3, Ivory 8, Apollo cards 8, News widgets 6, Google News 4, Pocket 6, Instapaper 6, Matter 7, Citizen 2, Maps recents 6, Find Hub 7, Recorder list 8, Phone recents 8, Contacts 7, Gmail 4, Outlook 3, Superhuman 6, Cron 8, Fantastical month 6, Calendar 6, Reminders 7, Keep widgets 8, Samsung widgets 6, Easy Notes widgets 5, ColorNote 2, Xiaomi notes 4, HyperOS 4, One UI notes 5, Nothing notes? 6, Penly 7, Notein 7, Squid 5.

**Winning grid DNA:** Craft card + Keep speed + VSCO quiet + Pixel Screenshots thumbnail.

## List view

**Accepted**

- Single column
- Leading: 40dp thumbnail or tonal initial
- Title + one preview line + time
- Swipe: pin (start) / archive or trash (end), with clear haptics
- Sticky headers in Recent: Today, Yesterday, This week, Older
- Projects view: Things-style section headers, not nested folders

**Rejected**

- Three-line dense email rows as the only view
- Swipe actions that conflict with back gesture
- Avatars (there is no "author" in a personal notebook)

List is the **power view** and the **accessibility view**. Remember the toggle.

## Navbar / chrome decision (the actual answer)

**We will not ship a Material NavigationBar with Home / Search / Notebooks / Settings.**

Why, after 100+ apps:

- Notes are one place. Extra destinations invent work.
- Premium apps (Things, Bear, Drafts, Halide, Shazam, Ivory) do not waste the thumb zone on peer tabs when there are no peers.
- Expressive NavigationBar is for Gmail-class apps.

**We will ship:**

1. **Pill ButtonGroup** under the app bar (All / Pinned / Recent / Projects)
2. **SplitButton FAB** bottom-end
3. Optional **HorizontalFloatingToolbar** only if we add a second persistent action (e.g. select mode). Not on day one.
4. **NavigationRail** when `width >= 600dp` (unfolded, tablet): All / Pinned / Recent / Projects / Settings as rail destinations, content on the right. This is the one place a "nav bar" is correct.

Search: top icon → full search surface (predictive, verbs). Profile: avatar → sheet.

## Split FAB contents

| Action | Creates | Haptic |
|---|---|---|
| **Note** (default tap) | Empty text note, open editor, caret in body | `CONFIRM` |
| Checklist | Note with one unchecked item focused | `CONFIRM` |
| Ink | Note with an ink block focused | `CONFIRM` |
| Image | Picker / camera, then note with image block | `CONFIRM` |

Default tap must be **Note**, 200ms to keyboard. The chevron is the only extra.

## Empty states (premium or nothing)

First launch, zero notes:

- Large quiet wordmark
- One line: "Write anything."
- The FAB is the only instruction
- No tutorial carousel
- No "import from Evernote" on the first frame (settings later)

Empty project: Things 3 energy. One sentence, not an illustration farm.

## Select mode

Long-press one card → multi-select.

- App bar morphs to count + pin + move + delete
- FAB hides
- Expressive connected buttons for bulk actions

## Performance rules that *are* UX

- First 20 cards drawn before any Convex call
- Thumbnails from local disk, never blocking bind
- Grid uses `LazyVerticalGrid` with stable keys = note ids
- Opening a note is a shared-element container transform of the **card**, not a new blank activity fade

## Accept / reject summary

**Accept:** dual view, pills not tabs, split FAB, pinned strip, Things headers in projects, shared-element open, local-first paint.

**Reject:** 5-tab bar, drawer-only nav, masonry v1, colorful Keep chips as identity, folder tree as home, onboarding carousel.
