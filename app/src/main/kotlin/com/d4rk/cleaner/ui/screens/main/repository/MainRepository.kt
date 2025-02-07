package com.d4rk.cleaner.ui.screens.main.repository

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import com.d4rk.android.libs.apptoolkit.notifications.managers.AppUpdateNotificationsManager
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.google.android.play.core.appupdate.AppUpdateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of the main repository for managing application settings and startup state.
 *
 * @property dataStore The data store used to persist settings and startup information.
 * @property application The application context.
 */
class MainRepository(dataStore : DataStore , application : Application) : MainRepositoryImplementation(application = application , dataStore = dataStore) {

    suspend fun checkForUpdates(
        updateResultLauncher : ActivityResultLauncher<IntentSenderRequest> ,
        appUpdateManager : AppUpdateManager ,
    ) {
        withContext(Dispatchers.IO) {
            checkForUpdatesImplementation(updateResultLauncher = updateResultLauncher , appUpdateManager = appUpdateManager)
        }
    }

    /**
     * Checks the application startup state and performs actions based on whether it's the first launch.
     *
     * This function checks if the app is launched for the first time and invokes the `onSuccess` callback
     * with the result on the main thread.
     *
     * @param onSuccess A callback function that receives a boolean indicating if it's the first launch.
     */
    suspend fun checkAndHandleStartupRepository(onSuccess : (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val isFirstTime : Boolean = checkStartupImplementation()
            withContext(Dispatchers.Main) {
                onSuccess(isFirstTime)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun checkAndScheduleUpdateNotificationsRepository(appUpdateNotificationsManager : AppUpdateNotificationsManager) {
        withContext(Dispatchers.IO) {
            checkAndScheduleUpdateNotificationsImplementation(appUpdateNotificationsManager = appUpdateNotificationsManager)
        }
    }

    suspend fun checkAppUsageNotificationsRepository(context : Context) {
        withContext(Dispatchers.IO) {
            checkAppUsageNotificationsManagerImplementation(context = context)
        }
    }

    /**
     * Sets up Firebase Analytics and Crashlytics based on stored settings.
     *
     * This function retrieves the "usageAndDiagnostics" setting from the data store and configures
     * Firebase Analytics and Crashlytics accordingly.
     */
    suspend fun setupSettingsRepository() {
        withContext(Dispatchers.IO) {
            val isEnabled : Boolean = dataStore.usageAndDiagnostics.first()
            withContext(Dispatchers.Main) {
                setupDiagnosticSettingsImplementation(isEnabled = isEnabled)
            }
        }
    }
}