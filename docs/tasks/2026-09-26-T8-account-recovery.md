# T8 — Account recovery code (server + app)

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first (RTL, Compose 1.5/Kotlin 1.9 shared code, Persian casual copy). Do NOT edit `docs/HANDOFF.md`.
Problem: accounts are anonymous (token on device). Reinstalling the app loses the account. Add a recovery code.

## Hard rules
Write ONLY: `server/src/`, `server/migrations/0005_recovery.sql`, `server/test/api.test.mjs`, `app/src/main/java/`, `tools/desktop-preview/src/`, `AGENTS.md` (API table rows only), `logs/oc/T8/`. NEVER run `wrangler deploy` or any `--remote` command. No git except status/diff. No wildcard rm.

## Server
1. Migration `0005_recovery.sql`: `ALTER TABLE users ADD COLUMN recovery_hash TEXT;` + index on it.
2. `POST /v1/me/recovery` (auth): generate a code of 12 chars from `ABCDEFGHJKLMNPQRSTUVWXYZ23456789` formatted `XXXX-XXXX-XXXX`, store `sha256` of the code without dashes, return `{code}`. Calling again replaces the old code.
3. `POST /v1/recover {code}` (no auth): normalize (uppercase, strip dashes/spaces, map Persian digits), look up by hash, rate-limit with the existing `rateLimit` helper: key `recover:<ip>`, 10 per hour. On success: issue a NEW token (store its hash like register does — invalidating the old one), return `{id, token, pairCode}` same shape as register. Wrong code → 404 `not_found`.
4. Tests in `api.test.mjs`: create recovery code, recover → new token works for `/v1/feed` and old token → 401; wrong code → 404; 11th attempt → 429 (use the same IP-header technique the existing rate-limit test uses).

## App
1. `Api.kt`: `createRecovery()`, `recover(code)`.
2. `AppStore`: `showRecoveryCode()` (calls server, keeps code in state for display; never persist it), `recoverAccount(code)` (on success save token/userId like registration, refresh feed, go Home; on 404 toast «این کد پیدا نشد»; 429 existing toast).
3. Profile/privacy screen: a row «کد بازیابی حساب» → sheet showing the code big in LTR (`Fa.code`-style isolate), text «این کد رو یه جای امن نگه دار. اگه اپ پاک شد، با همین برمی‌گردی.», buttons «کپی» and «کد جدید».
4. Welcome/onboarding: a small text button «قبلاً حساب داشتم» → screen with one field «کد بازیابی» + button «برگرد به حسابم».
5. Desktop preview: add screenshots `22-recovery` and `23-recover-enter`.

## Verify (paste real tails)
1. `cd server && npx tsc --noEmit`.
2. Local: `npx wrangler d1 migrations apply fsl --local --persist-to ../logs/oc/T8/_scratch/d1`, `npx wrangler dev --port 8787 --persist-to ../logs/oc/T8/_scratch/d1 --var FSL_TEST_CLOCK:1` in background, `node test/api.test.mjs` → all pass; then `JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home tools/desktop-preview/gradlew -p tools/desktop-preview run -Dapi=http://127.0.0.1:8787` → "e2e passed"; kill server (`lsof -tiTCP:8787 -sTCP:LISTEN | xargs kill`).
3. `JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home tools/desktop-preview/gradlew -p tools/desktop-preview run` → exit 0.
4. `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleDebug -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL.

## Report `logs/oc/T8/REPORT.md` — changes per file, test output tails. Honest.
