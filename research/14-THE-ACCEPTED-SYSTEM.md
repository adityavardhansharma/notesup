# 14 — The accepted system

> **SUPERSEDED** for implementation by [../BUILD.md](../BUILD.md) and [../FEATURES-NOW-LOCKED.md](../FEATURES-NOW-LOCKED.md). Tables, H3, code, quote, paper, themes are **in v1**. This file is historical thesis only.

Notesup is an **Android-only notebook**. It is the most premium notes surface on the platform because it is **fast like Keep, written like Bear, mixed like Apple Notes, grouped like Things 3, and dressed like Material Expressive** — not because it has more switches.

## Product sentence

Open the app. Tap once. Type. The note is already saved on the device. If you signed in, it will appear on your other Android. If you didn't, it is still a beautiful note.

## Who it is for

A person who lives on Android and wants their notes to feel like a crafted object. Not a second brain. Not a classroom paper pack. Not a team wiki.

## Surfaces (only these)

| Surface | Job |
|---|---|
| **Home** | Browse and capture |
| **Search** | Find and run verbs |
| **Editor** | Write, insert, draw, export |
| **Project** | See one bucket |
| **Account sheet** | Clerk + sync + settings |
| **Widgets** | Capture and glance without opening |

No fifth tab. No drawer of destinies.

## Home

- Collapsing app bar: wordmark, search, avatar
- Expressive **connected pills:** All · Pinned · Recent · Projects
- **Grid / list** icon toggle (shape-morph)
- **Pinned strip** on All (up to 6)
- **Grid (default):** 2 columns, paper cards, optional image/ink thumb, title, 2-line preview, quiet time
- **List:** thumbnail, title, preview, swipe pin / trash
- **Recent:** list with sticky Today / Yesterday / This week
- **Projects:** Inbox + named projects (color dot, optional emoji). No trees.
- **Split FAB:** tap = new text note in Inbox, 200ms to keyboard. Chevron = Checklist / Ink / Image
- Long-press → expressive menu or multi-select
- Shared-element card → editor
- Predictive back
- First paint from Room. Never a home shimmer.

Empty: "Write anything." and the FAB.

## Search

Search is a mode, not a tab. Pixel expansion + Raycast verbs.

Verbs: `pin`, `locked`, `ink`, `image`, `project:name`.

## Editor

A note is a title + `List<Block>`.

**v1 blocks:** paragraph (bold/italic/underline/strike/code/link), heading (one level), checklist, bullets, numbered, image, ink, divider.

- `LazyColumn` of blocks
- One live text field (focused block)
- Paragraph styling via **compose-rich-editor** or equivalent, inside the block
- **HorizontalFloatingToolbar** above IME: styles · convert · insert image/ink
- Focus mode: chrome fades while typing; typewriter scroll on long notes
- Images: local first, downsample, tap to expand
- Ink: block with live canvas when focused, PNG snapshot when not; pen / highlight / eraser / thickness / 6 colors
- `⋯`: pin, project, tint, lock, export Markdown, export PDF, share file, delete
- Undo stack for block ops
- Export uses system share sheet

Not in the editor: slash menu of 30, tables, embeds, stickers, canvas, markdown source as default.

## Organization

```
Inbox (unfiled)
Projects (shallow named buckets)
Pins (overlay)
Trash (30 days + snackbar undo)
```

A note has at most one project. Filing is optional forever.

No folder trees. No tags UI in v1 (schema may allow a label later). No graph. No teams.

## Widgets (Glance)

1. New note (control) — note / list / ink
2. Pinned (information)
3. Recent (collection)
4. Project (collection, configure on drop)

## Look

- Material 3 Expressive
- Dynamic color, harmonized to a warm paper seed
- One accent (deep carmine / sealing-wax)
- Tinted notes optional and quiet
- Dark = warm dark, not gamer OLED
- Type: theme scale, **~18sp body**, large optional title
- Icons: Material Symbols, outlined/filled
- Shapes: stadium pills, extra-round cards, floating toolbars
- MotionScheme on theme; shared element; no motion on keystrokes
- Pin = tertiary container + glyph, not a gold star

