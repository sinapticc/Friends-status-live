# AGENTS.md — guide for coding agents (Codex, Claude Code, …)

Friends Status Live («Friends Status Live») is an Android app where friends share what they're doing
right now ("on the toilet", "at the gym", …) with playful emoji characters. Statuses show
up in the app and on a home screen widget. This file is the handoff: what exists, why it is
built this way, how to build and test it, and what's left.

The owner writes in Persian. Reply to them in Persian; code, comments and commit messages are English.

## Product decisions (made with the owner — don't undo without asking)

- **Persian only, right-to-left.** All UI text is Persian, written inline in the composables.
  `AppRoot` forces `LayoutDirection.Rtl` regardless of the device language.
- **No location feature.** The owner removed location sharing entirely on 2026-09-25. No permission,
  onboarding step, coordinates, distance UI, API fields or widget location data. Migration 0002
  removes old server columns. Do not reintroduce location from historical `design/` files.
- **No boy/girl choice.** The design had gendered character sets; the owner removed it.
  Everyone uses the single "neutral" set, and every status/item is available to everyone.
- **Distinct character per status.** The owner explicitly requires a different character identity
  for every mood/status, not one mascot recolored, reposed or dressed differently. Keep a common
  handcrafted 3D finish, but vary species/object, silhouette, face and color. See
  `docs/CHARACTER-ART.md` for the identity/cue map. Gym headband and wristbands must never appear
  in sleeping artwork. High visual clarity at thumbnail size and very small final WebP files
  are acceptance criteria. Prefer the status symbol itself as the character: for `toilet`, make
  a cute poop character, not a character sitting on or using a toilet.
  Currently, statuses are displayed as **system emoji in colored category circles** (see `Catalog.emoji`,
  `emojiFor`, `categoryHueIndex` in `model/Catalog.kt` and `StatusChar` in `ui/components/Character.kt`).
  The WebP `ch_*` art is still in `res/drawable-nodpi` but unused for statuses; `me_*`/`acc_*` layers
  are still used by `MeChar` for the user's own character. Codex art masters in `source/character-art/`
  are for later integration.
- **Server: Cloudflare Workers + D1** (free tier), deployed by the owner via `scripts/deploy-server.sh`.
- **No sign-up.** Anonymous accounts: the server issues a random token on first use; the
  account lives on the device.
- **No Firebase/FCM** (owner decision 2026-09-26). The widget relies on WorkManager 15-minute
  background sync + in-app refresh. FCM code is still present in the codebase but unused without
  Firebase configuration.
- **When the app is closed:** a home screen **widget** (not notifications). Instagram-Notes style
  layout (`FslWidget.kt`: `NoteCell`/`NotesRow`): my own status first, per-widget group via
  `WidgetConfigActivity` (APPWIDGET_CONFIGURE, reconfigurable), 1-on-1 friends included in
  «همهی رفقا», one-off sync at the next status expiry (`fsl-expiry`). Responsive sizes:
  2×2 one big note, 4×2 me+3, 4×4 two rows me+7.
- **Account recovery.** Profile shows «کد بازیابی حساب»; welcome screen has «قبلاً حساب داشتم».
  Server endpoints: `POST /v1/me/recovery` (authed, generates `XXXX-XXXX-XXXX` code) and
  `POST /v1/recover` (no auth, rate-limited 10/h per IP).

## Repository layout

