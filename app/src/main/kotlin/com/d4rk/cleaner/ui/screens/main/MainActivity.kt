package com.d4rk.cleaner.ui.screens.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.d4rk.android.libs.apptoolkit.notifications.managers.AppUpdateNotificationsManager
import com.d4rk.cleaner.core.AppCoreManager
import com.d4rk.cleaner.ui.screens.settings.display.theme.style.AppTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult

class MainActivity : AppCompatActivity() {
    private val viewModel : MainViewModel by viewModels()
    private lateinit var appUpdateManager : AppUpdateManager
    private lateinit var appUpdateNotificationsManager : AppUpdateNotificationsManager
    private lateinit var updateResultLauncher : ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                ! (application as AppCoreManager).isAppLoaded()
            }
        }
        enableEdgeToEdge()
        initializeActivityComponents()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        with(receiver = viewModel) {
            checkAndHandleStartup()
            configureSettings()
            loadTrashSize()
            viewModel.checkForUpdates(
                appUpdateManager = appUpdateManager , updateResultLauncher = updateResultLauncher
            )
            checkAndScheduleUpdateNotifications(appUpdateNotificationsManager = appUpdateNotificationsManager)
            checkAppUsageNotifications(context = this@MainActivity)
        }
    }

    /**
     * This method overrides the `onBackPressed()` method **(deprecated in Java)** to display a confirmation dialog
     * before closing the activity. While this method might work, it's recommended to use more modern approaches
     * for handling back button presses, such as using Navigation components or Activity lifecycles.
     *
     * This method is annotated with `@Deprecated` and `@Suppress("DEPRECATION")` to explicitly mark it as deprecated
     * and suppress compiler warnings during its usage.
     *
     * Consider utilizing alternative approaches for handling back button events.
     */
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this).setTitle(com.d4rk.android.libs.apptoolkit.R.string.close).setMessage(com.d4rk.android.libs.apptoolkit.R.string.summary_close).setPositiveButton(android.R.string.yes) { _ , _ ->
            super.onBackPressed()
            moveTaskToBack(true)
        }.setNegativeButton(android.R.string.no , null).apply { show() }
    }

    /**
     * Overrides the `onActivityResult` method to handle the result of an activity launched for result.
     *
     * This function is specifically designed to handle the result of a request code (1)
     * which is used for in-app updates. It checks the `resultCode` to determine the outcome of the update process.
     * Depending on the `resultCode`, it either displays a Snackbar message indicating a successful update or
     * calls a function to show a Snackbar message indicating that the update failed.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     * allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (requestCode == 1) {
            when (resultCode) {
                RESULT_OK -> {
                    showUpdateSuccessfulSnackbar()
                }

                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    showUpdateFailedSnackbar()
                }
            }
        }
    }

    private fun initializeActivityComponents() {
        MobileAds.initialize(this@MainActivity)
        updateResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> showUpdateSuccessfulSnackbar()
                else -> showUpdateFailedSnackbar()
            }
        }
        appUpdateManager = AppUpdateManagerFactory.create(this@MainActivity)
        appUpdateNotificationsManager = AppUpdateNotificationsManager(context = this , channelId = "update_channel")
    }

    private fun showUpdateSuccessfulSnackbar() {
        val snackbar : Snackbar = Snackbar.make(
            findViewById(android.R.id.content) , com.d4rk.android.libs.apptoolkit.R.string.snack_app_updated , Snackbar.LENGTH_LONG
        ).setAction(android.R.string.ok , null)
        snackbar.show()
    }

    /**
     * Displays a Snackbar message indicating that the update process has failed.
     *
     * This function creates a Snackbar with a message indicating that the update process has failed.
     * The Snackbar includes a "Try Again" action which, when clicked, triggers the `checkForFlexibleUpdate` function
     * to check for updates and initiate the appropriate update flow if conditions are met.
     */
    private fun showUpdateFailedSnackbar() {
        val snackbar : Snackbar = Snackbar.make(
            findViewById(android.R.id.content) , com.d4rk.android.libs.apptoolkit.R.string.snack_update_failed , Snackbar.LENGTH_LONG
        ).setAction(com.d4rk.android.libs.apptoolkit.R.string.try_again) {
            viewModel.checkForUpdates(
                appUpdateManager = appUpdateManager , updateResultLauncher = updateResultLauncher
            )
        }
        snackbar.show()
    }
}