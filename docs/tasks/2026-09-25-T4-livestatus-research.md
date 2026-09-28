# T4 — Research the LiveStatus app (copy tone + UI/UX ideas). Read-only research.

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first. Write ONLY in `logs/agy/T4/`. Do not edit code or docs.

The owner finds our app's Persian texts "AI-generated" and the app too busy. They want us to learn from **LiveStatus – App for Couples**: https://apps.apple.com/us/app/livestatus-app-for-couples/id6443503124

## Steps
1. Open the App Store page (and the developer's website if linked). Read description, "What's New", and look at EVERY screenshot (download them to `logs/agy/T4/_scratch/`, open each image).
2. Also look for 1–2 other public sources (website, Google Play listing, reviews) for more screens.
3. Read our app's UI texts: all Persian strings in `app/src/main/java/com/sinapticc/friendsstatus/ui/**/*.kt` and `model/Catalog.kt`.
4. Take our current screens from `tools/desktop-preview/build/shots/*.png` (open 06-home, 08-sheet, 10-friend, 12-group, 21-add-sheet).

## Report `logs/agy/T4/REPORT.md` (write it in Persian)
A. **Copy tone of LiveStatus** — how they write: length, person, humor, emoji use, how status names are phrased. Describe patterns; quote at most a few words per example (no long copying).
B. **Our worst texts** — a table of ~40 current Persian strings (file:line) that sound robotic/AI, each with a proposed rewrite: short, casual spoken Persian (محاوره‌ای) like real friends texting, no marketing phrases. Status names too.
C. **UI/UX ideas from LiveStatus** that could make our app calmer and easier, especially: status picking flow, home/friends list, how groups vs individual people are separated, widget (do they show own status? per-widget choice?). For each idea: what they do, screenshot file, what we'd change, pros/cons. Number them so the owner can say yes/no per idea.
D. The English app name they use and 5 English name ideas for our app (short, memorable, not "LiveStatus", check none is obviously taken on Google Play by searching).
