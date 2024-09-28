package com.d4rk.cleaner.ui.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.d4rk.cleaner.constants.ui.bottombar.BottomBarRoutes
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.navigation.BottomNavigationScreen
import com.d4rk.cleaner.ui.screens.appmanager.AppManagerScreen
import com.d4rk.cleaner.ui.screens.home.HomeScreen
import com.d4rk.cleaner.ui.screens.memory.MemoryManagerComposable

@Composable
fun NavigationHost(
    navHostController : NavHostController ,
    dataStore : DataStore ,
    paddingValues : PaddingValues ,
) {
    val startupPage : String =
            dataStore.getStartupPage().collectAsState(initial = BottomBarRoutes.HOME).value

    NavHost(navController = navHostController , startDestination = startupPage) {
        composable(BottomNavigationScreen.Home.route) {
            Box(modifier = Modifier.padding(paddingValues)) {
                HomeScreen()
            }
        }
        composable(BottomNavigationScreen.AppManager.route) {
            Box(modifier = Modifier.padding(paddingValues)) {
                AppManagerScreen()
            }
        }
        composable(BottomNavigationScreen.MemoryManager.route) {
            Box(modifier = Modifier.padding(paddingValues)) {
                MemoryManagerComposable()
            }
        }
    }
}