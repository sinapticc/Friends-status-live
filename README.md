# رفقا لایو (Friends Status Live)

اپ اندرویدی برای اینکه با یه لمس به رفقات بگی الان داری چی‌کار می‌کنی. پیاده‌سازی طراحی «App Screens v2» از Claude Design با Kotlin و Jetpack Compose، با سرور روی Cloudflare Workers و D1.

## چی داره

- **اپ:** آنبوردینگ، صفحه‌ی رفقا با گروه‌ها و فضای دونفره، انتخاب وضعیت (خصوصی، اضطراری، انقضا)، جزئیات رفیق با سقلمه و خنده، حریم خصوصی (حالت روح، توقف، دقت مکان، سطح اشتراک هر گروه)، تنظیمات گروه (مدیر، حذف عضو، کد دعوت با QR، تغییر نام)، پروفایل، ویرایشگر کاراکتر و برش عکس. فقط فارسی و راست‌به‌چپ، تم تیره و روشن.
- **سرور (`server/`):** حساب بدون ثبت‌نام، گروه‌ها و کد دعوت ۶ رقمی، کد دونفره‌ی ۷ حرفی، وضعیت‌ها با تاریخچه‌ی یک‌روزه، مخفی کردن وضعیت خصوصی از کسایی که نباید ببینن، فاصله‌ی تقریبی، واکنش‌ها.
- **وقتی اپ بسته‌ست:** ویجت صفحه‌ی اصلی (سه اندازه). هر وقت رفیقی وضعیت بذاره سرور با FCM گوشی رو بیدار می‌کنه تا ویجت به‌روز بشه؛ اگه FCM نرسید، اپ هر ۱۵ دقیقه خودش چک می‌کنه.
- **حالت نمایشی:** اگه آدرس سرور تنظیم نشده باشه، اپ با داده‌ی نمونه کار می‌کنه.

## راه‌اندازی (یک بار)

همه‌ی تنظیمات در GitHub: **Settings › Secrets and variables › Actions**.

### ۱. سرور Cloudflare

1. در [dash.cloudflare.com](https://dash.cloudflare.com) حساب بساز. در بخش **Workers & Pages** یک بار وارد شو تا زیردامنه‌ی `workers.dev` ساخته بشه.
2. **شناسه‌ی حساب:** در صفحه‌ی Workers & Pages، سمت راست «Account ID» رو کپی کن.
3. **توکن:** My Profile › API Tokens › Create Token › قالب **Edit Cloudflare Workers**. یه دسترسی دیگه هم اضافه کن: `Account › D1 › Edit`. توکن رو کپی کن.
4. در GitHub دو **Secret** بساز: `CLOUDFLARE_ACCOUNT_ID` و `CLOUDFLARE_API_TOKEN`.
5. در تب **Actions**، ورک‌فلوی **Server** رو اجرا کن (Run workflow). اولین اجرا پایگاه‌داده رو می‌سازه و سرور رو منتشر می‌کنه. آدرس سرور در خلاصه‌ی اجرا نوشته می‌شه (مثل `https://fsl-api.xxx.workers.dev`).
6. در GitHub یک **Variable** (نه Secret) به اسم `FSL_API_URL` با همون آدرس بساز.

### ۲. Firebase برای به‌روزرسانی فوری (اختیاری ولی پیشنهادی)

بدون این مرحله هم ویجت کار می‌کنه، فقط هر ۱۵ دقیقه به‌روز می‌شه.

1. در [console.firebase.google.com](https://console.firebase.google.com) پروژه بساز (Analytics لازم نیست).
2. یه اپ Android اضافه کن با package name: `com.sinapticc.friendsstatus` و فایل `google-services.json` رو دانلود کن. کل محتواش رو در Secret به اسم `GOOGLE_SERVICES_JSON` بذار.
3. Project settings › Service accounts › **Generate new private key**. کل محتوای فایل JSON رو در Secret به اسم `FCM_SERVICE_ACCOUNT` بذار.
4. ورک‌فلوی **Server** رو دوباره اجرا کن تا سرور کلید رو بگیره.

### ۳. ساخت اپ

ورک‌فلوی **Android build** رو اجرا کن. فایل APK در بخش Artifacts همون اجرا (`app-debug`) هست. هر push هم خودکار APK جدید می‌سازه.

### نکته برای کاربرای داخل ایران

- ممکنه دامنه‌های `workers.dev` روی بعضی اینترنت‌ها فیلتر باشن. اگه اپ به سرور وصل نشد، یه دامنه‌ی شخصی در Cloudflare به Worker وصل کن و `FSL_API_URL` رو به اون تغییر بده.
- کنسول Firebase از ایران معمولاً فقط با تغییر IP باز می‌شه. رسیدن پیام‌ها به گوشی‌ها بستگی به Google Play Services گوشی داره؛ اگه نرسه، چک ۱۵ دقیقه‌ای جایگزینش می‌شه.

## توسعه

```sh
# سرور، روی کامپیوتر خودت
cd server && npm install
npx wrangler d1 migrations apply fsl --local
npx wrangler dev            # http://127.0.0.1:8787
node test/api.test.mjs      # تست‌های API (در یه ترمینال دیگه)

# اپ، با سرور محلی (شبیه‌ساز اندروید: 10.0.2.2)
./gradlew assembleDebug -PFSL_API_URL=http://10.0.2.2:8787
```

`app/google-services.json` اگه باشه خودکار خونده می‌شه (در git نمیاد).

## ساختار کد

| مسیر | محتوا |
|---|---|
| `server/src/index.ts` | کل API: حساب، فید، وضعیت، گروه، دعوت، واکنش |
| `server/src/fcm.ts` | ارسال پیام بی‌صدا با FCM (بدون SDK) |
| `server/migrations/` | جدول‌های D1 |
| `app/…/data/AppStore.kt` | وضعیت اپ و همه‌ی کارها؛ با سرور یا در حالت نمایشی |
| `app/…/data/Api.kt` | کلاینت سرور |
| `app/…/android/` | FCM، همگام‌سازی پس‌زمینه و ویجت |
| `app/…/ui/` | صفحه‌ها و اجزای رابط |
| `tools/characters/` | ساخت تصویر کاراکترها از فایل‌های طراحی |

## کاراکترها

کاراکترها در طراحی با SVG و جاوااسکریپت ساخته شده‌اند و با `tools/characters/render.mjs` به WebP تبدیل می‌شن:

```sh
cd tools/characters && npm i playwright && node render.mjs ../../app/src/main/res/drawable-nodpi
```

## محدودیت‌های فعلی

- حساب به گوشی وصله؛ با پاک کردن اپ از دست می‌ره (پشتیبان‌گیری هنوز نیست).
- عکس پروفایل فقط روی گوشی خودت ذخیره می‌شه.
- سرور محدودیت تعداد درخواست نداره؛ قبل از انتشار عمومی اضافه بشه.

فونت‌های وزیرمتن و لاله‌زار تحت مجوز SIL Open Font License هستند.
