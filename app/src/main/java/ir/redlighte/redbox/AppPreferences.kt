package ir.redlighte.redbox

import android.content.Context

object AppPreferences {
    private const val PREFS = "redbox_preferences"
    private const val KEY_DARK_THEME = "dark_theme"

    fun isDarkTheme(context: Context): Boolean? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_DARK_THEME)) return null
        return prefs.getBoolean(KEY_DARK_THEME, false)
    }

    fun setDarkTheme(context: Context, dark: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_THEME, dark)
            .apply()
    }
}
