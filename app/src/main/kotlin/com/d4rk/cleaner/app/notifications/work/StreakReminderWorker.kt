package com.d4rk.cleaner.app.notifications.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.notifications.notifications.StreakNotifier
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.utils.constants.TimeConstants
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StreakReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val dataStore: DataStore by inject()
    private val notifier: StreakNotifier by inject()

    override suspend fun doWork(): Result {
        val enabled = dataStore.streakReminderEnabled.first()
        if (!enabled) return Result.success()

        val lastClean = dataStore.lastCleanDay.first()
        val streak = dataStore.streakCount.first()
        val today = System.currentTimeMillis() / TimeConstants.DAY_MS
        val lastDay = lastClean / TimeConstants.DAY_MS
        val diff = today - lastDay

        val message = when {
            diff >= 1 -> applicationContext.getString(R.string.streak_notification_missed)
            streak >= 3 && streak <= 7 -> applicationContext.getString(
                R.string.streak_notification_milestone_format,
                streak
            )
            else -> applicationContext.getString(R.string.streak_notification_daily)
        }

        if (
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notifier.sendNotification(
                applicationContext.getString(R.string.streak_notification_title),
                message
            )
        }
        return Result.success()
    }

}
