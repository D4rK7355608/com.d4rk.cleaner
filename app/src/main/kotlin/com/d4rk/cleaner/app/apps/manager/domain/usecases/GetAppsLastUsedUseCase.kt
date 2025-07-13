package com.d4rk.cleaner.app.apps.manager.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppUsageStatsManager
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow

class GetAppsLastUsedUseCase(private val usageStatsManager: AppUsageStatsManager) {
    operator fun invoke(): Flow<DataState<Map<String, Long>, Errors>> {
        return usageStatsManager.getAppsLastUsed()
    }
}
