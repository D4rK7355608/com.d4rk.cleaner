package com.d4rk.cleaner.ui.screens.main.repository

import android.app.Application
import com.d4rk.cleaner.data.datastore.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of the main repository for managing application settings and startup state.
 *
 * @property dataStore The data store used to persist settings and startup information.
 * @property application The application context.
 */
class MainRepository(val dataStore: DataStore, application: Application) :
    MainRepositoryImplementation(application) {

    /**
     * Checks the application startup state and performs actions based on whether it's the first launch.
     *
     * This function checks if the app is launched for the first time and invokes the `onSuccess` callback
     * with the result on the main thread.
     *
     * @param onSuccess A callback function that receives a boolean indicating if it's the first launch.
     */
    suspend fun checkAndHandleStartup(onSuccess: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val isFirstTime: Boolean = checkStartup()
            withContext(Dispatchers.Main) {
                onSuccess(isFirstTime)
            }
        }
    }

    /**
     * Sets up Firebase Analytics and Crashlytics based on stored settings.
     *
     * This function retrieves the "usageAndDiagnostics" setting from the data store and configures
     * Firebase Analytics and Crashlytics accordingly.
     */
    suspend fun setupSettings() {
        withContext(Dispatchers.IO) {
            val isEnabled: Boolean = dataStore.usageAndDiagnostics.first()
            withContext(Dispatchers.Main) {
                setupDiagnosticSettings(isEnabled)
            }
        }
    }
}