# T6b — Calmer, simpler status picker (+ fix desktop preview home shots)

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first (RTL rules, Compose 1.5/Kotlin 1.9 for shared code). Do NOT edit `docs/HANDOFF.md`.
Owner complaint: "the status picker is too busy, new users get lost". Model: LiveStatus's picker (bottom sheet, category tabs, 4-column emoji grid, one tap, one send button). Status visuals are already emoji circles (`StatusChar`).

## Hard rules
- Write ONLY: `app/src/main/java/com/sinapticc/friendsstatus/ui/`, `.../data/AppStore.kt` (only if needed for picker state), `.../model/Catalog.kt` (category names only), `tools/desktop-preview/src/`, `logs/ds/T6b/`.
- Keep EVERY existing feature reachable (custom text, favorites/star, private visibility all/groups/one, choose groups, expiry, hue/accessory if they exist). Simplify by hiding secondary options, not by deleting them.
- No git commands except status/diff. No wildcard rm. Don't touch server, res, design, source, archive.

## Step 0 — fix desktop preview (tools/desktop-preview/src/main/kotlin/Main.kt)
1. Screens `06-home`, `07-home-one`, `19-home-light` currently end on the profile-setup screen. Make them start on Home with FakeData friends (use `initialState` with `screen = Screen.Home` and a registered fake user instead of calling onboarding actions that need a server).
2. The toast «آدرس سرور تنظیم نشده» appears in every screenshot. In screenshot mode (not e2e mode) make the FakePlatform's apiUrl a dummy value or suppress this toast, so no screenshot shows it. Do not change the app's real behavior.

## Step 1 — new picker layout (ui/screens/StatusPickerSheet.kt)
Top to bottom, nothing else visible by default:
1. Sheet handle + title «الان چی‌کار می‌کنی؟».
2. Horizontal category tabs (chips, one line, scrollable): first «محبوب» (favorites; if empty show popular defaults), then the Catalog categories. Rename categories to short friendly words: daily «روزمره», mood «حال‌وهوا», body «بدن», social «دورهمی», busy «سرگرم» (keep keys; change display names only).
3. Grid of 4 columns: each cell = `StatusChar` circle ~56 dp + ONE short label under it (1 line, ellipsis, 12 sp). Equal cells, generous spacing (12–16 dp). Long-press a cell = toggle favorite (star). Remove any always-visible star icons on cells.
4. Tapping a cell selects it (ring highlight) — does NOT post yet.
5. Bottom bar (sticky): left a small round icon button «⋯» / tune icon that opens «تنظیمات بیشتر» (collapsed section or second sheet) containing: custom text field (placeholder «یه چیزی بنویس… (اختیاری)»), «کی ببینه؟» (all / groups / one-on-one — existing options and explanatory lines), «پاک شه بعد از» (expiry, default = no expiry), and any hue/accessory controls. Right: primary button «بفرست» (disabled until a status is selected). If the user changed anything in «تنظیمات بیشتر», show a tiny summary line above the bar, e.g. «خصوصی · ۲ ساعت».
6. Custom status (key `custom`): selecting it auto-opens the text field.
7. Keep sheet height ≤ 80% of screen; grid scrolls inside.

## Step 2 — screenshots
Update desktop preview shots `08-sheet`, `09-sheet-private` (and add `08b-sheet-more` showing «تنظیمات بیشتر» open) to show the new picker.

## Step 3 — Verify (paste real tails)
1. `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleDebug -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL.
2. `JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home tools/desktop-preview/gradlew -p tools/desktop-preview run` → exit 0.
3. e2e must still pass: `cd server && npx wrangler dev --port 8787 --persist-to ../logs/ds/T6b/_scratch/d1 --var FSL_TEST_CLOCK:1` (after `npx wrangler d1 migrations apply fsl --local --persist-to ../logs/ds/T6b/_scratch/d1`) in background, then `JAVA_HOME=…openjdk@21… tools/desktop-preview/gradlew -p tools/desktop-preview run -Dapi=http://127.0.0.1:8787` → "e2e passed". Kill the dev server after (`lsof -tiTCP:8787 -sTCP:LISTEN | xargs kill`). If e2e drives the old picker and breaks, adapt the e2e driver, not the assertions.

## Report `logs/ds/T6b/REPORT.md`
What changed per file, where each old feature now lives (table: feature → how to reach it), verification tails. Honest.
