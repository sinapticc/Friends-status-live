# T15 — In-app language switch (Auto / فارسی / English)

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md`, `model/L10n.kt`. Do NOT edit `docs/HANDOFF.md`.

## Goal
Owner wants a language button. Keep "follow phone language" as the default, add manual choice.
1. `Platform` (data/Platform.kt) gets `fun getLanguagePref(): String` / `fun setLanguagePref(v: String)` with values "auto" | "fa" | "en" (Android: prefs() key `lang`; desktop FakePlatform: in-memory, default from `-Dlang`).
2. `L10n`: add `fun resolve(pref: String, systemLang: String): Boolean` = pref=="fa" → true, "en" → false, else systemLang in {"fa","per"}. Every place that currently sets `L10n.isFa` from Locale (MainActivity.onCreate, FslWidget.provideGlance, SyncWorker, WidgetConfigActivity) must use the saved pref via `resolve`.
3. UI: on the profile/settings screen («من»/"Me" tab), add a row «زبان» / "Language" showing the current choice; tapping opens a small sheet with three options: «خودکار (زبان گوشی)» / "Auto (phone language)", «فارسی», «English» (these two always written in their own language). Choosing one: save pref, set `L10n.isFa`, refresh widgets (`FslWidget.refreshAll` via a Platform hook, e.g. `platform.onLanguageChanged()`), and recreate the UI so direction (RTL/LTR) and all texts switch immediately (Android: `activity.recreate()`; desktop: just recompose).
4. Must not lose app state/account.

## Hard rules
Write ONLY `app/src/main/java/`, `tools/desktop-preview/src/`, `logs/agy/T15/` (scratch in `_scratch/`). No git except status/diff. No wildcard rm.

## Verify
`JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleDebug -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL; desktop preview (openjdk@21 `tools/desktop-preview/gradlew -p tools/desktop-preview run`) → exit 0, add shot `24-language` showing the sheet. Report `logs/agy/T15/REPORT.md`. Honest.
