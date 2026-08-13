# 14 — First-run onboarding (every box)

This is the first thing a new install shows.  
Product law lives in [BUILD.md](../BUILD.md) §9–§10 and §17.  
This file is the look and the clicks.

**Not decided before this file.** Earlier specs said “empty home is onboarding” and “Clerk `AuthView`.” Those two lines are **replaced**. Empty home is still the empty state. It is no longer the first frame of a new install.

---

## The decision in one paragraph

A new install is **two screens, one optional code, one optional sync question.**  
Screen 1 is **ours** (paper + a sentence + two actions).  
Screen 2 is **ours** (custom Clerk, never `AuthView`, never `clerk-android-ui`).  
Skip is the primary action. Sign-in is the secondary action. Both land on **home**.  
There is no carousel, no theme picker, no sample note, no permission, no “Secured by Clerk,” no password field on the first auth surface.

Typical skip: **one tap**.  
Typical Google: **two taps** (Sign in → account).  
Typical email: **three taps** (Sign in → Continue → code).

---

## What the user sees, in order

Android 12+ always shows a **system splash**. We do not add a second one.

1. **System splash** — paper-coloured (`background` / `paper` `#F6F1EA` light, `#161311` dark). Launcher mark. Dismissed on the first Room emission (usually 0 ms after process). This is the OS, not a Notesup logo screen.
2. **Welcome** — only when `onboarding_done == false` **and** `Clerk.user == null`.
3. **Sign in** — only if they tap `Sign in to sync` (or later tap Sign in on the account sheet).
4. **Email code** — only if they continue with email.
5. **Sync these notes?** — only if they just signed in **and** this phone already has notes with no `remoteId`.
6. **Home** — empty sentence + split plus, or their notes.

Every later cold start goes **splash → home**. Welcome never returns.

---

## Who never sees Welcome

Set `onboarding_done = true` and open the destination, skip Welcome:

- Widget new-note / pin / recent / project
- Launcher shortcuts (New note / checklist / drawing)
- Share target
- `ACTION_CREATE_NOTE` / `notesup://new…`
- `notesup://note/{id}` / `project/{id}` / `search`
- Clerk session already restored (`userFlow != null`) — reinstall with a living session, or process restore

Back from Welcome is **leave the app** (system predictive back to launcher). There is no “skip” chip. **Start writing** is skip.

---

## Screen 1 — Welcome

Full-screen `background`. Paper grain 2–3% (`PaperGrain`). Edge-to-edge. No app bar. No handle. No page dots. No illustration. No Lottie. No mascot. No Clerk.

```
┌─────────────────────────────────────┐
│ status                               │
│                                      │
│                                      │
│              Notesup                 │  displaySmall 500, onSurface
│                                      │  12 dp
│          Write anything.             │  bodyLarge, onSurface
│                                      │  8 dp
│     Notes live on this phone.        │  bodyMedium, onSurfaceVariant
│     Sign in later if you want        │  max 2 lines, maxWidth 280
│     them everywhere.                 │  textAlign Center
│                                      │
│                                      │
│         [  Start writing  ]          │  56 h, stadium, primary, filled
│                                      │  12 dp
│          Sign in to sync             │  text button, labelLarge, primary
│                                      │
│                         24 + nav     │
└─────────────────────────────────────┘
```

### Layout (phone)

- Horizontal pad **32**.
- Wordmark + sentence block is a column, `Arrangement.Center` in a `Box` that takes **weight 1** above the buttons.
- Wordmark `Notesup` — `displaySmall`, weight 500, `onSurface`. Shared-bounds key **`wordmark`** (same key as the home app-bar wordmark).
- 12 dp.
- `Write anything.` — `bodyLarge`, `onSurface`, center.
- 8 dp.
- `Notes live on this phone. Sign in later if you want them everywhere.` — `bodyMedium`, `onSurfaceVariant`, center, `maxWidth 280`, maxLines 2.
- Buttons sit above `24 + navigationBars` inset, full width of the 32 pad.
- **Start writing:** filled, 56 h, shape stadium (28), `primary` / `onPrimary`, `titleMedium`. Haptic `CONFIRM`. Sets `onboarding_done = true`. Navigates `welcome → home` with shared-bounds on `wordmark` + `motionScheme.slowSpatialSpec`.
- **Sign in to sync:** text button, `labelLarge`, `primary`. Does **not** set `onboarding_done`. Pushes `auth` (Welcome stays under so Back returns here).

