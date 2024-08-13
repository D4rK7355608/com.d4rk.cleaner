package com.d4rk.cleaner.utils.compose

import android.annotation.SuppressLint
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
import com.d4rk.cleaner.data.model.ui.button.ButtonState

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun Modifier.bounceClick() = composed {
    var buttonState: ButtonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale: Float by animateFloatAsState(
        if (buttonState == ButtonState.Pressed) 0.95f else 1f, label = ""
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { })
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