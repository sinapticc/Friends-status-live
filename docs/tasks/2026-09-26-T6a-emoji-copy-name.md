# T6a — Emoji status visuals, new Persian copy, English app name

Project root (ABSOLUTE): `/Users/sina/Documents/Codex/2026-09-25/f/outputs/Friends-status-live`. Read `AGENTS.md` first (RTL, Compose 1.5 / Kotlin 1.9 rules for shared code!). Do NOT edit `docs/HANDOFF.md`.

## Hard rules
- Write ONLY: `app/src/main/java/`, `app/src/main/res/values/strings.xml`, `tools/desktop-preview/src/`, `logs/ds/T6a/` (scratch in `logs/ds/T6a/_scratch/`).
- Do NOT delete any drawable/webp files, do not touch `source/`, `design/`, `archive/`, `server/`. No git commands except `git status`/`git diff`. No wildcard rm.
- Do not change status KEYS (server depends on them). Keep all features.

## Step 1 — Emoji instead of character images for STATUSES
1. `model/Catalog.kt`: add `val emoji: String` to `StatusDef` and fill it exactly with this map:
   toilet 💩, sleeping 😴, eating 🍜, gym 🏋️, studying 📚, work 💻, driving 🚗, gaming 🎮, partying 🥳, showering 🚿, coffee ☕, sick 🤒, walking 🚶, dnd 🔕, bored 🥱, free 🙌, custom ✨, period 🩸, spicy 🥵, spicy2 🔥, spicy3 🆘, inlove 😍, heartbroken 💔, hungover 🤕, angry 😤, crying 😭, date 🌹, shopping 🛍️, movie 🍿, traveling ✈️, cooking 🍳, meditating 🧘, hookah 💨, cleaning 🧹, traffic 🚦, lowbattery 🪫, overthinking 🌀, maincharacter 🎬, busy ⏳, period2 🍫, period3 🙅, football ⚽, barber 💈, beard 🪒.
   Add a helper `fun emojiFor(key: String): String` (fallback "✨").
