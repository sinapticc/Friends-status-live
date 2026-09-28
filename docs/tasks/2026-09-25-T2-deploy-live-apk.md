# T2 — Deploy the free Cloudflare server, build the live APK, install on the phone

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`
Read `AGENTS.md` first. Do NOT edit `docs/HANDOFF.md`. The owner authorized deploying the server to Cloudflare (free tier).
Wrangler on this Mac is already logged in (Workers + D1 write).

## Hard rules
- Write ONLY: `server/wrangler.toml` (database_id line only), `/Users/sina/Documents/Codex/2026-09-25/f/outputs/apk/`, `logs/agy/T2/` (scratch only in `logs/agy/T2/_scratch/`).
- Do not edit any source code. If something fails because of code, STOP and write it in the report.
- No `git commit`/`push`/`reset`/`checkout`/`stash`/`clean`. No wildcard `rm`. Do not delete any D1 database or Worker.
- Never enter passwords or tokens. If wrangler asks to log in, STOP and report.
- Phone (adb device `ytf6mv7lnzu8ivl7`, Redmi Note 11 Pro) belongs to the owner: only `adb install -r`, `monkey` launch, `screencap`, `dumpsys`, `logcat`. No taps/swipes/uninstall.

## Steps
1. `cd server && npx wrangler whoami` → note account name.
2. `npx wrangler d1 list --json`. If no database named `fsl` exists: `npx wrangler d1 create fsl`. Put its uuid in `server/wrangler.toml` line `database_id = "<uuid>"`.
3. `npx wrangler d1 migrations apply fsl --remote` → all migrations applied. Then `npx wrangler d1 execute fsl --remote --command "SELECT name, sql FROM sqlite_master WHERE type='table'"` → paste; confirm no location columns, no `muted` column, `rate_limits` table exists.
4. `npx wrangler deploy` → copy the `https://fsl-api.<sub>.workers.dev` URL. Do NOT pass `FSL_TEST_CLOCK` or RL overrides to production.
5. Smoke test the live URL with curl (paste real responses). Check `server/src/index.ts` for exact bodies.
   a. `GET /` → 200. b. `POST /v1/register` (nick «تست») → token. c. `GET /v1/feed` with `Authorization: Bearer <token>` → 200 with `me`.
   d. `POST /v1/status` `{"key":"coffee","visibility":"all"}` → 200; feed shows `coffee`. e. same with extra `"lat":1` → 400 `location_disabled`.
   f. `DELETE /v1/me` → 200 (cleanup).
6. `gh variable set FSL_API_URL --repo sinapticc/Friends-status-live --body "<url>"`, then `gh variable list --repo sinapticc/Friends-status-live`.
7. From project root `./gradlew assembleDebug -PFSL_API_URL=<url>` → BUILD SUCCESSFUL. Copy `app/build/outputs/apk/debug/app-debug.apk` to `/Users/sina/Documents/Codex/2026-09-25/f/outputs/apk/friends-status-live-live-debug.apk`. Move old `friends-status-live-no-location-debug.apk` to `/Users/sina/Documents/Codex/2026-09-25/f/outputs/apk/archive/`. `shasum -a 256` the new APK. `/Users/sina/Library/Android/sdk/build-tools/35.0.0/aapt dump permissions <apk>` → no location permission. `unzip -p <apk> 'classes*.dex' | strings | grep workers.dev` → URL present.
8. Phone: `adb -s ytf6mv7lnzu8ivl7 install -r <live apk>`; `adb -s ytf6mv7lnzu8ivl7 shell monkey -p com.sinapticc.friendsstatus -c android.intent.category.LAUNCHER 1`; wait 5 s; `adb -s ytf6mv7lnzu8ivl7 exec-out screencap -p > logs/agy/T2/phone-launch.png` (size > 20 KB); `adb -s ytf6mv7lnzu8ivl7 logcat -d -t 300 | grep -iE "friendsstatus|AndroidRuntime|FATAL"` → no FATAL.
9. `adb -s ytf6mv7lnzu8ivl7 shell dumpsys jobscheduler | grep -i friendsstatus | head` and `adb -s ytf6mv7lnzu8ivl7 shell dumpsys appwidget | grep -iA3 friendsstatus` → paste.

## Report
`logs/agy/T2/REPORT.md`: live URL, D1 uuid, each step's real output tail, APK path + SHA-256, screenshot path, any **FAIL** honestly. Do not claim anything you didn't run.
