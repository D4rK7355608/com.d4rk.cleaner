package com.d4rk.cleaner.app.widgets.domain.actions

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.d4rk.cleaner.app.main.ui.MainActivity
import com.d4rk.cleaner.app.widgets.domain.actions.isWidgetActionsEnabled

class OpenScanAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: androidx.glance.GlanceId, parameters: ActionParameters) {
        if (context.isWidgetActionsEnabled()) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("open_scan", true)
            }
            context.startActivity(intent)
        }
    }
}
