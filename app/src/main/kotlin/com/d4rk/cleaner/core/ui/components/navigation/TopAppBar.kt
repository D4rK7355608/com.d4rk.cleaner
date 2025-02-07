package com.d4rk.cleaner.core.ui.components.navigation

import android.app.Activity
import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.VolunteerActivism
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.ui.components.dialogs.VersionInfoAlertDialog
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.screens.support.SupportActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarMain(
    view : View ,
    context : Context ,
    navigationIcon : ImageVector ,
    onNavigationIconClick : () -> Unit ,
) {
    TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) } , navigationIcon = {
        IconButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onNavigationIconClick()
        }) {
            Icon(
                imageVector = navigationIcon , contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.navigation_drawer_open)
            )
        }
    } , actions = {
        IconButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openActivity(context , SupportActivity::class.java)
        }) {
            Icon(
                Icons.Outlined.VolunteerActivism , contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.support_us)
            )
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScaffoldWithBackButton(
    title : String , onBackClicked : () -> Unit , content : @Composable (PaddingValues) -> Unit
) {
    val scrollBehaviorState : TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val view : View = LocalView.current

    Scaffold(modifier = Modifier.nestedScroll(scrollBehaviorState.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(text = title) } , navigationIcon = {
            IconButton(modifier = Modifier.bounceClick() , onClick = {
                onBackClicked()
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null)
            }
        } , scrollBehavior = scrollBehaviorState)
    }) { paddingValues ->
        content(paddingValues)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScaffoldWithBackButtonAndActions(
    context : Context , activity : Activity , showDialog : MutableState<Boolean> , eulaHtmlString : String? , changelogHtmlString : String? , scrollBehavior : TopAppBarScrollBehavior , view : View
) {
    var showMenu : Boolean by remember { mutableStateOf(value = false) }

    LargeTopAppBar(
        title = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.help)) } ,
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
                DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.view_in_google_play_store)) } , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openUrl(
                        context , url = "https://play.google.com/store/apps/details?id=${activity.packageName}"
                    )
                })
                DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.version_info)) } , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    showDialog.value = true
                })
                DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.beta_program)) } , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openUrl(
                        context , url = "https://play.google.com/apps/testing/${activity.packageName}"
                    )
                })
                DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.terms_of_service)) } , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openUrl(
                        context , url = "https://sites.google.com/view/d4rk7355608/more/apps/terms-of-service"
                    )
                })
                DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.privacy_policy)) } , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openUrl(
                        context , url = "https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy"
                    )
                })
                DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.oss_license_title)) } , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openLicensesScreen(
                        context = context ,
                        eulaHtmlString = eulaHtmlString ,
                        changelogHtmlString = changelogHtmlString ,
                        appName = context.getString(R.string.app_name) ,
                        appVersion = BuildConfig.VERSION_NAME ,
                        appVersionCode = BuildConfig.VERSION_CODE ,
                        appShortDescription = R.string.app_short_description
                    )
                })
            }
            if (showDialog.value) {
                VersionInfoAlertDialog(onDismiss = { showDialog.value = false } , copyrightString = R.string.copyright , appName = R.string.app_full_name , versionName = BuildConfig.VERSION_NAME , versionString = com.d4rk.android.libs.apptoolkit.R.string.version)
            }
        } ,
        scrollBehavior = scrollBehavior ,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScaffold(
    title : String , content : @Composable (PaddingValues) -> Unit
) {
    val scrollBehaviorState : TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(modifier = Modifier.nestedScroll(scrollBehaviorState.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(text = title) } , scrollBehavior = scrollBehaviorState)
    }) { paddingValues ->
        content(paddingValues)
    }
}