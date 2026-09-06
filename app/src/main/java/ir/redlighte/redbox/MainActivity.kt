package ir.redlighte.redbox

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

private val Red = Color(0xFFE53935)
private val RedDark = Color(0xFFB71C1C)
private const val PREFS = "redbox_timer"
private const val ALARM_REQUEST = 4103

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent { RedBoxApp() }
    }
}

private data class Tool(val title: String, val subtitle: String, val icon: ImageVector)

private val tools = listOf(
    Tool("Calculator", "Fast calculations", Icons.Rounded.Calculate),
    Tool("Focus Timer", "Pomodoro & focus", Icons.Rounded.Schedule),
    Tool("GPA Calculator", "Calculate your GPA", Icons.Rounded.Checklist),
    Tool("Notes", "Quick notes & lists", Icons.Rounded.EditNote),
    Tool("Converter", "Units & values", Icons.Rounded.SwapHoriz),
    Tool("More Tools", "Coming soon", Icons.Rounded.Construction)
)

private data class Subject(
    val id: Int,
    val name: String,
    val grade: String,
    val credits: String
)

@Composable
private fun RedBoxApp() {
    var screen by remember { mutableStateOf("home") }
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = Red, secondary = RedDark)) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (screen) {
                "calculator" -> CalculatorScreen(onBack = { screen = "home" })
                "timer" -> FocusTimerScreen(onBack = { screen = "home" })
                "gpa" -> GpaCalculatorScreen(onBack = { screen = "home" })
                else -> HomeScreen(onToolClick = {
                    screen = when (it) {
                        "Calculator" -> "calculator"
                        "Focus Timer" -> "timer"
                        "GPA Calculator" -> "gpa"
                        else -> "home"
                    }
                })
            }
        }
    }
}

