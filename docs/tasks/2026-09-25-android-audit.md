# Android audit — 2026-09-25

Repository: `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`

Read `AGENTS.md` first. Audit the Android application only. Do not edit any tracked source file, do not run deployment, and do not use GitHub writes. Another reviewer is auditing the server and build setup.

Inspect `app/src/main/`, `app/build.gradle.kts`, and the shared desktop preview code. Focus on correctness in onboarding, account persistence, group and friend flows, status visibility, location permission and privacy, widget refresh, WorkManager, Firebase, and RTL UI. Check any claimed finding against the exact source. Do not call a feature verified on a phone: no device testing is requested in this audit.

Write one report at `logs/qa/android-audit-2026-09-25.md`. For every actionable finding include severity (critical/high/medium/low), exact file and line, triggering scenario, observed or logically certain effect, and the smallest sensible fix. Separate confirmed bugs from risks or untested assumptions. Include a short architecture overview and a list of areas inspected. Do not change any other file. Do not write scripts, install packages, or create empty screenshot files. If a test cannot run, explain why without claiming success.