```
app/                          Android app (Kotlin, Jetpack Compose)
  src/main/java/com/sinapticc/friendsstatus/
    MainActivity.kt           Activity, AppViewModel, AndroidPlatform (Platform impl)
    android/                  Android-only: Background.kt (WorkManager, LiveBus), FslWidget.kt (Glance widget), WidgetConfigActivity.kt
    data/
      AppStore.kt             The single state holder + every user action (see "Architecture")
      Api.kt                  OkHttp client + DTOs mirroring the server JSON
      Platform.kt             Interface for things only the host can do
    model/                    Models.kt, Catalog.kt (statuses, groups, look options, emoji, categoryHueIndex), Fa.kt (Persian digits etc.)
    platform/Assets.kt        Fonts + character drawable lookup (Android). Desktop twin lives in tools/desktop-preview
    ui/AppRoot.kt             Screen switch, bottom nav, sheets, toasts
    ui/screens/               One file per area (Home, StatusPickerSheet, Onboarding, FriendAndPrivacy, Group, Profile, AddSheet)
    ui/components/            Character.kt (StatusChar/MeChar), Common.kt (buttons, toggles…), Overlays.kt (sheets, toast, QR, confetti)
    ui/theme/Theme.kt         Design tokens (dark/light), type scale, insets
  src/main/res/drawable-nodpi/  Character art: ch_*.webp (unused for statuses, kept for future), me_*/acc_* (used by MeChar)
  src/main/res/font/            Vazirmatn + Lalezar (OFL)
server/                       Cloudflare Worker (TypeScript) + D1
  src/index.ts                Router and every endpoint
  src/fcm.ts                  FCM HTTP v1 sender (present but unused — no Firebase config)
  src/util.ts                 Validation, codes, hashing, rate limiting
  migrations/0001_init.sql    Historical initial schema
  migrations/0002_remove_location.sql  Drops stored location fields
  migrations/0003_drop_muted.sql       Drops the removed group mute flag
  migrations/0004_rate_limits.sql      Fixed-window rate limit buckets
  migrations/0005_recovery.sql         Account recovery codes
  test/api.test.mjs           End-to-end API tests (16 scenarios, run against `wrangler dev`)
design/                       The Claude Design export this app implements (.dc.html files, character JS, screenshots in shots/)
tools/characters/             Renders the design's SVG characters to WebP (Playwright) — ch_*.webp art not currently used for statuses
tools/desktop-preview/        Compiles the shared app code for desktop: type-check, screenshots, e2e against a server
scripts/setup.sh              One-time environment setup (Linux only; macOS: scripts/setup-macos.sh)
scripts/deploy-server.sh      Owner-runs deploy script (Cloudflare Workers + D1 migrations)
source/character-art/         Codex art masters (PNG) — for later integration
.github/workflows/            android.yml (build APK), server.yml (test + deploy), setup-check.yml (tests setup.sh)
```

## Environment setup

On macOS run `./scripts/setup-macos.sh` once (Homebrew, JDK 17 and 21, Android SDK, server dependencies).
The original `./scripts/setup.sh` is Linux-only. It installs the Android SDK (platform 35, build-tools 35) into
`$ANDROID_HOME` (default `~/android-sdk`), writes `local.properties`, and runs `npm ci` in
`server/`. It needs JDK 17+, Node 22+ and network access to `dl.google.com`, Google Maven,
Maven Central, the Gradle plugin portal and `registry.npmjs.org`. The "Setup script" workflow
runs it from scratch on GitHub to prove it works.

If `dl.google.com` is blocked (some sandboxes), the Android build is impossible there; use
`tools/desktop-preview` (Maven Central only) to compile and screenshot the shared code, and let
CI build the APK.

## Build, run, test

| What | Command | Notes |
|---|---|---|
| Android debug APK | `./gradlew assembleDebug` | Needs the Android SDK (Google Maven). CI does this on every push and uploads `app-debug`. |
| Android release APK | `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleRelease -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` | Signed with release keystore if `local.properties` has `fsl.release.*`; falls back to debug key. Current: v1.0.4 (versionCode 5). |
| APK against a local server | `./gradlew assembleDebug -PFSL_API_URL=http://10.0.2.2:8787` | Empty `FSL_API_URL` ⇒ fails to connect. |
| Server locally | `cd server && npm ci && npx wrangler d1 migrations apply fsl --local && npx wrangler dev --port 8787` | Node ≥ 22. |
| Server tests | `node test/api.test.mjs` (with the dev server running) | 16 scenarios, all passing (includes rate limit 429 tested via a unique IP). CI runs them. |
| Type-check server | `cd server && npx tsc --noEmit` | |
| Shared UI compile + screenshots (no Android SDK) | `cd tools/desktop-preview && ./gradlew run` | Writes PNGs of ~20 screens (uses fake data) to `build/shots/`. |
| End-to-end app ↔ server | `cd tools/desktop-preview && ./gradlew run -Dapi=http://127.0.0.1:8787` | Drives two app instances (create group, join, post, private status, poke, 1-on-1) and asserts results. |
| Regenerate character art | `cd tools/characters && npm i playwright && node render.mjs ../../app/src/main/res/drawable-nodpi` | Then regenerate the `art` map in `platform/Assets.kt` if files were added/removed. |

Always keep both green: CI `Android build` and `Server`. Before pushing UI changes, run the desktop
preview: it compiles everything except `android/`, `MainActivity.kt` and `platform/Assets.kt`.

## Architecture

**App state.** `AppStore` owns one immutable `AppState` (Compose `mutableStateOf`) and exposes
methods for every user action. Screens read `store.state` and call methods — no ViewModel per
screen, no navigation library. `Screen` enum + a back stack in state; `AppRoot` switches on it.
`MainActivity` wires the system back button to `store.back()`.

**Server errors.** `Platform.apiUrl` (from `BuildConfig.API_URL`) decides the server address.
Actions update state optimistically, then call the API through `remote { … }`
(errors become Persian toasts) and refresh the feed. `refresh()` maps `FeedDto` → UI models
in `apply()`. The feed is polled every 30 s while in the foreground.

