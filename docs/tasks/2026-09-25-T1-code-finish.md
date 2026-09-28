# T1 — Finish code: no demo data, remove "muted", expiry revert, rate limiting, widget review

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`
Read first: `AGENTS.md` and `docs/HANDOFF.md` in that folder. Do NOT edit `docs/HANDOFF.md` (Claude does that).
The working tree has many uncommitted changes (location removal). Keep them. Do NOT run `git checkout`, `git reset`, `git stash`, `git clean`, commit or push.

## Hard rules
- Write ONLY inside: `app/src/main/java/`, `app/src/main/AndroidManifest.xml`, `app/build.gradle.kts`, `server/src/`, `server/migrations/`, `server/test/`, `tools/desktop-preview/src/`, `.github/workflows/`, `README.md`, `AGENTS.md`, and `logs/agy/T1/` (your report + any scratch files go ONLY in `logs/agy/T1/_scratch/`).
- NEVER touch: `source/`, `design/`, `archive/`, `output/`, `app/src/main/res/drawable-nodpi/` (character art is Codex's job), `docs/HANDOFF.md`, `docs/CHARACTER-ART.md`.
- Do not install npm/Gradle packages into new places (no `npm install` in the project root). No wildcard `rm`. Do not deploy anything, do not run `wrangler deploy` or any `--remote` command.
- No location feature may come back (see AGENTS.md). Shared code must compile with Kotlin 1.9 / Compose 1.5 (desktop preview), see AGENTS.md "Conventions".
- UI text stays Persian.

## Step 1 — Remove demo/sample data from the production app
1. `app/src/main/java/com/sinapticc/friendsstatus/data/AppStore.kt` was already partly edited (demo branches removed, constructor has optional `initialState`). Read it fully and make it consistent: with a blank `apiUrl` the app must NOT show fake friends; it shows the normal empty states and a Persian toast/error like «آدرس سرور تنظیم نشده» when a server action is needed.
2. Move `app/src/main/java/com/sinapticc/friendsstatus/data/FakeData.kt` to `tools/desktop-preview/src/main/kotlin/FakeData.kt` (same content; adjust package/imports so it compiles there). Delete it from `app/src/main`. Nothing under `app/src/main` may reference `FakeData`.
3. Update `tools/desktop-preview/src/main/kotlin/Main.kt` so the screenshot mode builds the store with `initialState` filled from `FakeData` (screenshots still show friends), while the e2e mode (`-Dapi=...`) uses the real server with no fake data.
4. Update the comment in `data/Platform.kt` ("Blank means demo mode…") and README/AGENTS rows that mention demo mode.

## Step 2 — Remove the group "muted" (بی‌صدا) feature completely
1. App: remove `muted` from `model/Models.kt`, `data/Api.kt` DTO, `AppStore.toggleMute()` and its mapping line (~607), and the ActionTile in `ui/screens/GroupScreen.kt` (~141). Rebalance that row of tiles so the layout still looks intentional (no empty gap).
2. Server `server/src/index.ts`: remove `muted` from `MemberRow`, the feed SELECT (~225), feed JSON (~291) and `PATCH /v1/groups/:id/me` handling (~381). If that endpoint has no other field left, remove the route and its client call; otherwise keep it.
3. New migration `server/migrations/0003_drop_muted.sql`: `ALTER TABLE members DROP COLUMN muted;` Check it applies on a fresh local DB after 0001+0002.
4. Remove the `PATCH /v1/groups/:id/me` row / muted mentions from AGENTS.md API table.

## Step 3 — Expired status reverts to the previous status (owner decision)
The server already falls back to the previous non-expired status (query near line 305). Make it guaranteed:
1. Add an API test in `server/test/api.test.mjs`: user posts status A (no expiry), then status B with `expiresInMin: 1`; feed shows B. Then make B expired **without waiting 60 s** — e.g. add a test-only way: if env var `FSL_TEST_CLOCK=1` (set only via `wrangler dev --var FSL_TEST_CLOCK:1` in tests/CI) the server accepts header `X-Test-Now: <ms>` to override `now()`. Feed with a later time must show A for friends and for `me`. Make sure the header is ignored when the var is not set.
2. App side: a friend's status whose `expiresAt` is in the past must not be shown as current until the next refresh replaces it — in `AppStore.apply()` (or wherever feed maps), if a status is expired locally, schedule/trigger `refresh()` (at most once per minute). Widget (`android/FslWidget.kt` + snapshot in `Background.kt`): when rendering, if the snapshot status is expired, show it dimmed or trigger `Sync.now`, never show an expired status as live indefinitely.
3. Update CI `.github/workflows/server.yml` test step to start `wrangler dev` with `--var FSL_TEST_CLOCK:1`.

## Step 4 — Rate limiting (before public deploy)
1. New migration `server/migrations/0004_rate_limits.sql`: table `rate_limits (k TEXT PRIMARY KEY, window_start INTEGER NOT NULL, n INTEGER NOT NULL)`.
2. In `server/src/util.ts` add `async function rateLimit(db, key, limit, windowMs)` (fixed window, one UPSERT statement, throws `HttpError(429, "rate_limited")`). Key = route name + client IP (`CF-Connecting-IP` header, fallback `"local"`).
3. Apply: `POST /v1/register` 20 per hour per IP; `GET /v1/invite/:code` and `POST /v1/join` 30 per 10 minutes per IP (shared bucket "code"). Also a per-user bucket for `POST /v1/join`: 20 per 10 minutes per token-user.
4. Limits must be overridable with env vars `RL_REGISTER`, `RL_CODE` (numbers) so the test suite does not trip them; the CI/test `wrangler dev` command may pass higher values, BUT add one test that proves 429 appears (e.g. dedicated low bucket via test header only when `FSL_TEST_CLOCK=1`, or by running with the default limits against a separate key). Your choice, keep it simple, document it in AGENTS.md.
5. App: map HTTP 429 to the Persian toast «تعداد درخواست‌ها زیاده، چند دقیقه دیگه دوباره امتحان کن».

## Step 5 — Widget review (read + fix, no device yet)
Read `android/FslWidget.kt`, `android/Background.kt`, `MainActivity.kt`, `AndroidManifest.xml`, `app/src/main/res/xml/*widget*`. Check and fix:
- After the user posts their own status or the feed refreshes in the app, the snapshot is saved and ALL widget instances update (`updateAll`).
- WorkManager 15-min periodic job is enqueued at app start and after boot (unique work, KEEP), and runs without the app open; it needs a saved token.
- Widget with no account / no friends shows a Persian empty state, not a crash or blank.
- Tapping the widget opens the app.
- Widget sizes 2×2 / 4×2 / 4×4 have correct `minWidth/minHeight/targetCellWidth/targetCellHeight/resizeMode` in the widget-info XML.
- Character images in the widget load by resource name and don't crash if one is missing (fallback image).
List every issue found and what you changed in the report.

## Step 6 — Verify (all must pass; paste the tail of each output in the report)
Run from the project root, in this order:
1. `cd server && npx tsc --noEmit`
2. Fresh local DB: `rm -rf server/.wrangler/state` is NOT allowed; instead use `npx wrangler d1 migrations apply fsl --local --persist-to logs/agy/T1/_scratch/d1` then start `npx wrangler dev --port 8787 --persist-to logs/agy/T1/_scratch/d1 --var FSL_TEST_CLOCK:1 <your RL overrides>` in the background, run `node test/api.test.mjs` → every scenario passes (13 old + your new ones). Kill the dev server afterwards (`lsof -tiTCP:8787 | xargs kill`).
3. `./gradlew assembleDebug -PFSL_API_URL=https://example.invalid` → BUILD SUCCESSFUL.
4. `cd tools/desktop-preview && ./gradlew run` → screenshots in `build/shots/`, open 3 of them (home, group, sheet) and confirm friends show and the group screen has no mute tile.
5. `cd tools/desktop-preview && ./gradlew run -Dapi=http://127.0.0.1:8787` against a running dev server → e2e passes.
6. `grep -rn "FakeData" app/src/main` → no output. `grep -rniE "muted" app/src server/src` → no output. `grep -rniE "latitude|longitude|ACCESS_FINE|ACCESS_COARSE" app/src server/src` → no output.
7. `git status --short` → list NEW untracked files in the report; there must be none outside your write set.

## Report
Write `logs/agy/T1/REPORT.md`: for each step 1–5 what you changed (file:line), the verification outputs from step 6 (real command tails, not summaries), any failing item marked **FAIL** honestly, and open questions. Do not claim a check passed unless you ran it in this session.
