package ir.redlighte.redbox

import android.Manifest
import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

private val Red = Color(0xFFE53935)
private const val TIMER_PREFS = "redbox_timer"
private data class Tool(val title: String, val subtitle: String, val icon: ImageVector)
private data class Subject(val id: Int, val name: String, val grade: String, val credits: String)
private val tools = listOf(Tool("Calculator","Fast calculations",Icons.Rounded.Calculate),Tool("Focus Timer","Pomodoro & focus",Icons.Rounded.Schedule),Tool("GPA Calculator","Calculate your GPA",Icons.Rounded.Checklist),Tool("Notes","Quick notes & lists",Icons.Rounded.EditNote),Tool("Converter","Units & values",Icons.Rounded.SwapHoriz),Tool("More Tools","5 useful utilities",Icons.Rounded.Construction))

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); enableEdgeToEdge(); if (Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS); setContent { RedBoxApp() } }
}

@Composable private fun RedBoxApp() {
    val context = LocalContext.current
    val savedTheme = AppPreferences.isDarkTheme(context)
    val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
    var darkTheme by remember(savedTheme, systemDark) { mutableStateOf(savedTheme ?: systemDark) }
    var screen by remember { mutableStateOf("home") }
    RedBoxTheme(darkTheme = darkTheme) { Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { AnimatedContent(screen, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "navigation") { current ->
        when (current) {
            "calculator" -> CalculatorScreen { screen="home" }; "timer" -> FocusTimerScreen { screen="home" }; "gpa" -> GpaCalculatorScreen { screen="home" }; "notes" -> NotesScreen { screen="home" }; "converter" -> UnitConverterScreen { screen="home" }; "more" -> MoreToolsScreen { screen="home" }
            "settings" -> SettingsScreen(darkTheme, { darkTheme=it; AppPreferences.setDarkTheme(context,it) }) { screen="home" }
            "about" -> AboutScreen { screen="home" }
            else -> HomeScreen({ title -> DashboardPreferences.addRecent(context,title); screen=routeFor(title) }, { screen="settings" }, { screen="about" })
        }
    } } }
}
private fun routeFor(title:String)=when(title){"Calculator"->"calculator";"Focus Timer"->"timer";"GPA Calculator"->"gpa";"Notes"->"notes";"Converter"->"converter";"More Tools"->"more";else->"home"}

