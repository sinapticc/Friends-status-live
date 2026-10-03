# A2 — Full status set in the chosen STICKER style (41 images)

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md`, `docs/CHARACTER-ART.md` (cue table for every status) and `logs/codex/A1/REPORT.md` (section S3-sticker = the exact accepted prompt template). Do NOT edit `docs/HANDOFF.md`, `AGENTS.md` or code.
Owner chose style **S3 sticker** (see `source/art-explore/S3-sticker/toilet.png`, `coffee.png`, `heartbroken.png` — these 3 are DONE and already copied to `source/character-art-sticker/`).

## Generate exactly these 41 keys, one image per imagegen call, sequentially (not in parallel), no extra variants
sleeping, eating, gym, studying, work, driving, gaming, partying, showering, sick, walking, dnd, bored, free, custom, period, spicy, spicy2, spicy3, inlove, hungover, angry, crying, date, shopping, movie, traveling, cooking, meditating, hookah, cleaning, traffic, lowbattery, overthinking, maincharacter, busy, period2, period3, football, barber, beard

## Prompt
Use the S3 template from `logs/codex/A1/REPORT.md` word for word (flat hand-drawn vector cartoon, thick dark chocolate-brown outline, 2–3 flat colors + one highlight, Telegram-sticker feel, thick white sticker-cut border, ≥10% margin, genuine transparent alpha, no shadow/scene/floor/text/letters/logo/watermark/frame/gradients/3D/tiny details), and only change the `Subject:` sentence per status using the cue in `docs/CHARACTER-ART.md`. Prefer the status symbol itself as the character (cup, controller, heart, battery, suitcase…) with a cute face; every status must have a different silhouette and color so they don't look alike; no gym headband/wristbands outside `gym`. Sensitive ones stay discreet (period: hot-water bottle character; spicy*: flame/heat characters, nothing sexual). `custom` = a blank speech-bubble character with a pencil. `dnd` = sleep-mask character with a stop hand, no text. Must read at first glance at 48 px.
Pass the 3 done images as STYLE reference only (outline weight, border, palette treatment) — never copy their subject.

## Files
Save each as `source/character-art-sticker/<key>.png` (RGBA, transparent). If an image comes back without real alpha, retry once; if still opaque, save as `<key>_noalpha.png` and list it.
Write ONLY under `source/character-art-sticker/` and `logs/codex/A2/`. No git. Do not touch other files.

## Report `logs/codex/A2/REPORT.md`
Table: key, file, alpha yes/no, one-line description of what the image shows, retries. Also make `logs/codex/A2/contact-sheet.png` (all 44 at 128 px on dark #1B1026 with key labels) and look at it; list any image that is unclear at small size.
