package ir.redlighte.redbox

import android.content.Context

object DashboardPreferences {
    private const val PREFS = "redbox_dashboard"
    private const val FAVORITES = "favorites"
    private const val RECENTS = "recents"

    fun favorites(context: Context): Set<String> = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getStringSet(FAVORITES, emptySet())?.toSet() ?: emptySet()

    fun toggleFavorite(context: Context, title: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val next = favorites(context).toMutableSet()
        if (!next.add(title)) next.remove(title)
        prefs.edit().putStringSet(FAVORITES, next).apply()
    }

    fun recents(context: Context): List<String> = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getStringSet(RECENTS, emptySet())?.toList() ?: emptyList()

    fun addRecent(context: Context, title: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val next = recents(context).toMutableList()
        next.remove(title)
        next.add(0, title)
        prefs.edit().putStringSet(RECENTS, next.take(6).toSet()).apply()
    }
}