package com.d4rk.cleaner.notifications.managers

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.d4rk.cleaner.notifications.workers.AppUsageNotificationWorker
import java.util.concurrent.TimeUnit

/**
 * Utility class for managing app usage notifications.
 *
 * This class provides functionality to schedule periodic checks for app usage notifications
 * using WorkManager and a custom worker class.
 *
 * @property context The application context used for scheduling app usage checks.
 */
class AppUsageNotificationsManager(private val context: Context) {

    /**
     * Schedules a periodic check for app usage notifications.
     *
     * This function schedules a recurring task using WorkManager to perform app usage checks
     * every 3 days. It enqueues a PeriodicWorkRequest with a specified interval and triggers
     * an instance of the AppUsageNotificationWorker to handle the app usage check.
     */
    fun scheduleAppUsageCheck() {
        val workRequest = PeriodicWorkRequestBuilder<AppUsageNotificationWorker>(
            repeatInterval = 3,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        ).build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}