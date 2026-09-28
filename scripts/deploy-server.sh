#!/bin/bash
# Owner runs this: applies new D1 migrations to the live database and deploys the Worker.
set -e
cd /Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live/server
npx wrangler d1 migrations apply fsl --remote
npx wrangler deploy
echo; echo "Smoke test:"; curl -s -o /dev/null -w "GET / -> %{http_code}\n" https://fsl-api.mohammadisina2001.workers.dev/
curl -s -X POST https://fsl-api.mohammadisina2001.workers.dev/v1/recover -H 'Content-Type: application/json' -d '{"code":"AAAA-AAAA-AAAA"}' -w "  (recover with fake code, expect 404) -> %{http_code}\n"
