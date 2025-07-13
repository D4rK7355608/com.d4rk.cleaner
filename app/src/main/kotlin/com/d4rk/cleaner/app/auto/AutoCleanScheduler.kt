package com.d4rk.cleaner.app.auto

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

object AutoCleanScheduler {
    private const val WORK_NAME = "auto_clean_work"

    fun schedule(context: Context, dataStore: DataStore) {
        val frequency = runBlocking { dataStore.autoCleanFrequencyDays.first() }
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresDeviceIdle(true)
            .build()
        val request = PeriodicWorkRequestBuilder<AutoCleanWorker>(frequency.toLong(), TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
