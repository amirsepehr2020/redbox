package ir.redlighte.redbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.size(28.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, "Back") }
            Text("About RedBox", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.size(28.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935))
        ) {
            Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("R", color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Black)
                Text("RedBox", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("Everything you need. One RedBox.", color = Color.White.copy(alpha = .9f))
            }
        }
        Spacer(Modifier.size(22.dp))
        Text("Simple. Useful. Red.", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(8.dp))
        Text(
            "RedBox is an offline-first toolbox for school, study and everyday life. Fast tools, simple design, and no unnecessary complexity.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.size(18.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Version", style = MaterialTheme.typography.labelLarge)
                Text("0.8.0", fontWeight = FontWeight.Bold)
                Text("A Redlighte product", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.size(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Favorite, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Text("  Made with care by Redlighte", style = MaterialTheme.typography.bodySmall)
        }
    }
}
