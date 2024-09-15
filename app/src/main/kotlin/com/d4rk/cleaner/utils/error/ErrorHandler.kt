package com.d4rk.cleaner.utils.error

import android.app.Activity
import android.content.Context
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.error.ErrorType
import com.google.android.material.snackbar.Snackbar

object ErrorHandler {
    fun handleError(applicationContext: Context, errorType: ErrorType) {
        val message: String = when (errorType) {
            ErrorType.STORAGE_PERMISSION -> applicationContext.getString(R.string.storage_permission_error)
            ErrorType.ANALYSIS_ERROR -> applicationContext.getString(R.string.analysis_error)
            ErrorType.CLEANING_ERROR -> applicationContext.getString(R.string.cleaning_error)
            ErrorType.STORAGE_INFO_ERROR -> applicationContext.getString(R.string.storage_info_error)
            ErrorType.RAM_INFO_ERROR -> applicationContext.getString(R.string.ram_info_error)
            ErrorType.STORAGE_BREAKDOWN_ERROR -> applicationContext.getString(R.string.storage_breakdown_error)
            ErrorType.APP_LOADING_ERROR -> applicationContext.getString(R.string.app_loading_error)
            ErrorType.APK_INSTALLATION_ERROR -> applicationContext.getString(R.string.apk_installation_error)
            ErrorType.APK_SHARING_ERROR -> applicationContext.getString(R.string.apk_sharing_error) // Added
            ErrorType.APP_SHARING_ERROR -> applicationContext.getString(R.string.app_sharing_error) // Added
            ErrorType.APP_INFO_ERROR -> applicationContext.getString(R.string.app_info_error) // Added
            ErrorType.APP_UNINSTALLATION_ERROR -> applicationContext.getString(R.string.app_uninstallation_error) // Added
            ErrorType.UNKNOWN_ERROR -> applicationContext.getString(R.string.unknown_error)
        }

        (applicationContext as? Activity)?.let { activity ->
            activity.runOnUiThread {
                Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}