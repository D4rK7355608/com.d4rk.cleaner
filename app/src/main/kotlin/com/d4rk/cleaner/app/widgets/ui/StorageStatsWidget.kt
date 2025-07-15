package com.d4rk.cleaner.app.widgets.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.glance.unit.ColorProvider
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo
import com.d4rk.cleaner.app.widgets.data.StorageStatsRepository
import com.d4rk.cleaner.app.widgets.domain.actions.cleanAction
import com.d4rk.cleaner.app.widgets.domain.actions.scanAction
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter

class StorageStatsWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = StorageStatsRepository(context.applicationContext as Application)
        val info = repository.getStorageInfo()
        provideContent { StorageStatsContent(info) }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun StorageStatsContent(storageInfo: StorageInfo) {
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
                    color = ColorProvider(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        Box(modifier = GlanceModifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val progress = if (storageInfo.storageUsageProgress == 0f) 0f else storageInfo.usedStorage.toFloat() / storageInfo.storageUsageProgress
            LinearProgressIndicator(
                progress = progress,
                modifier = GlanceModifier.fillMaxWidth().height(10.dp),
                color = ColorProvider(MaterialTheme.colorScheme.primary),
                backgroundColor = ColorProvider(MaterialTheme.colorScheme.secondaryContainer)
            )
            Text(
                text = "${(progress * 100).toInt()}% ${stringResource(id = R.string.used)}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(MaterialTheme.colorScheme.onPrimary)
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = TextStyle(fontSize = 12.sp, color = ColorProvider(MaterialTheme.colorScheme.onSurfaceVariant))
        )
        Text(
            text = value,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ColorProvider(MaterialTheme.colorScheme.onSurface))
        )
    }
}

@Composable
private fun ActionButton(text: String, action: GlanceModifier, icon: Int) {
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
                color = ColorProvider(MaterialTheme.colorScheme.primary)
            )
        )
    }
}

