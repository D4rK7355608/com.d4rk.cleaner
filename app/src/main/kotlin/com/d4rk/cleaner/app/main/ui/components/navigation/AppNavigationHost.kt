package com.d4rk.cleaner.app.main.ui.components.navigation

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.d4rk.android.libs.apptoolkit.app.help.ui.HelpActivity
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.NavigationHost
import com.d4rk.android.libs.apptoolkit.app.settings.settings.ui.SettingsActivity
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.utils.constants.links.AppLinks
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerScreen
import com.d4rk.cleaner.app.clean.home.ui.HomeScreen
import com.d4rk.cleaner.app.clean.memory.ui.MemoryManagerComposable
import com.d4rk.cleaner.app.clean.trash.ui.TrashActivity
import com.d4rk.cleaner.app.images.picker.ui.ImagePickerActivity
import com.d4rk.cleaner.app.main.utils.constants.NavigationRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppNavigationHost(navController : NavHostController , snackbarHostState : SnackbarHostState , paddingValues : PaddingValues) {
    NavigationHost(navController = navController , startDestination = NavigationRoutes.ROUTE_HOME) {
        composable(route = NavigationRoutes.ROUTE_HOME) { backStackEntry ->
            HomeScreen(paddingValues = paddingValues)
        }
        composable(route = NavigationRoutes.ROUTE_APP_MANAGER) { backStackEntry ->
            AppManagerScreen(snackbarHostState = snackbarHostState , paddingValues = paddingValues)
        }
        composable(route = NavigationRoutes.ROUTE_MEMORY_MANAGER) { backStackEntry ->
            MemoryManagerComposable(paddingValues = paddingValues)
        }
    }
}


fun handleNavigationItemClick(context : Context , item : NavigationDrawerItem , drawerState : DrawerState? = null , coroutineScope : CoroutineScope? = null) {
    when (item.title) {
        com.d4rk.android.libs.apptoolkit.R.string.settings -> IntentsHelper.openActivity(context = context , activityClass = SettingsActivity::class.java)
        com.d4rk.android.libs.apptoolkit.R.string.help_and_feedback -> IntentsHelper.openActivity(context = context , activityClass = HelpActivity::class.java)
        com.d4rk.android.libs.apptoolkit.R.string.updates -> IntentsHelper.openUrl(context = context , url = AppLinks.githubChangelog(context.packageName))
        com.d4rk.android.libs.apptoolkit.R.string.share -> IntentsHelper.shareApp(context = context , shareMessageFormat = com.d4rk.android.libs.apptoolkit.R.string.summary_share_message)
        R.string.image_optimizer -> IntentsHelper.openActivity(context = context , activityClass = ImagePickerActivity::class.java)
        R.string.trash -> IntentsHelper.openActivity(context = context , activityClass = TrashActivity::class.java)
    }
    drawerState?.let { drawerState : DrawerState ->
        coroutineScope?.let { scope : CoroutineScope ->
            scope.launch { drawerState.close() }
        }
    }
}