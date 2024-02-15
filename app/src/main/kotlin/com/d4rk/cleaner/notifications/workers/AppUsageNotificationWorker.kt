package com.d4rk.cleaner.notifications.workers
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.d4rk.cleaner.R
class AppUsageNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val appUsageChannelId = "app_usage_channel"
    private val appUsageNotificationId = 0
    override fun doWork(): Result {
        val preferences = applicationContext.getSharedPreferences("app_usage", Context.MODE_PRIVATE)
        val lastUsedTimestamp = preferences.getLong("last_used", 0)
        val currentTimestamp = System.currentTimeMillis()
        val notificationThreshold = 3 * 24 * 60 * 60 * 1000
        if (currentTimestamp - lastUsedTimestamp > notificationThreshold) {
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val appUsageChannel = NotificationChannel(appUsageChannelId, applicationContext.getString(R.string.app_usage_notifications), NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(appUsageChannel)
            val notificationBuilder = NotificationCompat.Builder(applicationContext, appUsageChannelId)
                .setSmallIcon(R.drawable.ic_notification_important)
                .setContentTitle(applicationContext.getString(R.string.notification_last_time_used_title))
                .setContentText(applicationContext.getString(R.string.summary_notification_last_time_used))
                .setAutoCancel(true)
            notificationManager.notify(appUsageNotificationId, notificationBuilder.build())
        }
        preferences.edit().putLong("last_used", currentTimestamp).apply()
        return Result.success()
    }
}