## Feel

| Event | Motion | Haptic |
|---|---|---|
| Open note | Shared element | none |
| New note | 200ms transform | CONFIRM |
| Pin | Fly to strip | CONFIRM |
| Check | Circle fill | CONFIRM |
| Delete | Shrink + snackbar | REJECT |
| Ink stroke | Ink only | none |
| Type | Caret only | none |
| Scroll | System | none |

Platform `HapticFeedbackConstants` only. No one-shot buzzes.

## Auth (Clerk)

- Official `clerk-android-api` only — **no AuthView**
- Custom Compose: Google (Credential Manager) + email code + passkey
- First-run Welcome then optional `auth` — [ui/14-ONBOARDING.md](../ui/14-ONBOARDING.md)
- **Never an auth wall.** Start writing is primary.
- Avatar → sheet → same `auth` route
- First sign-in with existing locals asks `Sync these notes?`
- Settings / trash / privacy / manage: [ui/15-SETTINGS.md](../ui/15-SETTINGS.md)
- Capture / share-in: [ui/17-CAPTURE-SHARE.md](../ui/17-CAPTURE-SHARE.md)
- Lock gate: [ui/18-LOCK-GATE.md](../ui/18-LOCK-GATE.md)

## Sync (Convex)

- Official `android-convexmobile`
- `ClerkConvexAuthProvider` / `ConvexClientWithAuth`
- Webhook mirrors Clerk users
- Schema: users, projects, notes, media
- Local UUID primary keys; `remoteId` mapped
- Optimistic edits; debounce body mutations
- LWW + conflict copy if both sides dirty
- Files in Convex storage; ids on notes
- `webSocketStateFlow` → quiet "Sync paused"
- Debug logging never in release

## Architecture

```
Compose UI  →  ViewModel (StateFlow of sealed UiState)
            →  Repository
                 → Room (truth)
                 → Convex (sync)
                 → Clerk (session)
```

Kotlin house style: sealed blocks and screens, immutable `Note`, value-class IDs, no `!!`, no Compose-to-network, tokens not raw colors. minSdk **26**.

Large screens: `NavigationRail` + optional list | editor two-pane.

## Security / lock

- Biometric lock on a note (local)
- Locked body not uploaded as plaintext
- Delete account = Clerk + Convex purge

## v1 is done when

- [ ] Create a note in one tap, offline, under one second to keyboard
- [ ] Grid and list both feel intentional
- [ ] Pills, split FAB, floating toolbar are Expressive — not a catalog demo
- [ ] Image and ink sit in the same document as text
- [ ] Focus mode works
- [ ] Export MD and PDF
- [ ] Pin, project, trash+undo
- [ ] Three widgets
- [ ] Sign-in optional; sync after
- [ ] Dynamic color + paper seed
- [ ] Haptics only where they mean something
- [ ] Shared-element open / predictive back
- [ ] No fifth tab

## Explicitly later

Reminders, OCR, handwriting-to-text, shape snap, audio, PDF notebooks, tags UI, sections inside projects, masonry, live links, web, clipper, collaboration, CRDT, AI, templates shop, widgets for Assistant, reading serif.

## The premium test (use this in review)

Hand the phone to someone who uses Apple Notes or Keep.

If they say **"this feels more expensive than Keep and I didn't have to learn it,"** we succeeded.

If they say **"where do I create a workspace / database / notebook stack,"** we failed by adding someone else's product.

If they say **"why do I have to sign in,"** we failed Clerk.

If they say **"why is this bouncing while I type,"** we failed Expressive.

## North-star moodboard (keep these 12 on a wall)

Craft cards · Apple Notes mixed media · Bear type and focus · Drafts capture · Things 3 projects · Keep speed (structure) · Notally honesty · INKredible ink · reMarkable restraint · Material Expressive pills/FAB/toolbar · Copilot/Apollo haptic discipline · Google Clock widgets.
