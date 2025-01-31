package com.d4rk.cleaner.data.model.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationDrawerItem(
    val title : Int , val selectedIcon : ImageVector , val badgeText : String = ""
)