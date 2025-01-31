package com.d4rk.cleaner.ui.components.modifiers

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

fun Modifier.hapticPagerSwipe(pagerState : PagerState) : Modifier = composed {
    val haptic : HapticFeedback = LocalHapticFeedback.current
    var hasVibrated : Boolean by remember { mutableStateOf(value = false) }

    LaunchedEffect(key1 = pagerState.isScrollInProgress) {
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