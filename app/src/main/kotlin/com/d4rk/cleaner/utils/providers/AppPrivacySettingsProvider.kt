package com.d4rk.cleaner.utils.providers

import android.content.Context
import android.content.Intent
import com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper
import com.d4rk.android.libs.apptoolkit.utils.interfaces.providers.PrivacySettingsProvider
import com.d4rk.cleaner.core.AppCoreManager
import com.d4rk.cleaner.ui.screens.settings.general.GeneralSettingsActivity
import com.d4rk.cleaner.ui.screens.settings.general.SettingsContent
import com.d4rk.cleaner.ui.screens.settings.privacy.ads.AdsSettingsActivity
import com.d4rk.cleaner.ui.screens.settings.privacy.permissions.PermissionsSettingsActivity

class AppPrivacySettingsProvider : PrivacySettingsProvider {

    val context : Context = AppCoreManager.instance

    override fun openPermissionsScreen() {
        IntentsHelper.openActivity(context = context , activityClass = PermissionsSettingsActivity::class.java)
    }

    override fun openAdsScreen() {
        IntentsHelper.openActivity(context = context , activityClass = AdsSettingsActivity::class.java)
    }

    override fun openUsageAndDiagnosticsScreen() {

        val intent : Intent = Intent(context , GeneralSettingsActivity::class.java).apply {
            putExtra("extra_title" , context.getString(com.d4rk.android.libs.apptoolkit.R.string.usage_and_diagnostics))
            putExtra("extra_content" , SettingsContent.USAGE_AND_DIAGNOSTICS.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}