@Composable
private fun HomeScreen(onToolClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(34.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(Red, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Text("R", color = Color.White, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("RedBox", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Everything you need. One RedBox.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(28.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Red)) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("Your toolbox, simplified.", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Useful tools for school, study and everyday life — all in one place.", color = Color.White.copy(alpha = .88f), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Quick Tools", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("v0.4.0", style = MaterialTheme.typography.labelMedium, color = Red)
        }
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { items(tools) { tool -> ToolCard(tool, onClick = { onToolClick(tool.title) }) } }
    }
}

@Composable
private fun ToolCard(tool: Tool, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(tool.icon, contentDescription = tool.title, tint = Red, modifier = Modifier.size(30.dp))
            Spacer(Modifier.height(18.dp))
            Text(tool.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(tool.subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun GpaCalculatorScreen(onBack: () -> Unit) {
    var subjects by remember {
        mutableStateOf(
            listOf(
                Subject(1, "Mathematics", "", "3"),
                Subject(2, "English", "", "2"),
                Subject(3, "Science", "", "3")
            )
        )
    }
    var nextId by remember { mutableStateOf(4) }
    var scale by remember { mutableStateOf(20) }

    fun updateSubject(id: Int, transform: (Subject) -> Subject) {
        subjects = subjects.map { if (it.id == id) transform(it) else it }
    }

    val validSubjects = subjects.mapNotNull { subject ->
        val grade = subject.grade.replace(',', '.').toDoubleOrNull()
        val credits = subject.credits.toDoubleOrNull()
        if (grade != null && credits != null && credits > 0.0 && grade >= 0.0 && grade <= scale) {
            grade to credits
        } else null
    }
    val totalCredits = validSubjects.sumOf { it.second }
    val weightedAverage = if (totalCredits > 0.0) validSubjects.sumOf { it.first * it.second } / totalCredits else null
    val result = weightedAverage?.let { if (scale == 20) it else it / 5.0 }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(28.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back") }
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text("GPA Calculator", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Weighted by course credits.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(18.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(20, 4).forEach { value ->
                Card(
                    onClick = { scale = value },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = if (scale == value) Red else MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                        Text("${value}-point scale", color = if (scale == value) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Red)) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Your GPA", color = Color.White.copy(alpha = .85f), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                Text(result?.let { String.format(Locale.US, "%.2f", it) } ?: "—", color = Color.White, fontSize = 46.sp, fontWeight = FontWeight.Black)
                Text("${validSubjects.size} valid course${if (validSubjects.size == 1) "" else "s"} • ${formatCredits(totalCredits)} credits", color = Color.White.copy(alpha = .88f), style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Courses", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = {
                subjects = subjects + Subject(nextId, "New course", "", "3")
                nextId += 1
            }) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(Modifier.size(4.dp))
                Text("Add course")
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(subjects.size) { index ->
                val subject = subjects[index]
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = subject.name,
                                onValueChange = { updateSubject(subject.id) { it.copy(name = it) } },
                                label = { Text("Course") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { if (subjects.size > 1) subjects = subjects.filterNot { it.id == subject.id } }) {
                                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete course", tint = Red)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = subject.grade,
                                onValueChange = { updateSubject(subject.id) { it.copy(grade = it) } },
                                label = { Text("Grade (0–$scale)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = subject.credits,
                                onValueChange = { updateSubject(subject.id) { it.copy(credits = it) } },
                                label = { Text("Credits") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FocusTimerScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    var mode by remember { mutableStateOf(prefs.getString("mode", "Focus") ?: "Focus") }
    var focusMinutes by remember { mutableStateOf(prefs.getInt("focus_minutes", 25)) }
    var breakMinutes by remember { mutableStateOf(prefs.getInt("break_minutes", 5)) }
    var customMinutesText by remember { mutableStateOf(prefs.getInt("custom_minutes", 30).toString()) }
    var running by remember { mutableStateOf(prefs.getBoolean("running", false)) }
    var remainingMs by remember { mutableLongStateOf(loadRemaining(prefs)) }

    val selectedMinutes = when (mode) {
        "Focus" -> focusMinutes
        "Break" -> breakMinutes
        else -> customMinutesText.toIntOrNull()?.coerceIn(1, 180) ?: 30
    }

    fun saveState() {
        prefs.edit().putString("mode", mode).putInt("focus_minutes", focusMinutes).putInt("break_minutes", breakMinutes)
            .putInt("custom_minutes", customMinutesText.toIntOrNull()?.coerceIn(1, 180) ?: 30).putBoolean("running", running).apply()
    }

    fun start() {
        val duration = if (remainingMs > 0) remainingMs else selectedMinutes * 60_000L
        val endAt = System.currentTimeMillis() + duration
        remainingMs = duration
        running = true
        prefs.edit().putLong("end_at", endAt).putBoolean("running", true).apply()
        scheduleTimer(context, endAt, mode)
    }

    fun pause() {
        remainingMs = loadRemaining(prefs)
        running = false
        prefs.edit().putLong("remaining_ms", remainingMs).putBoolean("running", false).remove("end_at").apply()
        cancelTimer(context)
    }

    fun reset() {
        running = false
        remainingMs = selectedMinutes * 60_000L
        prefs.edit().putLong("remaining_ms", remainingMs).putBoolean("running", false).remove("end_at").apply()
        cancelTimer(context)
    }

    LaunchedEffect(running) {
        while (running) {
            val left = loadRemaining(prefs)
            remainingMs = left
            if (left <= 0L) {
                running = false
                mode = if (mode == "Focus") "Break" else "Focus"
                remainingMs = 0L
                prefs.edit().putBoolean("running", false).putString("mode", mode).remove("end_at").apply()
                break
            }
            delay(250L)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(28.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back") }
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text("Focus Timer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Stay focused. One session at a time.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Focus", "Break", "Custom").forEach { item ->
                val selected = mode == item
                Card(onClick = {
                    if (!running) {
                        mode = item
                        remainingMs = when (item) {
                            "Focus" -> focusMinutes * 60_000L
                            "Break" -> breakMinutes * 60_000L
                            else -> (customMinutesText.toIntOrNull()?.coerceIn(1, 180) ?: 30) * 60_000L
                        }
                        saveState()
                    }
                }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (selected) Red else MaterialTheme.colorScheme.surfaceContainerHighest)) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Text(item, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        if (mode == "Custom") {
            OutlinedTextField(value = customMinutesText, onValueChange = { customMinutesText = it.filter(Char::isDigit).take(3) }, label = { Text("Custom duration (minutes)") }, singleLine = true, enabled = !running, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (running) mode else "$mode session", color = Red, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text(formatTime(remainingMs), fontSize = 64.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(Modifier.height(8.dp))
                Text(if (running) "Timer is running in the background too" else "Ready when you are", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(20.dp))
        if (mode == "Focus" && !running) DurationPresets(focusMinutes) { focusMinutes = it; remainingMs = it * 60_000L; saveState() }
        else if (mode == "Break" && !running) DurationPresets(breakMinutes) { breakMinutes = it; remainingMs = it * 60_000L; saveState() }
        Spacer(Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { if (running) pause() else start() }, modifier = Modifier.weight(1f).height(58.dp), shape = RoundedCornerShape(20.dp)) {
                Icon(if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(if (running) "Pause" else "Start", fontWeight = FontWeight.Bold)
            }
            Card(onClick = { reset() }, modifier = Modifier.size(58.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Icon(Icons.Rounded.RestartAlt, contentDescription = "Reset", tint = Red) }
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { mode = "Focus"; focusMinutes = 25; breakMinutes = 5; customMinutesText = "30"; reset() }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Restore 25 / 5 Pomodoro") }
    }
}

@Composable
private fun DurationPresets(selected: Int, onSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(15, 25, 50).forEach { minutes ->
            Card(onClick = { onSelected(minutes) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (selected == minutes) Red.copy(alpha = .14f) else MaterialTheme.colorScheme.surfaceContainerHighest)) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp), contentAlignment = Alignment.Center) { Text("${minutes}m", color = if (selected == minutes) Red else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms.coerceAtLeast(0L) + 999L) / 1000L
    return String.format(Locale.US, "%02d:%02d", totalSeconds / 60L, totalSeconds % 60L)
}

private fun loadRemaining(prefs: android.content.SharedPreferences): Long {
    if (prefs.getBoolean("running", false)) return (prefs.getLong("end_at", 0L) - System.currentTimeMillis()).coerceAtLeast(0L)
    return prefs.getLong("remaining_ms", 0L)
}

private fun scheduleTimer(context: Context, endAt: Long, mode: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, FocusTimerReceiver::class.java).putExtra("mode", mode)
    val pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endAt, pendingIntent)
}

private fun cancelTimer(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, FocusTimerReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    alarmManager.cancel(pendingIntent)
}

@Composable
private fun CalculatorScreen(onBack: () -> Unit) {
    var display by remember { mutableStateOf("0") }
    var stored by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }
    var fresh by remember { mutableStateOf(true) }
    fun input(value: String) { if (fresh || display == "0") { display = value; fresh = false } else display += value }
    fun clear() { display = "0"; stored = null; operation = null; fresh = true }
    fun calculate() {
        val left = stored ?: return
        val right = display.toDoubleOrNull() ?: return
        val result = when (operation) { "+" -> left + right; "−" -> left - right; "×" -> left * right; "÷" -> if (right == 0.0) null else left / right; else -> right }
        display = result?.let { formatNumber(it) } ?: "Error"; stored = null; operation = null; fresh = true
    }
    fun setOperation(op: String) { val current = display.toDoubleOrNull() ?: return; if (stored != null && operation != null && !fresh) calculate(); stored = current; operation = op; fresh = true }
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(28.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back") }; Text("Calculator", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp)) }
        Spacer(Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) { Text(display, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 30.dp), textAlign = TextAlign.End, fontSize = 40.sp, fontWeight = FontWeight.SemiBold, maxLines = 1) }
        Spacer(Modifier.height(18.dp))
        val rows = listOf(listOf("C", "÷", "×", "⌫"), listOf("7", "8", "9", "−"), listOf("4", "5", "6", "+"), listOf("1", "2", "3", "="), listOf("0", "."))
        rows.forEach { row -> Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { row.forEach { key -> CalculatorKey(key, Modifier.weight(if (key == "0") 2f else 1f)) { when (key) { "C" -> clear(); "⌫" -> if (!fresh && display.length > 1) display = display.dropLast(1) else if (!fresh) { display = "0"; fresh = true }; "." -> if (!display.contains('.')) input("."); "+", "−", "×", "÷" -> setOperation(key); "=" -> calculate(); else -> input(key) } } } } }
    }
}

@Composable
private fun CalculatorKey(key: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val accent = key in listOf("+", "−", "×", "÷", "=")
    Card(onClick = onClick, modifier = modifier.height(68.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (accent) Red else MaterialTheme.colorScheme.surfaceContainerHighest)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text(key, color = if (accent) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 22.sp, fontWeight = FontWeight.SemiBold) }
    }
}

private fun formatCredits(value: Double): String = if (value % 1.0 == 0.0) value.toInt().toString() else String.format(Locale.US, "%.1f", value)

private fun formatNumber(value: Double): String = if (value % 1.0 == 0.0) value.toLong().toString() else value.toString().trimEnd('0').trimEnd('.')
