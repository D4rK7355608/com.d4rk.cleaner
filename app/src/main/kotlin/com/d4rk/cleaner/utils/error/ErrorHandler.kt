package com.d4rk.cleaner.utils.error

import android.app.Activity
import android.content.Context
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.error.ErrorType
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics

object ErrorHandler {

    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    fun handleError(applicationContext: Context, errorType: ErrorType, exception: Throwable?) {
        val message: String = applicationContext.getString(
            when (errorType) {
                ErrorType.SECURITY_EXCEPTION -> R.string.security_error
                ErrorType.IO_EXCEPTION -> R.string.io_error
                ErrorType.FILE_NOT_FOUND -> R.string.file_not_found
                ErrorType.ACTIVITY_NOT_FOUND -> R.string.activity_not_found
                ErrorType.ILLEGAL_ARGUMENT -> R.string.illegal_argument_error
                else -> R.string.unknown_error
            }
        )

        sendCrashlyticsException(exception = exception , displayMessage = message)
        showSnackbar(context = applicationContext, message = message)
    }

    fun handleInitializationFailure(applicationContext : Context , message : String , exception : Exception? = null) {
        val displayMessage : String = message.ifEmpty {
            applicationContext.getString(R.string.initialization_error)
        }

        sendCrashlyticsException(exception = exception , displayMessage = message)
        showSnackbar(context = applicationContext , message = displayMessage)
    }

    private fun sendCrashlyticsException(exception: Throwable?, displayMessage: String) {
        crashlytics.apply {
            setCustomKey("error_type", "ERROR_HANDLER_REPORT")
            setCustomKey("error_message", displayMessage)
            recordException(exception ?: Exception(displayMessage))
        }
    }

    private fun showSnackbar(context : Context , message : String) {
        (context as? Activity)?.let { activity ->
            activity.runOnUiThread {
                Snackbar.make(
                    activity.findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
}