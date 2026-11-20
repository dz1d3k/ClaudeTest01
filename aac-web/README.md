# Moje Słowa – web version (iPhone + Android)

The same AAC app as `aac-app/`, as an installable web app (PWA). Works offline after the first visit.

- `vocabulary.json` – all categories and words. **Shared format**: a future Flutter app can load this file unchanged.
- `app.js` / `app.css` / `index.html` – the app.
- `sw.js` – offline cache. Bump `VERSION` in it after changing any file, so phones download the update.

Install: open the page in **Safari** (iPhone) or **Chrome** (Android) → Share / menu → **Add to Home Screen**.

Voice: the phone's own Polish voices. Choose one under "⚙️ Ustawienia głosu" at the bottom of the home screen.
On iPhone, better voices: Settings → Accessibility → Spoken Content → Voices → Polish → download an "Enhanced" voice.
