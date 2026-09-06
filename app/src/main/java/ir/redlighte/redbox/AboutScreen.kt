package ir.redlighte.redbox

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.size(28.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { IconButton(onBack) { Icon(Icons.Rounded.ArrowForward, "بازگشت") }; Text("درباره RedBox", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.size(28.dp))
        Card(Modifier.fillMaxWidth(), RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF120606))) { Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) { Image(painterResource(R.drawable.redbox_logo), "لوگوی ردباکس", Modifier.size(110.dp)); Spacer(Modifier.size(10.dp)); Text("RedBox", color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black); Text("هر چیزی که نیاز داری. همه در یک ردباکس.",color=Color.White.copy(.9f)) } }
        Spacer(Modifier.size(22.dp)); Text("ساده. کاربردی. قرمز.",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold); Spacer(Modifier.size(8.dp)); Text("RedBox یک جعبه‌ابزار آفلاین برای مدرسه، مطالعه و کارهای روزمره است؛ سریع، ساده و بدون پیچیدگی اضافه.",style=MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.size(18.dp)); Card(Modifier.fillMaxWidth(),RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainerHighest)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Text("نسخه",style=MaterialTheme.typography.labelLarge);Text("۰.۱۰.۰",fontWeight=FontWeight.Bold);Text("محصولی از Redlighte",style=MaterialTheme.typography.bodySmall)}}
        Spacer(Modifier.size(20.dp)); Row(verticalAlignment=Alignment.CenterVertically){Icon(Icons.Rounded.Favorite,null,tint=MaterialTheme.colorScheme.primary,Modifier.size(18.dp));Text("  با دقت توسط Redlighte ساخته شده",style=MaterialTheme.typography.bodySmall)}
    }
}
