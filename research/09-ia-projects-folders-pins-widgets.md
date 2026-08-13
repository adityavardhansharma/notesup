# 09 — Information architecture: projects, folders, pins, widgets

User questions, answered after the 100-app pass:

- Can we add projects? **Yes.**
- Can we add folders? **A little, later, and not as a tree.**
- Can we add pinned notes? **Yes. Sacred.**
- Can we add everything? **No. That is how Evernote and Notion mobile died emotionally.**
- Can we add Android widgets? **Yes. Required for a premium Android citizen.**

## Organization models we studied

| Model | Who | Phone | Score | Verdict |
|---|---|---|---:|---|
| Flat + search + labels | Keep, Simplenote, Notally | Excellent | 8 | Base layer |
| Folders | Apple Notes, Samsung, Joplin | Familiar, becomes a dump | 6 | Not primary |
| Notebooks / stacks | Evernote, OneNote | Heavy | 3 | **R** |
| Tags everywhere | Bear, Obsidian | Power users | 6 | Optional metadata |
| Bidirectional links / graph | Roam, Logseq, Obsidian | Desktop | 4 | **R** v1–v2 |
| Objects / types | Capacities, Anytype, Tana | Conceptual | 5 | Later |
| Daily notes | Reflect, Logseq, Agenda | Nice habit | 6 | Optional later |
| **Areas / projects** | **Things 3, Craft spaces, OmniFocus** | Calm | 10 | **A** |
| Inbox then file | Drafts, note-gen | Excellent | 9 | **A** |
| Canvas / boards | Milanote, Heptabase | Weak phone | 4 | Later |
| Books / chapters | BookStack | Docs | 5 | **R** |

## Accepted IA

```
Account (Clerk)
└── Notesup library (one, personal, v1)
    ├── Inbox (implicit: notes with no project)
    ├── Projects[]          // user-named, Things-style
    │     └── Notes[]
    ├── Pins[]              // overlay, not a location
    └── Trash
```

A note has:

- `projectId: Id?` (null = Inbox)
- `pinned: Boolean`
- `locked: Boolean`
- `tint: Hue?`
- `tags: List<String>` (hidden in v1 UI or a single "label" later)
- timestamps + revision

**A note lives in at most one project.** Multi-project is tags. We do not do Evernote notebooks+tags+shortcuts+stacks.

### Projects (100 inspirations compressed)

Things 3 10 A, OmniFocus folders 7 F, Craft spaces 9 A, Notion workspaces 4 R (too big), Apple folders 6 F, Bear tags 7 F, Keep labels 7 F, Evernote notebooks 3 R, OneNote sections 3 R, Joplin notebooks 5 F, Standard Notes tags 6 F, Notesnook notebooks 6 F, Zoho notebooks 8 F (covers), Goodnotes notebooks 7 F, Samsung folders 5 F, Google Drive folders 3 R, Files 3 R, Linear projects 8 A, GitHub repos 5 F, Slack channels 2 R, Discord 1 R, Arc spaces 8 A, Chrome tab groups 5 F, Todoist projects 6 F, TickTick 4 R, Asana 2 R, ClickUp 1 R, Height 6 F, Jira 1 R, Capacities types 6 F, Anytype spaces 6 F, Tana supers 3 R, Roam daily 5 F, Reflect 8 A, Agenda 7 F, Noteplan 7 F, Day One journals 6 F, Journey 5 F, BookStack 5 F, Outline 4 R, Confluence 1 R, Dropbox 3 R, Box 2 R, Paper 5 F, Ulysses groups 8 A, iA library 7 F, Scrivener 4 R, Drafts workspaces 8 A, FSNotes folders 7 F, Markor folders 7 F, Obsidian vaults 4 R (one vault = the app), Logseq graphs 3 R, SiYuan notebooks 5 F, Trilium 4 R, AppFlowy 5 F, AFFiNE 5 F, Amplenote 4 R, Mem 3 R, Flomo tags 6 F, Memos tags 6 F, Niagara 8 A (less), Photos albums 6 F, Photos memories 5 F, Playlists 5 F, Apple Music playlists 6 F, Wallet 5 F, 1Password vaults 6 F, Monzo pots 8 A (mental model: named buckets), Copilot categories 7 F, YNAB envelopes 6 F, Mercury accounts 5 F, Gmail labels 5 F, Superhuman split 6 F, Outlook folders 2 R, Finder tags 5 F, Windows Explorer 2 R, Total Commander 1 R.

**Accepted project UX**

- A project is a **named bucket with a color dot and an optional emoji** (Things / Monzo pot energy).
- Home pill **Projects** shows a list of projects + Inbox.
- Opening a project is a Things grouped list / grid of its notes.
- Creating a note *inside* a project assigns `projectId`.
- Creating from global FAB lands in **Inbox**. File later, or never. Drafts wins here.
- No nested projects in v1. Nested is how folders rot.
- Empty project: one line, not a tutorial.

**Rejected:** folder trees, stacks, notebooks-plus-tags-plus-shortcuts, vault switcher, workspaces-as-accounts.

