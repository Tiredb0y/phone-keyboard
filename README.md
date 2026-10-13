# Phone Keyboard - اپ اندروید

اپ فقط یک WebView است که صفحهٔ کیبورد برنامهٔ کامپیوتر را باز می‌کند.

## ساخت APK
1. Android Studio را باز کن → Open → همین پوشه.
2. صبر کن Gradle sync شود.
3. Build → Build Bundle(s)/APK(s) → Build APK(s).
4. فایل `app/build/outputs/apk/debug/app-debug.apk` را بردار و
   کنار `phone_keyboard.py` بگذار با نام `phone_keyboard.apk`.

از آن به بعد `python phone_keyboard.py` اگر اپ روی گوشی نصب نبود خودش با adb نصبش می‌کند،
وگرنه فقط باز می‌کند.

(بدون Android Studio: با gradle نصب‌شده `gradle assembleDebug` هم کار می‌کند.)
