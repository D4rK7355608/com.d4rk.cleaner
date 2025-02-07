package com.d4rk.cleaner.core.data.model.ui.screens

import com.d4rk.android.libs.apptoolkit.data.model.ui.navigation.NavigationDrawerItem
import com.d4rk.cleaner.core.data.model.ui.navigation.BottomNavigationScreen

data class UiMainScreen(
    val navigationDrawerItems : List<NavigationDrawerItem> = listOf() ,
    val bottomNavigationItems : List<com.d4rk.cleaner.core.data.model.ui.navigation.BottomNavigationScreen> = listOf() ,
    val currentBottomNavigationScreen : com.d4rk.cleaner.core.data.model.ui.navigation.BottomNavigationScreen = com.d4rk.cleaner.core.data.model.ui.navigation.BottomNavigationScreen.Home ,
    val trashSize : String = "0 KB" ,
)