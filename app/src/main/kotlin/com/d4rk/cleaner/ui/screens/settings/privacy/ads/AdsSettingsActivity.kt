package com.d4rk.cleaner.ui.screens.settings.privacy.ads

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.ui.screens.settings.display.theme.style.AppTheme
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class AdsSettingsActivity : AppCompatActivity() {
    private lateinit var consentInformation : ConsentInformation
    private val isPrivacyOptionsRequired : Boolean
        get() = consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    private lateinit var consentForm : ConsentForm

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        consentInformation = UserMessagingPlatform.getConsentInformation(this@AdsSettingsActivity)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    AdsSettingsScreen(activity = this@AdsSettingsActivity)
                }
            }
        }
    }

    fun openForm() {
        UserMessagingPlatform.loadConsentForm(this , { consentForm ->
            this.consentForm = consentForm
            if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED || consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
                consentForm.show(this) {
                    loadForm()
                }
            }
        } , {})
    }

    private fun loadForm() {
        val params : ConsentRequestParameters =
                ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
        consentInformation.requestConsentInfoUpdate(this@AdsSettingsActivity , params , {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                this@AdsSettingsActivity
            ) {
                if (isPrivacyOptionsRequired) {
                    invalidateOptionsMenu()
                }
            }
        } , {})
    }
}