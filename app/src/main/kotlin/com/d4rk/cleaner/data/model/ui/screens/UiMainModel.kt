package com.d4rk.cleaner.data.model.ui.screens

import com.d4rk.cleaner.data.model.ui.navigation.BottomNavigationScreen

data class UiMainModel(
    val currentBottomNavigationScreen: BottomNavigationScreen = BottomNavigationScreen.Home,
    val trashSize: String = "0 KB",
)