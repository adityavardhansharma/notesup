# 12 — Clerk (every doc that matters for Notesup)

Authentication and user management. Official native Android SDK exists. Official Convex integration exists. We are not the first people to do this on a phone.

## What Clerk is for us

- Sign in / sign up
- Session
- Optional passkey + Google
- Profile bits (email, avatar)
- JWT that Convex already knows how to verify
- Webhook so Convex has a `users` row

It is **not** our note database. It is **not** required to write the first note.

## Docs / sources read and rated

| Doc | What we learned | Importance |
|---|---|---:|
| **Clerk Android SDK** (`/clerk/clerk-android`) | `Clerk.initialize(context, publishableKey)` in `Application`. `isInitialized`, `userFlow`, `sessionFlow`, `clientFlow`. minSdk **24**, Kotlin 2.4, Compose BOM current, SDK ~1.0.35 | **10** |
| **AuthView** prebuilt Compose | Exists. We **do not ship it.** Custom paper screens instead. | **2** |
| **Hosted auth** `Clerk.auth.startHostedAuth` | Browser sheet, `HostedAuthMode.SIGN_UP` / sign-in. Good fallback | **8** |
| **Passkeys** `signInWithPasskey` + `setActive` | Modern, premium, OS UI | **9** |
| **OAuth / Google** `signInWithOAuth` + Credential Manager | Primary Google path on our `auth` screen | **10** |
| **User model** emails, primary, verification | Show email on avatar sheet | **8** |
| **Reverification** session first/second factor helpers | Password, email code, TOTP, backup. For lock / export later | **7** |
| **Backend `auth.has({ reverification })`** | Step-up. More web-shaped; keep in mind | **5** |
| **Manual JWT verify** | We will **not** do this on Android; Convex + Clerk library does | **4** |
| **Clerk + Convex Android** `ClerkConvexAuthProvider` / `createConvexClientWithAuth` | The glue. Initialize both in `Application` | **10** |
| **Clerk webhook → Convex** (on Convex docs) | Svix verify, upsert/delete | **10** |
| **clerk-convex-kotlin README + workout sample** | Real Android app | **9** |
| **Clerk iOS SDK** | SwiftUI inspiration only | **3** |
| **Clerk Next.js / React prebuilts** | Web. Ignore for UI chrome | **3** |
| **Organizations / B2B** | Teams. **Rejected** for v1 | **3** |
| **Billing / subscriptions** | If we ever paid-tier | **4** |
| **User profile component (web)** | Don't embed web profile | **3** |
| **Bot protection / enterprise SSO** | Later | **3** |
| **Clerk Skills** (agent docs) | Implementation aid later | **6** |
| **Error handling guides** | `onFailure` / `onSuccess` on `ClerkResult` | **8** |

**Weighted conclusion:** Android **API** + custom screens + Convex provider + webhook are the four documents that matter. AuthView, organizations, billing, web components do not.

## Product rules for auth (this is UX)

Premium notes apps (Apple Notes, Bear, Craft, Keep) let you **write before you marry an account**.

**Accepted flow**

1. Cold first launch: Welcome (Start writing / Sign in to sync). After that, Room + FAB + widgets. See ui/14.
2. Avatar is a quiet "Sign in to sync" if signed out.
3. Sign-in is our `auth` route, not a wall and not AuthView.
4. After sign-in: upload local notes (with consent on first link: "Sync N notes to this account?").
5. Sign-out: local remains. Remote unswept.
6. Account switch: rare; confirm.

**Rejected flow**

- Clerk splash as launch
- "Create an account to continue"
- Social-only with no skip
- Organizations / workspace picker
- Auth0-shaped lock screen

## UI we will ship

| Piece | Choice | Score |
|---|---|---:|
| First version sign-in | **Custom Compose** (`clerk-android-api` only): Google + email code + passkey. See [ui/14-ONBOARDING.md](../ui/14-ONBOARDING.md) | 10 |
| AuthView / `clerk-android-ui` | **Banned.** Looks like Clerk, not paper. | 2 |
| Hosted auth browser | Fallback only if Play Services missing and they tapped Google | 6 |
| Profile sheet | Our UI, Clerk data | 10 |
| One Tap Google | ON | 9 |
| Passkeys | ON | 9 |
| Password | Allowed, not celebrated | 6 |
| MFA / TOTP | Optional in Clerk dashboard, used for reverification of export later | 6 |

Never show Clerk's default marketing chrome. The screens must look like Notesup paper. First-run is Welcome then this `auth` route — not a sheet of AuthView.

## Android lifecycle (official pattern we accept)

```kotlin
combine(Clerk.isInitialized, Clerk.userFlow) { ready, user ->
    when {
        !ready -> AuthUi.Loading
        user != null -> AuthUi.SignedIn(user)
        else -> AuthUi.SignedOut
    }
}
```

Loading must be **short** and must not block the note list. If Clerk is slow, treat as SignedOut + local.

## Tokens and Convex

Do not manually stuff `Authorization` headers. `ConvexClientWithAuth` + `ClerkConvexAuthProvider` owns that. If a session expires, the client should re-auth; we surface "Sync paused" not a crash.

Webhook is the server-side user mirror. Android never creates the Convex `users` row itself.

## Security UX

| Action | Auth need |
|---|---|
| Create/edit local | None |
| Sync | Session |
| Open locked note | Biometric local (AndroidX) |
| Change email | Clerk |
| Delete account | Clerk + Convex purge mutation |
| Export PDF | None (local) |
| Export everything / backup | Session + optional reverification |

## Privacy copy we will need (not legal advice)

- What syncs
- What Clerk stores (email, auth)
- What Convex stores (notes if you opt in)
- How to delete

This is part of premium. Cheap apps hide it.

## Accept / reject

**Accept:** official `clerk-android-api`; Clerk+Convex provider; webhook; **custom** Google / email-code / passkey screens; skippable auth; Credential Manager; local-first.

**Reject:** auth wall; `AuthView` / `clerk-android-ui`; organizations; building our own IdP; Firebase Auth; Auth0; embedding Clerk web profile; manual JWT on device.

## Risks

1. Custom flow is more code than AuthView — that is the point. Follow ui/14.
2. Hosted auth is a browser — feels less premium, fine as Play-Services-missing fallback.
3. minSdk 24 vs Convex sample 26 — take 26.
4. Session restore race on process death — list must not depend on it.
5. Webhook failure = orphaned Convex user — make mutations still key off `clerkUserId` from JWT, not the webhook row alone (webhook is cache).

## Doc importance, one line

If we can only keep three URLs: Clerk Android init + custom SignIn APIs, `clerk-convex-kotlin`, Convex webhook sample. Look: [ui/14-ONBOARDING.md](../ui/14-ONBOARDING.md).