@Composable private fun HomeScreen(onTool:(String)->Unit,onSettings:()->Unit,onAbout:()->Unit){
    val context=LocalContext.current; var query by remember{mutableStateOf("")}; var favorites by remember{mutableStateOf(DashboardPreferences.favorites(context))}; var recents by remember{mutableStateOf(DashboardPreferences.recents(context))}
    val visible=tools.filter{query.isBlank()||it.title.contains(query,true)||it.subtitle.contains(query,true)}; val favoriteTools=tools.filter{it.title in favorites}
    Column(Modifier.fillMaxSize().padding(horizontal=20.dp)){Spacer(Modifier.height(24.dp));Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Image(painterResource(R.drawable.redbox_logo),contentDescription="RedBox logo",modifier=Modifier.size(48.dp));Column(Modifier.padding(start=12.dp).weight(1f)){Text("RedBox",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);Text("Simple. Useful. Red.",style=MaterialTheme.typography.bodySmall)};IconButton(onAbout){Icon(Icons.Rounded.Info,"About",tint=Red)};IconButton(onSettings){Icon(Icons.Rounded.Settings,"Settings",tint=Red)}};Spacer(Modifier.height(18.dp));Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Red)){Column(Modifier.padding(22.dp)){Text("Everything you need.",color=Color.White,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Black);Text("One RedBox.",color=Color.White.copy(.9f),style=MaterialTheme.typography.titleMedium)}};Spacer(Modifier.height(16.dp));OutlinedTextField(query,{query=it},Modifier.fillMaxWidth(),singleLine=true,label={Text("Search tools")},leadingIcon={Icon(Icons.Rounded.Search,null)},trailingIcon={if(query.isNotBlank())IconButton({query=""}){Icon(Icons.Rounded.Close,"Clear")}})
        if(query.isBlank()&&favoriteTools.isNotEmpty()){Spacer(Modifier.height(18.dp));SectionTitle("Favorites",Icons.Rounded.Star);LazyVerticalGrid(GridCells.Adaptive(155.dp),Modifier.height(120.dp),horizontalArrangement=Arrangement.spacedBy(10.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){items(favoriteTools){tool->ToolCard(tool,true,{onTool(tool.title)}){DashboardPreferences.toggleFavorite(context,tool.title);favorites=DashboardPreferences.favorites(context)}}}}
        if(query.isBlank()&&recents.isNotEmpty()){Spacer(Modifier.height(14.dp));SectionTitle("Recent",Icons.Rounded.History);Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){recents.take(3).forEach{title->AssistChip(onClick={onTool(title)},label={Text(title)},leadingIcon={Icon(tools.first{it.title==title}.icon,null,Modifier.size(18.dp))})}}}
        Spacer(Modifier.height(18.dp));SectionTitle(if(query.isBlank())"All Tools" else "Results",Icons.Rounded.GridView);LazyVerticalGrid(GridCells.Adaptive(155.dp),Modifier.fillMaxWidth(),contentPadding=PaddingValues(bottom=28.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){items(visible){tool->ToolCard(tool,tool.title in favorites,{onTool(tool.title)}){DashboardPreferences.toggleFavorite(context,tool.title);favorites=DashboardPreferences.favorites(context);recents=DashboardPreferences.recents(context)}}}}
}
@Composable private fun SectionTitle(title:String,icon:ImageVector){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Icon(icon,null,tint=Red,modifier=Modifier.size(20.dp));Spacer(Modifier.width(8.dp));Text(title,style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)}}
@Composable private fun ToolCard(tool:Tool,favorite:Boolean,onClick:()->Unit,onFavorite:()->Unit){Card(onClick=onClick,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainerHighest)){Column(Modifier.padding(16.dp)){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Icon(tool.icon,null,tint=Red,modifier=Modifier.size(30.dp));Spacer(Modifier.weight(1f));IconButton(onFavorite,Modifier.size(32.dp)){Icon(if(favorite)Icons.Rounded.Star else Icons.Rounded.StarBorder,"Favorite",tint=if(favorite)Red else MaterialTheme.colorScheme.onSurfaceVariant)}};Spacer(Modifier.height(8.dp));Text(tool.title,fontWeight=FontWeight.Bold);Spacer(Modifier.height(3.dp));Text(tool.subtitle,style=MaterialTheme.typography.bodySmall)}}}

@Composable private fun CalculatorScreen(onBack:()->Unit){var d by remember{mutableStateOf("0")};var stored by remember{mutableStateOf<Double?>(null)};var op by remember{mutableStateOf<String?>(null)};var fresh by remember{mutableStateOf(true)};fun input(v:String){d=if(fresh||d=="0")v else d+v;fresh=false};fun clear(){d="0";stored=null;op=null;fresh=true};fun calc(){val a=stored?:return;val b=d.toDoubleOrNull()?:return;val r=when(op){"+"->a+b;"−"->a-b;"×"->a*b;"÷"->if(b==0.0)null else a/b;else->b};d=r?.let(::formatNumber)?:"Error";stored=null;op=null;fresh=true};fun setOp(o:String){stored=d.toDoubleOrNull();op=o;fresh=true};ToolPage("Calculator",onBack){Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainerHighest)){Text(d,Modifier.fillMaxWidth().padding(22.dp),textAlign=TextAlign.End,fontSize=40.sp,fontWeight=FontWeight.Bold)};Spacer(Modifier.height(14.dp));listOf(listOf("C","÷","×","⌫"),listOf("7","8","9","−"),listOf("4","5","6","+"),listOf("1","2","3","="),listOf("0",".")).forEach{row->Row(Modifier.fillMaxWidth().padding(vertical=5.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){row.forEach{k->Card(onClick={when(k){"C"->clear();"⌫"->if(!fresh&&d.length>1)d=d.dropLast(1);"."->if(!d.contains('.'))input(".");"+","−","×","÷"->setOp(k);"="->calc();else->input(k)}},modifier=Modifier.weight(if(k=="0")2f else 1f).height(62.dp),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=if(k in listOf("+","−","×","÷","="))Red else MaterialTheme.colorScheme.surfaceContainerHighest)){Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Text(k,color=if(k in listOf("+","−","×","÷","="))Color.White else MaterialTheme.colorScheme.onSurface,fontSize=20.sp)}}}}}}}

