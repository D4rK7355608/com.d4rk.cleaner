package com.d4rk.cleaner

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.d4rk.cleaner.data.store.DataStore
import com.d4rk.cleaner.notifications.managers.AppUpdateNotificationsManager
import com.d4rk.cleaner.notifications.managers.AppUsageNotificationsManager
import com.d4rk.cleaner.ui.settings.display.theme.AppTheme
import com.d4rk.cleaner.ui.startup.StartupActivity
import com.d4rk.cleaner.utils.FileScanner
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    private lateinit var dataStore : DataStore
    private lateinit var appUpdateManager : AppUpdateManager
    private var appUpdateNotificationsManager : AppUpdateNotificationsManager =
            AppUpdateNotificationsManager(this)

    private lateinit var fileScanner : FileScanner

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setupUpdateNotifications()
        dataStore = DataStore(this@MainActivity)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    MainComposable(this@MainActivity)
                }
            }
        }

        fileScanner = FileScanner(dataStore, resources)
        scanForFiles()

        setupSettings()
    }

    override fun onResume() {
        super.onResume()
        val appUsageNotificationsManager = AppUsageNotificationsManager(this)
        appUsageNotificationsManager.scheduleAppUsageCheck()
        appUpdateNotificationsManager.checkAndSendUpdateNotification()

        // TODO: Test on release
        //checkForFlexibleUpdate()
        startupScreen()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (requestCode == 1) {
            when (resultCode) {
                RESULT_OK -> {
                    val snackbar = Snackbar.make(
                        findViewById(android.R.id.content) ,
                        R.string.snack_app_updated ,
                        Snackbar.LENGTH_LONG
                    ).setAction(android.R.string.ok , null)
                    snackbar.show()
                }

                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    showUpdateFailedSnackbar()
                }
            }
        }
    }

    private fun scanForFiles() {
        CoroutineScope(Dispatchers.IO).launch {
            fileScanner.startScanning()
        }
    }

    private fun checkForFlexibleUpdate() {
        lifecycleScope.launch {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()
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
                                        info , AppUpdateType.IMMEDIATE , this@MainActivity , 1
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
                                        info , AppUpdateType.FLEXIBLE , this@MainActivity , 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showUpdateFailedSnackbar() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content) , R.string.snack_update_failed , Snackbar.LENGTH_LONG
        ).setAction(R.string.try_again) {
            checkForFlexibleUpdate()
        }
        snackbar.show()
    }

    private fun setupUpdateNotifications() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateNotificationsManager = AppUpdateNotificationsManager(this)
    }

    private fun setupSettings() {
        lifecycleScope.launch {
            val isEnabled = dataStore.usageAndDiagnostics.first()
            FirebaseAnalytics.getInstance(this@MainActivity)
                    .setAnalyticsCollectionEnabled(isEnabled)
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(isEnabled)
        }
    }

    private fun startupScreen() {
        lifecycleScope.launch {
            if (dataStore.startup.first()) {
                dataStore.saveStartup(false)
                startActivity(Intent(this@MainActivity , StartupActivity::class.java))
            }
        }
    }
}