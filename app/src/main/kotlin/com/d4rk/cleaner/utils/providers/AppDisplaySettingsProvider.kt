package com.d4rk.cleaner.utils.providers

import android.content.Intent
import androidx.compose.runtime.Composable
import com.d4rk.android.libs.apptoolkit.utils.interfaces.providers.DisplaySettingsProvider
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.ui.components.dialogs.SelectLanguageAlertDialog
import com.d4rk.cleaner.ui.components.dialogs.SelectStartupScreenAlertDialog
import com.d4rk.cleaner.ui.screens.settings.general.GeneralSettingsActivity
import com.d4rk.cleaner.ui.screens.settings.general.SettingsContent

class AppDisplaySettingsProvider : DisplaySettingsProvider {

    override val supportsStartupPage : Boolean = true

    @Composable
    override fun LanguageSelectionDialog(onDismiss : () -> Unit , onLanguageSelected : (String) -> Unit) {
        SelectLanguageAlertDialog(
            dataStore = AppCoreManager.dataStore , onDismiss = onDismiss , onLanguageSelected = onLanguageSelected
        )
    }

    @Composable
    override fun StartupPageDialog(onDismiss : () -> Unit , onStartupSelected : (String) -> Unit) {
        SelectStartupScreenAlertDialog(
            dataStore = AppCoreManager.dataStore , onDismiss = onDismiss , onStartupSelected = onStartupSelected
        )
    }

    override fun openThemeSettings() {
        val context : AppCoreManager = AppCoreManager.instance
        val intent : Intent = Intent(context , GeneralSettingsActivity::class.java).apply {
            putExtra("extra_title" , context.getString(com.d4rk.android.libs.apptoolkit.R.string.dark_theme))
            putExtra("extra_content" , SettingsContent.THEME.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}