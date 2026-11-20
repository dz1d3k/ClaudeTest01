package pl.mojeslowa.app

data class Word(val emoji: String, val text: String)

data class Category(
    val id: String,
    val emoji: String,
    val name: String,
    val color: String,
    val words: List<Word>,
)

/**
 * All words the app can say. To add or change a word, edit this list:
 * the first value is the picture (emoji), the second is exactly what is spoken.
 * Phrases are chosen so they work for both boys and girls in Polish.
 */
object Vocabulary {

    val categories: List<Category> = listOf(
        Category(
            "potrzeby", "🙋", "Potrzeby", "#FFCDD2", listOf(
                Word("👍", "Tak"),
                Word("👎", "Nie"),
                Word("🆘", "Pomocy"),
                Word("🚽", "Toaleta"),
                Word("🍽️", "Chcę jeść"),
                Word("🥤", "Chcę pić"),
                Word("😴", "Chcę spać"),
                Word("🤕", "Boli mnie"),
                Word("➕", "Jeszcze"),
                Word("✋", "Koniec"),
                Word("⏸️", "Przerwa"),
                Word("🤗", "Przytul mnie"),
                Word("🤷", "Nie wiem"),
                Word("🙏", "Proszę"),
                Word("💛", "Dziękuję"),
            )
        ),
        Category(
            "uczucia", "😊", "Uczucia", "#FFF9C4", listOf(
                Word("😄", "Wesoło mi"),
                Word("😢", "Smutno mi"),
                Word("😠", "Złoszczę się"),
                Word("😨", "Boję się"),
                Word("😴", "Chce mi się spać"),
                Word("😐", "Nudzi mi się"),
                Word("🥶", "Zimno mi"),
                Word("🥵", "Gorąco mi"),
                Word("🤢", "Niedobrze mi"),
                Word("🙂", "Dobrze się czuję"),
                Word("😞", "Źle się czuję"),
                Word("🔊", "Za głośno"),
            )
        ),
        Category(
            "jedzenie", "🍽️", "Jedzenie", "#FFE0B2", listOf(
                Word("🍞", "Chleb"),
                Word("🥪", "Kanapka"),
                Word("🥣", "Płatki"),
                Word("🍲", "Zupa"),
                Word("🍝", "Makaron"),
                Word("🍕", "Pizza"),
                Word("🍗", "Kurczak"),
                Word("🥔", "Ziemniaki"),
                Word("🍚", "Ryż"),
                Word("🥚", "Jajko"),
                Word("🧀", "Ser"),
                Word("🍎", "Jabłko"),
                Word("🍌", "Banan"),
                Word("🍓", "Truskawki"),
                Word("🍇", "Winogrona"),
                Word("🥕", "Marchewka"),
                Word("🥒", "Ogórek"),
                Word("🍪", "Ciastko"),
                Word("🍦", "Lody"),
                Word("🍫", "Czekolada"),
            )
        ),
        Category(
            "picie", "🥤", "Picie", "#B3E5FC", listOf(
                Word("💧", "Woda"),
                Word("🥛", "Mleko"),
                Word("🧃", "Sok"),
                Word("🍵", "Herbata"),
                Word("☕", "Kakao"),
                Word("🥤", "Lemoniada"),
            )
        ),
        Category(
            "obowiazki", "🧹", "Obowiązki", "#C8E6C9", listOf(
                Word("🦷", "Mycie zębów"),
                Word("🧼", "Mycie rąk"),
                Word("🛁", "Kąpiel"),
                Word("👕", "Ubieranie się"),
                Word("🧹", "Sprzątanie"),
                Word("🧸", "Sprzątanie zabawek"),
                Word("🍽️", "Nakrywanie do stołu"),
                Word("🧽", "Zmywanie"),
                Word("🧺", "Pranie"),
                Word("🛏️", "Ścielenie łóżka"),
                Word("🗑️", "Wynoszenie śmieci"),
                Word("🌷", "Podlewanie kwiatów"),
                Word("🐕", "Karmienie zwierzaka"),
            )
        ),
        Category(
            "miejsca", "📍", "Miejsca", "#D1C4E9", listOf(
                Word("🏠", "Dom"),
                Word("🏫", "Szkoła"),
                Word("🧸", "Przedszkole"),
                Word("🌳", "Park"),
                Word("🤸", "Plac zabaw"),
                Word("🛒", "Sklep"),
                Word("🏥", "Lekarz"),
                Word("🏊", "Basen"),
                Word("🌲", "Las"),
                Word("🏖️", "Plaża"),
                Word("🎬", "Kino"),
                Word("👵", "Do babci"),
                Word("🚗", "Samochód"),
                Word("🚌", "Autobus"),
                Word("🛏️", "Mój pokój"),
                Word("🚿", "Łazienka"),
                Word("🍳", "Kuchnia"),
                Word("🌻", "Ogród"),
            )
        ),
        Category(
            "osoby", "👪", "Osoby", "#F8BBD0", listOf(
                Word("👩", "Mama"),
                Word("👨", "Tata"),
                Word("👧", "Siostra"),
                Word("👦", "Brat"),
                Word("👵", "Babcia"),
                Word("👴", "Dziadek"),
                Word("👩‍🦰", "Ciocia"),
                Word("🧔", "Wujek"),
                Word("👩‍🏫", "Pani"),
                Word("👨‍🏫", "Pan"),
                Word("🧑‍⚕️", "Lekarz"),
                Word("🧒", "Kolega"),
                Word("👧", "Koleżanka"),
                Word("🙋", "Ja"),
                Word("👉", "Ty"),
            )
        ),
        Category(
            "zabawa", "🎨", "Zabawa", "#FFCCBC", listOf(
                Word("🖍️", "Rysowanie"),
                Word("🎨", "Malowanie"),
                Word("🧱", "Klocki"),
                Word("🧩", "Puzzle"),
                Word("📖", "Książka"),
                Word("📺", "Bajka"),
                Word("📱", "Tablet"),
                Word("⚽", "Piłka"),
                Word("🚲", "Rower"),
                Word("🚶", "Spacer"),
                Word("🎵", "Muzyka"),
                Word("💃", "Taniec"),
                Word("🧸", "Pluszak"),
                Word("🚂", "Pociąg"),
                Word("🏊", "Pływanie"),
                Word("🎈", "Balony"),
            )
        ),
        Category(
            "ubrania", "👕", "Ubrania", "#DCEDC8", listOf(
                Word("👕", "Koszulka"),
                Word("👖", "Spodnie"),
                Word("👗", "Sukienka"),
                Word("🧥", "Kurtka"),
                Word("🧢", "Czapka"),
                Word("🧣", "Szalik"),
                Word("🧤", "Rękawiczki"),
                Word("🧦", "Skarpetki"),
                Word("👟", "Buty"),
                Word("🩳", "Krótkie spodenki"),
                Word("🌙", "Piżama"),
            )
        ),
        Category(
            "cialo", "🧍", "Ciało", "#FFECB3", listOf(
                Word("👤", "Głowa"),
                Word("🧍", "Brzuch"),
                Word("🦷", "Ząb"),
                Word("👂", "Ucho"),
                Word("👁️", "Oko"),
                Word("👃", "Nos"),
                Word("👄", "Buzia"),
                Word("🗣️", "Gardło"),
                Word("✋", "Ręka"),
                Word("🦵", "Noga"),
                Word("🦶", "Stopa"),
                Word("🚶", "Plecy"),
            )
        ),
        Category(
            "zwierzeta", "🐾", "Zwierzęta", "#B2DFDB", listOf(
                Word("🐶", "Pies"),
                Word("🐱", "Kot"),
                Word("🐰", "Królik"),
                Word("🐟", "Rybka"),
                Word("🐦", "Ptak"),
                Word("🐴", "Koń"),
                Word("🐮", "Krowa"),
                Word("🐷", "Świnka"),
                Word("🐔", "Kura"),
                Word("🦆", "Kaczka"),
                Word("🐻", "Miś"),
                Word("🦁", "Lew"),
            )
        ),
    )

    fun category(id: String): Category? = categories.firstOrNull { it.id == id }
}
