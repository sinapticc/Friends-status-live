# T9 — Real release signing for sharing the APK with friends

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md`. Do NOT edit `docs/HANDOFF.md`.
Goal: a release APK signed with a real, permanent key so future updates install over it.

## Hard rules
Write ONLY: `app/build.gradle.kts`, `app/proguard-rules.pro` (create if needed), `local.properties`, `/Users/sina/.android/fsl-release/` (keystore folder), `logs/oc/T9/`. The keystore and passwords must NEVER be in the git repo. No git except status/diff. No wildcard rm. Do not print passwords in the report.

## Steps
1. Create folder `/Users/sina/.android/fsl-release/`. Generate a random 24-char password with `openssl rand -base64 18`. Create keystore: `keytool -genkeypair -v -keystore /Users/sina/.android/fsl-release/fsl-release.jks -alias fsl -keyalg RSA -keysize 4096 -validity 36500 -storepass <pw> -keypass <pw> -dname "CN=Friends Status Live, O=sinapticc, C=DE"` (use JDK 17's keytool: `/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home/bin/keytool`).
2. Save to `/Users/sina/.android/fsl-release/README.txt`: what this key is, that it must be backed up (losing it = friends must uninstall to update), alias `fsl`. Save the password in `/Users/sina/.android/fsl-release/password.txt` (chmod 600) and add to the project's `local.properties` (already gitignored): `fsl.release.store=/Users/sina/.android/fsl-release/fsl-release.jks`, `fsl.release.password=<pw>`, `fsl.release.alias=fsl`.
3. `app/build.gradle.kts`: add a `release` signingConfig that reads those three values from `local.properties` (use `java.util.Properties` + `rootProject.file("local.properties")`); the release buildType uses it when all values exist, otherwise falls back to the debug config (so CI without secrets still builds). Keep `minifyEnabled` as it is now (do not turn on shrinking).
4. Build: `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleRelease -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL.
5. Verify: `/Users/sina/Library/Android/sdk/build-tools/35.0.0/apksigner verify --print-certs app/build/outputs/apk/release/*.apk` → shows `CN=Friends Status Live`. `aapt dump badging` → label `Friends Status Live`, no location permission.
6. `git status --short` → confirm no keystore/password file appears.

## Report `logs/oc/T9/REPORT.md` — steps, apksigner output (cert DN + SHA-256 digest only), build tail. No passwords.
