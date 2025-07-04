package com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants

@Composable
fun CleanerProgressIndicator(
    progress: Float,
    icon: Painter,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
) {
    val animatedProgress: Float by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "Cleaner progress animation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer,
            strokeWidth = SizeConstants.ExtraSmallSize,
        )
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .animateContentSize()
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = SizeConstants.ExtraSmallSize,
            strokeCap = StrokeCap.Round,
        )
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(size / 2),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
