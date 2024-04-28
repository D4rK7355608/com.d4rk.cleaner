package com.d4rk.cleaner.ui.startup

import android.Manifest
import android.app.AppOpsManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.d4rk.cleaner.ui.settings.display.theme.AppTheme
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class StartupActivity : ComponentActivity() {
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
                    StartupComposable(this@StartupActivity)
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
        requestPermissions()
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

    /**
     * Checks if the access to usage statistics is granted for the application.
     *
     * This property retrieves the current access status for usage statistics based on the app's UID.
     * It uses deprecated methods due to the nature of permissions and operations involved.
     * The property determines if the access to usage statistics is allowed (`true`) or not (`false`).
     *
     * @return `true` if access to usage statistics is granted, `false` otherwise.
     * @suppress Use of deprecated methods for checking usage statistics access.
     */
    @Suppress("DEPRECATION")
    private val isAccessGranted : Boolean
        get() {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName , 0)
            val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
            @Suppress("DEPRECATION") val mode : Int = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS ,
                applicationInfo.uid ,
                applicationInfo.packageName
            )
            return mode == AppOpsManager.MODE_ALLOWED
        }

    /**
     * Requests necessary permissions required for the application.
     *
     * This function checks and requests permissions required for the application, particularly for storage access.
     * It checks the Android version to handle specific permission scenarios.
     * If running on Android 11 or later, it directs the user to manage app storage access settings if not granted.
     * Additionally, it prompts for usage access settings if usage access is not granted.
     *
     * @see android.Manifest.permission.WRITE_EXTERNAL_STORAGE
     * @see android.Manifest.permission.READ_EXTERNAL_STORAGE
     * @see android.os.Build.VERSION.SDK_INT
     * @see android.os.Build.VERSION_CODES.R
     */
    private fun requestPermissions() {
        val requiredPermissions = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (! Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package" , packageName , null)
                intent.data = uri
                startActivity(intent)
            }
            if (! isAccessGranted) {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }
        ActivityCompat.requestPermissions(this , requiredPermissions.toTypedArray() , 1)
    }
}