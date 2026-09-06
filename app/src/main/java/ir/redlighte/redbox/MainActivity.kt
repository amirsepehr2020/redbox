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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.toLayoutDirection
import kotlinx.coroutines.delay
import java.util.Locale

private val Red = Color(0xFFE53935)
private data class Tool(val id: String, val title: String, val subtitle: String, val icon: ImageVector)
private data class Subject(val id: Int, val name: String, val grade: String, val credits: String)

private val tools = listOf(
    Tool("calculator", "ماشین حساب", "محاسبات سریع", Icons.Rounded.Calculate),
    Tool("timer", "تایمر تمرکز", "پومودورو و تمرکز", Icons.Rounded.Schedule),
    Tool("gpa", "محاسبه معدل", "محاسبه معدل درسی", Icons.Rounded.Checklist),
    Tool("notes", "یادداشت‌ها", "یادداشت و فهرست سریع", Icons.Rounded.EditNote),
    Tool("converter", "مبدل واحد", "تبدیل واحدها و مقادیر", Icons.Rounded.SwapHoriz),
    Tool("more", "ابزارهای بیشتر", "۵ ابزار کاربردی", Icons.Rounded.Construction)
)

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        setContent { RedBoxApp() }
    }
}

@Composable private fun RedBoxApp() {
    val context = LocalContext.current
    val savedTheme = AppPreferences.isDarkTheme(context)
    val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
    var darkTheme by remember(savedTheme, systemDark) { mutableStateOf(savedTheme ?: systemDark) }
    var screen by remember { mutableStateOf("home") }
    RedBoxTheme(darkTheme) {
        CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                AnimatedContent(screen, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "navigation") { current ->
                    when (current) {
                        "calculator" -> CalculatorScreen { screen = "home" }
                        "timer" -> FocusTimerScreen { screen = "home" }
                        "gpa" -> GpaCalculatorScreen { screen = "home" }
                        "notes" -> NotesScreen { screen = "home" }
                        "converter" -> UnitConverterScreen { screen = "home" }
                        "more" -> MoreToolsScreen { screen = "home" }
                        "settings" -> SettingsScreen(darkTheme, { darkTheme = it; AppPreferences.setDarkTheme(context, it) }) { screen = "home" }
                        "about" -> AboutScreen { screen = "home" }
                        else -> HomeScreen({ tool -> DashboardPreferences.addRecent(context, tool.id); screen = tool.id }, { screen = "settings" }, { screen = "about" })
                    }
                }
            }
        }
    }
}

@Composable private fun HomeScreen(onTool: (Tool) -> Unit, onSettings: () -> Unit, onAbout: () -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var favorites by remember { mutableStateOf(DashboardPreferences.favorites(context)) }
    var recents by remember { mutableStateOf(DashboardPreferences.recents(context)) }
    val visible = tools.filter { query.isBlank() || it.title.contains(query, true) || it.subtitle.contains(query, true) }
    val favoriteTools = tools.filter { it.id in favorites }
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.redbox_logo), "لوگوی ردباکس", Modifier.size(48.dp))
            Column(Modifier.padding(start = 12.dp).weight(1f)) { Text("RedBox", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("ساده. کاربردی. قرمز.", style = MaterialTheme.typography.bodySmall) }
            IconButton(onAbout) { Icon(Icons.Rounded.Info, "درباره", tint = Red) }
            IconButton(onSettings) { Icon(Icons.Rounded.Settings, "تنظیمات", tint = Red) }
        }
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Red)) {
            Column(Modifier.padding(22.dp)) { Text("هر چیزی که نیاز داری.", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black); Text("همه در یک ردباکس.", color = Color.White.copy(.9f), style = MaterialTheme.typography.titleMedium) }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("جستجوی ابزارها") }, leadingIcon = { Icon(Icons.Rounded.Search, null) }, trailingIcon = { if (query.isNotBlank()) IconButton({ query = "" }) { Icon(Icons.Rounded.Close, "پاک کردن") } }, shape = RoundedCornerShape(18.dp))
        if (query.isBlank() && favoriteTools.isNotEmpty()) {
            Spacer(Modifier.height(18.dp)); SectionTitle("موردعلاقه‌ها", Icons.Rounded.Star)
            LazyVerticalGrid(GridCells.Adaptive(155.dp), Modifier.height(120.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(favoriteTools) { tool -> ToolCard(tool, true, { onTool(tool) }) { DashboardPreferences.toggleFavorite(context, tool.id); favorites = DashboardPreferences.favorites(context) } } }
        }
        if (query.isBlank() && recents.isNotEmpty()) {
            Spacer(Modifier.height(14.dp)); SectionTitle("اخیراً استفاده‌شده", Icons.Rounded.History)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                recents.take(3).mapNotNull { id -> tools.find { it.id == id } }.forEach { tool -> AssistChip(onClick = { onTool(tool) }, label = { Text(tool.title) }, leadingIcon = { Icon(tool.icon, null, Modifier.size(18.dp)) }, shape = RoundedCornerShape(18.dp)) }
            }
        }
        Spacer(Modifier.height(18.dp)); SectionTitle(if (query.isBlank()) "همه ابزارها" else "نتایج", Icons.Rounded.GridView)
        LazyVerticalGrid(GridCells.Adaptive(155.dp), Modifier.fillMaxWidth(), contentPadding = PaddingValues(bottom = 28.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { items(visible) { tool -> ToolCard(tool, tool.id in favorites, { onTool(tool) }) { DashboardPreferences.toggleFavorite(context, tool.id); favorites = DashboardPreferences.favorites(context); recents = DashboardPreferences.recents(context) } } }
    }
}

