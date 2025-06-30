package com.d4rk.cleaner.app.notifications.work

import android.content.Context
import android.app.PendingIntent
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.d4rk.cleaner.app.notifications.domain.usecases.ShouldShowCleanupNotificationUseCase
import com.d4rk.cleaner.app.notifications.notifications.CleanupNotifier
import com.d4rk.cleaner.app.notifications.notifications.CleanupDismissReceiver
import com.d4rk.cleaner.app.clean.memory.domain.interfaces.MemoryRepository
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.R
import kotlinx.coroutines.flow.first
import kotlin.random.Random
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CleanupReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val useCase: ShouldShowCleanupNotificationUseCase by inject()
    private val notifier: CleanupNotifier by inject()
    private val memoryRepository: MemoryRepository by inject()
    private val dataStore: DataStore by inject()

    override suspend fun doWork(): Result {
        if (!useCase()) return Result.success()

        val storageInfo = memoryRepository.getStorageInfo()
        val ramInfo = memoryRepository.getRamInfo()
        val lastScan = dataStore.lastScanTimestamp.first()

        val storagePercent = if (storageInfo.storageUsageProgress == 0f) 0 else
            (storageInfo.usedStorage.toFloat() / storageInfo.storageUsageProgress * 100).toInt()
        val ramPercent = if (ramInfo.totalRam == 0L) 0 else
            (ramInfo.usedRam.toFloat() / ramInfo.totalRam * 100).toInt()

        val daysSinceLastScan = ((System.currentTimeMillis() - lastScan) / (1000 * 60 * 60 * 24)).toInt()

        val (title, message) = createFriendlyMessage(storagePercent, ramPercent, daysSinceLastScan)

        val deleteIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent(applicationContext, CleanupDismissReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        notifier.sendNotification(title, message, deleteIntent)
        dataStore.saveLastCleanupNotificationShown(System.currentTimeMillis())

        return Result.success()
    }

    private fun createFriendlyMessage(storagePercent: Int, ramPercent: Int, days: Int): Pair<String, String> {
        val titles = listOf(
            R.string.cleanup_notification_friendly_title1,
            R.string.cleanup_notification_friendly_title2,
            R.string.cleanup_notification_friendly_title3,
        )
        val titleRes = titles.random(Random)
        val title = applicationContext.getString(titleRes)

        val message = when {
            storagePercent > 80 -> applicationContext.getString(
                R.string.cleanup_notification_msg_storage, storagePercent
            )
            ramPercent > 85 -> applicationContext.getString(
                R.string.cleanup_notification_msg_ram, ramPercent
            )
            days > 7 -> applicationContext.getString(
                R.string.cleanup_notification_msg_days, days
            )
            else -> applicationContext.getString(R.string.cleanup_notification_msg_default)
        }

        return title to message
    }
}