**Accounts.** `ensureAccount()` registers lazily (first server need). Token/user id are in
SharedPreferences `fsl` (`token`, `userId`). The worker and widget read the same file (`android/prefs()`).

**Platform.** `data/Platform.kt` is the seam for host features (clipboard, share, photo picker,
widget hand-off, push token). Android implementation: `AndroidPlatform` in
`MainActivity.kt`. Desktop: `FakePlatform` in `tools/desktop-preview/src/main/kotlin/Main.kt`.

**Background.** `android/Background.kt`: `SyncWorker` fetches the feed with the saved token, stores a
`WidgetState` snapshot, and refreshes widgets. `Sync.schedule` enqueues the 15-minute job.
FCM code is present but unused (no Firebase config). A one-time sync is scheduled at the
earliest status expiry (`fsl-expiry` WorkManager unique work) so the widget flips back on time.

**Widget.** `android/FslWidget.kt` (Glance). Instagram-Notes style: `NoteCell`/`NotesRow`.
2×2: one big note (featured friend or my status). 4×2: me + 3 tiles. 4×4: two rows of four.
My own status always appears first. Per-widget group choice via `WidgetConfigActivity`
(APPWIDGET_CONFIGURE intent, reconfigurable). 1-on-1 friends included in «همهی رفقا».
Renders from the saved snapshot only (no network in the widget).

**Characters.** Statuses are shown as **system emoji in colored category circles** (`StatusChar`).
`Catalog.emojiFor(key)` returns the emoji, `Catalog.categoryHueIndex(key)` picks the circle color.
The user's own character (`MeChar`) is stacked from `me_body_<tint>`, `me_face_<face>`,
`me_outfit_<o>`, `me_acc_<a>` WebP layers. Historical SVG/JS characters (`ch_*.webp`) are
still in `res/drawable-nodpi` but not used for statuses. Codex art masters in
`source/character-art/` are for later integration.

## Server API (all JSON, `Authorization: Bearer <token>` except register)

| Method & path | Purpose |
|---|---|
| `POST /v1/register` `{nick, avatar, look}` | → `{id, token, pairCode}` |
| `GET /v1/feed?since=<ms>` | me, my groups (+members, code for admins), friends (status, history), reactions since |
| `PATCH /v1/me` | nick, avatar, look, fcmToken, ghost, pausedUntil |
| `DELETE /v1/me` · `DELETE /v1/history` | delete account · clear my status history (keeps current) |
| `POST /v1/status` | key, text, hue, acc, visibility (all/groups/one), visGroups, expiresInMin |
| `POST /v1/react` `{to, kind}` | poke / laugh / out; stored and pushed |
| `GET /v1/invite/:code` · `POST /v1/join {code}` | preview / join. 6 digits = group invite, 7 chars = someone's 1-on-1 pair code |
| `POST /v1/groups` | create (caller becomes admin, gets an invite code) |
| `PATCH /v1/groups/:id` | admin: name, icon, color, codeTtl (24h/7d/30d/never) |
| `POST /v1/groups/:id/code` · `/leave` · `/members/:uid/role` · `DELETE …/members/:uid` | reset code · leave · (un)make admin · remove |
| `POST /v1/me/recovery` | (auth) generate recovery code `XXXX-XXXX-XXXX`, return `{code}`; calling again replaces old code |
| `POST /v1/recover` `{code}` | (no auth) recover account by code → new token, invalidates old; rate-limit 10/h per IP |

Privacy is enforced **on the server** in `feed()`: private statuses (`SENSITIVE` keys) are shown
as `busy` unless visibility allows the viewer (1-on-1 space or chosen group); ghost mode / pauses
show `dnd` and hide history. Legacy location payloads return `400 location_disabled`.
An expired status falls back to the previous non-expired one (owner decision); the app refreshes and
the widget hides expired entries. Rate limits (`util.rateLimit`, D1 table `rate_limits`, key = route +
`CF-Connecting-IP`): register 20/h, invite preview + join 30/10 min per IP, join 20/10 min per user,
recover 10/h per IP -> `429 rate_limited`. Worker vars `RL_REGISTER`/`RL_CODE` and `FSL_TEST_CLOCK=1` (enables `X-Test-Now`) are
for local tests only (`wrangler dev --var ...`), never production. Group mute was removed 2026-09-25.
The status key list exists twice — `STATUS_KEYS` in `server/src/index.ts` and `Catalog.statuses` —
keep them in sync. DTOs in `Api.kt` must match `feed()`'s JSON.

