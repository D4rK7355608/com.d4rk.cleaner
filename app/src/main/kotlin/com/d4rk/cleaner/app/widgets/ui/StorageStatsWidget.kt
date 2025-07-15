package com.d4rk.cleaner.app.widgets.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo
import com.d4rk.cleaner.app.widgets.data.StorageStatsRepository
import com.d4rk.cleaner.app.widgets.domain.actions.cleanAction
import com.d4rk.cleaner.app.widgets.domain.actions.scanAction
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class StorageStatsWidget : GlanceAppWidget(errorUiLayout = R.layout.widget_error) {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = StorageStatsRepository(context.applicationContext as Application)
        val info = withContext(Dispatchers.IO) { repository.getStorageInfo() }
        provideContent { StorageStatsContent(info) }
    }
}

@Composable
private fun StorageStatsContent(storageInfo: StorageInfo) {

    val storageColorProvider = ColorProvider(day = Color(0xFF2196F3), night = Color(0xFF90CAF9)) // Blue
    val progressBarBackgroundColorProvider = ColorProvider(day = Color(0xFFE3F2FD), night = Color(0xFF1A237E)) // Light blue / Dark blue
    val primaryTextColor = ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFF212121)) // White / Dark text
    val widgetSurfaceVariantColor = ColorProvider(day = Color(0xFF757575), night = Color(0xFFBDBDBD)) // Grey

    val widgetTitleColorProvider = widgetSurfaceVariantColor
    val widgetContentColorProvider = progressBarBackgroundColorProvider

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_voice_selection),
                contentDescription = stringResource(id = R.string.storage_information),
                modifier = GlanceModifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.widget_storage_title),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = widgetTitleColorProvider
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        Box(modifier = GlanceModifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val progress = if (storageInfo.storageUsageProgress == 0f) 0f else storageInfo.usedStorage.toFloat() / storageInfo.storageUsageProgress
            LinearProgressIndicator(
                progress = progress,
                modifier = GlanceModifier.fillMaxWidth().height(10.dp),
                color = storageColorProvider,
                backgroundColor = widgetContentColorProvider
            )
            Text(
                text = "${(progress * 100).toInt()}% ${stringResource(id = R.string.used)}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = primaryTextColor
                ),
                modifier = GlanceModifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StorageDetailItem(stringResource(id = R.string.used), FileSizeFormatter.format(storageInfo.usedStorage))
            Spacer(modifier = GlanceModifier.width(16.dp))
            StorageDetailItem(stringResource(id = R.string.free), FileSizeFormatter.format(storageInfo.freeStorage))
            Spacer(modifier = GlanceModifier.width(16.dp))
            StorageDetailItem(stringResource(id = R.string.total), FileSizeFormatter.format(storageInfo.storageUsageProgress.toLong()))
        }

        Spacer(modifier = GlanceModifier.height(16.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionButton(
                text = stringResource(id = R.string.widget_clean_up),
                action = GlanceModifier.cleanAction(),
                icon = R.drawable.ic_person_pin
            )
            Spacer(modifier = GlanceModifier.width(16.dp))
            ActionButton(
                text = stringResource(id = R.string.scan_now),
                action = GlanceModifier.scanAction(),
                icon = R.drawable.ic_folder_search
            )
        }
    }
}

@Composable
private fun StorageDetailItem(label: String, value: String) {
    val labelColor = ColorProvider(day = Color(0xFF757575), night = Color(0xFFBDBDBD))
    val valueColor = ColorProvider(day = Color(0xFF212121), night = Color(0xFFFFFFFF))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = TextStyle(fontSize = 12.sp, color = labelColor)
        )
        Text(
            text = value,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = valueColor)
        )
    }
}

@Composable
private fun ActionButton(text: String, action: GlanceModifier, icon: Int) {
    val textColor = ColorProvider(day = Color(0xFF2196F3), night = Color(0xFF90CAF9))

    Row(

        modifier = action.padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(icon),
            contentDescription = text,
            modifier = GlanceModifier.padding(end = 6.dp)
        )
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        )
    }
}
