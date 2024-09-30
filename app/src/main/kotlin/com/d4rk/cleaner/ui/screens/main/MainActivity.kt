package com.d4rk.cleaner.ui.screens.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.notifications.managers.AppUpdateNotificationsManager
import com.d4rk.cleaner.notifications.managers.AppUsageNotificationsManager
import com.d4rk.cleaner.ui.screens.settings.display.theme.style.AppTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private lateinit var dataStore: DataStore
    private lateinit var appUpdateManager: AppUpdateManager
    private var appUpdateNotificationsManager: AppUpdateNotificationsManager =
        AppUpdateNotificationsManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !(application as AppCoreManager).isAppLoaded()
            }
        }
        enableEdgeToEdge()
        dataStore = DataStore.getInstance(this@MainActivity)
        MobileAds.initialize(this@MainActivity)
        setupUpdateNotifications()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainComposable()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        val appUsageNotificationsManager = AppUsageNotificationsManager(this)
        appUsageNotificationsManager.scheduleAppUsageCheck()
        appUpdateNotificationsManager.checkAndSendUpdateNotification()
        checkForFlexibleUpdate()
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
        MaterialAlertDialogBuilder(this).setTitle(R.string.close).setMessage(R.string.summary_close)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                super.onBackPressed()
                moveTaskToBack(true)
            }.setNegativeButton(android.R.string.no, null).apply { show() }
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            when (resultCode) {
                RESULT_OK -> {
                    val snackbar: Snackbar = Snackbar.make(
                        findViewById(android.R.id.content), R.string.snack_app_updated,
                        Snackbar.LENGTH_LONG
                    ).setAction(android.R.string.ok, null)
                    snackbar.show()
                }

                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    showUpdateFailedSnackbar()
                }
            }
        }
    }

    /**
     * Checks for the availability of updates and triggers the appropriate update flow if conditions are met.
     *
     * This function uses the lifecycle scope to asynchronously check for available updates using the
     * Google Play Core library. If an update is available and meets certain conditions, it triggers
     * the update flow. The update can be of two types: IMMEDIATE or FLEXIBLE.
     *
     * For an IMMEDIATE update, it checks if the client version is more than 90 days old. If so, it triggers the update.
     * For a FLEXIBLE update, it checks if the client version is less than 90 days old. If so, it triggers the update.
     *
     * The function also ensures that no developer-triggered update is in progress before triggering a new update.
     *
     * @param lifecycleScope The lifecycle scope used for launching coroutines, obtained from the hosting activity.
     */
    private fun checkForFlexibleUpdate() {
        lifecycleScope.launch {
            try {
                val appUpdateInfo: AppUpdateInfo = appUpdateManager.appUpdateInfo.await()
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE
                    ) && appUpdateInfo.updateAvailability() != UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    @Suppress("DEPRECATION") appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                        when {
                            info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && info.isUpdateTypeAllowed(
                                AppUpdateType.IMMEDIATE
                            ) -> {
                                info.clientVersionStalenessDays()?.let {
                                    if (it > 90) {
                                        appUpdateManager.startUpdateFlowForResult(
                                            info, AppUpdateType.IMMEDIATE, this@MainActivity, 1
                                        )
                                    }
                                }
                            }

                            info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && info.isUpdateTypeAllowed(
                                AppUpdateType.FLEXIBLE
                            ) -> {
                                info.clientVersionStalenessDays()?.let {
                                    if (it < 90) {
                                        appUpdateManager.startUpdateFlowForResult(
                                            info, AppUpdateType.FLEXIBLE, this@MainActivity, 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (!BuildConfig.DEBUG) {
                    when (e) {
                        is NoConnectionError, is TimeoutError -> {
                            Snackbar.make(
                                findViewById(android.R.id.content),
                                getString(R.string.snack_network_error),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Snackbar.make(
                                findViewById(android.R.id.content),
                                getString(R.string.snack_general_error),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Displays a Snackbar message indicating that the update process has failed.
     *
     * This function creates a Snackbar with a message indicating that the update process has failed.
     * The Snackbar includes a "Try Again" action which, when clicked, triggers the `checkForFlexibleUpdate` function
     * to check for updates and initiate the appropriate update flow if conditions are met.
     */
    private fun showUpdateFailedSnackbar() {
        val snackbar: Snackbar = Snackbar.make(
            findViewById(android.R.id.content), R.string.snack_update_failed, Snackbar.LENGTH_LONG
        ).setAction(R.string.try_again) {
            checkForFlexibleUpdate()
        }
        snackbar.show()
    }

    private fun setupUpdateNotifications() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateNotificationsManager = AppUpdateNotificationsManager(this)
    }
}