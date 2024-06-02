package com.d4rk.cleaner.notifications.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.d4rk.cleaner.notifications.receivers.AppUsageNotificationReceiver
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
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationIntent =
        Intent(context, AppUsageNotificationReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

    /**
     * Schedules a periodic check for app usage notifications.
     *
     * This function schedules a recurring task using WorkManager to perform app usage checks
     * every 3 days. It enqueues a PeriodicWorkRequest with a specified interval and triggers
     * an instance of the AppUsageNotificationWorker to handle the app usage check.
     */
    fun scheduleAppUsageCheck() {
        val triggerTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, triggerTime, TimeUnit.DAYS.toMillis(3), notificationIntent
        )
    }
}