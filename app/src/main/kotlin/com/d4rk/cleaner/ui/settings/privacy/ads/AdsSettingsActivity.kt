package com.d4rk.cleaner.ui.settings.privacy.ads

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.ui.settings.display.theme.AppTheme
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform

class AdsSettingsActivity : ComponentActivity() {
    private lateinit var consentInformation : ConsentInformation
    private lateinit var consentForm : ConsentForm
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    AdsSettingsComposable(this@AdsSettingsActivity)
                }
            }
        }
    }

    fun loadForm() {
        UserMessagingPlatform.loadConsentForm(this , { consentForm ->
            this.consentForm = consentForm
            if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
                consentForm.show(this) {
                    loadForm()
                }
            }
        } , {})
    }
}