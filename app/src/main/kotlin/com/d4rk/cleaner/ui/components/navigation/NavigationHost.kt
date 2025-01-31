package com.d4rk.cleaner.ui.components.navigation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.d4rk.android.libs.apptoolkit.utils.helpers.ScreenHelper
import com.d4rk.cleaner.constants.ui.bottombar.BottomBarRoutes
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.navigation.BottomNavigationScreen
import com.d4rk.cleaner.ui.screens.appmanager.AppManagerScreen
import com.d4rk.cleaner.ui.screens.home.HomeScreen
import com.d4rk.cleaner.ui.screens.memory.MemoryManagerComposable

@Composable
fun NavigationHost(
    navHostController : NavHostController , dataStore : DataStore , paddingValues : PaddingValues
) {
    val context : Context = LocalContext.current
    val startupPage : String = dataStore.getStartupPage().collectAsState(initial = BottomBarRoutes.HOME).value
    val isTabletOrLandscape : Boolean = ScreenHelper.isLandscapeOrTablet(context = context)

    val finalPaddingValues : PaddingValues = if (isTabletOrLandscape) {
        PaddingValues(bottom = paddingValues.calculateBottomPadding())
    }
    else {
        paddingValues
    }

    NavHost(navController = navHostController , startDestination = startupPage) {
        composable(route = BottomNavigationScreen.Home.route) {
            Box(modifier = Modifier.padding(paddingValues = finalPaddingValues)) {
                HomeScreen()
            }
        }

        composable(route = BottomNavigationScreen.AppManager.route) {
            Box(modifier = Modifier.padding(paddingValues = finalPaddingValues)) {
                AppManagerScreen()
            }
        }

        composable(route = BottomNavigationScreen.MemoryManager.route) {
            Box(modifier = Modifier.padding(paddingValues = finalPaddingValues)) {
                MemoryManagerComposable()
            }
        }
    }
}