### Layout (width ≥ 640 dp)

Same column, **max width 400**, centered. No rail on this screen.

### What they do not see here

- Google button (that is screen 2)
- Continue without an account (Start writing **is** that)
- Terms (those live on Sign in)
- A third “maybe later”
- Theme / paper / font pickers
- Notification / files / role / biometric permission
- Credential Manager sheet (One Tap on Welcome would be an auth wall)

### Motion / state

- Enter: first frame **is** Welcome. No extra fade on top of the system splash.
- Reduce-motion: no shared-bounds; fade 160 ms to home.
- Clerk not initialized: Start writing still works. Sign in is enabled; the next screen waits on `isInitialized` with a 20 dp spinner **on that screen**, never here.

---

## Screen 2 — Sign in (custom Clerk)

Route: `auth`.  
Same paper + grain. **Our Compose.** `clerk-android-api` only. **Never** `AuthView`. **Never** `clerk-android-ui`. **Never** hosted browser unless Google Play Services is missing **and** they tapped Google.

```
┌─────────────────────────────────────┐
│ status                               │
│  ←                                 │  64 bar, 48 back, no title
│                                      │
│              Notesup                 │  titleLarge 500
│                                      │  16
│          Sign in to sync             │  titleMedium
│                                      │  4
│   Notes stay on this phone           │  bodyMedium, onSurfaceVariant
│   until you do.                      │  center, maxWidth 280
│                                      │  32
│   [ G    Continue with Google     ]  │  56 h, radius 16
│                                      │  16
│   ──────────  or  ──────────         │  labelSmall, outline
│                                      │  16
│   [ Email                        ]   │  OutlinedTextField 56
│                                      │  12
│         [     Continue     ]         │  56 h, stadium, primary
│                                      │  16
│          Use a passkey               │  text, only if one exists
│                                      │
│     Continue without an account      │  text, onSurfaceVariant
│                                      │  16
│  By signing in, notes you choose     │  labelSmall, onSurfaceVariant
│  to sync are stored in your account. │  center, maxWidth 280
│                         24 + nav     │
└─────────────────────────────────────┘
```

### Chrome

- Bar 64 + status: `arrow_back` 48 start 8. Content description `Back`.
- Back from first-run: pop to Welcome.
- Back from account-sheet entry: pop to home (sheet may still be open under, or closed — **close the sheet** when pushing `auth`, so Back is home).
- Wordmark `titleLarge` 500, center of the content column. **No** shared-bounds here (Welcome already used `wordmark` for the home transition).
- Horizontal pad 32. Column max width **400** on large screens.

### Google

- Button 56 h, width fill, radius **16** (not stadium — it is a method, not the primary write action).
- Fill `surfaceContainerHigh`. 1 dp `outlineVariant` border. No elevation.
- Leading: official Google **G** 20 dp (`i_google` brand asset, not a Symbol), start 16.
- Label `Continue with Google` `titleMedium` `onSurface`.
- Press: ripple only. Disable all actions. 20 dp `CircularProgressIndicator` replaces the G until Clerk returns.
- Implementation: Clerk’s native Google path (`Clerk.auth.signInWithOAuth(OAuthProvider.GOOGLE)` / Credential Manager). **No WebView. No legacy `play-services-auth`.**
- **On first composition of this screen, once per process:** if Play Services is present, also present the **Credential Manager account sheet** (Google’s recommended pair: sheet + dedicated button). Dismissing the sheet leaves our UI. Do not re-prompt until they leave and re-enter `auth`.
- Success: `setActive` → §After sign-in.
- Failure / cancel: re-enable. Inline `Couldn’t sign in.` `bodySmall` `error` under the Google button. Haptic `REJECT`. No dialog.
- No Play Services: tapping Google runs `Clerk.auth.startHostedAuth` (Custom Tab). Same success/fail. This is the only hosted fallback.

