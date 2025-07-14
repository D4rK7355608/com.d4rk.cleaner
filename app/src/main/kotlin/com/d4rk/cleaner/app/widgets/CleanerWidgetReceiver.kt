package com.d4rk.cleaner.app.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.compose.ui.unit.dp
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Spacer
import androidx.glance.layout.width
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.auto.AutoCleanScheduler
import com.d4rk.cleaner.app.main.ui.MainActivity
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CleanerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CleanerWidget()
}

class CleanerWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { WidgetContent() }
    }

    @Composable
    private fun WidgetContent() {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_folder_search),
                contentDescription = "scan",
                modifier = GlanceModifier.clickable(actionRunCallback<OpenScanAction>())
            )
            Spacer(modifier = GlanceModifier.width(16.dp))
            Image(
                provider = ImageProvider(R.drawable.ic_auto_fix_high),
                contentDescription = "clean",
                modifier = GlanceModifier.clickable(actionRunCallback<RunCleanAction>())
            )
        }
    }
}

class OpenScanAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        if (context.isWidgetActionsEnabled()) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("open_scan", true)
            }
            context.startActivity(intent)
        }
    }
}

class RunCleanAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        if (context.isWidgetActionsEnabled()) {
            AutoCleanScheduler.runOnce(context)
        }
    }
}

private fun Context.isWidgetActionsEnabled(): Boolean {
    val ds = DataStore(this)
    return runBlocking { ds.widgetActionsEnabled.first() }
}
