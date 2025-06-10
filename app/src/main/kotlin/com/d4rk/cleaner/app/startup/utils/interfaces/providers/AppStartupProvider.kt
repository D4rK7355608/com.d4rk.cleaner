package com.d4rk.cleaner.app.startup.utils.interfaces.providers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import com.d4rk.android.libs.apptoolkit.app.oboarding.ui.OnboardingActivity
import com.d4rk.android.libs.apptoolkit.app.startup.utils.interfaces.providers.StartupProvider
import com.google.android.ump.ConsentRequestParameters
import javax.inject.Inject

class AppStartupProvider @Inject constructor() : StartupProvider {
    override val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    }
    else {
        emptyArray()
    }
    override val consentRequestParameters : ConsentRequestParameters = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
    override fun getNextIntent(context : Context) = Intent(context , OnboardingActivity::class.java)
}