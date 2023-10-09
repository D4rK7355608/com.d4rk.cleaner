package com.d4rk.cleaner.notifications
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.d4rk.cleaner.R
class AppUsageNotificationsManager(private val context: Context) {
    private val appUsageChannelId = "app_usage_channel"
    private val appUsageNotificationId = 0
    fun checkAndSendAppUsageNotification() {
        val preferences = context.getSharedPreferences("app_usage", Context.MODE_PRIVATE)
        val lastUsedTimestamp = preferences.getLong("last_used", 0)
        val currentTimestamp = System.currentTimeMillis()
        val notificationThreshold = 3 * 24 * 60 * 60 * 1000
        if (currentTimestamp - lastUsedTimestamp > notificationThreshold) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val appUsageChannel = NotificationChannel(appUsageChannelId, context.getString(R.string.app_usage_notifications),NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(appUsageChannel)
            val notificationBuilder = NotificationCompat.Builder(context, appUsageChannelId)
                .setSmallIcon(R.drawable.ic_notification_important)
                .setContentTitle(context.getString(R.string.notification_last_time_used_title))
                .setContentText(context.getString(R.string.summary_notification_last_time_used))
                .setAutoCancel(true)
            notificationManager.notify(appUsageNotificationId, notificationBuilder.build())
        }
        preferences.edit().putLong("last_used", currentTimestamp).apply()
    }
}