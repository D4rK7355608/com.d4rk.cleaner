package com.d4rk.cleaner.app.apps.manager.data

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppUsageStatsManager
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppUsageStatsManagerImpl(private val application: Application) : AppUsageStatsManager {
    override fun getAppsLastUsed(): Flow<DataState<Map<String, Long>, Errors>> = flow {
        runCatching {
            val usageManager =
                application.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val end = System.currentTimeMillis()
            val stats = usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, end)
            stats.associate { it.packageName to it.lastTimeUsed }
        }.onSuccess { map ->
            emit(DataState.Success(map))
        }.onFailure { throwable ->
            emit(DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_GET_APP_USAGE_STATS)))
        }
    }
}
