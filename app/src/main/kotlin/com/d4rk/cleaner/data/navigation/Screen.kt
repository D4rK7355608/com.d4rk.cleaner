package com.d4rk.cleaner.data.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.sharp.AppRegistration
import androidx.compose.material.icons.sharp.Storage
import androidx.compose.ui.graphics.vector.ImageVector
import com.d4rk.cleaner.R

sealed class Screen(
    val route : String , val icon : ImageVector , val selectedIcon : ImageVector , val title : Int
) {
    data object Home : Screen("Home" , Icons.Outlined.Home , Icons.Filled.Home , R.string.home)

    data object AppManager : Screen(
        "App Manager" ,
        Icons.Sharp.AppRegistration ,
        Icons.Rounded.AppRegistration ,
        R.string.app_manager
    )

    data object MemoryManager : Screen(
        "Memory Manager" , Icons.Sharp.Storage , Icons.Rounded.Storage , R.string.memory_manager
    )
}