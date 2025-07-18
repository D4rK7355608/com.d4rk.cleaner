package com.d4rk.cleaner.app.widgets.domain.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.d4rk.cleaner.app.auto.AutoCleanScheduler
import com.d4rk.cleaner.app.widgets.domain.actions.isWidgetActionsEnabled

class RunCleanAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        if (context.isWidgetActionsEnabled()) {
            AutoCleanScheduler.runOnce(context)
        }
    }
}
