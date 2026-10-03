# A1 — Status art: style exploration (5 styles × 2 statuses = 10 images ONLY)

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` and `docs/CHARACTER-ART.md` for context. Do NOT edit `docs/HANDOFF.md`, `AGENTS.md` or any code.
Use your built-in image generation (imagegen skill, `~/.codex/skills/.system/imagegen/SKILL.md`). One image per call.

## Goal
The owner (Android friends-status app, statuses currently shown as emoji in a soft colored circle, 44–210 dp) wants to SEE several clearly different art styles before choosing one. Make 5 styles that do NOT look alike, each applied to the same 2 statuses only (owner wants to save usage — generate EXACTLY 10 images, no extras, no retries unless an image is unusable):
`toilet` (on the toilet → a cute poop character itself, no toilet) and `coffee` (coffee time).

Styles (make them genuinely different; keep each style internally consistent across its 2 images):
- **S1 clay** — hand-sculpted soft 3D clay / matte silicone creatures, a DIFFERENT character per status (species/object/silhouette/colors), warm studio light.
- **S2 glossy3d** — polished 3D icon style like premium app emoji: a single bold object or face per status, glossy materials, strong readable silhouette, no tiny details.
- **S3 sticker** — flat vector sticker, thick dark outline, 2–3 flat colors + one highlight, playful cartoon faces (Telegram-sticker feel), white sticker border.
- **S4 doodle** — hand-drawn ink + crayon/risograph texture, slightly wobbly lines, limited warm palette, charming and human (not clip-art).
- **S5 pixel** — chunky 32×32-look pixel art upscaled crisp (nearest-neighbor), limited palette, game-sprite charm.

Every image:
- Transparent background (real alpha). If your tool cannot do transparency, generate on a perfectly flat pure magenta #FF00FF background with no shadow on it and no magenta/pink in the subject, and name the file with suffix `_magenta`.
- Square, single subject centered, generous margins (~10%), no text, letters, logos, watermark, scene, floor or frame (S3's white sticker border is allowed).
- Must read at first glance at 48 px.
- Save as PNG: `source/art-explore/<style>/<key>.png` (e.g. `source/art-explore/S3-sticker/coffee.png`; use `<key>_magenta.png` when on magenta).

## Hard rules
Write ONLY under `source/art-explore/` and `logs/codex/A1/`. Do not touch anything else. No git commands. No deleting files outside `source/art-explore/`.

## Report `logs/codex/A1/REPORT.md`
Per style: the exact prompt you used (one template + per-status line), files created, transparency (alpha vs magenta), and anything that failed. Be honest; list missing images.
