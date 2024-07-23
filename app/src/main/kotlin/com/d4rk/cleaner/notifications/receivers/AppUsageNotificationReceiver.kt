package com.d4rk.cleaner.notifications.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.d4rk.cleaner.notifications.workers.AppUsageNotificationWorker

class AppUsageNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context : Context , intent : Intent?) {
        val workRequest = OneTimeWorkRequestBuilder<AppUsageNotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}