package com.d4rk.cleaner.app.settings.settings.utils.providers

import android.content.Context
import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.settings.general.ui.GeneralSettingsActivity
import com.d4rk.android.libs.apptoolkit.app.settings.utils.constants.SettingsContent
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.DisplaySettingsProvider
import com.d4rk.cleaner.app.settings.display.ui.components.dialogs.SelectStartupScreenAlertDialog

class AppDisplaySettingsProvider(val context : Context) : DisplaySettingsProvider {

    override fun openThemeSettings() {
        GeneralSettingsActivity.start(
            context = context , title = context.getString(R.string.dark_theme) , contentKey = SettingsContent.THEME
        )
    }

    override val supportsStartupPage : Boolean = true

    @Composable
    override fun StartupPageDialog(onDismiss : () -> Unit , onStartupSelected : (String) -> Unit) {
        SelectStartupScreenAlertDialog(onDismiss = onDismiss , onStartupSelected = onStartupSelected)
    }
}