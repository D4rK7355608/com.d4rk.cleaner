package com.d4rk.cleaner.utils.compose.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.utils.compose.bounceClick

/**
 * Composable function representing a circular determinate progress indicator with storage information.
 *
 * This composable displays a circular progress indicator representing a determinate progress value.
 * It also shows storage usage information (used/total) in gigabytes (GB).
 *
 * @param progress The progress value as a float, representing the completion percentage of the progress indicator.
 * @param storageUsed The amount of storage used, formatted as a string (e.g., "2.5 GB").
 * @param storageTotal The total amount of storage, formatted as a string (e.g., "10 GB").
 * @param modifier The modifier for styling and layout customization.
 */
@Composable
fun CircularDeterminateIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val animatedProgress: Float by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "Storage Progress Load Animation"
    )

    Box(
        contentAlignment = Alignment.Center, modifier = modifier.size(240.dp)
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer,
            strokeWidth = 8.dp,
        )
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .animateContentSize()
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 8.dp,
            strokeCap = StrokeCap.Round,
        )

        FilledTonalButton(
            modifier = Modifier
                .animateContentSize()
                .fillMaxSize()
                .padding(16.dp)
                .bounceClick(),
            onClick = {
                onClick()
            },
            colors = ButtonDefaults.filledTonalButtonColors()
        ) {
            Text(
                text = "Click here to clean",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }

        /*        Text(
                    modifier = Modifier.animateContentSize(),
                    text = stringResource(id = R.string.storage_used, storageUsed, storageTotal),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )*/
    }
}

@Composable
fun StorageProgressBar(storageInfo: StorageInfo) {
    val progress: Float =
        (storageInfo.usedStorage.toFloat() / storageInfo.totalStorage.toFloat()).coerceIn(
            0f, 1f
        )
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .height(8.dp),
        color = MaterialTheme.colorScheme.primary,
    )
}