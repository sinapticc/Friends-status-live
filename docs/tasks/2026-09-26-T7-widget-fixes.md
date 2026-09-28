# T7 — Widget fixes (Android only)

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first. Do NOT edit `docs/HANDOFF.md`.
Files: `app/src/main/java/com/sinapticc/friendsstatus/android/FslWidget.kt`, `android/Background.kt`, `android/WidgetConfigActivity.kt`, `MainActivity.kt`.

## Hard rules
Write ONLY in `app/src/main/java/com/sinapticc/friendsstatus/android/`, `.../MainActivity.kt`, `logs/oc/T7/`. No git except status/diff. No wildcard rm. Do not install on the phone.

## Bug 1 (must fix) — 1-on-1 friends missing from «همه‌ی رفقا» widget
Real snapshot from the phone (prefs key `widget`):
`{"title":"همه‌ی رفقا","friends":[{"nick":"رفیق تستی","key":"gaming",...,"groups":["788b60bb-…"]}],"me":{...},"groups":[{"id":"0c6a…","name":"رفقای سینا"},{"id":"920f…","name":"کرج"}]}`
The friend belongs only to a 1-on-1 pair group (`788b…`), which is NOT in `groups`, and the widget shows only «تو». Fix:
- Scope «همه‌ی رفقا» ("all") must show EVERY friend in `friends` (minus globally hidden groups from `widgetHidden` only if ALL of the friend's groups are hidden — a pair group is never hidden). Do not filter "all" by the `groups` list.
- Save pair groups too in the snapshot group list (with a flag `pair: true` and the friend's nick as name) so the config activity can offer them under a «دونفره‌ها» header; real groups under «گروه‌ها».
- A group-scoped widget shows friends whose `groups` contains that id.

## Bug 2 — expired status lingers up to 15 min
When the snapshot is saved (both in `MainActivity` onFeed and `SyncWorker`), find the earliest future `expiresAt` among me+friends; if any, enqueue a unique one-time WorkManager job (`OneTimeWorkRequest` of `SyncWorker`, `ExistingWorkPolicy.REPLACE`, name `fsl-expiry`) with initial delay = expiresAt − now + 5 s. Network constraint CONNECTED.

## Bug 3 — big list widget looks empty with 1–2 people
In the large list layout, when there are ≤ 2 rows (me + friends), make rows taller (≈64 dp, emoji 44) and under the rows show a small centered hint «رفقات که وضعیت بذارن این‌جا میان» in the secondary color. Never leave a blank area without that hint.

## Verify (paste real tails into the report)
1. `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleDebug -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL.
2. Write a tiny pure-Kotlin check if easy, otherwise explain by code reading how the snapshot above now yields 2 rows (me + رفیق تستی) for scope "all".

## Report `logs/oc/T7/REPORT.md` — files changed, how each bug is fixed, build tail. Honest.
