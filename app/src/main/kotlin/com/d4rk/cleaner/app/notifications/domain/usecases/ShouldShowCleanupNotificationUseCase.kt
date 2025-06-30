package com.d4rk.cleaner.app.notifications.domain.usecases

import com.d4rk.cleaner.app.clean.memory.domain.interfaces.MemoryRepository
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.days

class ShouldShowCleanupNotificationUseCase(
    private val memoryRepository: MemoryRepository,
    private val dataStore: DataStore
) {
    suspend operator fun invoke(): Boolean {
        val storageInfo = memoryRepository.getStorageInfo()
        val ramInfo = memoryRepository.getRamInfo()

        val lastScan = dataStore.lastScanTimestamp.first()
        val lastShown = dataStore.lastCleanupNotificationShown.first()
        val lastClicked = dataStore.lastCleanupNotificationClicked.first()
        val snoozedUntil = dataStore.cleanupNotificationSnoozedUntil.first()
        val frequency = dataStore.cleanupReminderFrequencyDays.first()

        if (frequency <= 0) return false

        val now = System.currentTimeMillis()
        val lastInteraction = maxOf(lastShown, lastClicked)
        val enoughTimePassed = now - lastInteraction > frequency.days.inWholeMilliseconds
        val snoozed = now < snoozedUntil

        if (!enoughTimePassed || snoozed) return false

        val storageRatio = if (storageInfo.storageUsageProgress == 0f) 0f
        else storageInfo.usedStorage.toFloat() / storageInfo.storageUsageProgress
        val ramRatio = if (ramInfo.totalRam == 0L) 0f else ramInfo.usedRam.toFloat() / ramInfo.totalRam

        val storageHigh = storageRatio >= 0.8f
        val ramHigh = ramRatio >= 0.9f
        val scanOld = now - lastScan > 7.days.inWholeMilliseconds

        return storageHigh || ramHigh || scanOld
    }
}
