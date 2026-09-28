# T5 — Review Codex character art, package good ones as WebP, generate the missing ones

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` and `docs/CHARACTER-ART.md` (art rules + cue table for all 44 statuses) first. Do NOT edit `docs/HANDOFF.md` or any code.

## Hard rules
- Write ONLY: `source/character-art/` (new PNG masters only; never overwrite or delete an existing PNG — rejected ones get moved to `archive/attempt-02/character-art/`), `app/src/main/res/drawable-nodpi/ch_*.webp` (replace in place, same names), `logs/agy/T5/` (scratch only in `logs/agy/T5/_scratch/`).
- Never touch `me_*`, `acc_*` files, `design/`, code files. No git commands except `git status`. No wildcard rm.
- Before changing any `ch_*.webp`, copy the whole original set once to `archive/attempt-02/drawable-nodpi-before-T5/`.

## Acceptance for each image (check every one yourself by LOOKING at it)
1. Status readable at first glance at 64 px without text (make a 64 px preview and look at it).
2. Distinct character per status (see CHARACTER-ART.md); `toilet` = cute poop character. No gym headband/wristband outside gym. No text, logo, watermark, scene, floor, card.
3. Real transparency: corners alpha = 0, no white/magenta fringe or halo when composited on dark `#1B1026` AND light `#F4EFE8` (make both previews and look).
4. Same finish as the good Codex images: handcrafted soft 3D clay/matte, warm light.
5. WebP ≤ 60 KB, 512×512.

## Steps
1. List the 44 status keys from `app/src/main/java/com/sinapticc/friendsstatus/model/Catalog.kt`. Existing masters: `source/character-art/ch_*.png` (from Codex).
2. For each existing Codex master: judge against the acceptance list. Good → `python3 tools/art/key_and_pack.py <png> logs/agy/T5/_scratch/<key>-1024.png app/src/main/res/drawable-nodpi/ch_<key>.webp --keyed` (keeps master untouched; the 1024 copy stays in scratch). Bad → record why; regenerate it in step 3.
3. For each missing or rejected key: use YOUR built-in image generation tool, one image per call, with the prompt scaffold + cue from `docs/CHARACTER-ART.md`, BUT replace "transparent background" with: **"isolated on a perfectly flat solid pure magenta #FF00FF background, no shadow on the background, no gradient, the character contains no magenta or pink-purple colors"**. You may pass 1–2 GOOD Codex images as style reference (finish/lighting only — never copy their character). Save the raw result to `logs/agy/T5/_scratch/raw_<key>.png`, then run `python3 tools/art/key_and_pack.py logs/agy/T5/_scratch/raw_<key>.png source/character-art/ch_<key>.png app/src/main/res/drawable-nodpi/ch_<key>.webp`. Check the acceptance list; retry up to 3 times per key. If you have NO image generation tool, STOP after step 2 and say so in the report.
4. Build a contact sheet `logs/agy/T5/contact-dark.png` and `contact-light.png`: all 44 `ch_*.webp` at 128 px in a grid with the key under each, on the two backgrounds. Look at them; fix outliers.
5. Build: `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleDebug -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL.

## Report `logs/agy/T5/REPORT.md`
Table of all 44 keys: source (Codex kept / Codex rejected+regenerated / new), attempts, WebP size, one-line visual description of what you SEE, pass/fail. List any key still failing. Paths of contact sheets. Honest — never claim you looked at an image you didn't open.
