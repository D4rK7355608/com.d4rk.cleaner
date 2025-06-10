package com.d4rk.cleaner.app.settings.settings.utils.providers

import android.content.Context
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.settings.general.ui.GeneralSettingsActivity
import com.d4rk.android.libs.apptoolkit.app.settings.utils.constants.SettingsContent
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.DisplaySettingsProvider

class AppDisplaySettingsProvider(val context : Context) : DisplaySettingsProvider {
    override fun openThemeSettings() {
        GeneralSettingsActivity.start(
            context = context , title = context.getString(R.string.dark_theme) , contentKey = SettingsContent.THEME
        )
    }
}