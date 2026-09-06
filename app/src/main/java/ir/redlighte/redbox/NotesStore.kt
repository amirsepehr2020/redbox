package ir.redlighte.redbox

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val pinned: Boolean = false
)

object NotesStore {
    private const val PREFS = "redbox_notes"
    private const val KEY = "notes"

    fun load(context: Context): List<Note> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    add(Note(o.getLong("id"), o.getString("title"), o.getString("content"), o.optBoolean("pinned")))
                }
            }
        }.getOrDefault(emptyList())
    }

    fun save(context: Context, notes: List<Note>) {
        val array = JSONArray()
        notes.forEach { note ->
            array.put(JSONObject().apply {
                put("id", note.id)
                put("title", note.title)
                put("content", note.content)
                put("pinned", note.pinned)
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, array.toString()).apply()
    }
}
