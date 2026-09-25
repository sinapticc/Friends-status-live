# AGENTS.md — guide for coding agents (Codex, Claude Code, …)

Friends Status Live («رفقا لایو») is an Android app where friends share what they're doing
right now ("on the toilet", "at the gym", …) with playful 3D-looking characters. Statuses show
up in the app and on a home screen widget. This file is the handoff: what exists, why it is
built this way, how to build and test it, and what's left.

The owner writes in Persian. Reply to them in Persian; code, comments and commit messages are English.

## Product decisions (made with the owner — don't undo without asking)

- **Persian only, right-to-left.** All UI text is Persian, written inline in the composables.
  `AppRoot` forces `LayoutDirection.Rtl` regardless of the device language.
- **No map.** The design had a map tab; the owner removed it. Location is only used for an
  approximate *distance* ("۲٫۳ کیلومتر") between friends.
- **No boy/girl choice.** The design had gendered character sets; the owner removed it.
  Everyone uses the single "neutral" set, and every status/item is available to everyone.
- **Server: Cloudflare Workers + D1** (free tier), deployed by GitHub Actions.
- **No sign-up.** Anonymous accounts: the server issues a random token on first use; the
  account lives on the device.
- **When the app is closed:** a home screen **widget** (not notifications). The server wakes
  phones with a silent **FCM** data message when a friend posts; a **WorkManager** job syncs
  every 15 minutes as a fallback (important for users in Iran, where Google services can be
  unreliable).
- **Widget look:** the design's "glow" direction, in 2×2 / 4×2 / 4×4 layouts.

## Repository layout

```
app/                          Android app (Kotlin, Jetpack Compose)
  src/main/java/com/sinapticc/friendsstatus/
    MainActivity.kt           Activity, AppViewModel, AndroidPlatform (Platform impl)
    android/                  Android-only: Background.kt (FCM, WorkManager, LiveBus), FslWidget.kt (Glance widget)
    data/
      AppStore.kt             The single state holder + every user action (see "Architecture")
      Api.kt                  OkHttp client + DTOs mirroring the server JSON
      Platform.kt             Interface for things only the host can do
      FakeData.kt             Sample data for demo mode (no server configured)
    model/                    Models.kt, Catalog.kt (statuses, groups, look options), Fa.kt (Persian digits etc.)
    platform/Assets.kt        Fonts + character drawable lookup (Android). Desktop twin lives in tools/desktop-preview
    ui/AppRoot.kt             Screen switch, bottom nav, sheets, toasts
    ui/screens/               One file per area (Home, StatusPickerSheet, Onboarding, FriendAndPrivacy, Group, Profile, AddSheet)
    ui/components/            Character.kt (StatusChar/MeChar), Common.kt (buttons, toggles…), Overlays.kt (sheets, toast, QR, confetti)
    ui/theme/Theme.kt         Design tokens (dark/light), type scale, insets
  src/main/res/drawable-nodpi/  Character art as WebP (generated, see tools/characters)
  src/main/res/font/            Vazirmatn + Lalezar (OFL)
server/                       Cloudflare Worker (TypeScript) + D1
  src/index.ts                Router and every endpoint
  src/fcm.ts                  FCM HTTP v1 sender (service-account JWT via WebCrypto, no SDK)
  src/util.ts                 Validation, codes, hashing, distance
  migrations/0001_init.sql    Schema
  test/api.test.mjs           End-to-end API tests (run against `wrangler dev`)
design/                       The Claude Design export this app implements (.dc.html files, character JS, screenshots in shots/)
tools/characters/             Renders the design's SVG characters to WebP (Playwright)
tools/desktop-preview/        Compiles the shared app code for desktop: type-check, screenshots, e2e against a server
scripts/setup.sh              One-time environment setup (Android SDK + server npm packages)
.github/workflows/            android.yml (build APK), server.yml (test + deploy), setup-check.yml (tests setup.sh)
```

## Environment setup

