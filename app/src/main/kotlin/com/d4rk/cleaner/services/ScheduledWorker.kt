package com.d4rk.cleaner.services
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.d4rk.cleaner.FileScanner
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.home.HomeFragment.Companion.convertSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class ScheduledWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val context = applicationContext
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_daily_clean), false)) {
            return@withContext Result.success()
        }
        try {
            val path = Environment.getExternalStorageDirectory()
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val fileScanner = FileScanner(path, context)
                .setEmptyDir(preferences.getBoolean(context.getString(R.string.key_filter_empty), false))
                .setAutoWhite(preferences.getBoolean(context.getString(R.string.key_auto_whitelist), true))
                .setDelete(true)
                .setInvalid(preferences.getBoolean(context.getString(R.string.key_invalid_media_cleaner), false))
                .setCorpse(preferences.getBoolean(context.getString(R.string.key_filter_corpse), false))
                .setGUI(null)
                .setContext(context)
                .setUpFilters(preferences.getBoolean(context.getString(R.string.key_filter_generic), true), preferences.getBoolean(context.getString(R.string.key_filter_aggressive), false), preferences.getBoolean(context.getString(R.string.key_filter_apk), false), preferences.getBoolean(context.getString(R.string.key_filter_archive), false))
            val kilobytesTotal = fileScanner.startScan()
            val title = context.getString(R.string.service_notification_title) + " " + convertSize(kilobytesTotal)
            makeStatusNotification(title, context)
        } catch (e: Exception) {
            makeStatusNotification(e.toString(), context)
        }
        Result.success()
    }
    companion object {
        fun makeStatusNotification(message: String?, context: Context) {
            val verboseNotificationChannelName = context.getString(R.string.daily_cleaner_notification_channel_name)
            val verboseNotificationChannelDescription = context.getString(R.string.daily_cleaner_notification_channel_description)
            val notificationTitle = context.getString(R.string.cleaning_completed_notification_title)
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