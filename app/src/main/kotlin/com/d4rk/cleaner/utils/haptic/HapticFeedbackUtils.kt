package com.d4rk.cleaner.utils.haptic

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

// TODO: Use in compose
@Composable
fun PerformHapticFeedback() {
    val haptic = LocalHapticFeedback.current
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
}

/**
 * Performs a slight haptic feedback.
 */
fun View.weakHapticFeedback() {
    this.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
}

/**
 * Performs a strong haptic feedback.
 */
fun View.strongHapticFeedback() {
    this.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
}