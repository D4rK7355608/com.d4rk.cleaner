package com.d4rk.cleaner.app.main.ui

import android.os.Bundle
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
import com.d4rk.cleaner.app.main.domain.actions.MainEvent
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    private val dataStore : DataStore by inject()
    private lateinit var updateResultLauncher : ActivityResultLauncher<IntentSenderRequest>
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        initializeDependencies()
        handleStartup()
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

        updateResultLauncher = registerForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {}

        viewModel = getViewModel { parametersOf(updateResultLauncher) }
    }
    private fun handleStartup() {
        lifecycleScope.launch {
            val isFirstLaunch : Boolean = dataStore.startup.first()

            // TODO: Calculate the total used

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
        ConsentFormHelper.showConsentFormIfRequired(activity = this , consentInfo = consentInfo)
    }
}