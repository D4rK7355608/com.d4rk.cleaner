package com.d4rk.cleaner.app.clean.analyze.ui.components

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith

object SelectAllTransitions {
    private const val DURATION = 300
    private val fadeScaleSpec = tween<Float>(DURATION)

    val fadeScale: ContentTransform by lazy {
        (fadeIn(animationSpec = fadeScaleSpec) + scaleIn(animationSpec = fadeScaleSpec))
            .togetherWith(fadeOut(animationSpec = fadeScaleSpec) + scaleOut(animationSpec = fadeScaleSpec))
    }
}
