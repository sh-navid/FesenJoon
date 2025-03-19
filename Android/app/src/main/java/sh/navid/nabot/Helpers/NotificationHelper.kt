package sh.navid.nabot.Helpers

import sh.navid.nabot.FLOATING_SERVICE_NOTIFICATION_ID
import sh.navid.nabot.FloatingWindowService
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Notification
import android.app.Service
import android.os.Build

class NotificationHelper {
    fun createNotificationChannel(service: Service,desc:String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FloatingWindowService.CHANNEL_ID,
                "Floating Window",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = desc
                enableLights(false)
                setShowBadge(false)
                enableVibration(false)
                setSound(null, null)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = service.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

     fun showForegroundNotification(service:Service,icon:Int,title:String,text:String) {
        val builder = NotificationCompat.Builder(service.baseContext,
            FloatingWindowService.CHANNEL_ID
        )
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setOngoing(true)

        service.startForeground(FLOATING_SERVICE_NOTIFICATION_ID, builder.build())
    }
}