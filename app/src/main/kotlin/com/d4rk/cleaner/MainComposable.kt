package com.d4rk.cleaner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.d4rk.cleaner.data.navigation.NavigationItem
import com.d4rk.cleaner.data.navigation.Screen
import com.d4rk.cleaner.ui.appmanager.AppManagerComposable
import com.d4rk.cleaner.ui.help.HelpActivity
import com.d4rk.cleaner.ui.home.HomeComposable
import com.d4rk.cleaner.ui.imageoptimizer.ImagePickerActivity
import com.d4rk.cleaner.ui.memory.MemoryManagerComposable
import com.d4rk.cleaner.ui.settings.SettingsActivity
import com.d4rk.cleaner.ui.support.SupportActivity
import com.d4rk.cleaner.ui.whitelist.WhitelistActivity
import com.d4rk.cleaner.utils.Utils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainComposable() {
    val bottomBarItems = listOf(
        Screen.Home, Screen.AppManager, Screen.MemoryManager
    )
    val drawerItems = listOf(
        NavigationItem(
            title = R.string.whitelist, selectedIcon = Icons.AutoMirrored.Outlined.ListAlt
        ),

        NavigationItem(
            title = R.string.image_optimizer, selectedIcon = Icons.Outlined.Image
        ),

        NavigationItem(
            title = R.string.settings,
            selectedIcon = Icons.Outlined.Settings,
        ),
        NavigationItem(
            title = R.string.help_and_feedback,
            selectedIcon = Icons.AutoMirrored.Outlined.HelpOutline,
        ),
        NavigationItem(
            title = R.string.updates,
            selectedIcon = Icons.AutoMirrored.Outlined.EventNote,
        ),
        NavigationItem(
            title = R.string.share, selectedIcon = Icons.Outlined.Share
        ),
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val context = LocalContext.current
    val selectedItemIndex by rememberSaveable { mutableIntStateOf(-1) }
    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            Spacer(modifier = Modifier.height(16.dp))
            drawerItems.forEachIndexed { index, item ->
                val title = stringResource(item.title)
                NavigationDrawerItem(
                    label = { Text(text = title) },
                    selected = index == selectedItemIndex,
                    onClick = {
                        when (item.title) {
                            R.string.whitelist -> {
                                Utils.openActivity(
                                    context, WhitelistActivity::class.java
                                )
                            }

                            R.string.image_optimizer -> {
                                Utils.openActivity(
                                    context, ImagePickerActivity::class.java
                                )
                            }

                            R.string.settings -> {
                                Utils.openActivity(
                                    context, SettingsActivity::class.java
                                )
                            }

                            R.string.help_and_feedback -> {
                                Utils.openActivity(
                                    context, HelpActivity::class.java
                                )
                            }

                            R.string.updates -> {
                                Utils.openUrl(
                                    context,
                                    "https://github.com/D4rK7355608/${context.packageName}/blob/master/CHANGELOG.md"
                                )
                            }

                            R.string.share -> {
                                Utils.shareApp(context)
                            }
                        }
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = {
                        Icon(
                            item.selectedIcon, contentDescription = title
                        )
                    },
                    badge = {
                        item.badgeCount?.let {
                            Text(text = item.badgeCount.toString())
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                if (item.title == R.string.image_optimizer) {
                    HorizontalDivider(modifier = Modifier.padding(8.dp))
                }
            }
        }

    }, content = {
        Scaffold(topBar = {
            TopAppBar(title = {
                Text(text = stringResource(R.string.app_name))
            }, navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu, contentDescription = "Menu"
                    )
                }
            }, actions = {
                IconButton(onClick = {
                    Utils.openActivity(context, SupportActivity::class.java)
                }) {
                    Icon(
                        Icons.Outlined.VolunteerActivism,
                        contentDescription = "Support"
                    )
                }
            })
        }, bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                bottomBarItems.forEach { screen ->
                    NavigationBarItem(icon = {
                        val iconResource =
                            if (currentRoute == screen.route) screen.selectedIcon else screen.icon
                        Icon(iconResource, contentDescription = null)
                    },
                        label = { Text(stringResource(screen.title)) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        })
                }
            }
        }) { innerPadding ->
            NavHost(navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) {
                    Box(modifier = Modifier.padding(innerPadding)) {
                        HomeComposable()
                    }
                }
                composable(Screen.AppManager.route) {
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppManagerComposable()
                    }
                }
                composable(Screen.MemoryManager.route) {
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MemoryManagerComposable()
                    }
                }
            }
        }
    })
}