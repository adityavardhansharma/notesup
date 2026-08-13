# 00 — Why notes UIs look like a joke

This file exists because a perfect UX spec can still ship a **cheap-looking** app. The joke is a look. We refuse it.

## The joke (do not ship)

| Joke | Where it comes from | What it signals |
|---|---|---|
| `#FFFFFF` cards on `#F5F5F5` | Android Studio Empty Activity | Tutorial |
| Default purple `primary` | Material baseline seed | Unowned |
| `Card { Column { Text } }` with 16 dp everywhere | Every GitHub notes clone | Homework |
| Keep’s 12 candy fills | “It looks like notes” | 2013 |
| Drop shadow + 8 dp radius | Material 2 muscle memory | Dated |
| Five-tab bar with a fake Profile | Instagram cargo-cult | Not a notebook |
| Neon FAB, extended label `CREATE NOTE` | Onboarding fear | Amateur |
| Lottie pencil mascot on empty | SaaS empty-state kits | Childish |
| Skeleton shimmer on local data | Social apps | Lie |
| Inter / Poppins as “premium” | Every AI landing page | Generic |
| Two-tone / colored icons | Play Store 2016 | Toy |
| OLED pure black + `#FFFFFF` 100% | “AMOLED” Reddit | Harsh, not paper |
| Badge counts, red dots, “NEW” | Growth |
| Gradient app bars | 2018 finance clones | Loud |
| Glass everywhere | iOS 26 cosplay | Port |
| Every Expressive widget on one screen | Catalog app | Trying too hard |
| Clerk `AuthView` / purple “Secured by Clerk” | Drop-in SaaS | Not our paper |
| 3-page carousel then login | Consumer SaaS | Lecture |

If a screenshot of home could be swapped with a student Room+Compose sample and nobody would notice, we failed the UI.

## What premium notes *look* like (not features)

**Things 3:** soft grouped paper, one accent, checkboxes as objects, almost no chrome color.  
**Bear:** type is the product, one red, no noise.  
**Craft:** the document is an object you would photograph.  
**reMarkable:** almost no UI.  
**Apple Notes (modern):** mixed media, quiet bars.  
**Copilot Money:** dark that is *warm*, type that is *architectural*, not a bank skin.

They do **not** look like Gmail. They do **not** look like a component gallery.

## Notesup’s look, in four words

**Warm paper. One jewel.**

The jewel is the **split plus** and the **connected pills**.  
Everything else is paper, type, and honest icons.

## Hard visual laws

1. **No drop shadows on note cards.** Tonal fill only.  
2. **No `#FFFFFF` and no `#000000` as the app background.** Paper cream / warm ink.  
3. **One accent** (sealing-wax). Never a second brand color.  
4. **Icons one family**, one weight, one optical size. Fill only when selected.  
5. **Type hierarchy is the luxury.** If body is 14 sp, it is a joke.  
6. **Expressive shapes on objects and jewels only** — cards, pills, split plus, sheets. Not on every icon button.  
7. **Empty is type, not art.**  
8. **If you need a label on the plus, the plus failed.**  
9. **Healthy sync is invisible.**  
10. **A screenshot must still look expensive in grayscale.**

## The grayscale test

Desaturate the home screen.  
If it still has: clear wordmark, quiet cards, one dark plus, readable type — pass.  
If it becomes a fog of equal-gray rectangles — fail.

## The thumb test

Hold a mid Pixel in one hand.  
Can you create a note without reading?  
Can you tell a pinned card from the others without a gold star?  
If no, the UI is decoration.

## What we *do* steal visually

| Steal | From | How it looks here |
|---|---|---|
| Paper as object | Craft, Things | 24 dp cards, no outline |
| One accent | Bear red | Wax `#8B2942` |
| Quiet home | Things, Linear | Few destinations |
| Big type | Cash App / Hello Weather empty | Wordmark + `Write anything.` |
| Native Expressive | Pixel Phone, Clock | Pills + split, not iOS glass |
| Ink as real ink | Samsung / Apple / Jetpack Ink | Not a polyline |

## What we never steal visually

Keep candy. Notion gray wiki. OneNote purple ribbon. Samsung dated chrome. Discord blurple. Superhuman custom non-OS chrome.
