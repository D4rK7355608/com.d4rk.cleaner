package com.d4rk.cleaner.app.widgets.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.compose.ui.unit.dp
import android.app.Application
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo
import com.d4rk.cleaner.app.widgets.data.StorageStatsRepository
import com.d4rk.cleaner.app.widgets.domain.actions.cleanAction
import com.d4rk.cleaner.app.widgets.domain.actions.scanAction
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter
import androidx.glance.appwidget.LinearProgressIndicator

class StorageStatsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = StorageStatsRepository(context.applicationContext as Application)
        val info = repository.getStorageInfo()
        provideContent { StorageStatsContent(info) }
    }
}

@Composable
private fun StorageStatsContent(storageInfo: StorageInfo) {
    Column(
        modifier = GlanceModifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Storage", style = TextStyle(fontWeight = FontWeight.Bold))
        Spacer(modifier = GlanceModifier.height(8.dp))
        val progress = if (storageInfo.storageUsageProgress == 0f) 0f else storageInfo.usedStorage.toFloat() / storageInfo.storageUsageProgress
        LinearProgressIndicator(progress = progress, modifier = GlanceModifier.fillMaxWidth())
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(text = "Used: ${FileSizeFormatter.format(storageInfo.usedStorage)}")
        Text(text = "Free: ${FileSizeFormatter.format(storageInfo.freeStorage)}")
        Text(text = "Total: ${FileSizeFormatter.format(storageInfo.storageUsageProgress.toLong())}")
        Spacer(modifier = GlanceModifier.height(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Scan", modifier = GlanceModifier.scanAction())
            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(text = "Clean", modifier = GlanceModifier.cleanAction())
        }
    }
}
