package ir.redlighte.redbox

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider

private val LauncherRed = Color(0xFFE53935)
private data class LauncherTool(val id: String, val title: String, val subtitle: String, val icon: ImageVector)
private val launcherTools = listOf(
    LauncherTool("calculator", "ماشین حساب", "محاسبات سریع", Icons.Rounded.Calculate),
    LauncherTool("timer", "تایمر تمرکز", "پومودورو و تمرکز", Icons.Rounded.Schedule),
    LauncherTool("gpa", "محاسبه معدل", "معدل درسی", Icons.Rounded.Checklist),
    LauncherTool("notes", "یادداشت‌ها", "یادداشت‌های شخصی", Icons.Rounded.EditNote),
    LauncherTool("converter", "مبدل واحد", "تبدیل واحدها", Icons.Rounded.SwapHoriz),
    LauncherTool("more", "ابزارهای بیشتر", "ابزارهای کاربردی", Icons.Rounded.Construction)
)

class LauncherActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        setContent { LauncherApp() }
    }
}

@Composable
private fun LauncherApp() {
    val context = LocalContext.current
    val savedTheme = AppPreferences.isDarkTheme(context)
    val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
    var darkTheme by remember(savedTheme, systemDark) { mutableStateOf(savedTheme ?: systemDark) }
    var screen by remember { mutableStateOf("home") }
    RedBoxTheme(darkTheme) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                AnimatedContent(screen, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "nav") { current ->
                    when (current) {
                        "settings" -> SettingsScreen(darkTheme, { darkTheme = it; AppPreferences.setDarkTheme(context, it) }) { screen = "home" }
                        "about" -> AboutScreen { screen = "home" }
                        else -> LauncherHome({ tool -> DashboardPreferences.addRecent(context, tool.id); screen = "home" }, { screen = "settings" }, { screen = "about" })
                    }
                }
            }
        }
    }
}

@Composable
private fun LauncherHome(onOpen: (LauncherTool) -> Unit, onSettings: () -> Unit, onAbout: () -> Unit) {
    val context = LocalContext.current
    var favorites by remember { mutableStateOf(DashboardPreferences.favorites(context)) }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.redbox_logo), "لوگوی ردباکس", Modifier.size(50.dp))
            Column(Modifier.weight(1f).padding(start = 12.dp)) { Text("RedBox", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black); Text("ساده. کاربردی. قرمز.", style = MaterialTheme.typography.bodySmall) }
            IconButton(onClick = onAbout) { Icon(Icons.Rounded.Info, "درباره", tint = LauncherRed) }
            IconButton(onClick = onSettings) { Icon(Icons.Rounded.Settings, "تنظیمات", tint = LauncherRed) }
        }
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = LauncherRed)) { Column(Modifier.padding(22.dp)) { Text("هر چیزی که نیاز داری.", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black); Text("همه در یک ردباکس.", color = Color.White.copy(.9f)) } }
        Spacer(Modifier.height(18.dp)); Text("همه ابزارها", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(10.dp))
        LazyVerticalGrid(GridCells.Adaptive(155.dp), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(launcherTools) { tool ->
                Card(onClick = { onOpen(tool) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Icon(tool.icon, null, tint = LauncherRed, modifier = Modifier.size(30.dp)); Spacer(Modifier.weight(1f)); IconButton(onClick = { DashboardPreferences.toggleFavorite(context, tool.id); favorites = DashboardPreferences.favorites(context) }, modifier = Modifier.size(32.dp)) { Icon(if (tool.id in favorites) Icons.Rounded.Star else Icons.Rounded.StarBorder, "موردعلاقه", tint = if (tool.id in favorites) LauncherRed else MaterialTheme.colorScheme.onSurfaceVariant) } }
                        Spacer(Modifier.height(8.dp)); Text(tool.title, fontWeight = FontWeight.Bold); Text(tool.subtitle, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
