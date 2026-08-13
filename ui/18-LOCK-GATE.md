# 18 — Lock gate

A locked note is a closed envelope. The OS biometric sheet is the wax. We do **not** draw a fingerprint.

Official: [BiometricPrompt](https://developer.android.com/identity/sign-in/biometric-auth) with `BIOMETRIC_STRONG or DEVICE_CREDENTIAL`. CryptoObject = the note’s AES-GCM cipher (auth-per-use). `setConfirmationRequired(false)`. **Cannot** set a negative button when device credential is allowed — that is the official restriction. Cancel is the system back.

---

## Card / list (locked, at rest)

Preview text is always `Locked note`. Never a snippet. Never a thumb of the body.  
If the note has no title, title = `Locked note` as well.  
Badge: `lock` 16 top-end.  
Tint/paper still show (the envelope color). Ink/image thumbs **hidden**.

Search: FTS row is gone (BUILD). A body word of a locked note returns **zero** hits. Title still matches if they named it.

---

## Gate screen (tap a locked note)

Full-screen `background` + grain. Same sharedBounds **container** as a normal open (the card expands), then the **body is the gate**, not plaintext.

```
┌─────────────────────────────────────┐
│ ←                                    │  64
│                                      │
│              [ lock 48 ]             │  onSurfaceVariant
│              16                      │
│         {title or Locked note}       │  titleLarge, center, 2 lines
│              8                       │
│            Locked note               │  bodyMedium, onSurfaceVariant
│                                      │
│          [    Unlock    ]            │  56 stadium, primary
│                                      │
└─────────────────────────────────────┘
```

- Title of the note is allowed (they named the envelope). Body never.
- `FLAG_SECURE` **on** this activity window as soon as the gate appears, and it stays on until they leave the decrypted editor.
- Recents snapshot: gate paper, not the body (SECURE).
- Auto-prompt: 200 ms after the gate is composed, show `BiometricPrompt` once. Do not loop.
- `Unlock` re-shows the prompt.

**Prompt copy (system sheet):**

- Title: `Unlock note`
- Subtitle: note title, or omitted if untitled
- Authenticators: `BIOMETRIC_STRONG or DEVICE_CREDENTIAL`

Success: decrypt in the same call (cipher from CryptoObject), restore blocks, rebuild FTS, replace gate with the real editor (fade 160). Haptic `CONFIRM`.

Fail (wrong face / finger): stay on gate. No toast. System sheet already said so.

Error cancel: stay. Inline under the button, `bodySmall` `error`: `Couldn’t unlock.`

`BIOMETRIC_ERROR_NONE_ENROLLED` / no hardware: hide Unlock. Show text `Set a screen lock to open locked notes.` + text button `Open settings` → `ACTION_BIOMETRIC_ENROLL`.

`locked_fail` (keystore gone): `This locked note can’t be opened on this device.` + `Delete` error text. Delete uses the normal delete + undo.

---

## While decrypted

Normal editor. Overflow `Unlock` becomes `Lock` (re-encrypt, purge FTS, pop to home).  
Screenshot / recents: still `FLAG_SECURE`.  
App backgrounded: **re-lock after 60 s** in the background (or immediately if `lock_new` style… **Decision:** re-lock when the process is stopped or after **60 s** `ON_STOP`. Returning within 60 s stays open. Lock screen of the **device** going on does not instantly re-lock (they may be reading). Process death: locked.

---

## Lock from overflow

If already enrolled: set lock, encrypt, FTS delete, haptic `CONFIRM`, stay in editor on the **gate** (they just locked it — show the envelope, don’t pop).  
If not enrolled: same enroll dialog as Settings.

---

## Lock-screen capture

A note created on the keyguard is **not** locked unless `Lock new notes` is On **and** they can auth. If they cannot auth on the keyguard, ignore `Lock new notes` for that capture and leave it unlocked (honest: we cannot encrypt without the key).

Opening a locked note from lock-screen history (only if consent On): `requestDismissKeyguard` → then this gate.

---

## Strings

```
locked_note=Locked note
unlock=Unlock
unlock_title=Unlock note
unlock_fail=Couldn’t unlock.
unlock_need_lock=Set a screen lock to open locked notes.
locked_fail=This locked note can’t be opened on this device.
```