### Email

- `OutlinedTextField`, 56 h, shape radius 16, `containerColor = surface`.
- Hint `Email`. `KeyboardType.Email`. `ImeAction.Go` = Continue.
- Continue: filled 56 h stadium `primary`. Disabled while email fails `Patterns.EMAIL_ADDRESS`.
- On tap: `SignIn.create(EmailCode(email))`. If Clerk says identifier unknown, **transfer to SignUp** with the same email and send the email code. One field is both sign-in and sign-up. The user never picks “create account.”
- Then push `auth/code`.
- No password field on this screen.
- No phone. No Apple. No GitHub / Facebook / Discord. No Clerk organizations.

### Passkey

- Show `Use a passkey` **only** if Credential Manager reports a passkey for our rpId.
- Text button, `labelLarge`, `primary`.
- `Clerk.auth.signInWithPasskey()` → `setActive` → §After sign-in.
- Hidden for a brand-new install (there is no passkey yet). It exists so a returning user who opened Sign in from the account sheet can skip email.

### Continue without an account

- Text button, `labelLarge`, `onSurfaceVariant`.
- Sets `onboarding_done = true`. Navigates to home (clear `welcome` + `auth` off the stack).
- Same destination as Start writing. Same haptic `CONFIRM`.

### Footer

`By signing in, notes you choose to sync are stored in your account.`  
`labelSmall`, `onSurfaceVariant`, center. No URL in v1. Full privacy copy is Settings later.

### Password fallback (only if Clerk requires it)

If the email exists **and** the only first factor is password (no email code):

- Replace Continue’s next screen with a **Password** screen: back, `Enter your password`, field, filled Continue, `Use a code instead` if email_code is also available.
- Do not put password on screen 2. Do not celebrate it.

### MFA (only if the account has it)

After Google / email / passkey, if `SignIn.status != COMPLETE`: TOTP screen, same 6-cell as email code, title `Enter your authenticator code`. Backup-code link `Use a backup code` (plain field). Rare.

---

## Screen 2b — Email code

Route: `auth/code`.

```
┌─────────────────────────────────────┐
│  ←                                 │
│                                      │
│         Check your email             │  titleMedium, center
│                                      │  8
│   We sent a 6-digit code to          │  bodyMedium, onSurfaceVariant
│   {email}                            │  onSurface, no wrap if possible
│                                      │  32
│   [ ] [ ] [ ] [ ] [ ] [ ]            │  48 × 56, gap 8, radius 12
│                                      │  16
│         Resend in 0:30               │  then “Resend code”
│                                      │
└─────────────────────────────────────┘
```

- Six boxes, 48 × 56, radius 12, fill `surfaceContainerHigh`. Focused box: 2 dp `primary` stroke.
- `titleLarge`, center, tabular. One digit each.
- Single hidden `BasicTextField` drives all six (a11y: one field, `contentDescription` `Verification code`).
- Auto-advance. Auto-submit on the 6th digit.
- Wrong code: boxes stroke `error`, haptic `REJECT`, clear, refocus first. Inline `That code wasn’t right.`
- Expired: same, `That code expired. Resend it.`
- Resend: disabled 30 s (`Resend in 0:30` `labelSmall` `onSurfaceVariant`), then text button `Resend code`.
- Success: `setActive` → §After sign-in.
- Back: pop to Sign in, keep the email filled.

---

## After sign-in — next screen

Set `onboarding_done = true`. Clear `welcome` / `auth` / `auth/code` off the stack. Haptic `CONFIRM`.

| Situation | Next screen |
|---|---|
| 0 notes on this phone | **Home**, empty. `Write anything.` + plus. Avatar is now the photo/letter. Sync is **on**. New notes upload. No dialog. No snackbar. |
| ≥ 1 note with no `remoteId` | **Home** with the notes, **then** dialog `Sync these notes?` |
| Opened from account sheet, 0 unsynced | **Home**. Sheet stays closed. Sync on. |
| Opened from account sheet, unsynced locals | **Home** + same dialog. |

