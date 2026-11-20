package pl.mojeslowa.app

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private companion object {
        const val SCREEN_HOME = "home"
        const val SCREEN_FAVS = "favs"
        const val KEY_SCREEN = "screen"
        const val PREFS = "moje_slowa"
        const val KEY_FAVS = "favourites"
        const val BACKGROUND = "#FFF8E7"
        const val TEXT_DARK = "#212121"
    }

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var pendingSpeech: String? = null

    private lateinit var prefs: SharedPreferences
    private lateinit var titleView: TextView
    private lateinit var spokenView: TextView
    private lateinit var scroll: ScrollView
    private lateinit var list: LinearLayout
    private lateinit var backButton: TextView
    private lateinit var homeButton: TextView

    /** "home", "favs", or a category id. */
    private var screen = SCREEN_HOME

    // ---------------------------------------------------------------- lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        setContentView(buildLayout())
        setUpSystemBars()

        tts = TextToSpeech(this, this)

        screen = savedInstanceState?.getString(KEY_SCREEN) ?: SCREEN_HOME
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SCREEN, screen)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }

    @Deprecated("Still delivered because the manifest does not opt in to predictive back.")
    override fun onBackPressed() {
        // On the home screen the system Back does nothing, so a child
        // cannot leave the app by accident. Adults can use the Home gesture.
        if (screen != SCREEN_HOME) goHome()
    }

    // ---------------------------------------------------------------- speech

    override fun onInit(status: Int) {
        val engine = tts ?: return
        if (status != TextToSpeech.SUCCESS) {
            showMessage(
                "Brak syntezatora mowy",
                "Zainstaluj „Mowa Google” (Speech Services by Google) ze Sklepu Play."
            )
            return
        }
        val result = engine.setLanguage(Locale.forLanguageTag("pl-PL"))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            AlertDialog.Builder(this)
                .setTitle("Brak polskiego głosu")
                .setMessage("Telefon nie ma polskiego głosu. Pobierz go w ustawieniach mowy.")
                .setPositiveButton("Pobierz") { _, _ -> openVoiceInstaller() }
                .setNegativeButton("Później", null)
                .show()
            return
        }
        engine.setSpeechRate(0.9f)
        ttsReady = true
        pendingSpeech?.let { speak(it) }
        pendingSpeech = null
    }

    private fun speak(text: String) {
        spokenView.text = text
        val engine = tts
        if (!ttsReady || engine == null) {
            pendingSpeech = text
            return
        }
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "say")
    }

    private fun openVoiceInstaller() {
        try {
            startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))
        } catch (e: ActivityNotFoundException) {
            showMessage("Nie udało się", "Otwórz Ustawienia → Zamiana tekstu na mowę i pobierz język polski.")
        }
    }

    // ---------------------------------------------------------------- navigation

    private fun goHome() {
        screen = SCREEN_HOME
        render()
    }

    private fun openCategory(category: Category) {
        speak(category.name)
        screen = category.id
        render()
    }

    private fun openFavourites() {
        speak("Ulubione")
        screen = SCREEN_FAVS
        render()
    }

    private fun render() {
        list.removeAllViews()
        val category = Vocabulary.category(screen)
        when {
            category != null -> {
                titleView.text = "${category.emoji} ${category.name}"
                category.words.forEach { word ->
                    list.addView(wordTile(category, word))
                }
            }
            screen == SCREEN_FAVS -> {
                titleView.text = "⭐ Ulubione"
                val favourites = favouriteWords()
                if (favourites.isEmpty()) {
                    list.addView(hint("Brak ulubionych.\nDotknij ☆ przy słowie, aby je dodać."))
                }
                favourites.forEach { (cat, word) -> list.addView(wordTile(cat, word)) }
            }
            else -> {
                screen = SCREEN_HOME
                titleView.text = "Moje Słowa"
                Vocabulary.categories.forEach { list.addView(categoryTile(it)) }
            }
        }
        val atHome = screen == SCREEN_HOME
        backButton.alpha = if (atHome) 0.3f else 1f
        homeButton.alpha = if (atHome) 0.3f else 1f
        scroll.scrollTo(0, 0)
    }

    // ---------------------------------------------------------------- favourites

    private fun favouriteKey(category: Category, word: Word) = "${category.id}|${word.text}"

    private fun favouriteKeys(): Set<String> = prefs.getStringSet(KEY_FAVS, emptySet()) ?: emptySet()

    private fun isFavourite(category: Category, word: Word) =
        favouriteKey(category, word) in favouriteKeys()

    private fun toggleFavourite(category: Category, word: Word) {
        val keys = favouriteKeys().toMutableSet()
        val key = favouriteKey(category, word)
        if (!keys.remove(key)) keys.add(key)
        prefs.edit().putStringSet(KEY_FAVS, keys).apply()
    }

    /** Favourites in the same order as the vocabulary, so the list stays stable. */
    private fun favouriteWords(): List<Pair<Category, Word>> {
        val keys = favouriteKeys()
        return Vocabulary.categories.flatMap { cat ->
            cat.words.filter { favouriteKey(cat, it) in keys }.map { cat to it }
        }
    }

    // ---------------------------------------------------------------- views

    private fun buildLayout(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(BACKGROUND))
        }

        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        backButton = barButton("⬅️", "Wstecz") { if (screen != SCREEN_HOME) goHome() }
        homeButton = barButton("🏠", "Start") { goHome() }
        titleView = TextView(this).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor(TEXT_DARK))
            gravity = Gravity.CENTER
            maxLines = 1
        }
        val favButton = barButton("⭐", "Ulubione") { openFavourites() }
        bar.addView(backButton)
        bar.addView(homeButton)
        bar.addView(titleView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        bar.addView(favButton)
        root.addView(bar)

        // Shows the last spoken word; tap it to say it again.
        spokenView = TextView(this).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor(TEXT_DARK))
            gravity = Gravity.CENTER
            minHeight = dp(64)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = rounded("#FFFFFF", 20, stroke = "#FFB74D")
            hint = "🔊"
            contentDescription = "Powtórz"
            setOnClickListener { if (text.isNotEmpty()) speak(text.toString()) }
        }
        root.addView(spokenView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(dp(12), 0, dp(12), dp(8)) })

        list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(4), dp(12), dp(24))
        }
        scroll = ScrollView(this).apply { addView(list) }
        root.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        return root
    }

    private fun barButton(emoji: String, description: String, onClick: () -> Unit) =
        TextView(this).apply {
            text = emoji
            contentDescription = description
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            gravity = Gravity.CENTER
            background = rounded("#FFFFFF", 18, stroke = "#BDBDBD")
            layoutParams = LinearLayout.LayoutParams(dp(68), dp(68)).apply {
                setMargins(dp(4), 0, dp(4), 0)
            }
            setOnClickListener { onClick() }
        }

    private fun categoryTile(category: Category): View =
        tile(category.color, category.emoji, category.name, star = null) { openCategory(category) }

    private fun wordTile(category: Category, word: Word): View {
        val star = TextView(this).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
            gravity = Gravity.CENTER
            setPadding(dp(16), 0, dp(8), 0)
            fun refresh() {
                val fav = isFavourite(category, word)
                text = if (fav) "★" else "☆"
                setTextColor(Color.parseColor(if (fav) "#F9A825" else "#9E9E9E"))
                contentDescription = if (fav) "Usuń z ulubionych" else "Dodaj do ulubionych"
            }
            refresh()
            setOnClickListener {
                toggleFavourite(category, word)
                if (screen == SCREEN_FAVS) render() else refresh()
            }
        }
        return tile(category.color, word.emoji, word.text, star) { speak(word.text) }
    }

    private fun tile(color: String, emoji: String, label: String, star: View?, onClick: () -> Unit): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(96)
            setPadding(dp(16), dp(10), dp(12), dp(10))
            background = rounded(color, 24)
            isClickable = true
            isFocusable = true
            contentDescription = label
            setOnClickListener { onClick() }
        }
        row.addView(TextView(this).apply {
            text = emoji
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            gravity = Gravity.CENTER
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }, LinearLayout.LayoutParams(dp(80), ViewGroup.LayoutParams.WRAP_CONTENT))
        row.addView(TextView(this).apply {
            text = label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor(TEXT_DARK))
            setPadding(dp(12), 0, 0, 0)
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        if (star != null) row.addView(star)

        row.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, dp(6), 0, dp(6)) }
        return row
    }

    private fun hint(message: String) = TextView(this).apply {
        text = message
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        setTextColor(Color.parseColor("#616161"))
        gravity = Gravity.CENTER
        setPadding(dp(16), dp(48), dp(16), dp(48))
    }

    private fun rounded(fill: String, radiusDp: Int, stroke: String? = null) = GradientDrawable().apply {
        setColor(Color.parseColor(fill))
        cornerRadius = dp(radiusDp).toFloat()
        if (stroke != null) setStroke(dp(2), Color.parseColor(stroke))
    }

    private fun showMessage(title: String, message: String) {
        AlertDialog.Builder(this).setTitle(title).setMessage(message)
            .setPositiveButton("OK", null).show()
    }

    /** Android 15 draws apps edge-to-edge, so keep content clear of the system bars. */
    private fun setUpSystemBars() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
        val content = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        content.setOnApplyWindowInsetsListener { view, insets ->
            val bars = insets.getInsets(WindowInsets.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
