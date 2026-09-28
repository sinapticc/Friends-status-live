# T3 — Test account on the live server + widget test on the phone

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first. Do NOT edit `docs/HANDOFF.md` or any source code.
Live server: `https://fsl-api.mohammadisina2001.workers.dev`. Phone: adb device `ytf6mv7lnzu8ivl7` (owner's Redmi). The owner has installed the live APK, created a profile and placed 3 widgets (2×2, 4×2, 4×4) on the home screen.

## Hard rules
- Write ONLY in `logs/agy/T3/` (scratch in `logs/agy/T3/_scratch/`). No git commands except `git status`. No wildcard rm. No `wrangler deploy`, no `wrangler secret`, no D1 writes (D1 only read-only SELECT).
- Phone: allowed only `adb shell monkey -p com.sinapticc.friendsstatus -c android.intent.category.LAUNCHER 1`, `adb shell input keyevent KEYCODE_HOME`, `adb exec-out screencap -p`, `dumpsys`, `logcat -d`, `cmd jobscheduler run -f`. No taps, swipes, text input, uninstall, settings changes.
- Look at every screenshot you take (open the image) and describe what the widgets actually show. Check each PNG is > 20 KB.

## Steps
1. Find the owner's account: `cd server && npx wrangler d1 execute fsl --remote --command "SELECT id, nick, pair_code, created_at FROM users ORDER BY created_at"` (check column names in `server/migrations/0001_init.sql` first). Pick the most recent non-test user. If unclear, STOP and report.
2. Register the test account: `POST /v1/register` with nick `رفیق تستی` (check body format in `server/src/index.ts`). Save the full JSON response to `logs/agy/T3/_scratch/test-account.json`.
3. Pair: `POST /v1/join` `{"code":"<owner pair_code>"}` with the test token → must succeed. `GET /v1/feed` with the test token → the owner appears as a friend.
4. Post `POST /v1/status` `{"key":"coffee","text":"وقت قهوه‌ست","visibility":"all"}` with the test token.
5. Refresh the phone: launch the app with monkey, wait 8 s, `screencap` → `logs/agy/T3/app-1.png` (the friend «رفیق تستی» with coffee should be visible). Then `input keyevent KEYCODE_HOME`, wait 5 s, `screencap` → `logs/agy/T3/widget-1.png`. `dumpsys appwidget | grep -iA5 friendsstatus` → number of widget instances.
6. Expiry test: post `{"key":"gym","text":"باشگاهم","visibility":"all","expiresInMin":2}`. Launch app, wait 8 s, HOME, wait 5 s, screencap → `widget-2.png` (gym expected). Wait 150 s. Force the sync job: find the job id with `dumpsys jobscheduler | grep -B2 -A10 friendsstatus`, run `cmd jobscheduler run -f com.sinapticc.friendsstatus <id>`, wait 15 s, screencap → `widget-3.png` (coffee expected again, gym gone). If the widget still shows gym, also launch the app + HOME and screencap `widget-3b.png`.
7. Background test: with the app in background (HOME pressed), post `{"key":"gaming","text":"دارم گیم می‌زنم","visibility":"all"}`, force the job as in step 6 WITHOUT opening the app, wait 15 s, screencap → `widget-4.png`.
8. `logcat -d -t 500 | grep -iE "friendsstatus|FATAL|AndroidRuntime|WM-"` → paste relevant lines.
9. Leave the test account and its pairing in place (the owner wants it for testing).

## Report `logs/agy/T3/REPORT.md`
Test account id + where the token is saved; for each screenshot: file, size, and exactly what each of the 3 widgets shows (friend name, status, character image visible or blank, layout problems, clipped text, RTL issues); which steps passed/failed; any widget bug you see, described precisely. Be honest — never claim a screenshot shows something you did not see.
