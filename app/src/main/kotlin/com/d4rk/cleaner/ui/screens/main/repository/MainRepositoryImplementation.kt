package com.d4rk.cleaner.ui.screens.main.repository

import android.app.Application
import com.d4rk.cleaner.data.datastore.DataStore
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.first
import com.d4rk.cleaner.data.datastore.DataStore.Companion as DataStore1

/**
 * Abstract base class for repository implementations related to main application functionality.
 *
 * This class provides common functionality for managing application startup state.
 *
 * @property application The application context.
 */
abstract class MainRepositoryImplementation(val application: Application) {

    /**
     * Checks if the application is being launched for the first time.
     *
     * This function retrieves the startup state from a data store and updates it if it's the first launch.
     *
     * @return `true` if it's the first launch, `false` otherwise.
     */
    suspend fun checkStartup(): Boolean {
        val dataStore: DataStore = DataStore1.getInstance(application)
        val isFirstTime: Boolean = dataStore.startup.first()
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
    fun setupDiagnosticSettings(isEnabled: Boolean) {
        FirebaseAnalytics.getInstance(application).setAnalyticsCollectionEnabled(isEnabled)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = isEnabled
    }
}