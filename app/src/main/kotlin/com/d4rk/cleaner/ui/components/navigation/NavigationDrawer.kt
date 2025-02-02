package com.d4rk.cleaner.ui.components.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.hapticDrawerSwipe
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.screens.MainScreenState
import com.d4rk.cleaner.ui.screens.help.HelpActivity
import com.d4rk.cleaner.ui.screens.imageoptimizer.imagepicker.ImagePickerActivity
import com.d4rk.cleaner.ui.screens.main.MainScaffoldContent
import com.d4rk.cleaner.ui.screens.settings.SettingsActivity
import com.d4rk.cleaner.ui.screens.trash.TrashActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    mainScreenState : MainScreenState
) {
    val uiState by mainScreenState.viewModel.uiState.collectAsState()
    val drawerItems = uiState.navigationDrawerItems
    val coroutineScope : CoroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(modifier = Modifier.hapticDrawerSwipe(drawerState = mainScreenState.drawerState) , drawerState = mainScreenState.drawerState , drawerContent = {
        ModalDrawerSheet {
            LargeVerticalSpacer()
            drawerItems.forEach { item ->
                NavigationDrawerItemContent(
                    item = item , coroutineScope = coroutineScope , drawerState = mainScreenState.drawerState , context = mainScreenState.context
                )
            }
        }
    }) {
        MainScaffoldContent(
            mainScreenState = mainScreenState , coroutineScope = coroutineScope
        )
    }
}

@Composable
private fun NavigationDrawerItemContent(
    item : com.d4rk.android.libs.apptoolkit.data.model.ui.navigation.NavigationDrawerItem , coroutineScope : CoroutineScope , drawerState : DrawerState , context : Context
) {
    val title = stringResource(id = item.title)
    NavigationDrawerItem(label = { Text(text = title) } , selected = false , onClick = {
        when (item.title) {
            R.string.image_optimizer -> IntentsHelper.openActivity(
                context = context , activityClass = ImagePickerActivity::class.java
            )

            R.string.trash -> IntentsHelper.openActivity(
                context = context , activityClass = TrashActivity::class.java
            )

            com.d4rk.android.libs.apptoolkit.R.string.settings -> {
                IntentsHelper.openActivity(
                    context = context , activityClass = SettingsActivity::class.java
                )
            }

            com.d4rk.android.libs.apptoolkit.R.string.help_and_feedback -> {
                IntentsHelper.openActivity(
                    context = context , activityClass = HelpActivity::class.java
                )
            }

            com.d4rk.android.libs.apptoolkit.R.string.updates -> {
                IntentsHelper.openUrl(
                    context = context , url = "https://github.com/D4rK7355608/${context.packageName}/blob/master/CHANGELOG.md"
                )
            }

            com.d4rk.android.libs.apptoolkit.R.string.share -> {
                IntentsHelper.shareApp(
                    context = context , shareMessageFormat = com.d4rk.android.libs.apptoolkit.R.string.summary_share_message
                )
            }
        }
        coroutineScope.launch { drawerState.close() }
    } , icon = {
        Icon(item.selectedIcon , contentDescription = title)
    } , badge = {
        if (item.badgeText.isNotBlank()) {
            Text(text = item.badgeText)
        }
    } , modifier = Modifier
            .padding(paddingValues = NavigationDrawerItemDefaults.ItemPadding)
            .bounceClick())

    if (item.title == R.string.trash) {
        HorizontalDivider(modifier = Modifier.padding(all = 8.dp))
    }
}