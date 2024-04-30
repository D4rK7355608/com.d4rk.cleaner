package com.d4rk.cleaner.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Utility object for common operations.
 */
object Utils {

    /**
     * Opens a URL in the default browser.
     *
     * @param context The Android context.
     * @param url The URL to open.
     */
    fun openUrl(context: Context, url: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens an activity.
     *
     * @param context The Android context.
     * @param activityClass The class of the activity to open.
     */
    fun openActivity(context: Context, activityClass: Class<*>) {
        Intent(context, activityClass).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens the app notification settings.
     *
     * @param context The Android context.
     */
    fun openAppNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}