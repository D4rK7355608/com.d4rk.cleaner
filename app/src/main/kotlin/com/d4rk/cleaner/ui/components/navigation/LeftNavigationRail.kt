package com.d4rk.cleaner.ui.components.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import com.d4rk.android.libs.apptoolkit.data.model.ui.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.navigation.BottomNavigationScreen
import com.d4rk.cleaner.data.model.ui.screens.MainScreenState
import com.d4rk.cleaner.data.model.ui.screens.UiMainScreen
import com.d4rk.cleaner.ui.screens.help.HelpActivity
import com.d4rk.cleaner.ui.screens.imageoptimizer.imagepicker.ImagePickerActivity
import com.d4rk.cleaner.ui.screens.settings.SettingsActivity
import com.d4rk.cleaner.ui.screens.trash.TrashActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LeftNavigationRail(
    mainScreenState : MainScreenState ,
    paddingValues : PaddingValues ,
    coroutineScope : CoroutineScope ,
    isRailExpanded : Boolean ,
) {
    val uiState : UiMainScreen by mainScreenState.viewModel.uiState.collectAsState()

    val bottomBarItems : List<BottomNavigationScreen> = uiState.bottomNavigationItems
    val drawerItems : List<NavigationDrawerItem> = uiState.navigationDrawerItems

    val navBackStackEntry : NavBackStackEntry? by mainScreenState.navHostController.currentBackStackEntryAsState()
    val currentRoute : String? = navBackStackEntry?.destination?.route

    val railWidth : Dp by animateDpAsState(
        targetValue = if (isRailExpanded) 200.dp else 72.dp , animationSpec = tween(durationMillis = 300)
    )

    Row(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
        NavigationRail(
            modifier = Modifier.width(width = railWidth)
        ) {
            bottomBarItems.forEach { screen ->
                NavigationRailItem(selected = currentRoute == screen.route , onClick = {
                    mainScreenState.navHostController.navigate(screen.route) {
                        popUpTo(mainScreenState.navHostController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                } , icon = {
                    Icon(
                        imageVector = if (currentRoute == screen.route) screen.selectedIcon else screen.icon , contentDescription = stringResource(id = screen.title)
                    )
                } , label = if (isRailExpanded) {
                    { Text(text = stringResource(id = screen.title)) }
                }
                else null)
            }

            Spacer(modifier = Modifier.weight(weight = 1f))

            drawerItems.forEach { item ->
                NavigationRailItem(selected = false , onClick = {
                    when (item.title) {
                        R.string.image_optimizer -> IntentsHelper.openActivity(
                            context = mainScreenState.context , activityClass = ImagePickerActivity::class.java
                        )

                        R.string.trash -> IntentsHelper.openActivity(
                            context = mainScreenState.context , activityClass = TrashActivity::class.java
                        )

                        com.d4rk.android.libs.apptoolkit.R.string.settings -> IntentsHelper.openActivity(
                            context = mainScreenState.context , activityClass = SettingsActivity::class.java
                        )

                        com.d4rk.android.libs.apptoolkit.R.string.help_and_feedback -> IntentsHelper.openActivity(
                            context = mainScreenState.context , activityClass = HelpActivity::class.java
                        )

                        com.d4rk.android.libs.apptoolkit.R.string.updates -> IntentsHelper.openUrl(
                            context = mainScreenState.context , url = "https://github.com/D4rK7355608/${mainScreenState.context.packageName}/blob/master/CHANGELOG.md"
                        )

                        com.d4rk.android.libs.apptoolkit.R.string.share -> IntentsHelper.shareApp(
                            context = mainScreenState.context , shareMessageFormat = com.d4rk.android.libs.apptoolkit.R.string.summary_share_message
                        )
                    }
                    coroutineScope.launch { mainScreenState.drawerState.close() }
                } , icon = {
                    Icon(
                        imageVector = item.selectedIcon , contentDescription = stringResource(id = item.title)
                    )
                } , label = if (isRailExpanded) {
                    { Text(text = stringResource(id = item.title)) }
                }
                else null)
            }
        }

        Box(modifier = Modifier.weight(weight = 1f)) {
            NavigationHost(
                navHostController = mainScreenState.navHostController , dataStore = mainScreenState.dataStore , paddingValues = paddingValues
            )
        }
    }
}