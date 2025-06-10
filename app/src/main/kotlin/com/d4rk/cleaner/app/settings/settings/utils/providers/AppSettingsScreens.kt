package com.d4rk.cleaner.app.settings.settings.utils.providers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.d4rk.cleaner.app.settings.cleaning.ui.CleaningSettingsList
import com.d4rk.cleaner.app.settings.settings.utils.constants.SettingsConstants

class AppSettingsScreens {
    val customScreens : Map<String , @Composable (PaddingValues) -> Unit> = mapOf(
        SettingsConstants.KEY_SETTINGS_CLEANING to { paddingValues -> CleaningSettingsList(paddingValues = paddingValues) })
}
