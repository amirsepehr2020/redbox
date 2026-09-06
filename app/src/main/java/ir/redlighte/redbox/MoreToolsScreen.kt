package ir.redlighte.redbox

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MoreToolsScreen(onBack: () -> Unit, onOpenConverter: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowForward, "بازگشت") }
            Text("ابزارهای بیشتر", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(18.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest), onClick = onOpenConverter) {
            Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.SwapHoriz, null, tint = Color(0xFFE53935), modifier = Modifier.size(34.dp))
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text("مبدل واحد", fontWeight = FontWeight.Bold)
                    Text("تبدیل طول، وزن، دما و سرعت", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
            Column(Modifier.padding(18.dp)) {
                Text("به‌زودی", fontWeight = FontWeight.Bold)
                Text("ابزارهای بیشتری در نسخه‌های بعدی به RedBox اضافه می‌شوند.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
