package com.d4rk.cleaner.app.notifications.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.android.material.color.MaterialColors
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.d4rk.cleaner.R
import androidx.core.content.ContextCompat
import com.d4rk.cleaner.app.main.ui.MainActivity

class CleanupNotifier(private val context: Context) {

    fun sendNotification(title: String, message: String, deleteIntent: PendingIntent) {
        createChannelIfNeeded()
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("open_scan", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_auto_fix_high)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(deleteIntent)
            .setColor(MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, 0))
            .setAutoCancel(true)
            .addAction(R.drawable.ic_auto_fix_high, context.getString(R.string.scan_now), pendingIntent)
            .build()

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } else {
            // Handle the case where permission is not granted, e.g., log an error or inform the user.
        }
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.cleanup_notification_title)
            val channel = NotificationChannel(NOTIFICATION_CHANNEL, name, NotificationManager.IMPORTANCE_DEFAULT)
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL = "cleanup_suggestion"
        const val NOTIFICATION_ID = 1001
    }
}