@Composable private fun SectionTitle(title: String, icon: ImageVector) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Red, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) } }

@Composable private fun ToolCard(tool: Tool, favorite: Boolean, onClick: () -> Unit, onFavorite: () -> Unit) { Card(onClick = onClick, Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) { Column(Modifier.padding(16.dp)) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Icon(tool.icon, null, tint = Red, Modifier.size(30.dp)); Spacer(Modifier.weight(1f)); IconButton(onFavorite, Modifier.size(32.dp)) { Icon(if (favorite) Icons.Rounded.Star else Icons.Rounded.StarBorder, "موردعلاقه", tint = if (favorite) Red else MaterialTheme.colorScheme.onSurfaceVariant) } }; Spacer(Modifier.height(8.dp)); Text(tool.title, fontWeight = FontWeight.Bold); Spacer(Modifier.height(3.dp)); Text(tool.subtitle, style = MaterialTheme.typography.bodySmall) } } }

@Composable private fun CalculatorScreen(onBack: () -> Unit) {
    var d by remember { mutableStateOf("0") }; var stored by remember { mutableStateOf<Double?>(null) }; var op by remember { mutableStateOf<String?>(null) }; var fresh by remember { mutableStateOf(true) }
    fun input(v: String) { d = if (fresh || d == "0") v else d + v; fresh = false }
    fun clear() { d = "0"; stored = null; op = null; fresh = true }
    fun calc() { val a = stored ?: return; val b = d.toDoubleOrNull() ?: return; val r = when (op) { "+" -> a + b; "−" -> a - b; "×" -> a * b; "÷" -> if (b == 0.0) null else a / b; else -> b }; d = r?.let(::formatNumber) ?: "خطا"; stored = null; op = null; fresh = true }
    fun setOp(o: String) { stored = d.toDoubleOrNull(); op = o; fresh = true }
    ToolPage("ماشین حساب", onBack) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) { Text(d, Modifier.fillMaxWidth().padding(22.dp), textAlign = TextAlign.End, fontSize = 40.sp, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(14.dp)); listOf(listOf("C","÷","×","⌫"), listOf("7","8","9","−"), listOf("4","5","6","+"), listOf("1","2","3","="), listOf("0",".")).forEach { row -> Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { row.forEach { k -> Card(onClick = { when (k) { "C" -> clear(); "⌫" -> if (!fresh && d.length > 1) d = d.dropLast(1); "." -> if (!d.contains('.')) input("."); "+","−","×","÷" -> setOp(k); "=" -> calc(); else -> input(k) } }, Modifier.weight(if (k == "0") 2f else 1f).height(62.dp), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (k in listOf("+","−","×","÷","=")) Red else MaterialTheme.colorScheme.surfaceContainerHighest)) { Box(Modifier.fillMaxSize(), Alignment.Center) { Text(k, color = if (k in listOf("+","−","×","÷","=")) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 20.sp) } } } } } }
}

