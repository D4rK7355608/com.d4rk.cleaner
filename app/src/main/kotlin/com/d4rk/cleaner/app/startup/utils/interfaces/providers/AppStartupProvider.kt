package com.d4rk.cleaner.app.startup.utils.interfaces.providers

import android.Manifest
import android.content.Context
import android.content.Intent
import com.d4rk.android.libs.apptoolkit.app.startup.utils.interfaces.providers.StartupProvider
import com.d4rk.cleaner.app.main.ui.MainActivity
import com.google.android.ump.ConsentRequestParameters
import javax.inject.Inject

class AppStartupProvider @Inject constructor() : StartupProvider {
    override val requiredPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    override val consentRequestParameters : ConsentRequestParameters = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
    override fun getNextIntent(context : Context) = Intent(context , MainActivity::class.java)
}
