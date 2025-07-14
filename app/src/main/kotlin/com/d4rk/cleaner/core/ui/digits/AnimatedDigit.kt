package com.d4rk.cleaner.core.ui.digits

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.LocalContentColor

@Composable
fun AnimatedDigit(
    digit: Char,
    color: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall
) {
    AnimatedContent(
        targetState = digit,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically(
                    animationSpec = tween(durationMillis = 400),
                    initialOffsetY = { fullHeight: Int -> -fullHeight }
                ) + fadeIn(animationSpec = tween(durationMillis = 400))).togetherWith(
                    slideOutVertically(
                        animationSpec = tween(durationMillis = 400),
                        targetOffsetY = { fullHeight: Int -> fullHeight }
                    ) + fadeOut(animationSpec = tween(durationMillis = 400))
                )
            } else {
                (slideInVertically(
                    animationSpec = tween(durationMillis = 400),
                    initialOffsetY = { fullHeight: Int -> fullHeight }
                ) + fadeIn(animationSpec = tween(durationMillis = 400))).togetherWith(
                    slideOutVertically(
                        animationSpec = tween(durationMillis = 400),
                        targetOffsetY = { fullHeight: Int -> -fullHeight }
                    ) + fadeOut(animationSpec = tween(durationMillis = 400))
                )
            }.using(sizeTransform = SizeTransform(clip = false))
        }
    ) { targetDigit: Char ->
        CompositionLocalProvider(LocalContentColor provides color) {
            Text(
                text = targetDigit.toString(),
                style = textStyle,
                fontWeight = textStyle.fontWeight ?: MaterialTheme.typography.headlineSmall.fontWeight
            )
        }
    }
}