Run `./scripts/setup.sh` once. It installs the Android SDK (platform 35, build-tools 35) into
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
| Android APK | `./gradlew assembleDebug` | Needs the Android SDK (Google Maven). CI does this on every push and uploads `app-debug`. |
| APK against a server | `./gradlew assembleDebug -PFSL_API_URL=http://10.0.2.2:8787` | Empty `FSL_API_URL` ⇒ demo mode with sample data. |
| Server locally | `cd server && npm ci && npx wrangler d1 migrations apply fsl --local && npx wrangler dev --port 8787` | Node ≥ 22. |
| Server tests | `node test/api.test.mjs` (with the dev server running) | 13 scenarios, all passing. CI runs them. |
| Type-check server | `cd server && npx tsc --noEmit` | |
| Shared UI compile + screenshots (no Android SDK) | `cd tools/desktop-preview && ./gradlew run` | Writes PNGs of ~20 screens (demo data) to `build/shots/`. |
| End-to-end app ↔ server | `cd tools/desktop-preview && ./gradlew run -Dapi=http://127.0.0.1:8787` | Drives two app instances (create group, join, post, private status, poke, 1-on-1) and asserts results. |
| Regenerate character art | `cd tools/characters && npm i playwright && node render.mjs ../../app/src/main/res/drawable-nodpi` | Then regenerate the `art` map in `platform/Assets.kt` if files were added/removed. |

Always keep both green: CI `Android build` and `Server`. Before pushing UI changes, run the desktop
preview: it compiles everything except `android/`, `MainActivity.kt` and `platform/Assets.kt`.

## Architecture

**App state.** `AppStore` owns one immutable `AppState` (Compose `mutableStateOf`) and exposes
methods for every user action. Screens read `store.state` and call methods — no ViewModel per
screen, no navigation library. `Screen` enum + a back stack in state; `AppRoot` switches on it.
`MainActivity` wires the system back button to `store.back()`.

**Server vs demo.** `Platform.apiUrl` (from `BuildConfig.API_URL`) decides the mode:
- Server mode: actions update state optimistically, then call the API through `remote { … }`
  (errors become Persian toasts) and refresh the feed. `refresh()` maps `FeedDto` → UI models
  in `apply()`. The feed is polled every 30 s while in the foreground and on every FCM push.
- Demo mode (`FakeData`): same UI, in-memory data, a fake "live" update every 11 s. The desktop
  preview screenshots use it.

**Accounts.** `ensureAccount()` registers lazily (first server need). Token/user id are in
SharedPreferences `fsl` (`token`, `userId`). The worker and widget read the same file (`android/prefs()`).

**Platform.** `data/Platform.kt` is the seam for host features (clipboard, share, photo picker,
location, widget hand-off, push token). Android implementation: `AndroidPlatform` in
`MainActivity.kt`. Desktop: `FakePlatform` in `tools/desktop-preview/src/main/kotlin/Main.kt`.

**Background.** `android/Background.kt`: `Push` initializes Firebase manually from BuildConfig
(no google-services Gradle plugin), `FslMessagingService` receives silent pushes → `LiveBus`
(for a running app) + `Sync.now`. `SyncWorker` fetches the feed with the saved token, stores a
`WidgetState` snapshot, and refreshes widgets. `Sync.schedule` enqueues the 15-minute job.

**Widget.** `android/FslWidget.kt` (Glance). Responsive sizes: small → one friend, wide → four
tiles, big → list of six. It renders from the saved snapshot only (no network in the widget).
Groups hidden with "ویجت‌ها" on the group screen are excluded (`widgetHidden` pref).

**Characters.** The design draws characters as SVG from JavaScript (`design/fsl-chars*.js`).
They're pre-rendered to WebP: `ch_<status>` for statuses, `acc_<item>` overlays (cap/bow/crown…,
positioned by `Catalog.accAnchors`), and `me_body_<tint>`, `me_face_<face>`, `me_outfit_<o>`,
`me_acc_<a>` layers stacked by `MeChar` for the user's own character. Status recoloring uses the
CSS `hue-rotate` matrix (`Character.kt`). Images are drawn 112% of their box, like the design.

## Server API (all JSON, `Authorization: Bearer <token>` except register)

