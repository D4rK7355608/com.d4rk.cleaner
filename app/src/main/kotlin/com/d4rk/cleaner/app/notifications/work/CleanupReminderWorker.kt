package com.d4rk.cleaner.app.notifications.work

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.memory.domain.interfaces.MemoryRepository
import com.d4rk.cleaner.app.notifications.domain.usecases.ShouldShowCleanupNotificationUseCase
import com.d4rk.cleaner.app.notifications.notifications.CleanerMessageProvider
import com.d4rk.cleaner.app.notifications.notifications.CleanupDismissReceiver
import com.d4rk.cleaner.app.notifications.notifications.CleanupNotifier
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

class CleanupReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val useCase: ShouldShowCleanupNotificationUseCase by inject()
    private val notifier: CleanupNotifier by inject()
    private val memoryRepository: MemoryRepository by inject()
    private val dataStore: DataStore by inject()

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        if (!useCase()) return Result.success()

        val storageInfo = memoryRepository.getStorageInfo()
        val lastScan = dataStore.lastScanTimestamp.first()

        val storagePercent = if (storageInfo.storageUsageProgress == 0f) 0 else
            (storageInfo.usedStorage.toFloat() / storageInfo.storageUsageProgress * 100).toInt()

        val now = System.currentTimeMillis()
        val daysSinceLastScan = lastScan?.let { ((now - it) / (1000 * 60 * 60 * 24)).toInt() }

        val (title, message) = createFriendlyMessage(storagePercent, daysSinceLastScan)

        val deleteIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent(applicationContext, CleanupDismissReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notifier.sendNotification(title, message, deleteIntent)
            dataStore.saveLastCleanupNotificationShown(System.currentTimeMillis())
        }
        return Result.success()
    }

    private fun createFriendlyMessage(storagePercent: Int, days: Int?): Pair<String, String> {
        val title = CleanerMessageProvider.getTitleVariants(applicationContext).random(Random)
        val base = CleanerMessageProvider.getStorageText(applicationContext, storagePercent)
        val message = if (days == null) {
            applicationContext.getString(R.string.cleanup_notification_never_scanned)
        } else {
            applicationContext.getString(
                R.string.cleanup_notification_last_scan_format,
                days,
                base
            )
        }
        return title to message
    }
}