@Composable private fun FocusTimerScreen(onBack: () -> Unit) { var minutes by remember { mutableIntStateOf(25) }; var remaining by remember { mutableLongStateOf(25 * 60000L) }; var running by remember { mutableStateOf(false) }; LaunchedEffect(running) { while (running) { delay(250); remaining = (remaining - 250).coerceAtLeast(0); if (remaining == 0L) running = false } }; ToolPage("تایمر تمرکز", onBack) { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(25, 5).forEach { m -> FilterChip(selected = minutes == m && !running, onClick = { if (!running) { minutes = m; remaining = m * 60000L } }, label = { Text("$m دقیقه") }, shape = RoundedCornerShape(18.dp)) } }; Spacer(Modifier.height(18.dp)); Card(Modifier.fillMaxWidth(), RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) { Column(Modifier.fillMaxWidth().padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("تمرکز", color = Red, fontWeight = FontWeight.Bold); Text(formatTime(remaining), fontSize = 64.sp, fontWeight = FontWeight.Black); Text(if (running) "در حال اجرا" else "آماده", style = MaterialTheme.typography.bodySmall) } }; Spacer(Modifier.height(18.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { Button({ running = !running }, Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(18.dp)) { Icon(if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null); Spacer(Modifier.width(8.dp)); Text(if (running) "توقف" else "شروع") }; OutlinedButton({ running = false; remaining = minutes * 60000L }, Modifier.height(54.dp), shape = RoundedCornerShape(18.dp)) { Text("بازنشانی") } } } }

@Composable private fun GpaCalculatorScreen(onBack: () -> Unit) { var subjects by remember { mutableStateOf(listOf(Subject(1,"ریاضی","","3"), Subject(2,"انگلیسی","","2"), Subject(3,"علوم","","3"))) }; var nextId by remember { mutableIntStateOf(4) }; val valid = subjects.mapNotNull { s -> val g = s.grade.replace(',','.').toDoubleOrNull(); val c = s.credits.replace(',','.').toDoubleOrNull(); if (g != null && c != null && c > 0 && g in 0.0..20.0) g to c else null }; val total = valid.sumOf { it.second }; val avg = if (total > 0) valid.sumOf { it.first * it.second } / total else null; ToolPage("محاسبه معدل", onBack) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Red)) { Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("معدل شما", color = Color.White.copy(.85f)); Text(avg?.let { String.format(Locale.US,"%.2f",it) } ?: "—", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black) } }; Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("درس‌ها", fontWeight = FontWeight.Bold); TextButton({ subjects += Subject(nextId,"درس جدید","","3"); nextId++ }) { Text("+ افزودن", color = Red) } }; LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 24.dp)) { items(subjects.size) { i -> val s = subjects[i]; Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) { Column(Modifier.padding(12.dp)) { OutlinedTextField(s.name,{v -> subjects = subjects.map { if (it.id == s.id) it.copy(name = v) else it }},Modifier.fillMaxWidth(),label={Text("نام درس")},singleLine=true,shape=RoundedCornerShape(18.dp)); Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) { OutlinedTextField(s.grade,{v -> subjects = subjects.map { if (it.id == s.id) it.copy(grade = v) else it }},Modifier.weight(1f),label={Text("نمره")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),singleLine=true,shape=RoundedCornerShape(18.dp)); OutlinedTextField(s.credits,{v -> subjects = subjects.map { if (it.id == s.id) it.copy(credits = v) else it }},Modifier.weight(1f),label={Text("واحد")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),singleLine=true,shape=RoundedCornerShape(18.dp)) } } } } } } }

