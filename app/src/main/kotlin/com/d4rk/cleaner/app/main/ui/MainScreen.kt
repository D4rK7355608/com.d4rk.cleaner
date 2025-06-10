package com.d4rk.cleaner.app.main.ui

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.LeftNavigationRail
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.MainTopAppBar
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHost
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ScreenHelper
import com.d4rk.cleaner.app.main.domain.model.MainScreenState
import com.d4rk.cleaner.app.main.domain.model.UiMainScreen
import com.d4rk.cleaner.app.main.ui.components.navigation.AppNavigationHost
import com.d4rk.cleaner.app.main.ui.components.navigation.BottomNavigationBar
import com.d4rk.cleaner.app.main.ui.components.navigation.NavigationDrawer
import com.d4rk.cleaner.app.main.ui.components.navigation.handleNavigationItemClick
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val viewModel : MainViewModel = koinViewModel()
    val screenState : UiStateScreen<UiMainScreen> by viewModel.screenState.collectAsState()
    val context : Context = LocalContext.current
    val isTabletOrLandscape : Boolean = ScreenHelper.isLandscapeOrTablet(context = context)

    val mainScreenState = MainScreenState(
        navController = rememberNavController() ,
                                          isFabVisible = remember { mutableStateOf(value = false) } ,
                                          isFabExtended = remember { mutableStateOf(value = true) } ,
                                          snackbarHostState = remember { SnackbarHostState() } ,
                                          scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior() ,
                                          coroutineScope = rememberCoroutineScope() ,
                                          mainViewModel = viewModel ,
                                          uiState = screenState.data ?: UiMainScreen())

    if (isTabletOrLandscape) {
        MainScaffoldTabletContent(mainScreenState = mainScreenState)
    }
    else {
        NavigationDrawer(mainScreenState = mainScreenState , viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldContent(drawerState : DrawerState , viewModel : MainViewModel , mainScreenState : MainScreenState) {
    Scaffold(modifier = Modifier.imePadding() , topBar = {
        MainTopAppBar(navigationIcon = if (drawerState.isOpen) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = { mainScreenState.coroutineScope.launch { drawerState.open() } } , scrollBehavior = mainScreenState.scrollBehavior)
    } , bottomBar = {
        BottomNavigationBar(viewModel = viewModel , navController = mainScreenState.navController)
    } , snackbarHost = {
        DefaultSnackbarHost(snackbarState = mainScreenState.snackbarHostState)
    }) { paddingValues : PaddingValues ->
        AppNavigationHost(navController = mainScreenState.navController , snackbarHostState = mainScreenState.snackbarHostState , paddingValues = paddingValues)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldTabletContent(mainScreenState : MainScreenState) {
    var isRailExpanded : Boolean by remember { mutableStateOf(value = false) }
    val context : Context = LocalContext.current
    val navBackStackEntry : NavBackStackEntry? by mainScreenState.navController.currentBackStackEntryAsState()
    val currentRoute : String? = navBackStackEntry?.destination?.route

    Scaffold(modifier = Modifier.fillMaxSize() , topBar = {
        MainTopAppBar(navigationIcon = if (isRailExpanded) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu , onNavigationIconClick = {
            mainScreenState.coroutineScope.launch { isRailExpanded = ! isRailExpanded }
        } , scrollBehavior = mainScreenState.scrollBehavior)
    }) { paddingValues : PaddingValues ->
        LeftNavigationRail(drawerItems = mainScreenState.uiState.navigationDrawerItems , currentRoute = currentRoute , isRailExpanded = isRailExpanded , paddingValues = paddingValues , onDrawerItemClick = { item : NavigationDrawerItem ->
            handleNavigationItemClick(context = context , item = item)
        } , content = {

        })
    }
}