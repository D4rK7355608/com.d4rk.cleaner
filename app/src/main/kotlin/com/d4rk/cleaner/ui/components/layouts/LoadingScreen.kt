package com.d4rk.cleaner.ui.components.layouts

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun LoadingScreen(progressAlpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
            .alpha(progressAlpha),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}