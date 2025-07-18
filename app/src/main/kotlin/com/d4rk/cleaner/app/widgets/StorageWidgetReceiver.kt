package com.d4rk.cleaner.app.widgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.d4rk.cleaner.app.widgets.ui.StorageStatsWidget

class StorageWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StorageStatsWidget()
}
