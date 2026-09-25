#!/usr/bin/env bash
# One-time environment setup for coding agents (Codex cloud, CI-like Linux boxes).
# Installs the Android SDK pieces the app needs and the server's npm packages.
# Needs: JDK 17+, Node 22+, curl, unzip, and network access to dl.google.com and registry.npmjs.org.
set -euo pipefail
cd "$(dirname "$0")/.."

SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/android-sdk}}"
export ANDROID_HOME="$SDK" ANDROID_SDK_ROOT="$SDK"

if [ ! -x "$SDK/cmdline-tools/latest/bin/sdkmanager" ]; then
  echo "Installing Android command-line tools into $SDK"
  mkdir -p "$SDK/cmdline-tools"
  tmp="$(mktemp -d)"
  curl -fsSL -o "$tmp/tools.zip" "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
  unzip -q "$tmp/tools.zip" -d "$tmp"
  rm -rf "$SDK/cmdline-tools/latest"
  mv "$tmp/cmdline-tools" "$SDK/cmdline-tools/latest"
  rm -rf "$tmp"
fi

yes | "$SDK/cmdline-tools/latest/bin/sdkmanager" --licenses >/dev/null || true
"$SDK/cmdline-tools/latest/bin/sdkmanager" "platforms;android-35" "build-tools;35.0.0" "platform-tools" >/dev/null

# Gradle finds the SDK through local.properties (ignored by git).
echo "sdk.dir=$SDK" > local.properties

if command -v npm >/dev/null; then
  (cd server && npm ci)
fi

echo
echo "Done. Try:"
echo "  ./gradlew assembleDebug                      # Android APK"
echo "  (cd server && npx wrangler d1 migrations apply fsl --local && npx wrangler dev --port 8787)"
echo "  (cd server && node test/api.test.mjs)        # with the dev server running"