### `Sync these notes?`

Dialog, radius 28 (ui/09).

- Title `Sync these notes?` `titleSmall`
- Body `{n} notes on this phone will upload to your account.` `bodyMedium` `onSurfaceVariant`
- `Not now` text button → signed in, **`sync_paused = true`**, notes stay local until they tap Sync on the account sheet
- `Sync` filled default → upload via Convex outbox, `sync_paused = false`

Signing in **is** choosing cloud. The dialog is only consent to **upload what is already here**. A brand-new writer never sees it.

### What “cloud or no” means

| Path | Account | Convex | Notes |
|---|---|---|---|
| Start writing / Continue without | None | Off | Phone only |
| Sign in, 0 notes | Clerk session | On | New notes sync |
| Sign in, Sync | Clerk session | On | Locals upload, then live |
| Sign in, Not now | Clerk session | Paused | Locals stay; avatar shows `Sync paused` |

There is no “cloud without an account.” There is no third onboarding toggle.

---

## Later visits (not first-run)

Account sheet (ui/10) **Out**: same copy, filled `Sign in` → **this** `auth` route (not a sheet of Clerk chrome).  
Account sheet **In**: email, `Synced` / `Sync paused`, Settings, About, Sign out.  
Sign out: local notes remain. Session ends. Sync stops. Home stays. Welcome does **not** return.

Settings row “Sign in / Manage account”: signed out → `auth`. Signed in → a **our** manage screen (email, sign out, delete account). Not Clerk’s UserProfile.

---

## Clerk wiring (locked)

Dependency: **`com.clerk:clerk-android-api` only.** Do not add `clerk-android-ui`.

```
Application: Clerk.initialize(this, BuildConfig.CLERK_PUBLISHABLE_KEY)
Dashboard: Native API on. Google OAuth on. Email code on. Passkeys on.
           Combined sign-in/up. Password allowed as fallback, not required.
           Organizations off. No Clerk branding in our UI.
```

| Action | API |
|---|---|
| Google | `Clerk.auth.signInWithOAuth(OAuthProvider.GOOGLE)` → Credential Manager |
| Email start | `SignIn.create(EmailCode)` or SignUp transfer + send code |
| Email verify | `signIn.verifyCode("123456")` / `attemptFirstFactor` |
| Passkey | `Clerk.auth.signInWithPasskey()` then `setActive` |
| Session | `Clerk.auth.setActive(sessionId)` |
| Hosted fallback | `startHostedAuth` only if Play Services missing + Google tap |
| Convex | `createClerkConvexClient` as already locked |

`combine(Clerk.isInitialized, Clerk.userFlow)`:

- Not ready + on Welcome / Home: treat as signed out. **Never block writing.**
- Not ready + on `auth`: 20 dp spinner centered under the wordmark, buttons disabled. Timeout 8 s → inline `Couldn’t reach sign-in.` + `Try again`.

Errors: inline `bodySmall` `error`. Haptic `REJECT`. Never a Clerk error JSON dump.

---

## Premium (what we add, what we refuse)

**Add**

- The object is the page: grain, cream, wordmark, one sentence.
- Shared-bounds wordmark Welcome → Home so Start writing feels like the cover opening, not a route change.
- System splash **is** the same paper, so there is no colour flash (F-25, honest).
- Google is a paper row + official G, then the OS account sheet — native, not a browser.
- Email is six quiet cells, not a password form.
- One field is sign-in **and** sign-up. No “Don’t have an account?”
- Skip is visually louder than sign-in on Welcome. Sign-in is visually louder than skip on the auth screen. Each screen has one obvious job.
- After success, nothing celebrates. Home is the reward.

**Refuse**

