package com.d4rk.cleaner.app.main.data.updates

import com.d4rk.cleaner.core.domain.DataError
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await
import com.d4rk.cleaner.core.domain.Result

class UpdateRepositoryImpl(private val appUpdateManager: AppUpdateManager) : UpdateRepository {
    override suspend fun checkForUpdates(): Result<Boolean, DataError> {
        return try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()
            Result.Success(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}