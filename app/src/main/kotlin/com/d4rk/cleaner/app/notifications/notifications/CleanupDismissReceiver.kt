package com.d4rk.cleaner.app.notifications.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.d4rk.cleaner.core.data.datastore.DataStore

class CleanupDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            DataStore(context).saveLastCleanupNotificationDismissed(System.currentTimeMillis())
        }
    }
}

