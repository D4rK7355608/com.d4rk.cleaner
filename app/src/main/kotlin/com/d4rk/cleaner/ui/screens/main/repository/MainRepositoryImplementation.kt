package com.d4rk.cleaner.ui.screens.main.repository

import android.app.Activity
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.notifications.managers.AppUpdateNotificationsManager
import com.d4rk.cleaner.notifications.managers.AppUsageNotificationsManager
import com.google.android.play.core.appupdate.AppUpdateManager
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
abstract class MainRepositoryImplementation(val application : Application) {

    /**
     * Checks if the application is being launched for the first time.
     *
     * This function retrieves the startup state from a data store and updates it if it's the first launch.
     *
     * @return `true` if it's the first launch, `false` otherwise.
     */
    suspend fun checkStartup() : Boolean {
        val dataStore : DataStore = AppCoreManager.dataStore
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
    fun setupDiagnosticSettings(isEnabled : Boolean) {
        FirebaseAnalytics.getInstance(application).setAnalyticsCollectionEnabled(isEnabled)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = isEnabled
    }

    suspend fun checkForUpdatesLogic(activity : Activity, appUpdateManager : AppUpdateManager) : Int {

        try {
            var updateResult = Activity.RESULT_CANCELED
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                ) && appUpdateInfo.updateAvailability() != UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                appUpdateInfo.clientVersionStalenessDays()?.let { stalenessDays ->
                    val updateType =
                            if (stalenessDays > 90) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE
                    @Suppress("DEPRECATION") appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo , updateType , activity , 1
                    )
                    updateResult = Activity.RESULT_OK
                }
            }
            return updateResult

        } catch (e : Exception) {
            return ActivityResult.RESULT_IN_APP_UPDATE_FAILED
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkAndScheduleUpdateNotificationsLogic(appUpdateNotificationsManager : AppUpdateNotificationsManager) {
        appUpdateNotificationsManager.checkAndSendUpdateNotification()
    }

    fun checkAppUsageNotificationsManager() {
        val appUsageNotificationsManager = AppUsageNotificationsManager(application)
        appUsageNotificationsManager.scheduleAppUsageCheck()
    }
}