2. `ui/components/Character.kt` → `StatusChar(...)`: keep the same signature (so no call site breaks) but render: a filled circle (size = the composable's box) with the status's circle color, and the emoji centered as `Text` at ~52% of the box size (use `BoxWithConstraints`, font size from maxWidth, `lineHeight` = fontSize, no padding). Keep the existing bounce/idle animation if simple; ignore `hue` and `acc` (accessory overlays are not drawn on emoji). Circle colors: soft tints — pick by the category the key belongs to in `Catalog` (4–5 categories): use these ARGB values: `0xFF3A2A55`, `0xFF2B3F5C`, `0xFF4A2F3F`, `0xFF2F4A3C`, `0xFF4A3F2A` for dark theme and `0xFFEDE4FA`, `0xFFE3ECF8`, `0xFFF8E4EC`, `0xFFE3F4EA`, `0xFFF6EEDC` for light theme (same order; choose via the theme's isDark flag already used in Theme.kt).
3. Widget `android/FslWidget.kt`: wherever a status image is shown (`charImage(...)`), replace with a Glance `Box` with rounded corners/background color (dark palette above) containing a Glance `Text` with the emoji (size ≈ 60% of the old image size in sp). Keep a fallback.
4. Do NOT change `MeChar` (the user's own customizable character) in this task.

## Step 2 — App name "Friends Status Live"
- `strings.xml` `app_name` → `Friends Status Live`. Widget header text «رفقا لایو» (FslWidget.kt ~156) → `Friends Status Live`. Share texts in `AppStore.kt` (~490, ~511) that say «رفقا لایو» → `Friends Status Live`. Any other «رفقا لایو» in app code → `Friends Status Live`.

## Step 3 — Status labels (Catalog.kt): replace ONLY these labels
free «بیکارم، پایه‌ام» · cleaning «خونه‌تکونی» · football «فوتبال می‌بینم» · cooking «دارم آشپزی می‌کنم»→«آشپزی می‌کنم» · overthinking «فکر و خیال» · period2 «هوس شکلات» · studying «درس می‌خونم» · gaming «گیم می‌زنم» · eating «دارم غذا می‌خورم»→«غذا می‌خورم» · walking «قدم می‌زنم» · shopping «خرید می‌کنم» · movie «فیلم می‌بینم» · crying «گریه‌م گرفته» · meditating «مدیتیشن می‌کنم» · beard «ریش می‌زنم».

## Step 4 — UI copy. Find each OLD string (search the exact Persian text; line numbers are approximate) and replace with NEW. If an old string is built from pieces (template with ${}), keep the variables.
| File (~line) | OLD | NEW |
|---|---|---|
| HomeScreen.kt ~110 | «X نفر همین الان فعالن» pattern | «الان X نفر فعالن» |
| HomeScreen.kt ~201 | «رفقات رو با کد گروه دعوت کن تا وضعیتشون این‌جا بیاد.» | «کد گروه رو بفرست تا رفقات بیان.» |
| HomeScreen.kt ~249 | «بزن تا اولین وضعیتت رو بذاری» | «اولین وضعیتت رو بذار» |
| HomeScreen.kt ~251 | «… · برای همه‌ی رفقات» | «… · همه می‌بینن» |
| StatusPickerSheet.kt ~147 | «هنوز محبوبی نداری. روی ستاره بزن تا اضافه بشه.» | «روی ستاره‌ی هر وضعیت بزن تا بیاد این‌جا.» |
| StatusPickerSheet.kt ~245 | «همه‌ی آدمای گروه‌هات می‌بیننش.» | «همه‌ی گروه‌هات می‌بینن.» |
| StatusPickerSheet.kt ~246 | «فقط «X» می‌بینه. بقیه‌ی گروه‌ها «سرم شلوغه» می‌بینن.» | «فقط «X» می‌بینه؛ بقیه «سرم شلوغه» می‌بینن.» |
| StatusPickerSheet.kt ~263 | «پاک بشه بعد از» | «پاک شه بعد از» |
| StatusPickerSheet.kt ~280 | «ارسال برای رفقا» | «بفرست» |
| FriendAndPrivacy.kt ~221 | «نامرئی شدی. رفقا آخرین وضعیتت رو «غیب‌شده» می‌بینن.» | «غیب شدی. رفقا می‌بینن «غیب شده».» |
| FriendAndPrivacy.kt ~221 | «وضعیتت رو از همه مخفی کن.» | «وضعیتت رو از همه قایم کن.» |
| FriendAndPrivacy.kt ~229 | «توقف وضعیت» | «یه مدت نشون نده» |
| FriendAndPrivacy.kt ~242 | «پاک کردن تاریخچه‌ی وضعیت‌هام» | «پاک کردن تاریخچه» |
| FriendAndPrivacy.kt ~243 | «ترک همه‌ی گروه‌ها و حذف داده‌ها» | «حذف حساب» |
| FriendAndPrivacy.kt ~249 | «از همه‌ی گروه‌ها بیرون میای و وضعیت‌ها و حسابت برای همیشه پاک می‌شه.» | «از همه‌ی گروه‌ها میای بیرون و حسابت برای همیشه پاک می‌شه.» |
| GroupScreen.kt ~134 | «X عضو» | «X نفر» |
| GroupScreen.kt ~144 | «برای گزینه‌ها روی ⋮ بزن» | «برای گزینه‌ها ⋮ رو بزن» |
| GroupScreen.kt ~202 | «ترک گروه» | «خروج از گروه» |
| GroupScreen.kt ~252 | «برداشتن مدیریت» / «مدیر کردن» | «دیگه مدیر نباشه» / «مدیرش کن» |
| GroupScreen.kt ~254 | «دیدن پروفایل» | «پروفایلش» |
| GroupScreen.kt ~291 | «دیگه وضعیت‌های «X» رو نمی‌بینه. با یه دعوت جدید می‌تونه برگرده.» | «دیگه وضعیت‌های «X» رو نمی‌بینه. با دعوت دوباره برمی‌گرده.» |
| GroupScreen.kt ~318 | «کد همیشه معتبره» | «کد همیشه کار می‌کنه» |
| GroupScreen.kt ~323 | «از یکی از مدیرهای گروه بخواه کد دعوت رو برات بفرسته.» | «از مدیر گروه بخواه کد رو برات بفرسته.» |
| GroupScreen.kt ~355 | «اشتراک لینک» | «فرستادن لینک» |
| GroupScreen.kt ~368 | «انقضای کد» | «اعتبار کد» |
| GroupScreen.kt ~384 | «کد جدید بساز» / «کد قبلی باطل می‌شه» | «کد جدید» / «کد قبلی دیگه کار نمی‌کنه» |
| AddSheet.kt ~117 | «کد دعوت گروه (۶ رقم) یا کد دونفره‌ی یه رفیق (۷ حرف)» | «کد ۶ رقمی گروه یا کد ۷ حرفی دونفره» |
| AddSheet.kt ~119 | «پیوستن» | «عضو شو» |
| AddSheet.kt ~128 | «ساختن گروه» | «بساز» |
| AddSheet.kt ~130 | «کد دونفره‌ی خودت توی پروفایله؛ بفرستش برای رفیقت.» | «کد دونفره‌ت توی پروفایله؛ بفرستش برای رفیقت.» |
| OnboardingScreens.kt ~163 | «کد ۶ رقمی‌ای که رفیقت فرستاده رو وارد کن.» | «کدی که رفیقت فرستاده رو بزن.» |
| OnboardingScreens.kt ~233 | «ورود به «X»» | «بریم تو «X»» |
| OnboardingScreens.kt ~247 | «این‌طوری روی صفحه‌ی رفقات دیده می‌شی.» | «رفقات این‌جوری می‌بیننت.» |
| OnboardingScreens.kt ~287 | «رفقا بعداً می‌تونن برات لقب بذارن. آماده باش.» | «رفقات بعداً می‌تونن برات لقب بذارن.» |
| ProfileScreens.kt ~236 | «برای یه فضای خصوصی با یه رفیق» | «برای یه فضای دونفره با یه رفیق» |
| ProfileScreens.kt ~368 | «ذخیره‌ی استایل» | «ذخیره» |
| ProfileScreens.kt ~471 | «با دو انگشت بزرگ کن · بکش تا جابه‌جا بشه · کاراکترت روش می‌شینه» | «با دو انگشت زوم کن و بکش تا جابه‌جا شه» |
If an OLD string is not found exactly, search for its closest match; if still not found, list it in the report as NOT FOUND (do not invent).

## Step 5 — Verify (paste real output tails in the report)
1. `JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ./gradlew assembleDebug -PFSL_API_URL=https://fsl-api.mohammadisina2001.workers.dev` → BUILD SUCCESSFUL.
2. `JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home tools/desktop-preview/gradlew -p tools/desktop-preview run` → exit 0, PNGs in `tools/desktop-preview/build/shots/`.
3. `grep -rn "رفقا لایو" app/src/main` → no output. For every OLD string in the Step 4 table: `grep -rn "<OLD>" app/src/main` → no output.
4. `git status --short` → no new files outside the write set.

## Report `logs/ds/T6a/REPORT.md`
Changed files with a one-line summary each, Step 4 table with FOUND/NOT FOUND per row, verification tails, anything you couldn't do. Honest.
