package com.d4rk.cleaner.app.settings.settings.utils.providers

import android.content.Context
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.AdvancedSettingsProvider

class AppAdvancedSettingsProvider(val context : Context) : AdvancedSettingsProvider {
    override val bugReportUrl : String get() = "https://github.com/D4rK7355608/${context.packageName}/issues/new"
}