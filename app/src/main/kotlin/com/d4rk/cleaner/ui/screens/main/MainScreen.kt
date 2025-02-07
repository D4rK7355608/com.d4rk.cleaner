package com.d4rk.cleaner.ui.screens.main

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.utils.helpers.ScreenHelper
import com.d4rk.cleaner.core.AppCoreManager
import com.d4rk.cleaner.core.ui.components.navigation.BottomNavigationBar
import com.d4rk.cleaner.core.ui.components.navigation.LeftNavigationRail
import com.d4rk.cleaner.core.ui.components.navigation.NavigationDrawer
import com.d4rk.cleaner.core.ui.components.navigation.NavigationHost
import com.d4rk.cleaner.core.ui.components.navigation.TopAppBarMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel : MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController = rememberNavController()
    val context = LocalContext.current
    val view = LocalView.current
    val dataStore = AppCoreManager.dataStore

    val isTabletOrLandscape : Boolean = ScreenHelper.isLandscapeOrTablet(context = context)

    val mainScreenState = remember {
        com.d4rk.cleaner.core.data.model.ui.screens.MainScreenState(
            context = context , view = view , drawerState = drawerState , navHostController = navController , dataStore = dataStore , viewModel = viewModel
        )
    }

    if (isTabletOrLandscape) {
        MainScaffoldTabletContent(mainScreenState = mainScreenState)
    }
    else {
        NavigationDrawer(
            mainScreenState = mainScreenState
        )
    }
}

@Composable
fun MainScaffoldContent(
    mainScreenState : com.d4rk.cleaner.core.data.model.ui.screens.MainScreenState , coroutineScope : CoroutineScope
) {
    Scaffold(modifier = Modifier.imePadding() , topBar = {

        TopAppBarMain(view = mainScreenState.view , context = mainScreenState.context , navigationIcon = if (mainScreenState.drawerState.isOpen) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = {
            coroutineScope.launch {
                mainScreenState.drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        })
    } , bottomBar = {
        BottomNavigationBar(
            navController = mainScreenState.navHostController , dataStore = mainScreenState.dataStore , view = mainScreenState.view , viewModel = mainScreenState.viewModel
        )
    }) { paddingValues ->
        NavigationHost(
            navHostController = mainScreenState.navHostController , dataStore = mainScreenState.dataStore , paddingValues = paddingValues
        )
    }
}

@Composable
fun MainScaffoldTabletContent(mainScreenState : com.d4rk.cleaner.core.data.model.ui.screens.MainScreenState) {
    var isRailExpanded : Boolean by remember { mutableStateOf(value = false) }
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val context : Context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize() , topBar = {
        TopAppBarMain(view = mainScreenState.view , context = context , navigationIcon = if (isRailExpanded) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = {
            isRailExpanded = ! isRailExpanded
        })
    }) { paddingValues ->
        LeftNavigationRail(
            coroutineScope = coroutineScope ,
            mainScreenState = mainScreenState ,
            paddingValues = paddingValues ,
            isRailExpanded = isRailExpanded ,
        )
    }
}