package ir.redlighte.redbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Red = Color(0xFFE53935)
private val RedDark = Color(0xFFB71C1C)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

@Composable
private fun RedBoxApp() {
    var screen by remember { mutableStateOf("home") }
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = Red, secondary = RedDark)) {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (screen == "calculator") CalculatorScreen(onBack = { screen = "home" })
            else HomeScreen(onToolClick = { if (it == "Calculator") screen = "calculator" })
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
            Text("v0.2.0", style = MaterialTheme.typography.labelMedium, color = Red)
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
private fun CalculatorScreen(onBack: () -> Unit) {
    var display by remember { mutableStateOf("0") }
    var stored by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }
    var fresh by remember { mutableStateOf(true) }

    fun input(value: String) {
        if (fresh || display == "0") { display = value; fresh = false }
        else display += value
    }
    fun clear() { display = "0"; stored = null; operation = null; fresh = true }
    fun calculate() {
        val left = stored ?: return
        val right = display.toDoubleOrNull() ?: return
        val result = when (operation) { "+" -> left + right; "−" -> left - right; "×" -> left * right; "÷" -> if (right == 0.0) null else left / right; else -> right }
        display = result?.let { formatNumber(it) } ?: "Error"
        stored = null; operation = null; fresh = true
    }
    fun setOperation(op: String) {
        val current = display.toDoubleOrNull() ?: return
        if (stored != null && operation != null && !fresh) calculate()
        stored = current; operation = op; fresh = true
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(28.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back") }
            Text("Calculator", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
        }
        Spacer(Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
            Text(display, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 30.dp), textAlign = TextAlign.End, fontSize = 40.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        Spacer(Modifier.height(18.dp))
        val rows = listOf(
            listOf("C", "÷", "×", "⌫"),
            listOf("7", "8", "9", "−"),
            listOf("4", "5", "6", "+"),
            listOf("1", "2", "3", "="),
            listOf("0", ".")
        )
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { key ->
                    CalculatorKey(key, modifier = Modifier.weight(if (key == "0") 2f else 1f)) {
                        when (key) {
                            "C" -> clear()
                            "⌫" -> if (!fresh && display.length > 1) display = display.dropLast(1) else if (!fresh) { display = "0"; fresh = true }
                            "." -> if (!display.contains('.')) input(".")
                            "+", "−", "×", "÷" -> setOperation(key)
                            "=" -> calculate()
                            else -> input(key)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorKey(key: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier.height(68.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (key in listOf("+", "−", "×", "÷", "=")) Red else MaterialTheme.colorScheme.surfaceContainerHighest)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(key, color = if (key in listOf("+", "−", "×", "÷", "=")) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun formatNumber(value: Double): String = if (value % 1.0 == 0.0) value.toLong().toString() else value.toString().trimEnd('0').trimEnd('.')
