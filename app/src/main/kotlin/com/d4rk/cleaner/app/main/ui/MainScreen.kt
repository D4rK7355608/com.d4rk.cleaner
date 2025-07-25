package com.d4rk.cleaner.app.main.ui

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.sharp.AppRegistration
import androidx.compose.material.icons.sharp.Storage
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.d4rk.android.libs.apptoolkit.app.main.domain.model.BottomBarItem
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.dialogs.ChangelogDialog
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.BottomNavigationBar
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.LeftNavigationRail
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHost
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ScreenHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import com.d4rk.cleaner.app.main.domain.model.UiMainScreen
import com.d4rk.cleaner.app.main.ui.components.navigation.AppNavigationHost
import com.d4rk.cleaner.app.main.ui.components.navigation.CleanerTopAppBar
import com.d4rk.cleaner.app.main.ui.components.navigation.NavigationDrawer
import com.d4rk.cleaner.app.main.ui.components.navigation.handleNavigationItemClick
import com.d4rk.cleaner.app.main.utils.constants.NavigationRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val screenState: UiStateScreen<UiMainScreen> by viewModel.uiState.collectAsState()
    val context: Context = LocalContext.current
    val isTabletOrLandscape: Boolean = ScreenHelper.isLandscapeOrTablet(context = context)

    if (isTabletOrLandscape) {
        MainScaffoldTabletContent()
    } else {
        NavigationDrawer(screenState = screenState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldContent(drawerState: DrawerState) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }

    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val appManagerEntry = remember(backStackEntry) {
        runCatching { navController.getBackStackEntry(NavigationRoutes.ROUTE_APP_MANAGER) }.getOrNull()
    }
    val appManagerViewModel: AppManagerViewModel? = appManagerEntry?.let { entry ->
        koinViewModel(viewModelStoreOwner = entry)
    }
    val searchQuery by appManagerViewModel?.searchQuery?.collectAsState()
        ?: remember { mutableStateOf("") }
    val bottomItems = listOf(
        BottomBarItem(
            route = NavigationRoutes.ROUTE_HOME,
            icon = Icons.Outlined.CleaningServices,
            selectedIcon = Icons.Filled.CleaningServices,
            title = R.string.scanner
        ),
        BottomBarItem(
            route = NavigationRoutes.ROUTE_APP_MANAGER,
            icon = Icons.Sharp.AppRegistration,
            selectedIcon = Icons.Rounded.AppRegistration,
            title = R.string.app_manager
        ),
        BottomBarItem(
            route = NavigationRoutes.ROUTE_MEMORY_MANAGER,
            icon = Icons.Sharp.Storage,
            selectedIcon = Icons.Rounded.Storage,
            title = R.string.memory_manager
        ),
    )

    Scaffold(modifier = Modifier.imePadding(), topBar = {
        CleanerTopAppBar(
            navigationIcon = if (drawerState.isOpen) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu,
            onNavigationIconClick = { coroutineScope.launch { drawerState.open() } },
            scrollBehavior = scrollBehavior,
            showSearch = currentRoute == NavigationRoutes.ROUTE_APP_MANAGER,
            searchQuery = searchQuery,
            onSearchQueryChange = { query -> appManagerViewModel?.onSearchQueryChange(query) }
        )
    }, snackbarHost = {
        DefaultSnackbarHost(snackbarState = snackBarHostState)
    }, bottomBar = {
        BottomNavigationBar(navController = navController, items = bottomItems)
    }) { paddingValues ->
        AppNavigationHost(
            navController = navController,
            snackbarHostState = snackBarHostState,
            paddingValues = paddingValues
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldTabletContent() {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var isRailExpanded by remember { mutableStateOf(value = false) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val context: Context = LocalContext.current
    val snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }

    val viewModel: MainViewModel = koinViewModel()
    val screenState: UiStateScreen<UiMainScreen> by viewModel.uiState.collectAsState()
    val uiState: UiMainScreen = screenState.data ?: UiMainScreen()
    val navController: NavHostController = rememberNavController()

    val changelogUrl: String = koinInject(qualifier = named("github_changelog"))
    val buildInfoProvider: BuildInfoProvider = koinInject()
    var showChangelog by remember { mutableStateOf(false) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: navController.currentDestination?.route
    val appManagerEntry = remember(backStackEntry) {
        runCatching { navController.getBackStackEntry(NavigationRoutes.ROUTE_APP_MANAGER) }.getOrNull()
    }
    val appManagerViewModel: AppManagerViewModel? = appManagerEntry?.let { entry ->
        koinViewModel(viewModelStoreOwner = entry)
    }
    val searchQuery by appManagerViewModel?.searchQuery?.collectAsState()
        ?: remember { mutableStateOf("") }

    val bottomItems = listOf(
        BottomBarItem(
            route = NavigationRoutes.ROUTE_HOME,
            icon = Icons.Outlined.CleaningServices,
            selectedIcon = Icons.Filled.CleaningServices,
            title = R.string.scanner
        ),
        BottomBarItem(
            route = NavigationRoutes.ROUTE_APP_MANAGER,
            icon = Icons.Sharp.AppRegistration,
            selectedIcon = Icons.Rounded.AppRegistration,
            title = R.string.app_manager
        ),
        BottomBarItem(
            route = NavigationRoutes.ROUTE_MEMORY_MANAGER,
            icon = Icons.Sharp.Storage,
            selectedIcon = Icons.Rounded.Storage,
            title = R.string.memory_manager
        ),
    )

    Scaffold(
        topBar = {
            CleanerTopAppBar(
                navigationIcon = if (isRailExpanded) Icons.AutoMirrored.Outlined.MenuOpen else Icons.Default.Menu,
                onNavigationIconClick = {
                    coroutineScope.launch {
                        isRailExpanded = !isRailExpanded
                    }
                },
                scrollBehavior = scrollBehavior,
                showSearch = currentRoute == NavigationRoutes.ROUTE_APP_MANAGER,
                searchQuery = searchQuery,
                onSearchQueryChange = { query -> appManagerViewModel?.onSearchQueryChange(query) }
            )
        }) { paddingValues ->
        LeftNavigationRail(
            drawerItems = uiState.navigationDrawerItems,
            bottomItems = bottomItems,
            currentRoute = currentRoute,
            isRailExpanded = isRailExpanded,
            paddingValues = paddingValues,
            onBottomItemClick = { item: BottomBarItem -> navController.navigate(item.route) },
            onDrawerItemClick = { item: NavigationDrawerItem ->
                handleNavigationItemClick(
                    context = context,
                    item = item,
                    onChangelogRequested = { showChangelog = true },
                )
            },
            content = {
                AppNavigationHost(
                    navController = navController,
                    snackbarHostState = snackBarHostState,
                    paddingValues = PaddingValues()
                )
            })
    }

    if (showChangelog) {
        ChangelogDialog(
            changelogUrl = changelogUrl,
            buildInfoProvider = buildInfoProvider,
            onDismiss = { showChangelog = false }
        )
    }
}