# 19 — Images (sheet + lightbox)

Insert rules (downsample, Room, Convex) stay in BUILD §14.  
This file is what they see.

---

## Source sheet

Opened from: insert sheet `Image`, slash `image`, split-plus `Image`, Magic… no.

Sheet, wrap height, radius 28, handle.

Title `Add image` `titleSmall`.

Rows 56, icon 24, `bodyLarge`:

1. `photo_camera` **Take photo**
2. `photo_library` **Choose from gallery**

No Files app row. No “Last photo.” No stickers.

**Take photo:** `ActivityResultContracts.TakePicture` to `cache/capture_{uuid}.jpg`. No `CAMERA` permission if the system capture intent is used. If the device has no camera activity: hide row 1.

**Gallery:** `PickVisualMedia(ImageOnly)`, `maxItems = 10`. Android Photo Picker. No `READ_MEDIA_IMAGES`.

Cancel: swipe / back. Nothing inserted.

---

## After pick / shot

Copy → `files/media/{uuid}.jpg`, long edge ≤ 2560, JPEG 88, apply orientation, strip extra EXIF.  
Insert image block(s) at caret / after current.  
Thumb 400 px JPEG in `thumbPath` for home.

Failed decode / no file: haptic `REJECT`. Replace / don’t insert. Editor inline on a ghost block: `Couldn’t add image` `bodySmall` `error` + tap retries the sheet.

Share-in failures: [ui/17-CAPTURE-SHARE.md](17-CAPTURE-SHARE.md).

---

## In the note

Radius 16. Max height **360**. `ContentScale.Fit`. Width fill of the 20-pad column.  
Tap: lightbox.  
Long-press: menu `Replace` · `Caption` · `Delete`.

Caption: `bodySmall` `onSurfaceVariant` under the image, tap to edit, hint `Caption`. Empty caption takes no height.

Loading bind: `surfaceContainer` rectangle same radius, **no shimmer**, height 200 until measured.

---

## Lightbox

Not a route. Overlay on the editor.

- Scrim `#000000` **92%**.
- Image `sharedBounds` from the block (`NoteImageKey(id)`). Pinch-zoom, pan, double-tap 1× ↔ 2.5×.
- Bar 64 transparent: `close` 48 start, `onPrimary`-equivalent white 100%. No title.
- Predictive back: scale toward the block, scrim → 0.
- Reduce-motion: fade only.
- `FLAG_SECURE` if the **note** is unlocked-locked (decrypted locked note). Otherwise screenshots OK.
- No share button here (overflow on the editor already has Share).
- Multiple images: horizontal pager, 8 dp dots `onSurface` @ 40% / 100%, 16 from bottom. Does not wrap.

---

## Capture-behind (ROLE_NOTES float only)

[ui/17-CAPTURE-SHARE.md](17-CAPTURE-SHARE.md). Lands as a normal image block.

---

## Strings

```
add_image=Add image
take_photo=Take photo
choose_gallery=Choose from gallery
caption=Caption
image_fail=Couldn’t add image
replace=Replace
cd_close_image=Close image
```
