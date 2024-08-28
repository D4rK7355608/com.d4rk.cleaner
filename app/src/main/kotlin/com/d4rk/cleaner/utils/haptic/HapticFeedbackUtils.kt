package com.d4rk.cleaner.utils.haptic

import android.view.HapticFeedbackConstants
import android.view.View

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