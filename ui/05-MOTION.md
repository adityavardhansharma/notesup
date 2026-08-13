# 05 — Motion

`MotionScheme.expressive()` on the theme.

**Material owns** pills, sheets, menus, SplitButton, toolbar, dialogs, selection. **No milliseconds for those.** They inherit spatial/effects springs (visible overshoot is OK on jewels, **never** on caret/type/IME).

**We own** only the rows marked `us`.

## Every animation

| Event | Owner | Spec |
|---|---|---|
| Cold start | us + OS | System splash is `background` paper (`core-splashscreen`, keep until first Room emission). First Compose frame is **Welcome** if `!onboarding_done && user == null`, else **home**. No custom logo splash. |
| Pill change | **component** | inherit |
| Grid/list toggle | **component** | inherit; icon fill morph |
| Card press | us | fill → `surfaceContainerHighest` (~120, color only, no scale) |
| Open note | **component + us** | `sharedBounds` + `motionScheme.slowSpatialSpec<Rect>()` |
| New note | us | Editor fade in; IME system |
| Predictive back | system + us | Seek sharedBounds with `progress`; no haptic |
| Back commit / cancel | us | finish spring / reset + rethrow cancel |
| Hardware back | us | same spring 0→1 |
| Home → launcher | **system** | — |
| Search open/close | **component + us** | sharedBounds on field |
| Plus menu | **component** | trailing spins **and shape-morphs** |
| Plus → sheet | **component** | inherit |
| Magic-plus drop | us | ghost follows; settle inherit |
| Checkbox | us | 200 ms fill + **circle → soft squircle morph** |
| Pin | us | tint to tertiaryContainer |
| Delete / snackbar | **component** / us | snackbar **dwell 4000** (us) |
| Focus chrome | us | **240** out after **2000** idle; **160** in |
| Sentence dim | us | **200** to **62%** |
| IME / toolbar follow | **system** | — |
| Sheet / menu / dialog | **component** | inherit |
| Theme change | us | scheme crossfade inherit if possible |
| Sync spinner | us | **1200 ms**/turn linear, CONNECTING only |
| Image lightbox | **component + us** | sharedBounds — [ui/19](19-MEDIA.md) |
| Lock gate open | us | sharedBounds card → gate, then fade 160 to editor on success |
| Capture Done | us | `finish()` system |
| Two-pane swap | **component** | 160 crossfade, no sharedBounds |
| Drawing tool select | **component** | shape morph |
| Width/opacity popover | **component** | inherit |
| Block insert | us | height animate inherit if possible |
| Select mode | **component** | inherit |
| Widget launch | **system** | — |
| Pull refresh / splash / confetti | — | **does not exist** |

**Reduce motion:** skip sharedBounds; fade only. IME and system home-back stay.