@Composable private fun FocusTimerScreen(onBack:()->Unit){var minutes by remember{mutableIntStateOf(25)};var remaining by remember{mutableLongStateOf(25*60000L)};var running by remember{mutableStateOf(false)};LaunchedEffect(running){while(running){delay(250);remaining=(remaining-250).coerceAtLeast(0);if(remaining==0L)running=false}};ToolPage("Focus Timer",onBack){Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf(25,5).forEach{m->FilterChip(selected=minutes==m&&!running,onClick={if(!running){minutes=m;remaining=m*60000L}},label={Text("$m min")})}};Spacer(Modifier.height(18.dp));Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainerHighest)){Column(Modifier.fillMaxWidth().padding(30.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("Focus",color=Red,fontWeight=FontWeight.Bold);Text(formatTime(remaining),fontSize=64.sp,fontWeight=FontWeight.Black);Text(if(running)"Running"else"Ready",style=MaterialTheme.typography.bodySmall)}};Spacer(Modifier.height(18.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){Button({running=!running},Modifier.weight(1f).height(54.dp)){Icon(if(running)Icons.Rounded.Pause else Icons.Rounded.PlayArrow,null);Spacer(Modifier.width(8.dp));Text(if(running)"Pause"else"Start")};OutlinedButton({running=false;remaining=minutes*60000L},Modifier.height(54.dp)){Text("Reset")}}}}

@Composable private fun GpaCalculatorScreen(onBack:()->Unit){var subjects by remember{mutableStateOf(listOf(Subject(1,"Mathematics","","3"),Subject(2,"English","","2"),Subject(3,"Science","","3")))};var nextId by remember{mutableIntStateOf(4)};val valid=subjects.mapNotNull{s->val g=s.grade.replace(',','.').toDoubleOrNull();val c=s.credits.replace(',','.').toDoubleOrNull();if(g!=null&&c!=null&&c>0&&g in 0.0..20.0)g to c else null};val total=valid.sumOf{it.second};val avg=if(total>0)valid.sumOf{it.first*it.second}/total else null;ToolPage("GPA Calculator",onBack){Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=Red)){Column(Modifier.fillMaxWidth().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("Your GPA",color=Color.White.copy(.85f));Text(avg?.let{String.format(Locale.US,"%.2f",it)}?:"—",color=Color.White,fontSize=42.sp,fontWeight=FontWeight.Black)}};Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){Text("Courses",fontWeight=FontWeight.Bold);TextButton({subjects+=Subject(nextId,"New course","","3");nextId++}){Text("+ Add")}};LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp),contentPadding=PaddingValues(bottom=24.dp)){items(subjects.size){i->val s=subjects[i];Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainerHighest)){Column(Modifier.padding(12.dp)){OutlinedTextField(s.name,{v->subjects=subjects.map{if(it.id==s.id)it.copy(name=v)else it}},Modifier.fillMaxWidth(),label={Text("Course")},singleLine=true);Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(s.grade,{v->subjects=subjects.map{if(it.id==s.id)it.copy(grade=v)else it}},Modifier.weight(1f),label={Text("Grade")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),singleLine=true);OutlinedTextField(s.credits,{v->subjects=subjects.map{if(it.id==s.id)it.copy(credits=v)else it}},Modifier.weight(1f),label={Text("Credits")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),singleLine=true)}}}}}}}

