package com.d4rk.cleaner.ui.components.navigation

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.navigation.BottomNavigationScreen
import com.d4rk.cleaner.ui.components.ads.AdBannerFull
import com.d4rk.cleaner.ui.components.modifiers.bounceClick

@Composable
fun BottomNavigationBar(
    navController : NavController , dataStore : DataStore , view : View
) {
    val bottomBarItems : List<BottomNavigationScreen> = listOf(
        BottomNavigationScreen.Home ,
        BottomNavigationScreen.AppManager ,
        BottomNavigationScreen.MemoryManager
    )
    val showLabels : Boolean =
            dataStore.getShowBottomBarLabels().collectAsState(initial = true).value

    Column {
        AdBannerFull(modifier = Modifier.fillMaxWidth())
        NavigationBar {
            val navBackStackEntry : NavBackStackEntry? by navController.currentBackStackEntryAsState()
            val currentRoute : String? = navBackStackEntry?.destination?.route
            bottomBarItems.forEach { screen ->
                NavigationBarItem(modifier = Modifier.bounceClick() , icon = {
                    val iconResource : ImageVector =
                            if (currentRoute == screen.route) screen.selectedIcon else screen.icon
                    Icon(
                        iconResource , contentDescription = null
                    )
                } , label = {
                    if (showLabels) Text(
                        text = stringResource(id = screen.title) ,
                        overflow = TextOverflow.Ellipsis ,
                        modifier = Modifier.basicMarquee()
                    )
                } , selected = currentRoute == screen.route , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                })
            }
        }
    }
}