@Composable private fun UnitConverterScreen(onBack: () -> Unit) { val categories = listOf("طول","وزن","دما","سرعت"); var category by remember { mutableStateOf("طول") }; var from by remember { mutableStateOf("متر") }; var to by remember { mutableStateOf("کیلومتر") }; var input by remember { mutableStateOf("") }; val units = when (category) { "طول" -> listOf("متر","کیلومتر","سانتی‌متر","مایل","فوت","اینچ"); "وزن" -> listOf("کیلوگرم","گرم","پوند","اونس"); "دما" -> listOf("سلسیوس","فارنهایت","کلوین"); else -> listOf("متر بر ثانیه","کیلومتر بر ساعت","مایل بر ساعت") }; LaunchedEffect(category) { from = units.first(); to = units.getOrElse(1) { units.first() }; input = "" }; val result = convertValueFa(input.toDoubleOrNull(), category, from, to); ToolPage("مبدل واحد", onBack) { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) { categories.forEach { x -> FilterChip(selected=x==category,onClick={category=x},label={Text(x)},shape=RoundedCornerShape(18.dp)) } }; Spacer(Modifier.height(12.dp)); OutlinedTextField(input,{input=it.filter{c->c.isDigit()||c=='.'||c=='-'}.take(15)},Modifier.fillMaxWidth(),label={Text("مقدار")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),singleLine=true,shape=RoundedCornerShape(18.dp)); Spacer(Modifier.height(10.dp)); UnitSelector("از",from,units){from=it}; Spacer(Modifier.height(8.dp)); UnitSelector("به",to,units){to=it}; Spacer(Modifier.height(14.dp)); Card(Modifier.fillMaxWidth(),RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=Red)){Column(Modifier.padding(20.dp)){Text("نتیجه",color=Color.White.copy(.85f));Text(result?.let(::formatNumber)?:"—",color=Color.White,fontSize=34.sp,fontWeight=FontWeight.Black)}} } }

@Composable private fun UnitSelector(label:String,value:String,options:List<String>,onChange:(String)->Unit){var expanded by remember{mutableStateOf(false)};Box{OutlinedTextField(value,{},Modifier.fillMaxWidth(),label={Text(label)},readOnly=true,trailingIcon={IconButton({expanded=true}){Icon(Icons.Rounded.ExpandMore,"انتخاب")}},shape=RoundedCornerShape(18.dp));DropdownMenu(expanded,{expanded=false},shape=RoundedCornerShape(18.dp)){options.forEach{DropdownMenuItem(text={Text(it)},onClick={onChange(it);expanded=false})}}}}

private fun convertValueFa(v:Double?,cat:String,from:String,to:String):Double?{if(v==null)return null;if(from==to)return v;return when(cat){"طول"->toLengthFa(fromToMetersFa(from,v),to);"وزن"->toWeightFa(fromToKgFa(from,v),to);"دما"->toTempFa(fromToCFa(from,v),to);else->toSpeedFa(fromToMpsFa(from,v),to)}}
private fun fromToMetersFa(u:String,v:Double)=when(u){"کیلومتر"->v*1000;"سانتی‌متر"->v/100;"مایل"->v*1609.344;"فوت"->v*.3048;"اینچ"->v*.0254;else->v};private fun toLengthFa(v:Double,u:String)=when(u){"کیلومتر"->v/1000;"سانتی‌متر"->v*100;"مایل"->v/1609.344;"فوت"->v/.3048;"اینچ"->v/.0254;else->v};private fun fromToKgFa(u:String,v:Double)=when(u){"گرم"->v/1000;"پوند"->v*.45359237;"اونس"->v*.028349523125;else->v};private fun toWeightFa(v:Double,u:String)=when(u){"گرم"->v*1000;"پوند"->v/.45359237;"اونس"->v/.028349523125;else->v};private fun fromToCFa(u:String,v:Double)=when(u){"فارنهایت"->(v-32)*5/9;"کلوین"->v-273.15;else->v};private fun toTempFa(v:Double,u:String)=when(u){"فارنهایت"->v*9/5+32;"کلوین"->v+273.15;else->v};private fun fromToMpsFa(u:String,v:Double)=when(u){"کیلومتر بر ساعت"->v/3.6;"مایل بر ساعت"->v*.44704;else->v};private fun toSpeedFa(v:Double,u:String)=when(u){"کیلومتر بر ساعت"->v*3.6;"مایل بر ساعت"->v/.44704;else->v}

@Composable private fun ToolPage(title:String,onBack:()->Unit,content:@Composable ColumnScope.()->Unit){Column(Modifier.fillMaxSize().padding(horizontal=18.dp)){Spacer(Modifier.height(24.dp));Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){IconButton(onBack){Icon(Icons.Rounded.ArrowForward,"بازگشت")};Text(title,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)};Spacer(Modifier.height(14.dp));Column(Modifier.fillMaxWidth(),content=content)}}
private fun formatNumber(v:Double)=if(v%1.0==0.0)v.toLong().toString()else v.toString().trimEnd('0').trimEnd('.')
private fun formatTime(ms:Long):String{val s=(ms.coerceAtLeast(0)+999)/1000;return String.format(Locale.US,"%02d:%02d",s/60,s%60)}
