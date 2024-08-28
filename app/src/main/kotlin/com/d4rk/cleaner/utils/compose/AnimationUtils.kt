package com.d4rk.cleaner.utils.compose

import android.annotation.SuppressLint
import android.view.View
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import com.d4rk.cleaner.data.model.ui.button.ButtonState
import com.d4rk.cleaner.utils.haptic.weakHapticFeedback

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun Modifier.bounceClick() = composed {
    var buttonState: ButtonState by remember { mutableStateOf(ButtonState.Idle) }
    val view : View = LocalView.current
    val scale: Float by animateFloatAsState(
        if (buttonState == ButtonState.Pressed) 0.95f else 1f, label = "Button Press Scale Animation"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                view.weakHapticFeedback()
            })
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(requireUnconsumed = false)
                    ButtonState.Pressed
                }
            }
        }
}