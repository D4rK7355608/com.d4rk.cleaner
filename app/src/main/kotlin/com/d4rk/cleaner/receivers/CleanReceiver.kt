package com.d4rk.cleaner.receivers
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.d4rk.cleaner.services.ScheduledWorker
class CleanReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == null) {
                        enqueueWork(context)
                } else {
                        scheduleAlarm(context)
                }
        }
        companion object {
                private const val PERIOD = 86400000
                private const val INITIAL_DELAY = 3600000
                @JvmStatic
                fun enqueueWork(context: Context) {
                        val workRequest = OneTimeWorkRequest.Builder(ScheduledWorker::class.java).build()
                        WorkManager.getInstance(context).enqueue(workRequest)
                }
                @JvmStatic
                fun scheduleAlarm(context: Context) {
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(context, CleanReceiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + INITIAL_DELAY, PERIOD.toLong(), pendingIntent)
                }
                @JvmStatic
                fun cancelAlarm(context: Context) {
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(context, CleanReceiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                        alarmManager.cancel(pendingIntent)
                }
        }
}