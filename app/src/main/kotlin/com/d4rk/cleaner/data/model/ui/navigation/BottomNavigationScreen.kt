package com.d4rk.cleaner.data.model.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.sharp.AppRegistration
import androidx.compose.material.icons.sharp.Storage
import androidx.compose.ui.graphics.vector.ImageVector
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.constants.ui.bottombar.BottomBarRoutes

sealed class BottomNavigationScreen(
    val route : String , val icon : ImageVector , val selectedIcon : ImageVector , val title : Int
) {
    data object Home : BottomNavigationScreen(
        BottomBarRoutes.HOME , Icons.Outlined.Home , Icons.Filled.Home , com.d4rk.android.libs.apptoolkit.R.string.home
    )

    data object AppManager : BottomNavigationScreen(
        BottomBarRoutes.APP_MANAGER , Icons.Sharp.AppRegistration , Icons.Rounded.AppRegistration , R.string.app_manager
    )

    data object MemoryManager : BottomNavigationScreen(
        BottomBarRoutes.MEMORY_MANAGER , Icons.Sharp.Storage , Icons.Rounded.Storage , R.string.memory_manager
    )
}