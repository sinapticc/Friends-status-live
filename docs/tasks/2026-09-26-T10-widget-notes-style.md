# T10 — Widget redesign: Instagram "Notes" style

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first. Do NOT edit `docs/HANDOFF.md`.
Owner: the widget looks like a contacts list. Make it look like Instagram's "Notes" row: a row of big circles; above each circle a small rounded speech bubble with the person's short note; the name under the circle; a small green dot on recently active people.

## Hard rules
Write ONLY: `app/src/main/java/com/sinapticc/friendsstatus/android/FslWidget.kt` (+ new files in `android/` if needed), `app/src/main/res/drawable/` (new XML shapes only, e.g. bubble), `app/src/main/res/xml/` widget info, `logs/oc/T10/`. Keep data loading, per-widget group filter, me-tile logic, expiry handling, click-to-open exactly as they are. No git except status/diff. No wildcard rm. Don't install on the phone.

## Design (Glance; must be RTL-correct: first item = rightmost)
Each person cell (vertical, centered):
1. **Bubble** on top: rounded rect (corner 14 dp), background white 92% (`#EBFFFFFF`) with dark text `#1B1026`, 11 sp bold, max 2 lines, max width = cell width, padding 8×5 dp. Text = the status's custom text if set, else the status label. Below the bubble a tiny tail: a 8 dp rounded square/diamond or small circle of the same color, slightly overlapping the circle (use a Box stack; a small circle "tail" is fine if a triangle is hard in Glance).
2. **Circle**: the status emoji centered on the category-colored circle (existing palette), large (see sizes). If a friend has no status: show their avatar initial on a neutral circle.
3. **Green dot**: 12 dp green `#4ADE80` with 2 dp dark ring at the bottom-left of the circle if the status was set in the last 60 min.
4. **Name** under the circle, 12 sp, 1 line, ellipsis, secondary color. My own cell says «تو».
5. **My cell** always first. If I have no status: circle shows ✨ with a small «+» badge (like Instagram's "Leave a note") and bubble text «یه وضعیت بذار»; tap opens the app on the status picker if easy, else the app.

Layouts:
- **Small (2×2)**: one cell, centered, circle 72 dp: the most recent friend (or me if no friends); my tiny «تو» chip stays in a corner as now.
- **Wide (4×2)**: one row of up to 4 cells (me + 3 most recent friends), circle 52 dp, bubbles above. No header bar; tiny group name (10 sp) top corner only.
- **Big (4×4)**: two rows of 4 cells (me + 7 most recent), circle 56 dp; header = group name small. If fewer people, center the rows and show hint «رفقات که وضعیت بذارن این‌جا میان» under them.
Background: keep the current dark rounded widget background; more vertical breathing room so bubbles never clip. Remove list-row/contact styling (no row cards, no timestamps column).

## Verify
1. `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleRelease -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL.
2. Also `assembleDebug` with the same flag → BUILD SUCCESSFUL.
## Report `logs/oc/T10/REPORT.md` — what changed, sizes used, build tails. Honest.
