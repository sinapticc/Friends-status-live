#!/bin/bash
# Owner runs this once: opens Antigravity (Gemini 3.8 Flash) in Terminal to execute docs/tasks/2026-09-25-T2-deploy-live-apk.md
P=/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live
MODEL=${1:-gemini-3.8-flash-low}
mkdir -p "$P/logs/agy/T2/_scratch"
cd "$P" && agy -i "Read $P/AGENTS.md, then execute the task spec $P/docs/tasks/2026-09-25-T2-deploy-live-apk.md exactly, step by step, and write the report it asks for. Use JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home for gradlew. Stay strictly inside the write set." --model "$MODEL" --dangerously-skip-permissions --log-file "$P/logs/agy/T2/run.log"
