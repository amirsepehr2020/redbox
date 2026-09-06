package ir.redlighte.redbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        NoteEditorScreen(
            note = editing,
            onBack = { editing = null; creating = false },
            onSave = { saved ->
                notes = if (editing == null) notes + saved else notes.map { if (it.id == saved.id) saved else it }
                NotesStore.save(context, notes)
                editing = null
                creating = false
            }
        )
        return
    }

    val filtered = notes
        .filter { query.isBlank() || it.title.contains(query, true) || it.content.contains(query, true) }
        .sortedWith(compareByDescending<Note> { it.pinned }.thenByDescending { it.id })

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(28.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back") }
                Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                    Text("Notes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Private notes stored on this device.", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { creating = true }) { Icon(Icons.Rounded.Add, contentDescription = "New note", tint = NotesRed) }
            }
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                placeholder = { Text("Search notes") },
                shape = RoundedCornerShape(18.dp)
            )
            Spacer(Modifier.height(14.dp))
            if (filtered.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.EditNote, contentDescription = null, tint = NotesRed, modifier = Modifier.size(42.dp))
                        Spacer(Modifier.height(10.dp))
                        Text(if (query.isBlank()) "No notes yet" else "No matching notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(if (query.isBlank()) "Create your first note with +" else "Try another search", style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 28.dp)) {
                    items(filtered, key = { it.id }) { note ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest), onClick = { editing = note }) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text(note.title.ifBlank { "Untitled" }, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    if (note.pinned) Icon(Icons.Rounded.PushPin, contentDescription = "Pinned", tint = NotesRed, modifier = Modifier.size(18.dp))
                                    IconButton(onClick = {
                                        notes = notes.map { if (it.id == note.id) it.copy(pinned = !it.pinned) else it }
                                        NotesStore.save(context, notes)
                                    }) { Icon(if (note.pinned) Icons.Rounded.Star else Icons.Rounded.PushPin, contentDescription = "Pin", tint = NotesRed) }
                                    IconButton(onClick = {
                                        notes = notes.filterNot { it.id == note.id }
                                        NotesStore.save(context, notes)
                                    }) { Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete", tint = NotesRed) }
                                }
                                if (note.content.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(note.content, maxLines = 4, style = MaterialTheme.typography.bodyMedium)
                                }
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

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(28.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back") }
            Text(if (note == null) "New Note" else "Edit Note", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(start = 4.dp))
            TextButton(onClick = {
                val id = note?.id ?: System.currentTimeMillis()
                onSave(Note(id, title.trim(), content.trim(), pinned))
            }) { Text("Save", color = NotesRed, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Title") }, singleLine = true, shape = RoundedCornerShape(18.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = content, onValueChange = { content = it }, modifier = Modifier.fillMaxWidth().weight(1f), label = { Text("Note") }, shape = RoundedCornerShape(18.dp))
        Spacer(Modifier.height(10.dp))
        TextButton(onClick = { pinned = !pinned }) {
            Icon(Icons.Rounded.PushPin, contentDescription = null, tint = if (pinned) NotesRed else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.size(6.dp))
            Text(if (pinned) "Pinned" else "Pin note")
        }
        Spacer(Modifier.height(18.dp))
    }
}
