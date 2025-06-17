package com.d4rk.cleaner.app.main.ui.components.navigation

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.app.main.ui.MainViewModel
import com.d4rk.cleaner.core.data.datastore.DataStore
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun BottomNavigationBar(
    navController : NavController , viewModel : MainViewModel , adsConfig : AdsConfig = koinInject(qualifier = named(name = "full_banner"))
) {
    val view : View = LocalView.current
    val dataStore : DataStore = koinInject()
    val showLabels : Boolean = dataStore.getShowBottomBarLabels().collectAsState(initial = true).value

    viewModel.screenState.collectAsState().value.data?.bottomBarItems?.let { bottomBarItems ->
        Column {
            AdBanner(
                modifier = Modifier
                        .fillMaxWidth(), adsConfig = adsConfig
            )
            NavigationBar {
                val navBackStackEntry : NavBackStackEntry? by navController.currentBackStackEntryAsState()
                val currentRoute : String? = navBackStackEntry?.destination?.route
                bottomBarItems.forEach { screen ->
                    NavigationBarItem(icon = {
                        val iconResource : ImageVector = if (currentRoute == screen.route) screen.selectedIcon else screen.icon
                        Icon(
                            imageVector = iconResource , modifier = Modifier.bounceClick() , contentDescription = null
                        )
                    } , label = {
                        if (showLabels) Text(
                            text = stringResource(id = screen.title) , overflow = TextOverflow.Ellipsis , modifier = Modifier.basicMarquee()
                        )
                    } , selected = currentRoute == screen.route , onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        if (currentRoute != screen.route) {
                            navController.navigate(route = screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = false
                                }
                                launchSingleTop = true
                            }
                        }
                    })
                }
            }
        }
    }
}