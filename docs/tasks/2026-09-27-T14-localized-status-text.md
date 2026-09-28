# T14 — Show default status labels in the viewer's language

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first. Do NOT edit `docs/HANDOFF.md`.

## Bug
A status's `text` is stored on the server in the poster's language (e.g. «خمارم» for key `hungover`). An English viewer sees Persian text in the app and widget (and vice versa).

## Fix (client only; server unchanged)
1. In `model/Catalog.kt` add `fun displayText(key: String, text: String?): String`: if `text` is null/blank, or equals (trimmed) the Persian OR English default label of `key` (StatusDef stores both), return the current-language label (`label`); otherwise return `text` unchanged (real custom text). Also treat the old Persian labels from before the label rename as defaults: check `git show HEAD:app/src/main/java/com/sinapticc/friendsstatus/model/Catalog.kt` for the original Persian labels and include them in an `oldLabels` map (key → set of strings).
2. Use `displayText` everywhere a friend's or my status text is SHOWN: home list, friend screen (incl. history rows), group member rows, scope sheet, profile, widget snapshot rendering (`android/FslWidget.kt` NoteCellBody bubble and any other text). Do NOT change what is sent to the server.
3. Special server texts: private statuses are masked by the server as key `busy` with text «سرم شلوغه», ghost as key `dnd` with «غیب شده» → map these to the viewer language too (`busy` label; for ghost: t("غیب شده", "Away")).

## Hard rules
Write ONLY `app/src/main/java/`, `tools/desktop-preview/src/`, `logs/agy/T14/` (scratch in `_scratch/`). No git except status/diff/show. No wildcard rm.

## Verify
`JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleDebug -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL; desktop preview (openjdk@21) `tools/desktop-preview/gradlew -p tools/desktop-preview run -Dlang=en` → exit 0 and open `en-06-home.png`: FakeData statuses whose text is a default label must now show English. Report `logs/agy/T14/REPORT.md` with changed call sites and build tails. Honest.
