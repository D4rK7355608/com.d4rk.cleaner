package com.d4rk.cleaner.data.navigation

import com.d4rk.cleaner.R

sealed class Screen(
    val route: String,
    val icon: Int,
    val selectedIcon: Int,
    val title: Int
) {
    data object Home :
        Screen("Home", R.drawable.ic_home, R.drawable.ic_home_filled, R.string.home)

    data object AppManager : Screen(
        "App Manager",
        R.drawable.ic_app_registration_sharp,
        R.drawable.ic_app_registration_rounded,
        R.string.app_manager
    )

    data object MemoryManager : Screen(
        "Memory Manager",
        R.drawable.ic_storage_sharp,
        R.drawable.ic_storage_rounded,
        R.string.memory_manager
    )
}