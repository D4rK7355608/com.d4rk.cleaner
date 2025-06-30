package com.d4rk.cleaner.app.notifications.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object CleanupReminderScheduler {
    private const val WORK_NAME = "cleanup_reminder_work"

    fun schedule(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<CleanupReminderWorker>(12, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
