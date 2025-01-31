package com.d4rk.cleaner.utils.providers

import com.d4rk.android.libs.apptoolkit.utils.interfaces.providers.UsageAndDiagnosticsSettingsProvider
import com.d4rk.cleaner.BuildConfig

class AppUsageAndDiagnosticsProvider : UsageAndDiagnosticsSettingsProvider {

    override val isDebugBuild : Boolean
        get() {
            return BuildConfig.DEBUG
        }
}