package com.d4rk.cleaner.utils.error

import com.d4rk.android.libs.apptoolkit.utils.interfaces.ErrorReporter
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsErrorReporter : ErrorReporter {

    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    override fun recordException(throwable: Throwable, message: String?) {
        message?.let {
            crashlytics.setCustomKey("error_message", it)
        }
        crashlytics.recordException(throwable)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
}