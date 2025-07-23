package com.d4rk.cleaner.app.clean.memory.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo

@Composable
fun StorageInfoCard(storageInfo: StorageInfo) {
    Column(
        modifier = Modifier
            .padding(all = SizeConstants.LargeSize)
            .animateContentSize()
    ) {
        Text(
            modifier = Modifier.basicMarquee(),
            text = stringResource(id = R.string.storage_information),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        SmallVerticalSpacer()
        LinearProgressIndicator(
            progress = {
                if (storageInfo.storageUsageProgress.toLong() == 0L) {
                    0f
                } else {
                    storageInfo.usedStorage.toFloat() / storageInfo.storageUsageProgress
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(height = SizeConstants.SmallSize),
            color = MaterialTheme.colorScheme.primary,
        )
        SmallVerticalSpacer()
        StorageInfoText(label = stringResource(id = R.string.used), size = storageInfo.usedStorage)
        StorageInfoText(label = stringResource(id = R.string.free), size = storageInfo.freeStorage)
        StorageInfoText(
            label = stringResource(id = R.string.total),
            size = storageInfo.storageUsageProgress.toLong()
        )
    }
}