### Folders

**v1: no folders.** Projects *are* the named containers.

If users scream, v1.2 can add **sections inside a project** (Things headings) — still not a filesystem.

Deep trees scored 3 across Apple power users vs 9 for Things headings. We take headings if we take anything.

### Pins

Pins are an **overlay**. A pinned note still lives in Inbox or a project.

| Rule | Why |
|---|---|
| Pin strip on All (max ~6 featured, then "see all") | Keep + Easy Notes |
| Pinned pill shows only pins | Fast |
| Pinning does not remove from project | Sanity |
| Pin haptic CONFIRM | File 07 |
| Pin is not a color, not a star farm | Quiet glyph + tertiary container |

100 pin inspirations (selected): Keep 9 A, Apple 8 A, Easy Notes 7 F, Gmail 7 F, Slack 4 R, Maps 6 F, Chrome 6 F, Phone recents 6 F, Contacts star 5 F, Instagram 2 R, Twitter 4 R, YouTube 3 R, Spotify 4 R, Photos favorites 7 F, Files 4 R.

### Archive vs trash

- **Trash** with 30-day restore (Apple). Snackbar undo on delete (Gmail). Both.
- **Archive** is optional v1.1 for people who do not like trash. Not two overlapping concepts in v1.

### Search is organization

If search is great, folders can stay dead. Pixel / Keep / Raycast.

Accepted search fields: title, body, project name, OCR later, handwriting later.

Accepted verbs: `pin`, `locked`, `ink`, `image`, `project:name`.

## Widgets (Android citizen)

Sources: [App widgets overview](https://developer.android.com/develop/ui/views/appwidgets/overview), Jetpack Glance, Keep / Clock / Easy Notes.

Widgets are mini-apps with **tap + vertical swipe only**. Design like newspaper teasers.

### Accepted widget set

| Widget | Type | Sizes | Job |
|---|---|---|---|
| **New note** | Control | 2x1, 2x2 | Split actions: note / list / ink. Fastest capture in the OS. |
| **Pinned** | Information | 2x2, 4x2 | One pinned note preview. Tap opens. |
| **Recent** | Collection | 4x2, 4x3, 4x4 | Scroll recent titles. Tap opens. |
| **Project** | Collection + config | 4x2+ | Notes in one project. Configure project on drop. |

Resize: collection widgets grow the list. Information widgets add preview lines, then a thumbnail.

**Accepted implementation:** Jetpack Glance (Compose-style). Not 2012 RemoteViews XML as the authoring model (RemoteViews still underneath).

**Rejected widgets:** animated live wallpaper notes, sticky-note skeuomorph in neon, configurable 20-option kitchen sink, horizontal paging.

### Widget inspirations (40)

Keep 9 A, Samsung Notes 6 F, Easy Notes 6 F, ColorNote 3 R, Google Clock 10 A, Weather 8 A, Recorder 7 F, Phone 7 F, Gmail 5 F, Calendar 7 F, Tasks 7 F, Files 4 R, Photos 6 F, Nothing 7 F, Niagara 6 F, Pixel At a Glance 8 A, Apple Notes widget 8 A, Things widget 9 A, Drafts widget 10 A, Bear 7 F, Craft 6 F, Notion 4 R, Obsidian 5 F, Todoist 6 F, Streaks 8 A, Copilot 6 F, Flighty 8 A, CARROT 6 F, Hello Weather 8 A, Music 7 F, Overcast 7 F, Wallet 6 F, 1Password 5 F, ChatGPT 4 R, Assistant 5 F.

**Winning widget DNA:** Drafts (capture) + Keep (list) + Clock (resize honesty).

### Assistant / At a Glance

If we have time later: App Actions for "note to Notesup". Not v1.

## Locking

Apple Notes lock is trusted. Biometric via AndroidX Biometric + optional Clerk reverification for cloud-only operations.

v1: local lock (can't open preview). Sync of locked notes: encrypt body at rest on device; decide cloud encryption in implementation (Convex store ciphertext). Do not ship lock theater that still uploads plaintext.

## "Can we add everything?" — the list we will not add in v1

Collaboration, sharing live links, comments, versions UI, graph, daily notes as home, reminders (maybe v1.2), audio, OCR, PDF notebooks, stickers, templates marketplace, AI writer, public blog, web clipper, browser extension, nested folders, multi-vault, teams, Clerk organizations.

Reminders and a web clipper are the first two to reconsider after widgets.

## 100 project-thinking notes (the user asked)

Looked at 100 ways people group work (Things, Linear, Monzo pots, Craft spaces, Arc spaces, albums, playlists, Drive, Evernote...). The ones that feel premium on a phone are **named shallow buckets + an inbox**. The ones that feel like work are trees and workspaces.

**Accept projects as Things 3 areas. Reject folders as Explorer.**

## Accept / reject

**Accept:** Inbox + Projects + Pins + Trash + Search; widgets (new, pinned, recent, project); biometric lock planned; Glance.

**Reject:** folder trees, Evernote stacks, graph, teams, archive+trash together, "everything."
