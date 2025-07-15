package com.d4rk.cleaner.core.ui.digits

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset

object AnimatedDigitTransitions {

    private const val DURATION = 400
    private val slideSpec = tween<IntOffset>(DURATION)
    private val fadeSpec = tween<Float>(DURATION)

    val increase by lazy {
        (slideInVertically(slideSpec) { -it } + fadeIn(fadeSpec)).togetherWith(
            slideOutVertically(slideSpec) { it } + fadeOut(fadeSpec)
        ).apply { SizeTransform(clip = false) }
    }

    val decrease by lazy {
        (slideInVertically(slideSpec) { it } + fadeIn(fadeSpec)).togetherWith(
            slideOutVertically(slideSpec) { -it } + fadeOut(fadeSpec)
        ).apply { SizeTransform(clip = false) }
    }
}

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
                AnimatedDigitTransitions.increase
            } else {
                AnimatedDigitTransitions.decrease
            }
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
