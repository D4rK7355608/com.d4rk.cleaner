package com.d4rk.cleaner.ui.startup

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.ui.settings.display.theme.style.AppTheme
import com.d4rk.cleaner.utils.PermissionsUtils
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class StartupActivity : AppCompatActivity() {
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
                    StartupComposable()
                }
            }
        }
        val params = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this , params , {
            if (consentInformation.isConsentFormAvailable) {
                loadForm()
            }
        } , {})
        if (!PermissionsUtils.hasNotificationPermission(this)) {
            PermissionsUtils.requestNotificationPermission(this)
        }
    }

    /**
     * Loads the consent form for user messaging platform (UMP) based on consent status.
     *
     * This function initiates the loading of the consent form using UserMessagingPlatform (UMP) API.
     * Upon successful loading of the consent form, it assigns the form to a local variable `consentForm`.
     * If user consent is required (`ConsentStatus.REQUIRED`), the form is displayed to the user.
     * If the consent status is not required or an error occurs during loading, the function handles this gracefully.
     *
     * @see com.google.android.gms.ads.UserMessagingPlatform
     * @see com.google.ads.consent.ConsentInformation
     */
    private fun loadForm() {
        UserMessagingPlatform.loadConsentForm(this , { consentForm ->
            this.consentForm = consentForm
            if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                consentForm.show(this) {
                    loadForm()
                }
            }
        } , {})
    }
}