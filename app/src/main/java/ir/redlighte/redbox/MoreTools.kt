package ir.redlighte.redbox

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.Period
import java.util.Locale

private val MoreRed = Color(0xFFE53935)

@Composable
fun MoreToolsScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf("درصد") }
    val options = listOf("درصد", "تخفیف", "تبدیل مبنا", "سن", "شمارش معکوس")
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth()) { IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowForward, "بازگشت") }; Column { Text("ابزارهای بیشتر", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("ابزارهای کوچک، صرفه‌جویی بزرگ در زمان.", style = MaterialTheme.typography.bodySmall) } }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 28.dp)) {
            item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) { options.forEach { option -> FilterChip(selected = selected == option, onClick = { selected = option }, label = { Text(option) }, shape = RoundedCornerShape(18.dp)) } } }
            item { when (selected) { "درصد" -> PercentageTool(); "تخفیف" -> DiscountTool(); "تبدیل مبنا" -> BaseConverterTool(); "سن" -> AgeTool(); else -> CountdownTool() } }
        }
    }
}

@Composable private fun ToolPanel(content: @Composable ColumnScope.() -> Unit) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) { Column(Modifier.padding(18.dp), content = content) } }
@Composable private fun PercentageTool() { var value by remember { mutableStateOf("") }; var percent by remember { mutableStateOf("") }; val result = value.toDoubleOrNull()?.let { v -> percent.toDoubleOrNull()?.let { p -> v*p/100 } }; ToolPanel { Text("محاسبه درصد", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Spacer(Modifier.height(10.dp)); NumberField(value,"مقدار"){value=it}; NumberField(percent,"درصد"){percent=it}; Text(result?.let(::formatMore) ?: "—", fontSize=34.sp, fontWeight=FontWeight.Black, color=MoreRed) } }
@Composable private fun DiscountTool() { var price by remember { mutableStateOf("") }; var discount by remember { mutableStateOf("") }; val saved=price.toDoubleOrNull()?.let{p->discount.toDoubleOrNull()?.let{d->p*d/100}}; val final=price.toDoubleOrNull()?.let{p->saved?.let{p-it}}; ToolPanel { Text("محاسبه تخفیف", style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold); Spacer(Modifier.height(10.dp)); NumberField(price,"قیمت اصلی"){price=it}; NumberField(discount,"درصد تخفیف"){discount=it}; Text("مقدار صرفه‌جویی: ${saved?.let(::formatMore) ?: "—"}",fontWeight=FontWeight.Bold); Text("قیمت نهایی: ${final?.let(::formatMore) ?: "—"}",fontSize=30.sp,fontWeight=FontWeight.Black,color=MoreRed) } }
@Composable private fun BaseConverterTool() { var input by remember{mutableStateOf("")}; var from by remember{mutableStateOf("ده‌دهی")}; var to by remember{mutableStateOf("دودویی")}; val result=convertBaseFa(input,from,to); ToolPanel { Text("تبدیل مبنا",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold); Spacer(Modifier.height(10.dp)); NumberField(input,"عدد"){input=it}; Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){listOf("ده‌دهی","دودویی","هگزادسیمال").forEach{x->FilterChip(from==x,{from=x},label={Text(x)},shape=RoundedCornerShape(18.dp))}}; Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){listOf("ده‌دهی","دودویی","هگزادسیمال").forEach{x->FilterChip(to==x,{to=x},label={Text(x)},shape=RoundedCornerShape(18.dp))}}; Text("نتیجه: ${result ?: "—"}",fontSize=28.sp,fontWeight=FontWeight.Black,color=MoreRed) } }
@Composable private fun AgeTool() { var year by remember{mutableStateOf("")}; var month by remember{mutableStateOf("")}; var day by remember{mutableStateOf("")}; val age=calculateAge(year.toIntOrNull(),month.toIntOrNull(),day.toIntOrNull()); ToolPanel { Text("محاسبه سن",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold); Spacer(Modifier.height(10.dp)); NumberField(year,"سال تولد"){year=it}; NumberField(month,"ماه تولد"){month=it}; NumberField(day,"روز تولد"){day=it}; Text(age ?: "—",fontSize=28.sp,fontWeight=FontWeight.Black,color=MoreRed) } }
@Composable private fun CountdownTool() { var input by remember{mutableStateOf("60")}; var remaining by remember{mutableIntStateOf(60)}; var running by remember{mutableStateOf(false)}; LaunchedEffect(running){while(running&&remaining>0){delay(1000);remaining--};if(remaining==0)running=false}; ToolPanel { Text("شمارش معکوس",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold); Spacer(Modifier.height(10.dp)); NumberField(input,"ثانیه"){input=it.filter(Char::isDigit).take(6)}; Text(formatSeconds(remaining),fontSize=36.sp,fontWeight=FontWeight.Black,color=MoreRed); Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button({remaining=input.toIntOrNull()?.coerceIn(1,359999)?:60;running=true},enabled=!running,shape=RoundedCornerShape(18.dp)){Text("شروع")};OutlinedButton({running=false;remaining=input.toIntOrNull()?.coerceIn(1,359999)?:60},shape=RoundedCornerShape(18.dp)){Text("بازنشانی")}} } }
@Composable private fun NumberField(value:String,label:String,onValue:(String)->Unit){OutlinedTextField(value,onValue,Modifier.fillMaxWidth().padding(bottom=8.dp),label={Text(label)},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),shape=RoundedCornerShape(18.dp))}
private fun formatMore(v:Double)=String.format(Locale.US,"%.4f",v).trimEnd('0').trimEnd('.')
private fun convertBaseFa(input:String,from:String,to:String):String?=runCatching{if(input.isBlank())return null;val radix=when(from){"دودویی"->2;"هگزادسیمال"->16;else->10};val n=input.trim().removePrefix("0x").removePrefix("0X").toLong(radix);when(to){"دودویی"->n.toString(2);"هگزادسیمال"->n.toString(16).uppercase();else->n.toString()}}.getOrNull()
private fun calculateAge(year:Int?,month:Int?,day:Int?):String?=runCatching{if(year==null||month==null||day==null)return null;val birth=LocalDate.of(year,month,day);val today=LocalDate.now();if(birth.isAfter(today))return null;val p=Period.between(birth,today);"${p.years} سال، ${p.months} ماه، ${p.days} روز"}.getOrNull()
private fun formatSeconds(total:Int)=String.format(Locale.US,"%02d:%02d",total/60,total%60)
