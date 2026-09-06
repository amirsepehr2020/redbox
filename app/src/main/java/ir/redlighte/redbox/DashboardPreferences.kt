package ir.redlighte.redbox

import android.content.Context

object DashboardPreferences {
    private const val PREFS = "redbox_dashboard"
    private const val FAVORITES = "favorites"
    private const val RECENTS = "recents_ordered"
    private const val LEGACY_RECENTS = "recents"

    fun favorites(context: Context): Set<String> = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getStringSet(FAVORITES, emptySet())?.toSet() ?: emptySet()

    fun toggleFavorite(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val next = favorites(context).toMutableSet()
        if (!next.add(id)) next.remove(id)
        prefs.edit().putStringSet(FAVORITES, next).apply()
    }

    fun recents(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val ordered = prefs.getString(RECENTS, null)
        if (ordered != null) return ordered.split('|').filter { it.isNotBlank() }
        val legacy = prefs.getStringSet(LEGACY_RECENTS, emptySet())?.toList().orEmpty()
        return legacy.mapNotNull(::normalize).distinct().take(6)
    }

    fun addRecent(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val normalized = normalize(id) ?: return
        val next = recents(context).toMutableList()
        next.remove(normalized)
        next.add(0, normalized)
        prefs.edit().putString(RECENTS, next.take(6).joinToString("|")).apply()
    }

    private fun normalize(value: String): String? = when (value.trim().lowercase()) {
        "calculator", "ماشین حساب" -> "calculator"
        "timer", "focus timer", "تایمر تمرکز" -> "timer"
        "gpa", "gpa calculator", "محاسبه معدل" -> "gpa"
        "notes", "یادداشت‌ها" -> "notes"
        "converter", "unit converter", "convertor", "cinvertor", "مبدل واحد" -> "converter"
        "more", "more tools", "ابزارهای بیشتر" -> "more"
        else -> null
    }
}
