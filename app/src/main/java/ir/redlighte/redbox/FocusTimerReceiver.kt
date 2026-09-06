package ir.redlighte.redbox

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class FocusTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mode = intent.getStringExtra("mode") ?: "Focus"
        val nextMode = if (mode == "Focus") "Break" else "Focus"

        val preferences = context.getSharedPreferences("redbox_timer", Context.MODE_PRIVATE)
        preferences.edit()
            .putBoolean("running", false)
            .putString("mode", nextMode)
            .remove("end_at")
            .apply()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            "focus_timer",
            "Focus Timer",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, "focus_timer")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("RedBox • $mode finished")
            .setContentText("Time for your $nextMode session.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }
}
