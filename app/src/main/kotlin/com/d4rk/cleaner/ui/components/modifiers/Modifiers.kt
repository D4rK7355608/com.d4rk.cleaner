package com.d4rk.cleaner.ui.components.modifiers

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.button.ButtonState

@Composable
fun Modifier.bounceClick(
    animationEnabled : Boolean = true ,
) : Modifier = composed {
    var buttonState : ButtonState by remember { mutableStateOf(ButtonState.Idle) }
    val context : Context = LocalContext.current
    val dataStore : DataStore = AppCoreManager.dataStore
    val bouncyButtonsEnabled : Boolean by dataStore.bouncyButtons.collectAsState(initial = true)
    val scale : Float by animateFloatAsState(
        if (buttonState == ButtonState.Pressed && animationEnabled && bouncyButtonsEnabled) 0.96f else 1f ,
        label = "Button Press Scale Animation"
    )

    if (bouncyButtonsEnabled) {
        return@composed this
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .pointerInput(buttonState) {
                    awaitPointerEventScope {
                        buttonState = if (buttonState == ButtonState.Pressed) {
                            waitForUpOrCancellation()
                            ButtonState.Idle
                        }
                        else {
                            awaitFirstDown(requireUnconsumed = false)
                            ButtonState.Pressed
                        }
                    }
                }
    }
    else {
        return@composed this
    }
}

fun Modifier.hapticDrawerSwipe(drawerState : DrawerState) : Modifier = composed {
    val haptic : HapticFeedback = LocalHapticFeedback.current
    var hasVibrated : Boolean by remember { mutableStateOf(value = false) }

    LaunchedEffect(drawerState.currentValue , drawerState.targetValue) {
        if (drawerState.isAnimationRunning && ! hasVibrated) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            hasVibrated = true
        }

        if (! drawerState.isAnimationRunning) {
            hasVibrated = false
        }
    }

    return@composed this
}

fun Modifier.hapticPagerSwipe(pagerState : PagerState) : Modifier = composed {
    val haptic : HapticFeedback = LocalHapticFeedback.current
    var hasVibrated : Boolean by remember { mutableStateOf(value = false) }

    LaunchedEffect(pagerState.isScrollInProgress) {
        if (pagerState.isScrollInProgress && ! hasVibrated) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            hasVibrated = true
        }
        else if (! pagerState.isScrollInProgress) {
            hasVibrated = false
        }
    }

    return@composed this
}

fun Modifier.animateVisibility(
    visible : Boolean = true ,
    index : Int = 0 ,
    offsetY : Int = 50 ,
    durationMillis : Int = 300 ,
    delayPerItem : Int = 64
) = composed {
    val alpha = animateFloatAsState(
        targetValue = if (visible) 1f else 0f , animationSpec = tween(
            durationMillis = durationMillis , delayMillis = index * delayPerItem
        ) , label = "Alpha"
    )

    val offsetYState = animateFloatAsState(
        targetValue = if (visible) 0f else offsetY.toFloat() , animationSpec = tween(
            durationMillis = durationMillis , delayMillis = index * delayPerItem
        ) , label = "OffsetY"
    )

    this
            .offset { IntOffset(x = 0 , offsetYState.value.toInt()) }
            .graphicsLayer {
                this.alpha = alpha.value
            }
            .padding(vertical = 4.dp)
}