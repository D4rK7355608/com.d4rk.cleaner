package com.d4rk.cleaner.app.main.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.sharp.AppRegistration
import androidx.compose.material.icons.sharp.Storage
import androidx.compose.ui.graphics.vector.ImageVector
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.cleaner.app.main.utils.constants.NavigationRoutes

sealed class BottomNavigationScreen(
    val route : String , val icon : ImageVector , val selectedIcon : ImageVector , val title : Int
) {
    data object Home : BottomNavigationScreen(
        NavigationRoutes.ROUTE_HOME , Icons.Outlined.Home , Icons.Filled.Home , R.string.home
    )

    data object AppManager : BottomNavigationScreen(
        NavigationRoutes.ROUTE_APP_MANAGER , Icons.Sharp.AppRegistration , Icons.Rounded.AppRegistration , com.d4rk.cleaner.R.string.app_manager
    )

    data object MemoryManager : BottomNavigationScreen(
        NavigationRoutes.ROUTE_MEMORY_MANAGER , Icons.Sharp.Storage , Icons.Rounded.Storage , com.d4rk.cleaner.R.string.memory_manager
    )
}