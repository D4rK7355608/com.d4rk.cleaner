package com.d4rk.cleaner.ui.components.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationDrawerItem(
    val title: Int, val selectedIcon: ImageVector, val badgeCount: Int? = null
)