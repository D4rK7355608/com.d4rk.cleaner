package com.d4rk.cleaner.app.main.ui

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.d4rk.android.libs.apptoolkit.app.startup.ui.StartupActivity
import com.d4rk.android.libs.apptoolkit.app.theme.style.AppTheme
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ConsentFormHelper
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ConsentManagerHelper
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ReviewHelper
import com.d4rk.cleaner.app.main.domain.actions.MainEvent
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class MainActivity : AppCompatActivity() {

    private val dataStore: DataStore by inject()
    private var updateResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {}
    private lateinit var viewModel: MainViewModel
    private var keepSplashVisible: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashVisible }
        enableEdgeToEdge()
        initializeDependencies()
        handleStartup()
        handleNotificationIntent(intent)
        checkInAppReview()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(event = MainEvent.CheckForUpdates)
        checkUserConsent()
    }

    private fun initializeDependencies() {
        CoroutineScope(context = Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
            ConsentManagerHelper.applyInitialConsent(dataStore = dataStore)
        }

        viewModel = getViewModel { parametersOf(updateResultLauncher) }
    }

    private fun handleStartup() {
        lifecycleScope.launch {
            val isFirstLaunch : Boolean = dataStore.startup.first()

            val trashDir = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")
            val actualTrashSize = withContext(Dispatchers.IO) { calculateDirectorySize(trashDir) }
            val storedTrashSize = dataStore.trashSize.first()
            when {
                actualTrashSize > storedTrashSize -> dataStore.addTrashSize(actualTrashSize - storedTrashSize)
                actualTrashSize < storedTrashSize -> dataStore.subtractTrashSize(storedTrashSize - actualTrashSize)
            }

            keepSplashVisible = false

            if (isFirstLaunch) {
                startStartupActivity()
            }
            else {
                setMainActivityContent()
            }
        }
    }

    private fun startStartupActivity() {
        IntentsHelper.openActivity(context = this , activityClass = StartupActivity::class.java)
        finish()
    }

    private fun setMainActivityContent() {
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }

    private fun checkUserConsent() {
        val consentInfo: ConsentInformation = UserMessagingPlatform.getConsentInformation(this)
        ConsentFormHelper.showConsentFormIfRequired(activity = this, consentInfo = consentInfo)
    }

    private fun checkInAppReview() {
        lifecycleScope.launch {
            val sessionCount: Int = dataStore.sessionCount.first()
            val hasPrompted: Boolean = dataStore.hasPromptedReview.first()
            ReviewHelper.launchInAppReviewIfEligible(
                activity = this@MainActivity,
                sessionCount = sessionCount,
                hasPromptedBefore = hasPrompted
            ) {
                lifecycleScope.launch { dataStore.setHasPromptedReview(value = true) }
            }
            dataStore.incrementSessionCount()
        }
    }

    private fun calculateDirectorySize(directory : File) : Long {
        if (! directory.exists()) return 0L

        var totalSize = 0L
        directory.listFiles()?.forEach { file : File ->
            totalSize += if (file.isFile) file.length() else calculateDirectorySize(file)
        }
        return totalSize
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("open_scan", false) == true) {
            CoroutineScope(Dispatchers.IO).launch {
                dataStore.saveLastCleanupNotificationClicked(System.currentTimeMillis())
            }
        }
    }
}