# T6d — Friends screen: separate groups vs people; calmer home

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first (RTL, Compose 1.5 / Kotlin 1.9 for shared code). Do NOT edit `docs/HANDOFF.md`.
Owner: "on the «رفقا» screen it's still hard to tell groups from people" and "the app should be less busy without losing any feature". Reference: LiveStatus = one calm column of breathable person cards.

## Hard rules
- Write ONLY: `app/src/main/java/com/sinapticc/friendsstatus/ui/`, `.../data/AppStore.kt` (only small state additions if needed), `tools/desktop-preview/src/`, `logs/ds/T6d/`.
- Keep every feature reachable. No server/res/design/source/archive edits. No git except status/diff. No wildcard rm.

## Step 1 — Home («رفقا» tab, ui/screens/HomeScreen.kt)
Current home stacks: top bar (add-person, settings/avatars pill), live counter, big title, group chips row, my-status hero card, «همین الان» list, FAB «وضعیت بذار», bottom nav. Make it calmer:
1. Top bar: keep add button (left) and ONE compact group switcher on the right showing the current scope name («همه» / group name) with a small chevron; tapping opens a sheet listing «همه‌ی رفقا», then a section «گروه‌ها» (group icon + name + member count) and a section «دونفره‌ها» (1-on-1 people). Remove the avatar pill + the horizontal group-chip row from home (the sheet replaces them).
2. Title: one line «رفقا الان» + small live counter under it («الان ۱۰ نفر فعالن»). Remove the second huge title line.
3. My status: a slimmer card (height ~72 dp): my emoji circle, «تو · <status>» and time; tap = open picker. Remove the separate FAB «وضعیت بذار» only if this card is always visible at top; otherwise keep FAB. Choose one — no duplicates.
4. Friends list = PEOPLE only, one column of cards (~84 dp): emoji circle 56 dp (status), name bold, status text, time ago on the other side, the person's avatar as a tiny badge on the emoji circle. More spacing (12 dp) between cards.
5. When scope = «همه‌ی رفقا» and a person is in several groups, show them once.

## Step 2 — Groups vs people everywhere
1. Anywhere groups and people appear together (add sheet, profile, group lists), groups use a ROUNDED-SQUARE icon tile with the group icon + a «گروه» label or member count; people use a CIRCLE avatar. Never mix the two shapes.
2. If there is a place listing groups (e.g. profile or a groups screen), put a section header «گروه‌ها» and a separate section «دونفره‌ها».

## Step 3 — Declutter pass (all screens in ui/screens)
- Remove duplicate hints/sub-labels that repeat what a button says. Max one helper line per section.
- Consistent spacing: 16 dp screen padding, 12 dp between cards, 24 dp between sections.
- Keep colors/theme tokens from `ui/theme/Theme.kt`; don't invent new colors.

## Step 4 — Verify (paste real tails)
1. `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleDebug -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL.
2. `JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home tools/desktop-preview/gradlew -p tools/desktop-preview run` → exit 0; add a shot `06b-scope-sheet` showing the group switcher sheet open.
3. e2e: `cd server && npx wrangler d1 migrations apply fsl --local --persist-to ../logs/ds/T6d/_scratch/d1 && npx wrangler dev --port 8787 --persist-to ../logs/ds/T6d/_scratch/d1 --var FSL_TEST_CLOCK:1` in background, then `JAVA_HOME=…openjdk@21… tools/desktop-preview/gradlew -p tools/desktop-preview run -Dapi=http://127.0.0.1:8787` → "e2e passed"; kill the server afterwards (`lsof -tiTCP:8787 -sTCP:LISTEN | xargs kill`). Adapt the e2e driver if UI moved, never weaken assertions.

## Report `logs/ds/T6d/REPORT.md`
Per file changes; table "feature → where it is now"; verification tails. Honest.
