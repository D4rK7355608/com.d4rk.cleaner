package com.d4rk.cleaner.app.clean.dashboard.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically

object DashboardTransitions {
    val enter: EnterTransition by lazy {
        fadeIn() + expandVertically()
    }

    val exit: ExitTransition by lazy {
        fadeOut() + shrinkVertically()
    }
}
