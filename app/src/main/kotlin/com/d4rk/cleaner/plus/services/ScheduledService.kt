@file:Suppress("DEPRECATION")
package com.d4rk.cleaner.plus.services
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.plus.FileScanner
import com.d4rk.cleaner.plus.MainActivity.Companion.convertSize
import com.d4rk.cleaner.plus.R
class ScheduledService : JobIntentService() {
    public override fun onHandleWork(i: Intent) {
        try {
            val path = Environment.getExternalStorageDirectory()
            val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val fileScanner = FileScanner(path, applicationContext)
                .setEmptyDir(preferences.getBoolean(getString(R.string.key_filter_empty), false))
                .setAutoWhite(preferences.getBoolean(getString(R.string.key_auto_whitelist), true))
                .setDelete(true)
                .setCorpse(preferences.getBoolean(getString(R.string.key_filter_corpse), false))
                .setGUI(null)
                .setContext(applicationContext)
                .setUpFilters(preferences.getBoolean(getString(R.string.key_filter_generic), true), preferences.getBoolean(getString(R.string.key_filter_aggressive), false), preferences.getBoolean(getString(R.string.key_filter_apk), false), preferences.getBoolean(getString(R.string.key_filter_archive), false))
            val kilobytesTotal = fileScanner.startScan()
            val title = applicationContext.getString(R.string.service_notification_title) + " " + convertSize(kilobytesTotal)
            makeStatusNotification(title, applicationContext)
        } catch (e: Exception) {
            makeStatusNotification(e.toString(), applicationContext)
        }
    }
    companion object {
        private const val UNIQUE_JOB_ID = 1337
        @JvmStatic
        fun enqueueWork(context: Context) {
            enqueueWork(context.applicationContext, ScheduledService::class.java, UNIQUE_JOB_ID, Intent(context, ScheduledService::class.java))
        }
        fun makeStatusNotification(message: String?, context: Context) {
            val verboseNotificationChannelName = context.getString(R.string.settings_notification_name)
            val verboseNotificationChannelDescription = context.getString(R.string.settings_notification_sum)
            val notificationTitle = context.getString(R.string.service_channel_name)
            val channelId = "VERBOSE_NOTIFICATION"
            val notificationId = 1
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, verboseNotificationChannelName, importance).apply {
                description = verboseNotificationChannelDescription
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            val builder = NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(R.drawable.ic_broom)
                setContentTitle(notificationTitle)
                setContentText(message ?: "")
                setAutoCancel(true)
                priority = NotificationCompat.PRIORITY_DEFAULT
                setVibrate(LongArray(0))
            }
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(notificationId, builder.build())
                return
            }
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }
    }
}