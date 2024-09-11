package com.d4rk.cleaner.utils.error

import android.app.Activity
import android.content.Context
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.error.ErrorType
import com.google.android.material.snackbar.Snackbar

object ErrorHandler {
    fun handleError(applicationContext: Context, errorType: ErrorType) {
        val message = when (errorType) {
            ErrorType.STORAGE_PERMISSION -> applicationContext.getString(R.string.storage_permission_error)
            ErrorType.ANALYSIS_ERROR -> applicationContext.getString(R.string.analysis_error)
            ErrorType.CLEANING_ERROR -> applicationContext.getString(R.string.cleaning_error)
            ErrorType.UNKNOWN_ERROR -> applicationContext.getString(R.string.unknown_error)
        }

        (applicationContext as? Activity)?.let { activity ->
            activity.runOnUiThread {
                Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}