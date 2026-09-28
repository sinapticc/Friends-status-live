# T11 — Refresh AGENTS.md and README.md to match the code (docs only)

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`.
Write ONLY `AGENTS.md`, `README.md`, `logs/oc/T11/REPORT.md`. Do NOT touch code, `docs/HANDOFF.md`, or anything else. No git except status/diff. Keep AGENTS.md's structure and all existing product decisions; edit facts that are now wrong and add the new ones below. English prose.

Read first: `docs/HANDOFF.md` (the full current state), then check facts in code before writing them.

Facts to reflect (verify each in code):
1. App name «Friends Status Live» (strings.xml, widget, share texts). Versions: see `app/build.gradle.kts` (versionCode/versionName); every release needs a higher versionCode.
2. Statuses are shown as system emoji in colored category circles (`Catalog.emoji`, `emojiFor`, `categoryHueIndex`, `StatusChar` in `ui/components/Character.kt`). The WebP `ch_*` art is still in res but unused for statuses; `me_*`/`acc_*` still used by `MeChar`. Codex art masters in `source/character-art/` are for later.
3. Status picker: tabs + 4-column grid + «بفرست»; extra options behind the tune button (`StatusPickerSheet.kt`).
4. Home: scope switcher sheet with «گروه‌ها» / «دونفره‌ها» sections; groups = rounded squares, people = circles.
5. Widget: Instagram-Notes style (`FslWidget.kt`: NoteCell/NotesRow), my own status first, per-widget group via `WidgetConfigActivity` (APPWIDGET_CONFIGURE, reconfigurable), 1-on-1 friends included in «همه‌ی رفقا», one-off sync at the next expiry (`fsl-expiry`).
6. No Firebase/FCM (owner decision 2026-09-26): WorkManager 15 min + in-app refresh. The FCM code is still present but unused without config.
7. Server: live at `https://fsl-api.mohammadisina2001.workers.dev`, D1 `fsl` uuid in `server/wrangler.toml`, migrations 0001–0005, rate limiting, expiry fallback, test clock (`FSL_TEST_CLOCK`), recovery endpoints `POST /v1/me/recovery`, `POST /v1/recover`. Deploy: owner runs `scripts/deploy-server.sh` (Claude's auto mode blocks deploys). Tests: count them in `server/test/api.test.mjs`.
8. Account recovery code (profile «کد بازیابی حساب», welcome «قبلاً حساب داشتم»).
9. Release signing: keystore outside the repo at `/Users/sina/.android/fsl-release/` read via `local.properties` (`fsl.release.*`); falls back to debug key if absent. Build: `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleRelease -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev`. Shareable APKs go to `/Users/sina/Documents/Codex/2026-09-25/f/outputs/apk/`.
10. Agents: default sub-agent OpenCode `--agent mimo`; fallback `ds-minimal`; image review by Claude Haiku subagents; cheap agents stalled on Glance widget UI (Claude did it).
11. Update the "Status and next steps" section: remove done items (rate limiting, deploy, expiry placeholder, …) and list what is really left: R2 profile photos (needs card), light widget, AppStore unit tests, MeChar/editor art vs emoji consistency, Codex art later, git commit/push of all local work (not done yet).

Report `logs/oc/T11/REPORT.md`: list of sections changed and any fact you could not verify.
