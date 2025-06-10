package com.d4rk.cleaner.app.settings.settings.utils.providers

import android.content.Context
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.ads.ui.AdsSettingsActivity
import com.d4rk.android.libs.apptoolkit.app.permissions.ui.PermissionsActivity
import com.d4rk.android.libs.apptoolkit.app.settings.general.ui.GeneralSettingsActivity
import com.d4rk.android.libs.apptoolkit.app.settings.utils.constants.SettingsContent
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.PrivacySettingsProvider
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper

class AppPrivacySettingsProvider(val context : Context) : PrivacySettingsProvider {

    override fun openPermissionsScreen() {
        IntentsHelper.openActivity(context = context , activityClass = PermissionsActivity::class.java)
    }

    override fun openAdsScreen() {
        IntentsHelper.openActivity(context = context , activityClass = AdsSettingsActivity::class.java)
    }

    override fun openUsageAndDiagnosticsScreen() {
        GeneralSettingsActivity.start(context = context , title = context.getString(R.string.usage_and_diagnostics) , contentKey = SettingsContent.USAGE_AND_DIAGNOSTICS)
    }
}