@Composable private fun UnitConverterScreen(onBack:()->Unit){val categories=listOf("Length","Weight","Temperature","Speed");var category by remember{mutableStateOf("Length")};var from by remember{mutableStateOf("Meter")};var to by remember{mutableStateOf("Kilometer")};var input by remember{mutableStateOf("")};val units=when(category){"Length"->listOf("Meter","Kilometer","Centimeter","Mile","Foot","Inch");"Weight"->listOf("Kilogram","Gram","Pound","Ounce");"Temperature"->listOf("Celsius","Fahrenheit","Kelvin");else->listOf("m/s","km/h","mph")};LaunchedEffect(category){from=units.first();to=units.getOrElse(1){units.first()};input=""};val result=convertValue(input.toDoubleOrNull(),category,from,to);ToolPage("Unit Converter",onBack){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){categories.forEach{x->FilterChip(x==category,{category=x},label={Text(x)})}};Spacer(Modifier.height(12.dp));OutlinedTextField(input,{input=it.filter{c->c.isDigit()||c=='.'||c=='-'}.take(15)},Modifier.fillMaxWidth(),label={Text("Value")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),singleLine=true);Spacer(Modifier.height(10.dp));UnitSelector("From",from,units){from=it};Spacer(Modifier.height(8.dp));UnitSelector("To",to,units){to=it};Spacer(Modifier.height(14.dp));Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=Red)){Column(Modifier.padding(20.dp)){Text("Result",color=Color.White.copy(.85f));Text(result?.let(::formatNumber)?:"—",color=Color.White,fontSize=34.sp,fontWeight=FontWeight.Black)}}}}
@Composable private fun UnitSelector(label:String,value:String,options:List<String>,onChange:(String)->Unit){var expanded by remember{mutableStateOf(false)};Box{OutlinedTextField(value,{},Modifier.fillMaxWidth(),label={Text(label)},readOnly=true,trailingIcon={IconButton({expanded=true}){Icon(Icons.Rounded.ExpandMore,"Choose")}});DropdownMenu(expanded,{expanded=false}){options.forEach{DropdownMenuItem(text={Text(it)},onClick={onChange(it);expanded=false})}}}}
private fun convertValue(v:Double?,cat:String,from:String,to:String):Double?{if(v==null)return null;if(from==to)return v;return when(cat){"Length"->toLength(fromToMeters(from,v),to);"Weight"->toWeight(fromToKg(from,v),to);"Temperature"->toTemp(fromToC(from,v),to);else->toSpeed(fromToMps(from,v),to)}}
private fun fromToMeters(u:String,v:Double)=when(u){"Kilometer"->v*1000;"Centimeter"->v/100;"Mile"->v*1609.344;"Foot"->v*.3048;"Inch"->v*.0254;else->v};private fun toLength(v:Double,u:String)=when(u){"Kilometer"->v/1000;"Centimeter"->v*100;"Mile"->v/1609.344;"Foot"->v/.3048;"Inch"->v/.0254;else->v};private fun fromToKg(u:String,v:Double)=when(u){"Gram"->v/1000;"Pound"->v*.45359237;"Ounce"->v*.028349523125;else->v};private fun toWeight(v:Double,u:String)=when(u){"Gram"->v*1000;"Pound"->v/.45359237;"Ounce"->v/.028349523125;else->v};private fun fromToC(u:String,v:Double)=when(u){"Fahrenheit"->(v-32)*5/9;"Kelvin"->v-273.15;else->v};private fun toTemp(v:Double,u:String)=when(u){"Fahrenheit"->v*9/5+32;"Kelvin"->v+273.15;else->v};private fun fromToMps(u:String,v:Double)=when(u){"km/h"->v/3.6;"mph"->v*.44704;else->v};private fun toSpeed(v:Double,u:String)=when(u){"km/h"->v*3.6;"mph"->v/.44704;else->v}
@Composable private fun ToolPage(title:String,onBack:()->Unit,content:@Composable ColumnScope.()->Unit){Column(Modifier.fillMaxSize().padding(horizontal=18.dp)){Spacer(Modifier.height(24.dp));Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){IconButton(onBack){Icon(Icons.Rounded.ArrowBack,"Back")};Text(title,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)};Spacer(Modifier.height(14.dp));Column(Modifier.fillMaxWidth(),content=content)}}
private fun formatNumber(v:Double)=if(v%1.0==0.0)v.toLong().toString()else v.toString().trimEnd('0').trimEnd('.')
private fun formatTime(ms:Long):String{val s=(ms.coerceAtLeast(0)+999)/1000;return String.format(Locale.US,"%02d:%02d",s/60,s%60)}
