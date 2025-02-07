package com.d4rk.cleaner.ui.screens.main.repository

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import com.d4rk.android.libs.apptoolkit.notifications.managers.AppUpdateNotificationsManager
import com.d4rk.android.libs.apptoolkit.notifications.managers.AppUsageNotificationsManager
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

/**
 * Abstract base class for repository implementations related to main application functionality.
 *
 * This class provides common functionality for managing application startup state.
 *
 * @property application The application context.
 */
abstract class MainRepositoryImplementation(val application : Application , val dataStore : DataStore) {

    /**
     * Checks if the application is being launched for the first time.
     *
     * This function retrieves the startup state from a data store and updates it if it's the first launch.
     *
     * @return `true` if it's the first launch, `false` otherwise.
     */
    suspend fun checkStartupImplementation() : Boolean {
        val isFirstTime : Boolean = dataStore.startup.first()
        if (isFirstTime) {
            dataStore.saveStartup(isFirstTime = false)
        }
        return isFirstTime
    }

    /**
     * Configures Firebase Analytics and Crashlytics data collection.
     *
     * Enables or disables data collection for both Firebase Analytics and Crashlytics
     * based on the provided flag.
     *
     * @param isEnabled `true` to enable data collection, `false` to disable.
     */
    fun setupDiagnosticSettingsImplementation(isEnabled : Boolean) {
        FirebaseAnalytics.getInstance(application).setAnalyticsCollectionEnabled(isEnabled)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = isEnabled
    }

    suspend fun checkForUpdatesImplementation(
        appUpdateManager : AppUpdateManager , updateResultLauncher : ActivityResultLauncher<IntentSenderRequest>
    ) : Int {
        return runCatching {
            val appUpdateInfo : AppUpdateInfo = appUpdateManager.appUpdateInfo.await()
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                val stalenessDays = appUpdateInfo.clientVersionStalenessDays() ?: 0
                val updateType = if (stalenessDays > 90) {
                    AppUpdateType.IMMEDIATE
                }
                else {
                    AppUpdateType.FLEXIBLE
                }

                val appUpdateOptions : AppUpdateOptions = AppUpdateOptions.newBuilder(updateType).build()

                val didStart : Boolean = appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo , updateResultLauncher , appUpdateOptions
                )

                if (didStart) return@runCatching Activity.RESULT_OK
            }
            Activity.RESULT_CANCELED
        }.getOrElse {
            ActivityResult.RESULT_IN_APP_UPDATE_FAILED
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkAndScheduleUpdateNotificationsImplementation(appUpdateNotificationsManager : AppUpdateNotificationsManager) {
        appUpdateNotificationsManager.checkAndSendUpdateNotification()
    }

    fun checkAppUsageNotificationsManagerImplementation(context : Context) {
        val appUsageNotificationsManager = AppUsageNotificationsManager(context = context)
        appUsageNotificationsManager.scheduleAppUsageCheck(notificationSummary = R.string.summary_notification_last_time_used)
    }
}