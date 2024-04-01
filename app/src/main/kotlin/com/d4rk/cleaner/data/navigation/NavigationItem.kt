package com.d4rk.cleaner.data.navigation

import androidx.compose.ui.graphics.painter.Painter

data class NavigationItem(
    val title : Int,
    val selectedIcon : Painter,
    val badgeCount : Int? = null
)