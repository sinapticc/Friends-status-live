# T13 — Tap a person in the widget → open the app on that person's status

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first. Do NOT edit `docs/HANDOFF.md`.

## Goal
In the widget (`android/FslWidget.kt`, Notes-style cells `NoteCell`/`NoteCellBody`, small layout `One`), tapping a FRIEND's cell (bubble, circle or name) opens the app directly on that friend's screen (the same screen the app shows when you tap the friend in the home list — find it in `data/AppStore.kt`, e.g. an `openFriend(...)`/`Screen.Friend` action). Tapping MY cell («من»/"Me") opens the app on the status picker. Tapping empty widget space keeps opening the app home.

## Hard rules
Write ONLY: `app/src/main/java/com/sinapticc/friendsstatus/android/`, `.../MainActivity.kt`, `.../data/AppStore.kt` (only a small entry point if needed), `logs/agy/T13/` (scratch only in `logs/agy/T13/_scratch/`). No git except status/diff. No wildcard rm. Don't touch server/design/source/archive/res except nothing. Shared code must compile with Kotlin 1.9 / Compose 1.5 (see AGENTS.md).

## Steps
1. Add the friend's user id to the widget snapshot: `WidgetFriend` gets `val id: String = ""` (default keeps old snapshots decodable). Fill it everywhere the snapshot is built (MainActivity onFeed path and SyncWorker/WidgetData).
2. Each friend cell: `GlanceModifier.clickable(actionStartActivity<MainActivity>(actionParametersOf(FriendKey to f.id)))` (define `val FriendKey = ActionParameters.Key<String>("fsl_friend")`). Glance passes parameters as intent extras. Me cell: parameter `fsl_open` = "picker". The outer widget Box keeps its plain open-app click.
3. `MainActivity`: in `onCreate` AND `onNewIntent` (set `launchMode` only if needed — prefer handling `onNewIntent` and calling `setIntent`), read the extras: friend id → after the store is ready and the feed contains that friend, open that friend's screen (if the friend isn't loaded yet, wait for the first refresh, then open; if not found, stay on home). "picker" → open the status picker sheet. Clear the extras after handling so rotation/back doesn't reopen.
4. Build: `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleDebug -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL. Also `JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home tools/desktop-preview/gradlew -p tools/desktop-preview run` → exit 0.
5. Verify without the phone: `adb` must NOT be used. Instead explain in the report which code path handles a cold start and a warm start (app already open).

## Report `logs/agy/T13/REPORT.md` — changes per file, cold/warm start explanation, build tails. Honest.
