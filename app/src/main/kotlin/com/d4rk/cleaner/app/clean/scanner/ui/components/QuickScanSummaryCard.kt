package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DiscFull
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.digits.AnimatedDigit
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.notifications.notifications.CleanerMessageProvider

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

    val context = LocalContext.current
    val tip: String = remember { CleanerMessageProvider.getRandomQuickScanTip(context = context) }

    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimation = true }

    val animatedFreePercent by animateIntAsState(
        targetValue = if (startAnimation) freePercent else 0,
        animationSpec = tween(durationMillis = 1000),
        label = "FreePercentAnimation"
    )
    val animatedUsedPercent by animateIntAsState(
        targetValue = if (startAnimation) usedPercent else 0,
        animationSpec = tween(durationMillis = 1000),
        label = "UsedPercentAnimation"
    )

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = SizeConstants.ExtraLargeSize),
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
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.animateContentSize()
                    )
                }

                SmallVerticalSpacer()

                Row(verticalAlignment = Alignment.CenterVertically) {

                    val storageIcon = when {
                        usedPercent >= 75 -> Icons.Outlined.DiscFull
                        usedPercent >= 25 -> Icons.Outlined.Storage
                        else -> Icons.Outlined.Storage
                    }

                    Icon(
                        imageVector = storageIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    SmallHorizontalSpacer()
                    val freeColor = when {
                        freePercent >= 75 -> MaterialTheme.colorScheme.tertiary
                        freePercent >= 50 -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                    val usedColor = when {
                        usedPercent >= 90 -> MaterialTheme.colorScheme.error
                        usedPercent >= 75 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.animateContentSize()
                    ) {
                        Text(
                            text = stringResource(id = R.string.free) + " ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row {
                            animatedFreePercent.toString().forEach { digit ->
                                AnimatedDigit(
                                    digit = digit,
                                    color = freeColor,
                                    textStyle = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Text(text = "% • ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = stringResource(id = R.string.used) + " ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row {
                            animatedUsedPercent.toString().forEach { digit ->
                                AnimatedDigit(
                                    digit = digit,
                                    color = usedColor,
                                    textStyle = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Text(text = "%", style = MaterialTheme.typography.bodyMedium)
                    }
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
                        text = tip,
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
