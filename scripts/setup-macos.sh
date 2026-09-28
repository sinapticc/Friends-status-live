#!/usr/bin/env bash
# Prepare a macOS workstation for Android builds and desktop previews.
set -euo pipefail
cd "$(dirname "$0")/.."

if [ "$(uname -s)" != "Darwin" ]; then
  echo "This script is for macOS; use scripts/setup.sh on Linux." >&2
  exit 1
fi
command -v brew >/dev/null || { echo "Homebrew is required." >&2; exit 1; }
command -v node >/dev/null || { echo "Node.js 22 or newer is required." >&2; exit 1; }
node -e 'if (Number(process.versions.node.split(".")[0]) < 22) process.exit(1)' || {
  echo "Node.js 22 or newer is required." >&2
  exit 1
}

brew list --versions openjdk@17 >/dev/null 2>&1 || brew install openjdk@17
brew list --versions openjdk@21 >/dev/null 2>&1 || brew install openjdk@21
brew list --cask --versions android-commandlinetools >/dev/null 2>&1 || brew install --cask android-commandlinetools

export JAVA_HOME="$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
export ANDROID_HOME="$SDK" ANDROID_SDK_ROOT="$SDK"
mkdir -p "$SDK"
yes | sdkmanager --sdk_root="$SDK" --licenses >/dev/null || true
sdkmanager --sdk_root="$SDK" "platforms;android-35" "build-tools;35.0.0" "platform-tools"
printf 'sdk.dir=%s\n' "$SDK" > local.properties

(cd server && npm ci)
echo "Android SDK: $SDK"
echo "Build APK: JAVA_HOME=$JAVA_HOME ./gradlew assembleDebug"
echo "Desktop preview: JAVA_HOME=$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home tools/desktop-preview/gradlew -p tools/desktop-preview run"
