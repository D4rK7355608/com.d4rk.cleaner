package com.d4rk.cleaner.ui.components.navigation

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.components.modifiers.bounceClick
import com.d4rk.cleaner.ui.screens.help.HelpActivity
import com.d4rk.cleaner.ui.screens.support.SupportActivity
import com.d4rk.cleaner.utils.helpers.IntentsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarMain(
    view : View , drawerState : DrawerState , coroutineScope : CoroutineScope , context : Context
) {
    TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) } , navigationIcon = {
        IconButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(
                SoundEffectConstants.CLICK
            )
            coroutineScope.launch {
                drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }) {
            Icon(
                imageVector = Icons.Default.Menu ,
                contentDescription = stringResource(id = R.string.navigation_drawer_open)
            )
        }
    } , actions = {
        IconButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(
                SoundEffectConstants.CLICK
            )
            IntentsHelper.openActivity(
                context , SupportActivity::class.java
            )
        }) {
            Icon(
                Icons.Outlined.VolunteerActivism ,
                contentDescription = stringResource(id = R.string.support_us)
            )
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScaffoldWithBackButtonAndActions(
    context : Context ,
    activity : HelpActivity ,
    showDialog : MutableState<Boolean> ,
    eulaHtmlString : String? ,
    changelogHtmlString : String? ,
    scrollBehavior : TopAppBarScrollBehavior ,
    view : View
) {
    var showMenu : Boolean by remember { mutableStateOf(value = false) }

    LargeTopAppBar(
        title = { Text(text = stringResource(id = R.string.help)) } ,
        navigationIcon = {
            IconButton(modifier = Modifier.bounceClick() , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                activity.finish()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null
                )
            }
        } ,
        actions = {
            IconButton(modifier = Modifier.bounceClick() , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                showMenu = true
            }) {
                Icon(
                    Icons.Default.MoreVert , contentDescription = "Localized description"
                )
            }
            DropdownMenu(expanded = showMenu , onDismissRequest = {
                showMenu = false
            }) {
                DropdownMenuItem(modifier = Modifier.bounceClick() ,
                                 text = { Text(text = stringResource(id = R.string.view_in_google_play_store)) } ,
                                 onClick = {
                                     view.playSoundEffect(SoundEffectConstants.CLICK)
                                     IntentsHelper.openUrl(
                                         context ,
                                         url = "https://play.google.com/store/apps/details?id=${activity.packageName}"
                                     )
                                 })
                DropdownMenuItem(modifier = Modifier.bounceClick() ,
                                 text = { Text(text = stringResource(id = R.string.version_info)) } ,
                                 onClick = {
                                     view.playSoundEffect(SoundEffectConstants.CLICK)
                                     showDialog.value = true
                                 })
                DropdownMenuItem(modifier = Modifier.bounceClick() ,
                                 text = { Text(text = stringResource(id = R.string.beta_program)) } ,
                                 onClick = {
                                     view.playSoundEffect(SoundEffectConstants.CLICK)
                                     IntentsHelper.openUrl(
                                         context ,
                                         url = "https://play.google.com/apps/testing/${activity.packageName}"
                                     )
                                 })
                DropdownMenuItem(modifier = Modifier.bounceClick() ,
                                 text = { Text(text = stringResource(id = R.string.terms_of_service)) } ,
                                 onClick = {
                                     view.playSoundEffect(SoundEffectConstants.CLICK)
                                     IntentsHelper.openUrl(
                                         context ,
                                         url = "https://sites.google.com/view/d4rk7355608/more/apps/terms-of-service"
                                     )
                                 })
                DropdownMenuItem(modifier = Modifier.bounceClick() ,
                                 text = { Text(text = stringResource(id = R.string.privacy_policy)) } ,
                                 onClick = {
                                     view.playSoundEffect(SoundEffectConstants.CLICK)
                                     IntentsHelper.openUrl(
                                         context ,
                                         url = "https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy"
                                     )
                                 })
                DropdownMenuItem(modifier = Modifier.bounceClick() ,
                                 text = { Text(text = stringResource(id = R.string.oss_license_title)) } ,
                                 onClick = {
                                     view.playSoundEffect(SoundEffectConstants.CLICK)
                                     IntentsHelper.openLicensesScreen(
                                         context = context ,
                                         eulaHtmlString = eulaHtmlString ,
                                         changelogHtmlString = changelogHtmlString
                                     )
                                 })
            }
            if (showDialog.value) {
                com.d4rk.android.libs.apptoolkit.ui.components.dialogs.VersionInfoAlertDialog(onDismiss = { showDialog.value = false } ,
                                                                                              copyrightString = R.string.copyright ,
                                                                                              appName = R.string.app_name ,
                                                                                              versionName = BuildConfig.VERSION_NAME ,
                                                                                              versionString = com.d4rk.android.libs.apptoolkit.R.string.version)
            }
        } ,
        scrollBehavior = scrollBehavior ,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScaffoldWithBackButton(
    title: String, onBackClicked: () -> Unit, content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehaviorState: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val view: View = LocalView.current

    Scaffold(modifier = Modifier.nestedScroll(scrollBehaviorState.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = { Text(title) }, navigationIcon = {
                IconButton(modifier = Modifier.bounceClick(), onClick = {
                    onBackClicked()
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }, scrollBehavior = scrollBehaviorState
            )
        }) { paddingValues ->
        content(paddingValues)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScaffold(
    title: String, content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehaviorState: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(modifier = Modifier.nestedScroll(scrollBehaviorState.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = { Text(title) }, scrollBehavior = scrollBehaviorState
            )
        }) { paddingValues ->
        content(paddingValues)
    }
}