| Method & path | Purpose |
|---|---|
| `POST /v1/register` `{nick, avatar, look}` | → `{id, token, pairCode}` |
| `GET /v1/feed?since=<ms>` | me, my groups (+members, code for admins), friends (status, history, distanceKm), reactions since |
| `PATCH /v1/me` | nick, avatar, look, fcmToken, ghost, pausedUntil, precision, lat/lng |
| `DELETE /v1/me` · `DELETE /v1/history` | delete account · clear my status history (keeps current) |
| `POST /v1/status` | key, text, hue, acc, visibility (all/groups/one), visGroups, expiresInMin, lat/lng |
| `POST /v1/react` `{to, kind}` | poke / laugh / out; stored and pushed |
| `GET /v1/invite/:code` · `POST /v1/join {code}` | preview / join. 6 digits = group invite, 7 chars = someone's 1-on-1 pair code |
| `POST /v1/groups` | create (caller becomes admin, gets an invite code) |
| `PATCH /v1/groups/:id` | admin: name, icon, color, codeTtl (24h/7d/30d/never) |
| `PATCH /v1/groups/:id/me` | my muted flag and location share level for that group |
| `POST /v1/groups/:id/code` · `/leave` · `/members/:uid/role` · `DELETE …/members/:uid` | reset code · leave · (un)make admin · remove |

Privacy is enforced **on the server** in `feed()`: private statuses (`SENSITIVE` keys) are shown
as `busy` unless visibility allows the viewer (1-on-1 space or chosen group); ghost mode / pauses
show `dnd` and hide history; distance respects the sharer's per-group share level and precision.
The status key list exists twice — `STATUS_KEYS` in `server/src/index.ts` and `Catalog.statuses` —
keep them in sync. DTOs in `Api.kt` must match `feed()`'s JSON.

## Configuration (GitHub → Settings → Secrets and variables → Actions)

| Name | Kind | Used by |
|---|---|---|
| `CLOUDFLARE_API_TOKEN`, `CLOUDFLARE_ACCOUNT_ID` | secret | server deploy (token needs Workers Scripts Edit + D1 Edit) |
| `FCM_SERVICE_ACCOUNT` | secret | server → FCM (Firebase service account JSON) |
| `GOOGLE_SERVICES_JSON` | secret | app build → Firebase client config (optional) |
| `FSL_API_URL` | variable | app build → server address (`https://fsl-api.<sub>.workers.dev`) |

The deploy job runs on the default branch or manually; it creates the D1 database on first run,
applies migrations, sets the FCM secret and prints the worker URL in the run summary. Without
the Cloudflare secrets it skips with a warning.

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
- Release builds are signed with the debug key (see `app/build.gradle.kts`) — replace before publishing.

## Status and next steps

Working and verified: full UI (screenshots in desktop preview), server with 13 passing API
tests, app ↔ server end-to-end flow, CI green for both workflows.

Not yet verified: nothing has run on a real phone/emulator yet, and the server hasn't been
deployed to Cloudflare (needs the owner's secrets). FCM delivery and the widget are untested on
a device.

Suggested next steps, roughly in priority order:
1. Owner adds the Cloudflare secrets → run the Server workflow → set `FSL_API_URL` → rebuild APK
   → test on two phones (onboarding, join, statuses, widget, background refresh).
2. Firebase project + `GOOGLE_SERVICES_JSON` / `FCM_SERVICE_ACCOUNT`; confirm silent pushes wake
   the widget with the app closed.
3. Rate limiting on the server (register, join, invite preview — 6-digit codes are guessable).
4. Account recovery (e.g., a backup code) — reinstalling the app loses the account today.
5. Profile photos are device-only; upload to R2 if friends should see them.
6. Place labels (only distance today), and "clear after" for normal statuses shows a
   "no status" placeholder when it expires.
7. Widget: light look, per-widget group choice (configuration activity).
8. Unit tests for `AppStore` (it's plain Kotlin; the desktop preview's `FakePlatform` shows how to fake the platform).
9. Upgrade library versions; add R8 rules if release shrinking breaks serialization.
