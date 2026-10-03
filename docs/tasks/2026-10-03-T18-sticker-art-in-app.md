# T18 — Show the new sticker art instead of emoji (app + widget), emoji as fallback

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md`. Do NOT edit `docs/HANDOFF.md`.
New art (owner-approved sticker style) is ALREADY packed: `app/src/main/res/drawable-nodpi/ch_<key>.webp` for all 44 status keys (512×512, transparent, white sticker border; ~10% margin). Masters: `source/character-art-sticker/` (read-only).
Today statuses render as emoji in a colored circle: `ui/components/Character.kt` (`StatusChar`), widget `android/FslWidget.kt` (`CharEmoji`), drawable lookup `platform/Assets.kt` (Android `art` map / `artRes`) and its desktop twin in `tools/desktop-preview/src/main/kotlin/com/sinapticc/friendsstatus/platform/DesktopAssets.kt`.

## Do
1. `StatusChar`: keep the same signature and the colored category circle; draw the `ch_<key>` image centered at ~86% of the circle size (no hue-rotate color matrix, no old 112% oversize, no accessory overlays). If the drawable is missing → current emoji fallback. Keep idle/bounce animation.
2. Widget: replace the emoji inside the circle with a Glance `Image(ImageProvider(resId))` at ~86% of the circle, `ContentScale.Fit`; fallback to the emoji Text if the resource id is 0. Keep everything else (bubbles, names, sizes, tap intents, people selection).
3. Make sure `platform/Assets.kt` maps all 44 `ch_*` names (regenerate the map if needed) and the desktop preview loads the same files from `app/src/main/res/drawable-nodpi/`.
4. Status picker grid, home list, friend screen, history rows, group member rows, scope sheet — all use `StatusChar`, so they switch automatically; check none of them still draws the emoji directly (grep `emojiFor(` outside fallbacks).
5. `MeChar` (user's own character with `me_*` layers) unchanged.

## Hard rules
Write ONLY `app/src/main/java/`, `tools/desktop-preview/src/`, `app/build.gradle.kts` (version lines only), `/Users/sina/Documents/Codex/2026-09-25/f/outputs/apk/`, `logs/agy/T18/` (scratch only in `_scratch/`). Never modify `app/src/main/res/drawable-nodpi/` or `source/`. No git except status/diff. No wildcard rm. Never uninstall the app.

## Verify
1. versionCode +1, versionName 1.3.0. `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleRelease -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL.
2. Desktop preview (openjdk@21): `tools/desktop-preview/gradlew -p tools/desktop-preview run -Dlang=en` → exit 0; open `en-06-home.png` and `en-08-sheet.png` and check the stickers show inside the circles (describe honestly).
3. Phone: `adb -s ytf6mv7lnzu8ivl7 install -r app/build/outputs/apk/release/app-release.apk`. If unlocked (`dumpsys window | grep isKeyguardShowing=false`), monkey-launch the app, wait 8 s, screencap `logs/agy/T18/_scratch/app.png`; `input keyevent KEYCODE_HOME`, wait 6 s, screencap `home.png`; LOOK at both. No other taps.
4. Copy APK to `/Users/sina/Documents/Codex/2026-09-25/f/outputs/apk/FriendsStatusLive-1.3.0.apk`, move older `FriendsStatusLive-*.apk` there into `archive/`.
Report `logs/agy/T18/REPORT.md` (write early, update): changes per file, build tails, what the screenshots show. Honest.
