package com.d4rk.cleaner.app.clean.scanner.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo

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
fun StorageProgressButton(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    onClick: () -> Unit = {}
) {
    val view : View = LocalView.current
    val isVisible = remember { mutableStateOf(true) }

    val animatedProgress : Float by animateFloatAsState(
        targetValue = progress , animationSpec = tween(durationMillis = 1000 , easing = LinearOutSlowInEasing) , label = "Storage Progress Load Animation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        CircularProgressIndicator(
            progress = { 1f } ,
            modifier = Modifier.fillMaxSize() ,
            color = MaterialTheme.colorScheme.primaryContainer ,
            strokeWidth = SizeConstants.ExtraSmallSize ,
        )
        CircularProgressIndicator(
            progress = { animatedProgress } ,
            modifier = Modifier
                    .animateContentSize()
                    .fillMaxSize() ,
            color = MaterialTheme.colorScheme.primary ,
            strokeWidth = SizeConstants.ExtraSmallSize ,
            strokeCap = StrokeCap.Round ,
        )

        AnimatedVisibility(
            visible = isVisible.value , enter = scaleIn() , exit = scaleOut()
        ) {
            FilledTonalButton(modifier = Modifier
                    .animateContentSize()
                    .fillMaxSize()
                    .padding(all = SizeConstants.SmallSize)
                    .bounceClick() , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onClick()
            } , colors = ButtonDefaults.filledTonalButtonColors()) {
                Text(
                    text = stringResource(id = R.string.quick_scan),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun StorageProgressBar(storageInfo : StorageInfo) {
    val progress : Float = (storageInfo.usedStorage.toFloat() / storageInfo.storageUsageProgress).coerceIn(
        0f , 1f
    )
    LinearProgressIndicator(
        progress = { progress } ,
        modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .height(SizeConstants.SmallSize) ,
        color = MaterialTheme.colorScheme.primary ,
    )
}