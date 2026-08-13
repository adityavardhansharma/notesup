# 04 — Icons

**Family:** Material Symbols **Rounded**  
**Size:** 24 × 24 dp in chrome. 16 dp on cards. 20 dp in list leading if needed.  
**Weight:** 400. **Grade:** 0. **Optical size:** 24.  
**Fill:** 0 at rest. **1** only when that control is the selected state.  
**Color:** see 02. Never multi-color glyphs. Never emoji in chrome (project emoji is *data*, not an icon).

Hit target always **48 dp**, icon centered. `contentDescription` required.

## Complete inventory

| ID | Glyph | Fill rest / on | dp | Description | Where |
|---|---|---|---:|---|---|
| back | `arrow_back` | 0/0 | 24 | Back | Editor, nested |
| search | `search` | 0/1 open | 24 | Search | Home bar |
| close | `close` | 0/0 | 24 | Close | Search, select, sheet |
| account | `account_circle` | 0/0 | 24 / 64 sheet | Account | Bar / sheet |
| grid | `grid_view` | 0/1 | 24 | Grid view | Home |
| list | `view_agenda` | 0/1 | 24 | List view | Home |
| pin | `keep` | 0/1 | 24 | Pin | Menu, swipe, select |
| pin_badge | `keep` | 1/1 | 16 | Pinned | Card |
| add | `add` | 0/0 | 24 | New note | Split primary |
| split | `keyboard_arrow_up` | 0/0 | 24 | More note types | Split secondary |
| type_text | `notes` | 0/0 | 24 | Text note | Split menu |
| type_list | `checklist` | 0/0 | 24 | Checklist | Split menu |
| type_ink | `draw` | 0/0 | 24 | Drawing | Split / insert |
| type_image | `image` | 0/0 | 24 | Image | Insert |
| camera | `photo_camera` | 0/0 | 24 | Camera | Image sheet |
| gallery | `photo_library` | 0/0 | 24 | Gallery | Image sheet |
| more | `more_vert` | 0/0 | 24 | More | App bars |
| share | `share` | 0/0 | 24 | Share | Overflow |
| export_md | `description` | 0/0 | 24 | Export Markdown | Overflow |
| export_pdf | `picture_as_pdf` | 0/0 | 24 | Export PDF | Overflow |
| lock | `lock` | 0/1 | 24 / 16 card | Lock | Overflow / card |
| unlock | `lock_open` | 0/0 | 24 | Unlock | Overflow |
| delete | `delete` | 0/0 | 24 | Delete | Overflow, swipe |
| undo | `undo` | 0/0 | 24 | Undo | Drawing, snackbar action is text |
| redo | `redo` | 0/0 | 24 | Redo | Drawing |
| project | `layers` | 0/0 | 24 | Project | Insert/move |
| inbox | `inbox` | 0/0 | 24 | Inbox | Move sheet |
| settings | `settings` | 0/0 | 24 | Settings | Account |
| sync | `sync` | 0/0 | 20 | Syncing | Avatar overlay, spins 1.2 s/turn only while CONNECTING |
| offline | `cloud_off` | 0/0 | 16 | Offline | Avatar / sheet |
| conflict | `error_outline` | 0/0 | 24 | Sync conflict | Snackbar context |
| check | `check` | 0/0 | 24 / 14 in ring | Done / checked | Drawing done, checkbox |
| bold | `format_bold` | 0/1 | 24 | Bold | Toolbar |
| italic | `format_italic` | 0/1 | 24 | Italic | Toolbar |
| underline | `format_underlined` | 0/1 | 24 | Underline | Toolbar |
| strike | `format_strikethrough` | 0/1 | 24 | Strikethrough | Toolbar |
| code_inline | `code` | 0/1 | 24 | Code | Toolbar inline |
| link | `link` | 0/0 | 24 | Link | Toolbar |
| heading | `title` | 0/1 | 24 | Heading | (prefer H1/H2/H3 chips) |
| bullet | `format_list_bulleted` | 0/1 | 24 | Bulleted list | Toolbar |
| number | `format_list_numbered` | 0/1 | 24 | Numbered list | Toolbar |
| divider | `horizontal_rule` | 0/0 | 24 | Divider | Insert |
| table | `table_chart` | 0/0 | 24 | Table | Insert |
| quote | `format_quote` | 0/0 | 24 | Quote | Insert |
| code_block | `terminal` | 0/0 | 24 | Code block | Insert |
| insert | `add` | 0/0 | 24 | Insert | Toolbar + |
| paper | `grid_on` | 0/0 | 24 | Paper | Overflow |
| font | `text_fields` | 0/0 | 24 | Type | Overflow |
| tint | `format_color_fill` | 0/0 | 24 | Note color | Overflow |
| move | `drive_file_move` | 0/0 | 24 | Move to project | Overflow |
| select | `select_all` | 0/0 | 24 | Select | (rare) |
| drag | `drag_indicator` | 0/0 | 24 | Reorder | Select / table |
| info | `info` | 0/0 | 24 | About | Settings |
| sign_in | `login` | 0/0 | 24 | Sign in | Account |
| sign_out | `logout` | 0/0 | 24 | Sign out | Account |
| passkey | `key` | 0/0 | 24 | Passkey | Clerk custom |
| google | official G | — | 20 | Continue with Google | Auth (`ui/14`) |
| pencil | `edit` | 0/1 | 24 | Pencil | Drawing |
| marker | `draw` | 0/1 | 24 | Marker | Drawing |
| highlighter | `highlight` | 0/1 | 24 | Highlighter | Drawing |
| eraser | `ink_eraser` | 0/1 | 24 | Eraser | Drawing |
| width | `line_weight` | 0/0 | 24 | Thickness | Drawing popover |
| opacity | `opacity` | 0/0 | 24 | Opacity | Drawing popover |
| palette | `palette` | 0/0 | 24 | Color | Drawing |
| copy | `content_copy` | 0/0 | 20 | Copy | Code block |
| expand | `open_in_full` | 0/0 | 20 | Expand image | Lightbox |
| capture | `crop_free` | 0/0 | 24 | Capture what’s behind | ROLE_NOTES float only |
| restore | `undo` | 0/0 | 24 | Restore | Trash |
| radio | (component) | — | — | Radio | Focus / move |
| see_all | none — text `See all` | — | — | Pin strip | |
| empty | **no icon** | — | — | — | |
| sync_ok | **no icon** | — | — | — | |

Checkbox control is **not** an icon: 22 dp ring, 2 dp `outline`, fill `primary` + 14 dp `check` `onPrimary`.

Launcher: cream sheet + wax wedge on ink background. See BUILD §8.

## Optical rules

- Never 20 dp and 24 dp mixed in the same bar.  
- Drawing toolbar is all 24.  
- Card badges 16, top-end, 8 dp inset.  
- Spin only `sync`, and only while connecting.  
- If a Symbol is missing, export that **same name** SVG from fonts.google.com/icons (Rounded 24). Do not pick a cousin.
