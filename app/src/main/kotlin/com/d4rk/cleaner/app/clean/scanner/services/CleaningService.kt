package com.d4rk.cleaner.app.clean.scanner.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.utils.helpers.CleaningProgressBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.provider.MediaStore
import java.io.File

class CleaningService : Service() {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Cleaning", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val paths = intent?.getStringArrayExtra(EXTRA_PATHS) ?: return START_NOT_STICKY
        startForeground(NOTIFICATION_ID, buildNotification(0))
        scope.launch {
            performDelete(paths.map { File(it) })
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private suspend fun performDelete(files: List<File>) {
        val total = files.size
        files.chunked(50).forEachIndexed { chunkIndex, chunk ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val uris = chunk.map { it.toUri() }
                val request = MediaStore.createDeleteRequest(contentResolver, uris)
                request.intentSender?.let { sender ->
                    withContext(Dispatchers.Main) { startIntentSender(sender, null, 0, 0, 0) }
                }
            } else {
                chunk.forEach { it.deleteRecursively() }
            }
            val progress = (((chunkIndex + 1) * chunk.size) * 100 / total).coerceAtMost(100)
            CleaningProgressBus.update(progress)
            notificationManager.notify(NOTIFICATION_ID, buildNotification(progress))
        }
        CleaningProgressBus.update(100)
    }

    private fun buildNotification(progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.deleting_files_progress, progress))
            .setSmallIcon(R.drawable.ic_cleaner_notify)
            .setProgress(100, progress, false)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val EXTRA_PATHS = "extra_paths"
        private const val CHANNEL_ID = "cleaning_service"
        private const val NOTIFICATION_ID = 1001
    }
}