## Configuration (GitHub → Settings → Secrets and variables → Actions)

| Name | Kind | Used by |
|---|---|---|
| `CLOUDFLARE_API_TOKEN`, `CLOUDFLARE_ACCOUNT_ID` | secret | server deploy (token needs Workers Scripts Edit + D1 Edit) |
| `FCM_SERVICE_ACCOUNT` | secret | server → FCM (currently unused — no Firebase project) |
| `GOOGLE_SERVICES_JSON` | secret | app build → Firebase client config (currently unused) |
| `FSL_API_URL` | variable | app build → server address (`https://fsl-api.mohammadisina2001.workers.dev`) |

The deploy job runs on the default branch or manually; it creates the D1 database on first run,
applies migrations, sets the FCM secret and prints the worker URL in the run summary. Without
the Cloudflare secrets it skips with a warning. Owner deploys via `scripts/deploy-server.sh`
(Claude's auto mode blocks deploys).

## Conventions and gotchas

- **Right-to-left:** `Modifier.offset` mirrors in RTL (good for layout); use `absoluteOffset`
  for artwork positions that must not mirror. Back buttons use `Icons.Rounded.ArrowForward`;
  direction-bearing icons get `Modifier.mirror()` or `PrimaryButton(mirrorIcon = true)`.
  Numbers/codes: show with `Fa.digits`/`Fa.num`; invite codes with `Fa.code()` (wraps in an LTR
  isolate so "۴۸۲ ۹۱۳" doesn't flip). Parse user digits with `Fa.toAscii`.
- **Compose API level:** shared code must compile on Compose 1.5 (desktop preview) and the app's
  Compose BOM 2024.12. So no `Icons.AutoMirrored`, etc. If you need newer APIs, upgrade the
  preview tool too or keep that code in Android-only files.
- **Kotlin versions:** app uses Kotlin 2.0.21; the preview tool uses 1.9.22. Avoid language
  features newer than 1.9 in shared code.
- **Android-only code** goes in `android/`, `MainActivity.kt` or `platform/Assets.kt` (all
  excluded from the preview build). Everything else must stay platform-neutral.
- Inside `AndroidPlatform`, the `prefs` property shadows the top-level `prefs()` function — it's
  imported as `sharedPrefs`.
- Toggles/segments/tiles use the design's springy motion (`bouncy()`), press scale (`pressTap`)
  and no ripples (`tap`).
- Library versions (AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.12.01, Glance 1.1.1, WorkManager
  2.10.0, firebase-messaging 24.1.0) were picked conservatively and are known to build in CI.
- Release builds are signed with the release keystore in `/Users/sina/.android/fsl-release/`
  (read via `local.properties` `fsl.release.*`); falls back to debug key if absent.
  Current release: v1.0.4 (versionCode 5). Future updates must use a higher versionCode and
  the same keystore. Shareable APKs go to `/Users/sina/Documents/Codex/2026-09-25/f/outputs/apk/`.

## Status and next steps

Working and verified on 2026-09-27: Android debug and release APK builds, desktop-preview
screenshots, server typecheck and 16 API tests, desktop app ↔ local server end-to-end flow,
release signing (CN=Friends Status Live), account recovery end-to-end on a real device (Redmi
Note 11 Pro), live server at `https://fsl-api.mohammadisina2001.workers.dev` (migrations
0001–0005 applied), widget in Instagram-Notes style (2×2/4×2/4×4) with per-widget group
choice and expiry sync, no-FCM fallback (WorkManager 15 min + in-app refresh), emoji-based
status display with colored category circles. Version 1.0.4 (versionCode 5) is the current
shareable release. All local work is uncommitted.

Not yet done:
1. **R2 profile photos** — needs a Cloudflare R2 card; currently device-only.
2. **Light widget theme** — current widget is dark-only.
3. **AppStore unit tests** — `AppStore` is plain Kotlin; the desktop preview's `FakePlatform`
   shows how to fake the platform.
4. **MeChar/editor art vs emoji consistency** — statuses use emoji, but the user's own character
   still uses WebP layers; visual consistency check needed.
5. **Codex art integration** — 23 PNG masters in `source/character-art/`; integrate when ready.
6. **Git commit/push** of all local work — not done yet.

## Delegation agents

- **Default sub-agent:** OpenCode `--agent mimo` (opencode/mimo-v2.5-free, free).
- **Fallback:** `ds-minimal` (DeepSeek-V4-Flash, AMD).
- **Image review:** Claude Haiku subagents.
- **Lesson learned:** cheap agents (MiMo, DeepSeek) stalled on Glance widget UI work; Claude
  did the Notes-style widget rewrite itself. Keep widget layout work in Claude.
