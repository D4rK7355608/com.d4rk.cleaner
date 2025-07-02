package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.R
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants

@Composable
fun QuickScanSummaryCard(
    cleanedSize: String,
    freePercent: Int,
    usedPercent: Int,
    progress: Float,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 112.dp,
    onQuickScanClick: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.quick_scan_cleaned_format, cleanedSize),
                    style = MaterialTheme.typography.titleMedium
                )
                SmallVerticalSpacer()
                Text(
                    text = stringResource(id = R.string.quick_scan_free_format, freePercent, usedPercent),
                    style = MaterialTheme.typography.bodyMedium
                )
                SmallVerticalSpacer()
                Text(
                    text = stringResource(id = R.string.quick_scan_summary_tip),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Box(
                modifier = Modifier.padding(start = SizeConstants.LargeSize),
                contentAlignment = Alignment.Center
            ) {
                StorageProgressButton(
                    progress = progress,
                    size = buttonSize,
                    onClick = onQuickScanClick
                )
            }
        }
    }
}
