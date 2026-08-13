# 08 — Color, type, icons, 100 inspirations per key component

## Color

### What "premium color" is in 2026

- **Material You** on Android is the native luxury. Fighting it makes the app feel like a port.
- **One seed + tonal palette** is how Expressive apps stay calm.
- Keep's 12 candy chips are memorable and look cheap next to Craft / Bear / Things.

### Accepted palette system

1. Enable dynamic color.
2. Harmonize user wallpaper colors toward a Notesup seed so we never go neon-green-on-pink by accident.
3. Seed (when dynamic is off or for brand moments):
   - Light: warm paper background (not #FFFFFF), ink-near-black on-surface, a deep **carmine / sealing-wax** primary (Bear-adjacent, not identical).
   - Dark: warm dark (not #000000), paper-cream on-surface, same primary at higher chroma.
4. Note tints: optional, **container-level only**, 8 hues max, 12% of users will use them. Default is untinted paper.
5. Semantic:
   - Pin = tertiary
   - Lock = secondary
   - Error = error
   - Sync = on-surface-variant (no traffic-light green)

### 100 color inspirations (score = steal?)

Bear red 9, Craft warm gray 10, Things 3 pastel surfaces 10, iA black/white 8, reMarkable cream 10, Apple Notes yellow (legacy) 4, Apple Notes modern 7, Keep candy 3, Samsung blue 4, OneNote purple 3, Evernote green 3, Notion default 6, Obsidian purple 5, Linear indigo 9, Superhuman black 6, Stripe purple 7, Mercury white 8, Cash App green 6, Copilot Money soft 9, Monzo coral 8, N26 white 7, Wise green 6, Wallet 8, Apple Music 7, Apple Music Classical 9, Spotify black 4, Nothing monochrome 8, Pixel Expressive 9, Gmail colorful 5, Clock 8, Recorder 8, Photos 7, VSCO film 8, Darkroom 8, Halide 8, Flighty status 9, Hello Weather 9, CARROT 5, Oura black 7, Gentler Streak 9, Streaks 8, Fitness rings 4, Ivory 8, Apollo orange 6, Tweetbot blue 5, Instagram 3, TikTok 2, BeReal 5, Day One 7, Flomo 7, Memos 7, Medium 8, Dropbox Paper 7, iA syntax 7, Ulysses 8, Typora 6, Nord theme 5, Dracula 3, Solarized 4, Catppuccin 4, OLED true black 5, Material You teal 7, Material You salon 6, Samsung One UI 5, HyperOS 4, ColorOS 4, Nothing glyph 7, Arc orange 6, Dia 6, Raycast red 6, Fantastical 7, Structured 8, Cron 8, Todoist red 5, TickTick 3, OmniFocus 4, Slack aubergine 3, Discord blurple 2, Telegram 4, Signal blue 6, WhatsApp 3, iMessage 6, Kindle 7, Instapaper 8, Matter 7, Reader 7, Goodnotes paper 7, Nebo 6, Penly 6, INKredible white 8, Squid 5, Concepts 6, Procreate dark 6, Figma 6, FigJam 4, Miro 3, Milanote 8, Heptabase 7, Capacities 8, Anytype 7, Standard Notes 6, Joplin 4, Notesnook 6, Notally 8, Quillnote You 8, Markor 6, Fossify 5.

**Accepted DNA:** Craft warmth + Things surfaces + Bear one accent + Pixel dynamic + reMarkable paper.

**Rejected:** Keep candy as default, OLED gamer dark, Discord blurple, traffic-light sync.

## Typography

Android premium type in 2026 is **variable system fonts** (Roboto Flex / brand if we ship one) with a real scale, not 14sp everywhere.

| Role | Size / weight | Use |
|---|---|---|
| Display | 36–40, medium | Empty home wordmark |
| Headline | 28 | Editor title |
| Title large | 22 | Home app bar |
| Title medium | 16–18 | Card title |
| Body large | 17–18 | Editor paragraph (this is the product) |
| Body small | 13 | Card preview |
| Label | 12–14, medium | Pills, meta |

**Accepted**

- Optical size if we ship a variable font
- Comfortable measure: editor max width ~66ch on tablets, full width on phone with 20–24dp side
- Tabular numbers for times
- No fake-bold
- Avoid Inter-on-Android-as-personality (it reads as every SaaS)

**Rejected:** Comic paper fonts, handwritten display as UI, 12sp body, justified text.

### Type inspirations (selected, 40)

Apple Notes 8, Bear 10, iA Writer 10, iA Duo 9, Ulysses 9, Craft 10, Medium 9, Instapaper 8, Reader 8, reMarkable 10, Kindle 7, Things 3 9, Linear 9, Stripe 9, Mercury 8, Hello Weather 9, Apple Music Classical 10, Ivory 8, Notally 7, Material 3 default 7, Roboto Flex 8, Noto Serif (reading mode later) 8, Source Serif 7, Literata 8, Newsreader 7, Fraunces (too much) 4, Playfair (no) 2.

**Accepted:** one sans for UI + editor. Optional **reading face** (a real text serif) as a toggle later, not default.

## Icons

**Accepted:** Material Symbols (variable optical size, weight 400, grade aligned with Expressive). Filled when selected, outlined when not — Expressive already wants this.

**Rejected:** custom 2px stroke icon set that fights the OS, iOS SF Symbols copies, colorful duo-tone everywhere, emoji as nav.

### Icon inspirations (selected)

Material Symbols 10, SF Symbols (meaning, not shapes) 8, Phosphor 6, Lucide 6, Feather 5, Samsung One 5, Nothing 7, Linear 8, Craft 8, Bear 8, Things 9, Keep 6.

## 100 inspirations × key components

Each line: what we saw → score → accept/reject for Notesup.

### 1) Home note card (the object)

Craft 10 A, Apple note row 8 A, Keep tile 6 F, Zoho cover 8 F, Photos tile 7 F, Wallet pass 8 A, Things to-do card 8 A, VSCO 8 A, Darkroom 7 F, Pixel Screenshots 8 A, Notally row 8 A, Milanote 6 F, Notion gallery 6 F, Goodnotes cover 6 F, Memos card 7 F, Day One 7 F, Flomo 7 A, Linear issue 7 F (too tool), Ivory post 7 F.

**Accept:** quiet paper card, optional thumb, 2-line title, 2-line preview, tiny meta. **Reject:** loud color fill, huge cover only, status chrome.

### 2) Filter pills

Expressive ButtonGroup 10 A, Apple Music 9 A, Airbnb 8 A, Pixel Now Bar 7 F, Keep labels 6 F, ChipGroup M2 5 R, tab layout 4 R, segmented iOS 8 A (meaning), Nothing 7 F, Samsung chips 5 R.

**Accept:** connected stadium pills, 4 items max. **Reject:** scrollable chip junk drawer.

### 3) Split FAB / capture

M3 SplitButton 10 A, M3 FAB menu 9 A, Keep FAB 8 A, Gmail compose 7 F, Apple compose 8 A, Drafts instant 10 A, Shazam 9 A (one job), Cash App $ 7 F, Telegram pencil 5 R, Instagram + 3 R.

**Accept:** split, default = note. **Reject:** speed-dial of 8.

### 4) Search

Pixel search 10 A, Keep 9 A, Raycast 10 A (verbs), Linear 9 A, Superhuman 8 A, Spotlight 9 A, Gmail 6 F, Notion 5 R, Evernote 4 R.

**Accept:** icon → expanding field → verbs (`pin`, `pdf`, `ink`, `project:`). **Reject:** search tab.

### 5) Editor title

Apple 9 A, Bear 10 A, Craft 10 A, iA 9 A, Medium 9 A, Google Docs 4 R, Word 2 R, Notion untitled 6 F.

**Accept:** large, placeholder "Title", not required.

### 6) Editor body

Bear 10 A, Apple 9 A, iA 10 A, Craft 9 A, Medium 8 A, Keep 5 R, Samsung 4 R, OneNote 3 R.

**Accept:** 17–18sp, 1.45–1.55 line height, paper bg.

### 7) Floating format toolbar

M3 HorizontalFloatingToolbar 10 A, Apple Notes 9 A, Craft 8 A, Pixel 8 A, Samsung ribbon 3 R, Word 2 R, Notion / 6 F.

### 8) Insert image

Apple 9 A, Craft 9 A, Photos 8 A, Keep attachment 5 F, Slack 5 F.

### 9) Ink canvas

INKredible 9 A, Apple sketch 9 A, reMarkable 10 A, Samsung 7 F, Goodnotes 6 F, Keep doodle 4 R.

### 10) Checkbox

Things 10 A, Streaks 9 A, Apple 8 A, Keep 7 F, Notion 6 F, Todoist 5 F.

### 11) Overflow menu

Expressive vertical menu 10 A, Apple 8 A, Android default 7 F, hamburger drawers 3 R.

### 12) Empty home

Things 10 A, Craft 9 A, iA 8 A, Linear 9 A, Stripe 9 A, illustration Lottie 3 R, mascot 2 R.

### 13) Settings

Android Settings 10 A, Apple 8 A, Notion 4 R, in-app shop 1 R.

### 14) Account / Clerk

Custom Clerk screens 10 A ([ui/14-ONBOARDING.md](../ui/14-ONBOARDING.md)). AuthView 2 R. Hosted auth 6 A as Play-Services-missing fallback. FirebaseUI 3 R.

### 15) Widget

Keep 8 A, Clock 10 A, Google Notes? 7, Easy Notes 5 F, ColorNote 2 R, Glance samples 8 A.

### 16) Project header

Things 10 A, Craft spaces 9 A, Apple folders 6 F, Evernote stack 3 R, Notion sidebar 4 R.

### 17) Pin

Keep 8 A, Apple 8 A, Gmail 7 F, gold star 4 R, emoji 2 R.

### 18) Lock

Apple Notes 9 A, Wallet 8 A, 1Password 8 A, padlock cartoon 3 R.

### 19) Export sheet

System share 10 A, Bear export 9 A, Craft 8 A, custom branded share 4 R.

### 20) Dialog

M3 Expressive 10 A, iOS alert 7 F (don't fake), full-screen 5 F.

## Component → best design (the cheat sheet)

| Component | Best of 100 | We do |
|---|---|---|
| Card | Craft | Paper, thumb, quiet |
| Pills | Expressive + Music | 4 filters |
| Capture | Drafts + SplitButton | 1 tap write |
| Search | Pixel + Raycast | Mode + verbs |
| Title | Bear | Large optional |
| Body | iA / Bear | Type, focus |
| Toolbar | Floating Expressive | Few actions |
| Ink | INKredible / Apple | Block |
| Check | Things | Joy, haptic |
| Empty | Things / Linear | One sentence |
| Widget | Clock / Keep | Glance |
| Project | Things | Headers |
| Nav large | Rail | Not a phone tab bar |

## Accept / reject

**Accept:** dynamic + warm seed, one accent, Material Symbols, real type scale, 18sp body, paper not white.

**Reject:** candy grid, custom icon religion, Inter-as-brand, serif UI, shop colors, green/red sync lights.
