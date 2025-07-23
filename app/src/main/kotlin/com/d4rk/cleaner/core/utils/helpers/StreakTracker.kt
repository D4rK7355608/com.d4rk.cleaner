package com.d4rk.cleaner.core.utils.helpers

import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.utils.constants.TimeConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object StreakTracker : KoinComponent {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val dataStore: DataStore by inject()


    fun initialize() {
        scope.launch {
            CleaningEventBus.events.collect {
                updateStreak()
            }
        }
    }

    private suspend fun updateStreak() {
        val lastClean = dataStore.lastCleanDay.first()
        val streak = dataStore.streakCount.first()
        val today = System.currentTimeMillis() / TimeConstants.DAY_MS
        val lastDay = lastClean / TimeConstants.DAY_MS
        val newStreak = when {
            lastClean == 0L -> 1
            today - lastDay >= 2L -> 1
            today - lastDay == 1L -> streak + 1
            else -> streak
        }
        dataStore.saveStreakCount(newStreak)
        dataStore.saveLastCleanDay(System.currentTimeMillis())
    }
}
