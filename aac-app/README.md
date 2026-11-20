# Moje Słowa – simple Polish AAC app (Android)

Big picture tiles that speak Polish out loud, for children who don't speak.

- Home screen: categories (Potrzeby, Uczucia, Jedzenie, Picie, Obowiązki, Miejsca, Osoby, Zabawa, Ubrania, Ciało, Zwierzęta)
- Tap a category → it says the name (e.g. "Jedzenie") and shows the words
- Tap a word → it is spoken; the big bar at the top shows it, tap the bar to repeat
- ☆ next to a word adds it to favourites, ⭐ in the top bar opens favourites
- ⬅️ back, 🏠 home. The system Back button does nothing on the home screen, so a child can't exit by accident.

Voice: the phone's built-in text-to-speech (free, works offline once the Polish voice is downloaded).

## Install on the phone (no Play Store)

1. On GitHub open **Actions → Build Moje Słowa (Android)** → newest green run → download **moje-slowa-apk** (a .zip).
2. Unzip it on the phone and tap the `.apk` file. Allow "install unknown apps" for your browser/file manager when Android asks.
3. If it says there is no Polish voice: Settings → Text-to-speech → Google speech engine → install Polish.

## Change the words

Edit `app/src/main/java/pl/mojeslowa/app/Vocabulary.kt` – each line is `Word("emoji", "spoken text")`. Push, and GitHub builds a new APK.
Updates install over the old app and keep favourites (the build uses a fixed signing key in `app/debug.keystore`, sideloading only – a Play Store release needs a private key).
