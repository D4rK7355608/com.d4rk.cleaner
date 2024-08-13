package com.d4rk.cleaner.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.d4rk.cleaner.R

/**
 * A utility object for performing common operations such as opening URLs, activities, and app notification settings.
 *
 * This object provides functions to open a URL in the default browser, open an activity, and open the app's notification settings.
 * All operations are performed in the context of an Android application.
 */
object IntentUtils {

    /**
     * Opens a specified URL in the default browser.
     *
     * This function creates an Intent with the ACTION_VIEW action and the specified URL, and starts an activity with this intent.
     * The activity runs in a new task.
     *
     * @param context The Android context in which the URL should be opened.
     * @param url The URL to open.
     */
    fun openUrl(context: Context, url: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens a specified activity.
     *
     * This function creates an Intent with the specified activity class, and starts an activity with this intent. The activity runs in a new task.
     *
     * @param context The Android context in which the activity should be opened.
     * @param activityClass The class of the activity to open.
     */
    fun openActivity(context: Context, activityClass: Class<*>) {
        Intent(context, activityClass).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens the app's notification settings.
     *
     * This function creates an Intent with the ACTION_APP_NOTIFICATION_SETTINGS action and the app's package name, and starts an activity with this intent.
     * The activity runs in a new task.
     *
     * @param context The Android context in which the app's notification settings should be opened.
     */
    fun openAppNotificationSettings(context: Context) {
        val intent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Opens the app's share sheet.
     *
     * This function creates an Intent with the ACTION_SEND action and a share message containing the app's name and a link to the app's Play Store listing.
     * The intent is then wrapped in a chooser Intent to allow the user to select an app to share the message with.
     *
     * @param context The Android context in which the share sheet should be opened.
     */
    fun shareApp(context: Context) {
        val shareMessage: String = context.getString(
            R.string.summary_share_message,
            "https://play.google.com/store/apps/details?id=${context.packageName}"
        )
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        context.startActivity(
            Intent.createChooser(
                shareIntent, context.resources.getText(R.string.send_email_using)
            )
        )
    }

    /**
     * Composes and sends an email to the developer with a predefined subject and message body.
     *
     * This function constructs an email Intent with the developer's email address pre-filled in the "to" field.
     * The subject line includes the application name for easy identification. The email body starts with
     * a generic salutation and leaves the rest of the body empty for the user to fill in their feedback.
     * A chooser is displayed, allowing the user to select their preferred email app for sending the email.
     *
     * @param context The Android context used to access resources and start the activity.
     */
    fun sendEmailToDeveloper(context: Context) {
        val developerEmail = "d4rk7355608@gmail.com"
        val appName: String = context.getString(R.string.app_name)
        val subject: String = context.getString(R.string.feedback_for) + appName
        val body: String = context.getString(R.string.dear_developer) + "\n\n"

        val emailIntent: Intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$developerEmail")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        context.startActivity(
            Intent.createChooser(
                emailIntent, context.resources.getText(R.string.send_email_using)
            )
        )
    }
}