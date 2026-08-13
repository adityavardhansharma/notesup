# 11 — Convex (every doc that matters for Notesup)

Cloud sync plane. Not the source of truth. Room is.

Official Android path is real as of the docs we read (2026): `dev.convex:android-convexmobile`, Rust-under-the-hood WebSocket client, Kotlin serialization, `ConvexClientWithAuth`, first-class **Clerk** provider.

## What Convex is (in one honest paragraph)

A hosted reactive database plus serverless functions. You write TypeScript `query` / `mutation` / `action` on the server. Clients subscribe to queries and the UI updates when the data changes. There is a file store. There is an auth layer that expects a JWT from a provider (Clerk, Auth0, custom OIDC).

It is **excellent at live lists** (other device pins a note, this phone's home moves). It is **not** a CRDT document editor. If two devices edit the same paragraph at once, we must design conflict, not hope.

## Docs read and rated

| Doc | What we learned | Importance |
|---|---|---:|
| [Android Kotlin Quickstart](https://docs.convex.dev/quickstart/android) | Empty Activity, min SDK 26 in the sample, INTERNET permission, `kotlinx.serialization`, `ConvexClient(url)`, `subscribe` in Compose, dashboard edits stream into the app | **10** |
| [Android client overview](https://docs.convex.dev/client/android/overview) | Client lifetime, Application subclass, Flow subscriptions, args as `Map<String, Any?>`, mutation/action, Auth0 + **Clerk**, `ConvexClientWithAuth`, flavors for prod/dev URLs, UDF + Repository + ViewModel, testing (`open` class), Rust+Tokio under the hood, `webSocketStateFlow`, `initConvexLogging()` | **10** |
| Convex schema (`defineSchema` / `defineTable` / `v`) | Typed tables. Use this for notes, projects, users | **10** |
| Queries + indexes | `withIndex` for `by_user_updated`, `by_project` | **10** |
| Mutations | All writes. Keep them small. | **10** |
| Actions | Third-party / long / non-deterministic. PDF *generation* could live here later. Not for note writes. | **7** |
| File storage | Images / ink PNG / exports. Store ids on the note. | **9** |
| Auth overview + Clerk webhook sample | `user.created/updated/deleted` → upsert local `users` table via Svix | **10** |
| Error handling (`ConvexError`, `ServerError`) | Map to sealed `WriteResult` | **8** |
| Reactor / subscriptions mental model | Home list is a subscription, editor is a subscription + local echo | **9** |
| Production deployments | Dev vs prod URL via `resValue` / flavors. Don't commit URLs. | **8** |
| Workout sample + Clerk workout sample | Multi-screen Android structure | **8** |
| Data types / numbers / `_id` mapping | Kotlin gotchas | **9** |
| Auth0 Android | Exists. We are not using it. | **3** |
| Custom `AuthProvider` | Escape hatch | **6** |
| Rust client internals | Interesting, don't touch | **2** |
| React/Next/Vue/Svelte/Nuxt quickstarts | Web-only. Useful if we ever build a viewer. | **3** |
| Convex Auth (first-party, not Clerk) | Alternative. **Rejected** — user chose Clerk. | **4** |
| AI / agents Convex features | Not the product | **2** |
| Dashboard usage | Seed + debug | **6** |
| `llms.txt` full index | Discovery | **5** |

**Weighted conclusion:** the Android + schema + mutations + Clerk webhook docs are the only ones we must know by heart. Everything else is situational.

## Official Android facts we will treat as constraints

- Artifact: `dev.convex:android-convexmobile:0.8.0@aar` (`isTransitive = true`)
- Needs `kotlinx-serialization-json`
- `ConvexClient` is process-scoped
- `subscribe<T>("module:function", args)` → `Flow<Result<T>>`
- Subscription dies when nothing collects (VM/composable gone) — **correct**
- `mutation<T>(name, args)` suspends, throws on error
- `action` same shape, use rarely from the phone
- Numbers: use `@ConvexNum` / documented conversions
- `_id` and `_creationTime` need field-name conversion on Kotlin DTOs
- WebSocket is either CONNECTING or CONNECTED; client always tries
- Logging can leak note bodies — debug builds only
- minSdk in Clerk Android is 24; Convex sample says 26. **Notesup minSdk: 26** unless we prove 24 works.

## Clerk glue (official)

Docs show **two** complementary pieces:

1. **Webhook** on Convex HTTP router: verify Svix, upsert/delete `users`.
2. **Android:** `clerk-convex-kotlin` → `ClerkConvexAuthProvider().createConvexClientWithAuth(url, context)`.

Also: Convex's own Android overview "Authentication → Clerk" points at `clerk-convex-kotlin` README and a Clerk-ported workout app.

**Accepted:** this exact stack. Do not invent JWT plumbing.

**Rejected:** Auth0. **Rejected:** Convex Auth as a second identity system.

## Proposed Convex schema (research only)

```ts
// conceptual — not shipping code
users:     { clerkUserId, createdAt }
projects:  { userId, name, hue, emoji, order, updatedAt }
notes:     { userId, projectId?, title, blocks, pinned, locked, tint, updatedAt, deletedAt? }
media:     { userId, noteId, storageId, kind, mime, width?, height? }
```

Indexes:

- `notes.by_user_updated` (userId, updatedAt)
- `notes.by_user_pinned` (userId, pinned, updatedAt)
- `notes.by_project` (userId, projectId, updatedAt)
- `projects.by_user` (userId, order)

`blocks` as `v.any()` or a recursive union. Prefer a validated union so a bad client cannot store garbage.

Files: `ctx.storage` for images and ink previews. Note body never holds bytes.

## Sync design we accept

| Rule | Why |
|---|---|
| Local UUID is primary | Offline create |
| Convex `_id` stored as `remoteId` | Mapping |
| Every local write timestamps `updatedAt` | LWW for metadata |
| Body conflict: last-write-wins in v1, with a **conflict copy** if both dirty | Honest |
| CRDT / Yjs later if we ever collab | Not now |
| Subscribe to lists, not to "the whole vault" as one query | Bandwidth |
| Editor: optimistic local, mutation debounce 400–800ms | Typing |
| Images: upload action/mutation after local disk write | Never block editor |
| Delete = `deletedAt` then purge | Undo + multi-device |
| Locked notes: store ciphertext or do not sync body | No lock theater |

## What Convex should not do

- Be the thing the FAB waits on
- Render UI
- Store original 12MP JPEGs uncompressed without a plan
- Run AI over every note
- Replace Room
- Hold Clerk secrets on the client

## Offline

Convex Android is a live socket, not a full offline replica (as of these docs). **We build offline.** Queue mutations. Replay when `webSocketStateFlow` is CONNECTED and Clerk session is valid.

If the official client later grows a local cache we can delete our queue — not before.

## Environments

```
debug  → CONVEX_DEV_URL
release → CONVEX_PROD_URL
```

Via `resValue` / `local.properties` ignored by git. Matches official flavor guidance.

## Testing

- Unit: fake `ConvexClient` (open)
- Instrumentation: dedicated backend or local Convex
- Never hit prod from tests

## Accept / reject

**Accept:** Convex as reactive sync + file store + Clerk JWT; Android official client; schema as above; LWW + conflict copy; mutation debounce; flavor URLs.

**Reject:** Convex as local DB; Convex Auth; Auth0; actions for every keystroke; whole-vault subscribe; building a web app first.

## Risks (write them down so we don't forget)

1. **Maturity of mobile client** vs web — smaller community. Workout sample is the north star.
2. **No first-class offline** — we own it.
3. **Conflict** on a block document is on us.
4. **AAR + Rust** — keep versions pinned; upgrade deliberately.
5. **Serialization gotchas** — DTO layer is mandatory.

These risks are acceptable for a premium personal notes app. They would be less acceptable for a collaborative docs company.
