package ir.redlighte.redbox

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun MoreToolsScreen(onBack: () -> Unit) {
    var tool by remember { mutableStateOf("Percentage") }
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(28.dp))
        Row(Modifier.fillMaxWidth(), Alignment.CenterVertically) {
            IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, "Back") }
            Column {
                Text("More Tools", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Small tools, big time-savers.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(14.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                ToolChoiceRow(tool) { tool = it }
            }
            item {
                when (tool) {
                    "Percentage" -> PercentageTool()
                    "Discount" -> DiscountTool()
                    "Base Converter" -> BaseConverterTool()
                    "Age" -> AgeTool()
                    else -> CountdownTool()
                }
            }
        }
    }
}

@Composable
private fun ToolChoiceRow(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("Percentage", "Discount", "Base Converter", "Age", "Countdown")
    LazyColumn(Modifier.fillMaxWidth().height(170.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options.size) { index ->
            val option = options[index]
            Card(
                onClick = { onSelect(option) },
                Modifier.fillMaxWidth(),
                RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (selected == option) Color(0xFFE53935) else MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when (option) {
                            "Percentage" -> Icons.Rounded.Percent
                            "Discount" -> Icons.Rounded.Calculate
                            "Base Converter" -> Icons.Rounded.Tag
                            "Age" -> Icons.Rounded.Schedule
                            else -> Icons.Rounded.Schedule
                        },
                        null,
                        tint = if (selected == option) Color.White else Color(0xFFE53935)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(option, color = if (selected == option) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun PercentageTool() {
    var value by remember { mutableStateOf("") }
    var percent by remember { mutableStateOf("") }
    val result = value.toDoubleOrNull()?.let { v -> percent.toDoubleOrNull()?.let { p -> v * p / 100.0 } }
    ToolCard("Percentage Calculator", "What is X% of Y?", listOf(value to "Value", percent to "Percent") , listOf({ value = it }, { percent = it })) {
        Text(result?.let { format(it) } ?: "—", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color(0xFFE53935))
    }
}

@Composable
private fun DiscountTool() {
    var price by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    val saved = price.toDoubleOrNull()?.let { p -> discount.toDoubleOrNull()?.let { d -> p * d / 100.0 } }
    val final = price.toDoubleOrNull()?.let { p -> saved?.let { p - it } }
    ToolCard("Discount Calculator", "Calculate savings and final price.", listOf(price to "Original price", discount to "Discount %"), listOf({ price = it }, { discount = it })) {
        Text("You save: ${saved?.let(::format) ?: "—"}", fontWeight = FontWeight.Bold)
        Text("Final price: ${final?.let(::format) ?: "—"}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFFE53935))
    }
}

@Composable
private fun BaseConverterTool() {
    var input by remember { mutableStateOf("") }
    var from by remember { mutableStateOf("Decimal") }
    var to by remember { mutableStateOf("Binary") }
    val result = convertBase(input, from, to)
    ToolCard("Number Base Converter", "Decimal, binary and hexadecimal.", listOf(input to "Number"), listOf({ input = it })) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Decimal", "Binary", "Hex").forEach { base ->
                FilterChip(selected = from == base, onClick = { from = base }, label = { Text(base) })
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Decimal", "Binary", "Hex").forEach { base ->
                FilterChip(selected = to == base, onClick = { to = base }, label = { Text(base) })
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Result: ${result ?: "—"}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFFE53935))
    }
}

@Composable
private fun AgeTool() {
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    val age = calculateAge(year.toIntOrNull(), month.toIntOrNull(), day.toIntOrNull())
    ToolCard("Age Calculator", "Enter your birth date.", listOf(year to "Birth year", month to "Month", day to "Day"), listOf({ year = it }, { month = it }, { day = it })) {
        Text(age ?: "—", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFFE53935))
    }
}

@Composable
private fun CountdownTool() {
    var secondsInput by remember { mutableStateOf("60") }
    var remaining by remember { mutableIntStateOf(60) }
    var running by remember { mutableStateOf(false) }
    LaunchedEffect(running) {
        while (running && remaining > 0) {
            delay(1000)
            remaining--
        }
        if (remaining == 0) running = false
    }
    ToolCard("Countdown", "A simple in-app countdown.", listOf(secondsInput to "Seconds"), listOf({ secondsInput = it.filter(Char::isDigit).take(6) })) {
        Text(formatSeconds(remaining), fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color(0xFFE53935))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { remaining = secondsInput.toIntOrNull()?.coerceIn(1, 359999) ?: 60; running = true }, enabled = !running) { Text("Start") }
            OutlinedButton(onClick = { running = false; remaining = secondsInput.toIntOrNull()?.coerceIn(1, 359999) ?: 60 }) { Text("Reset") }
        }
    }
}

@Composable
private fun ToolCard(
    title: String,
    subtitle: String,
    fields: List<Pair<String, String>>,
    setters: List<(String) -> Unit>,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            fields.forEachIndexed { index, (value, label) ->
                OutlinedTextField(
                    value,
                    setters[index],
                    Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    label = { Text(label) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            content()
        }
    }
}

private fun format(value: Double): String = String.format(Locale.US, "%.4f", value).trimEnd('0').trimEnd('.')

private fun convertBase(input: String, from: String, to: String): String? = runCatching {
    if (input.isBlank()) return null
    val radix = when (from) { "Binary" -> 2; "Hex" -> 16; else -> 10 }
    val number = input.trim().removePrefix("0x").removePrefix("0X").toLong(radix)
    when (to) { "Binary" -> number.toString(2); "Hex" -> number.toString(16).uppercase(); else -> number.toString() }
}.getOrNull()

private fun calculateAge(year: Int?, month: Int?, day: Int?): String? = runCatching {
    if (year == null || month == null || day == null) return null
    val birth = java.time.LocalDate.of(year, month, day)
    val today = java.time.LocalDate.now()
    if (birth.isAfter(today)) return null
    val age = java.time.Period.between(birth, today)
    "${age.years} years, ${age.months} months, ${age.days} days"
}.getOrNull()

private fun formatSeconds(total: Int): String = String.format(Locale.US, "%02d:%02d", total / 60, total % 60)