- Clerk `AuthView` / `clerk-android-ui` / purple Clerk chrome / “Secured by Clerk”
- 3–5 page carousel, progress dots, “Skip” in the corner of a lecture
- Sample notes, “Import from Keep / Evernote”
- Theme / paper / font / ROLE_NOTES / notifications on first run
- Auth wall (Google as the only button, no Start writing)
- One Tap on Welcome
- Confetti, check-circle hero, “You’re all set”
- Snackbar `Welcome back.` / `Synced.` (ui/09 still forbids those)
- Password as the lead method
- A third “Do you want cloud sync?” wizard after they already chose

---

## DataStore

```
onboarding_done: Boolean = false
sync_paused: Boolean = false   // only meaningful when signed in
```

`onboarding_done` flips true on: Start writing, Continue without, successful `setActive`, or any skip-Welcome entry (widget, share, shortcut, deep link, existing session).

---

## Motion table (this file owns these rows)

| Event | Spec |
|---|---|
| System splash → Welcome / Home | OS. `windowBackground` = `background`. `core-splashscreen` keep-on-screen until first Room emission. |
| Welcome → Home | sharedBounds `wordmark` + `slowSpatialSpec`. Reduce-motion: fade 160. |
| Welcome → Auth | fade + slide up inherit (component). Back reverses. |
| Auth → Code | fade inherit. |
| Auth success → Home | fade 160 + haptic `CONFIRM`. No shared-bounds (avatar change is enough). |
| Code boxes | no bounce. Digit appears. Error = color only. |
| Google / Continue loading | 20 dp spinner on the button. No full-screen veil. |

Cold start after onboarding: first Compose frame is **home**. ui/05 “first frame is home” applies to every launch except the first Welcome.

---

## Strings (this file)

```
welcome_line=Write anything.
welcome_body=Notes live on this phone. Sign in later if you want them everywhere.
start_writing=Start writing
sign_in_to_sync=Sign in to sync
sign_in_body=Notes stay on this phone until you do.
continue_google=Continue with Google
continue_email=Continue
continue_without=Continue without an account
email_hint=Email
use_passkey=Use a passkey
or=or
auth_legal=By signing in, notes you choose to sync are stored in your account.
check_email=Check your email
code_sent=We sent a 6-digit code to
code_cd=Verification code
code_wrong=That code wasn’t right.
code_expired=That code expired. Resend it.
resend_in=Resend in 0:%02d
resend=Resend code
auth_fail=Couldn’t sign in.
auth_timeout=Couldn’t reach sign-in.
try_again=Try again
password_title=Enter your password
password_hint=Password
use_code_instead=Use a code instead
totp_title=Enter your authenticator code
backup_code=Use a backup code
```

`empty_home`, `sync_these`, `sync_these_body`, `not_now`, `sync` stay as in BUILD.

---

## Files to create (when coding)

```
ui/onboarding/WelcomeScreen.kt
ui/account/AuthScreen.kt          // already in the tree
ui/account/AuthCodeScreen.kt
data/auth/AuthRepository.kt       // already in the tree
```

No `AuthView`. No Clerk theme wrapper.

**Password / TOTP / backup / hosted spinner / landscape / IME insets / pressed+loading:** [ui/22-SYSTEM-EDGES.md](22-SYSTEM-EDGES.md) §12. Manage account / sign-out: [ui/15-SETTINGS.md](15-SETTINGS.md) + ui/22.

Welcome and Auth: `WindowInsets.ime + navigationBars` pad the action stack. Continue is never under the keyboard.

---

## Acceptance

- New install, no tap: Welcome, not home, not Clerk.
- One tap Start writing: empty home, plus works, IME ≤ 200 ms from plus, never asked again.
- Sign in → Google → (0 notes) → empty home, avatar is them, next plus syncs.
- Sign in → email → 6 cells → same.
- Continue without → same as Start writing.
- Back on Welcome leaves the app.
- Second launch is home.
- Widget / share / `CREATE_NOTE` never show Welcome.
- Account sheet Sign in is the same `auth` screen.
- `AuthView` does not exist in the APK.
- TalkBack: Welcome reads wordmark, sentence, body, Start writing, Sign in to sync. Code screen is one field.
