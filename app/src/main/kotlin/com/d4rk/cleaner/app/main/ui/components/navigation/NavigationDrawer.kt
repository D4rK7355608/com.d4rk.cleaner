package com.d4rk.cleaner.app.main.ui.components.navigation

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.hapticDrawerSwipe
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.cleaner.app.main.domain.model.MainScreenState
import com.d4rk.cleaner.app.main.ui.MainScaffoldContent
import com.d4rk.cleaner.app.main.ui.MainViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun NavigationDrawer(mainScreenState : MainScreenState , viewModel : MainViewModel) {
    val drawerState : DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val context : Context = LocalContext.current

    ModalNavigationDrawer(
        modifier = Modifier.hapticDrawerSwipe(state = drawerState) , drawerState = drawerState , drawerContent = {
            ModalDrawerSheet {
                LargeVerticalSpacer()
                mainScreenState.uiState.navigationDrawerItems.forEach { item : NavigationDrawerItem ->
                    NavigationDrawerItemContent(item = item , handleNavigationItemClick = {
                        handleNavigationItemClick(context = context , item = item , drawerState = drawerState , coroutineScope = coroutineScope)
                    })
                }
            }
        }) {
        MainScaffoldContent(drawerState = drawerState , mainScreenState = mainScreenState , viewModel = viewModel)
    }
}