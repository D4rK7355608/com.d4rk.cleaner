package com.d4rk.cleaner.notifications.managers
import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.d4rk.cleaner.notifications.workers.AppUsageNotificationWorker
import java.util.concurrent.TimeUnit
class AppUsageNotificationsManager(private val context: Context) {
    fun scheduleAppUsageCheck() {
        val workRequest = PeriodicWorkRequestBuilder<AppUsageNotificationWorker>(3, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}