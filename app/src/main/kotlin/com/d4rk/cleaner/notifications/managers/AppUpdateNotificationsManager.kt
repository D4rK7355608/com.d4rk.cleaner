package com.d4rk.cleaner.notifications.managers
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.d4rk.cleaner.R
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
class AppUpdateNotificationsManager(private val context: Context) {
    private val updateChannelId = "update_channel"
    private val updateNotificationId = 0
    fun checkAndSendUpdateNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val appUpdateInfoTask = AppUpdateManagerFactory.create(context).appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                val updateChannel = NotificationChannel(updateChannelId, context.getString(R.string.update_notifications), NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(updateChannel)
                val updateBuilder = NotificationCompat.Builder(context, updateChannelId)
                    .setSmallIcon(R.drawable.ic_notification_update)
                    .setContentTitle(context.getString(R.string.notification_update_title))
                    .setContentText(context.getString(R.string.summary_notification_update))
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(context, 0, Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")), PendingIntent.FLAG_IMMUTABLE))
                notificationManager.notify(updateNotificationId, updateBuilder.build())
            }
        }
    }
}