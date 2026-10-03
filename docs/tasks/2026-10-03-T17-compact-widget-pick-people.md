# T17 — Compact widget (only the circles) + choose exactly which people appear

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md`, `docs/HANDOFF.md` (read-only). Do NOT edit `docs/HANDOFF.md`.
Current widget: `android/FslWidget.kt` (Notes style: NoteCell/NotesRow, layouts One/Four/ListView, dark rounded background `widget_bg`), config screen `android/WidgetConfigActivity.kt` (choose a group per widget, pref `widgetGroup_<id>`), widget info `app/src/main/res/xml/fsl_widget_info.xml`. Tap on a person opens their screen (explicit Intents with `fsl://friend/<id>` — keep that working).

## Owner request (2026-10-03)
1. "The widget is too big — it should only take the space of the people's circles."
2. "With 10 friends I don't want all of them — only the people I choose."

## Part A — compact, background-free widget
1. Remove the big dark panel: the widget root is transparent (no `widget_bg`, no header/title bar, no hint text). Each person = bubble + circle + name exactly as now (keep bubble readable on any wallpaper: bubble stays white 92%; name text gets a subtle dark pill background `#99000000` with 6 dp radius so it reads on light wallpapers).
2. Sizes: allow `minWidth`/`minHeight` for a **1×1** cell (one person) and resizing horizontally to 2, 3, 4… cells (`resizeMode="horizontal|vertical"`, `targetCellWidth=1`, `targetCellHeight=1`, `minResizeWidth` ≈ 1 cell). Number of people shown = what fits: 1 per ~70 dp of width; 2 rows only if height ≥ 2 cells. Use `SizeMode.Exact` (or Responsive with 1×1, 2×1, 3×1, 4×1, 4×2 sizes) and lay out only as many cells as fit, centered. Circle size adapts to the cell (≈ 44–56 dp).
3. Empty state (no chosen people with data): one small circle with ✨ and name «وضعیت بذار»/"Post a status" (tap → picker), no big box.

## Part B — pick people per widget
1. `WidgetConfigActivity`: replace the single group choice with a two-step screen (RTL/LTR by `L10n.isFa`, texts via `t(fa, en)`):
   - Top: «کیا توی این ویجت باشن؟» / "Who's in this widget?"
   - A checklist of: «من» / "Me" (default ON), then every friend from the snapshot (avatar initial circle + name + current status emoji), sorted by name. Quick filter chips: «همه» / "All", each group name — tapping a group chip pre-selects its members (still editable).
   - Button «ذخیره» / "Save" (enabled when ≥1 selected). Order = order of selection (first tapped first).
2. Save as pref `widgetPeople_<appWidgetId>` = comma-separated user ids (`me` for myself). Keep reading the old `widgetGroup_<id>` as fallback for widgets created before this change (convert on first load: group members). Remove prefs in `onDeleted`.
3. `provideGlance`: show exactly the chosen people in the chosen order (skip people with no data/status → show their circle with initial and no bubble). If nothing chosen (configuration skipped) → Me + 3 most recent friends.
4. The snapshot must contain ALL friends (not only those with a status) with id, nick, avatar, groups, status (nullable) — adjust `WidgetData`/`WidgetFriend` with defaults so old snapshots still decode.
5. Reconfigure (long-press → reconfigure on Android 12+) opens the same screen with current selection pre-checked.

## Hard rules
Write ONLY `app/src/main/java/com/sinapticc/friendsstatus/android/`, `.../MainActivity.kt` (only if needed), `app/src/main/res/xml/`, `app/src/main/res/layout/` (only Glance-required files if any), `app/build.gradle.kts` (version lines only), `logs/agy/T17/` (scratch only in `logs/agy/T17/_scratch/`). No git except status/diff. No wildcard rm. Never uninstall the app on the phone.

## Verify
1. Bump versionCode +1 and versionName to 1.2.0 in `app/build.gradle.kts`. `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleRelease -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL. Also desktop preview (openjdk@21) `tools/desktop-preview/gradlew -p tools/desktop-preview run` → exit 0.
2. Install on the owner's phone ONLY with `adb -s ytf6mv7lnzu8ivl7 install -r app/build/outputs/apk/release/app-release.apk` (never uninstall). If the phone is unlocked (check `dumpsys power` Awake and `dumpsys window | grep isKeyguardShowing=false`), take a home-screen screenshot after `monkey` launch + `KEYCODE_HOME` (no other taps) to `logs/agy/T17/_scratch/home.png` and describe it honestly. Placing/resizing widgets is the owner's job.
3. Copy APK to `/Users/sina/Documents/Codex/2026-09-25/f/outputs/apk/FriendsStatusLive-1.2.0.apk` and move older `FriendsStatusLive-*.apk` from that folder to its `archive/`.

Report `logs/agy/T17/REPORT.md` (write early, update): changes per file, sizes, how selection/fallback works, build tails, screenshot description. Honest.
