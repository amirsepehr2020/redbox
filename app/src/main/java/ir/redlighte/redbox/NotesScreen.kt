package ir.redlighte.redbox

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val NotesRed = Color(0xFFE53935)

@Composable
fun NotesScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var notes by remember { mutableStateOf(NotesStore.load(context)) }
    var query by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<Note?>(null) }
    var creating by remember { mutableStateOf(false) }
    if (editing != null || creating) {
        NoteEditorScreen(editing, { editing = null; creating = false }, { saved ->
            notes = if (editing == null) notes + saved else notes.map { if (it.id == saved.id) saved else it }
            NotesStore.save(context, notes); editing = null; creating = false
        })
        return
    }
    val filtered = notes.filter { query.isBlank() || it.title.contains(query, true) || it.content.contains(query, true) }
        .sortedWith(compareByDescending<Note> { it.pinned }.thenByDescending { it.id })

    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(28.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowForward, "بازگشت") }
                Column(Modifier.weight(1f).padding(start = 4.dp)) {
                    Text("یادداشت‌ها", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("یادداشت‌های خصوصی روی همین دستگاه ذخیره می‌شوند.", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { creating = true }) { Icon(Icons.Rounded.Add, "یادداشت جدید", tint = NotesRed) }
            }
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Rounded.Search, null) }, placeholder = { Text("جستجوی یادداشت‌ها") }, shape = RoundedCornerShape(18.dp))
            Spacer(Modifier.height(14.dp))
            if (filtered.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                    Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.EditNote, null, tint = NotesRed, modifier = Modifier.size(42.dp))
                        Spacer(Modifier.height(10.dp))
                        Text(if (query.isBlank()) "هنوز یادداشتی نداری" else "یادداشتی پیدا نشد", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(if (query.isBlank()) "با دکمه + اولین یادداشتت را بساز" else "عبارت دیگری را امتحان کن", style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 28.dp)) {
                    items(filtered, key = { it.id }) { note ->
                        Card(onClick = { editing = note }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                            Column(Modifier.padding(16.dp)) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text(note.title.ifBlank { "بدون عنوان" }, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    if (note.pinned) Icon(Icons.Rounded.PushPin, null, tint = NotesRed, modifier = Modifier.size(18.dp))
                                    IconButton(onClick = { notes = notes.map { if (it.id == note.id) it.copy(pinned = !it.pinned) else it }; NotesStore.save(context, notes) }) { Icon(if (note.pinned) Icons.Rounded.Star else Icons.Rounded.PushPin, "سنجاق", tint = NotesRed) }
                                    IconButton(onClick = { notes = notes.filterNot { it.id == note.id }; NotesStore.save(context, notes) }) { Icon(Icons.Rounded.DeleteOutline, "حذف", tint = NotesRed) }
                                }
                                if (note.content.isNotBlank()) { Spacer(Modifier.height(6.dp)); Text(note.content, maxLines = 4, style = MaterialTheme.typography.bodyMedium) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteEditorScreen(note: Note?, onBack: () -> Unit, onSave: (Note) -> Unit) {
    var title by remember(note?.id) { mutableStateOf(note?.title ?: "") }
    var content by remember(note?.id) { mutableStateOf(note?.content ?: "") }
    var pinned by remember(note?.id) { mutableStateOf(note?.pinned ?: false) }
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(28.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowForward, "بازگشت") }
            Text(if (note == null) "یادداشت جدید" else "ویرایش یادداشت", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(start = 4.dp))
            TextButton(onClick = { onSave(Note(note?.id ?: System.currentTimeMillis(), title.trim(), content.trim(), pinned)) }) { Text("ذخیره", color = NotesRed, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("عنوان") }, singleLine = true, shape = RoundedCornerShape(18.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(content, { content = it }, Modifier.fillMaxWidth().weight(1f), label = { Text("متن یادداشت") }, shape = RoundedCornerShape(18.dp))
        Spacer(Modifier.height(10.dp))
        TextButton(onClick = { pinned = !pinned }) { Icon(Icons.Rounded.PushPin, null, tint = if (pinned) NotesRed else MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.width(6.dp)); Text(if (pinned) "سنجاق شده" else "سنجاق کردن") }
        Spacer(Modifier.height(18.dp))
    }
}
