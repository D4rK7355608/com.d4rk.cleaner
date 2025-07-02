package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R

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
        colors = CardDefaults.outlinedCardColors(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(SizeConstants.LargeSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CleaningServices,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    SmallHorizontalSpacer()
                    Text(
                        text = stringResource(id = R.string.quick_scan_cleaned_format, cleanedSize),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                SmallVerticalSpacer()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Storage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    SmallHorizontalSpacer()
                    val freeColor = MaterialTheme.colorScheme.tertiary
                    val usedColor = MaterialTheme.colorScheme.error

                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.free) + " ")
                            withStyle(SpanStyle(color = freeColor)) {
                                append("$freePercent%")
                            }
                            append(" • ")
                            append(stringResource(id = R.string.used) + " ")
                            withStyle(SpanStyle(color = usedColor)) {
                                append("$usedPercent%")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                SmallVerticalSpacer()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SmallHorizontalSpacer()
                    Text(
                        text = stringResource(id = R.string.quick_scan_summary_tip),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
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
