# 20 — Empty states (every one)

Law: one sentence. No art. No Lottie. No second button except where a plus already exists.  
Type: `bodyLarge` `onSurfaceVariant`, center, maxWidth 280.  
Vertical center of the **content** canvas (below the bar/pills, not including the plus).

| Where | Sentence | Extra |
|---|---|---|
| Home · All | `Write anything.` | Wordmark `displaySmall` 8 above. Plus visible. |
| Home · Pinned | `Nothing pinned.` | Plus visible. Pin strip **absent**. |
| Home · Recent | `Nothing yet.` | Plus visible. |
| Home · Projects (0 projects) | `A project is a place.` | Plus is still capture. App-bar `add` is how you create. |
| Inbox row | never empty-stated | Inbox is a row, not a canvas |
| Project screen | `Nothing in {name} yet.` | Plus creates **in** the project |
| Search miss | `Nothing matches.` | 48 below the field. No plus. |
| Search empty query | chips only, no sentence | — |
| Trash | `Nothing in Trash.` | No plus |
| Welcome | not an empty — [ui/14](14-ONBOARDING.md) | — |
| Gate | not an empty — [ui/18](18-LOCK-GATE.md) | — |
| Missing note | `This note isn’t here.` | [ui/22](22-SYSTEM-EDGES.md) |
| Widget pinned empty | `Pin a note` | Glance |
| Widget recent empty | `Write anything.` | Glance |
| Widget project empty | `Nothing in {name} yet.` | Glance |
| Select mode, 0 left | leave select | — |

**Projects filter with projects but you haven’t opened one:** not empty — the list **is** the content.

Never:

- Illustrations, mascots, empty-box icons
- “Create your first note” buttons (the plus is the button)
- Import from Keep / Evernote
- Tips carousels
- “Pull to refresh”

---

## Strings

```
empty_home=Write anything.
empty_pinned=Nothing pinned.
empty_recent=Nothing yet.
empty_projects=A project is a place.
empty_project=Nothing in %s yet.
empty_search=Nothing matches.
empty_trash=Nothing in Trash.
empty_missing=This note isn’t here.
pin_widget_empty=Pin a note
```
