package com.d4rk.cleaner.ui.components.navigation.actions

import android.app.Activity
import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Shop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedButtonDirection
import com.d4rk.android.libs.apptoolkit.ui.components.dialogs.VersionInfoAlertDialog
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R

@Composable
fun HelpScreenMenuActions(
    context: Context ,
    activity: Activity ,
    showDialog: MutableState<Boolean> ,
    eulaHtmlString: String? ,
    changelogHtmlString: String? ,
    view: View
) {
    var showMenu: Boolean by remember { mutableStateOf(value = false) }

    AnimatedButtonDirection(
        fromRight = true,
        contentDescription = null,
        icon = Icons.Default.MoreVert,
        onClick = {  showMenu = true }
    )

    DropdownMenu(expanded = showMenu, onDismissRequest = {
        showMenu = false
    }) {
        DropdownMenuItem(
            modifier = Modifier.bounceClick(),
            text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.view_in_google_play_store)) },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Shop, contentDescription = null) },
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                IntentsHelper.openUrl(
                    context = context , url = "https://play.google.com/store/apps/details?id=${activity.packageName}"
                )
            }
        )
        DropdownMenuItem(
            modifier = Modifier.bounceClick(),
            text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.version_info)) },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Info, contentDescription = null) },
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                showDialog.value = true
            }
        )
        DropdownMenuItem(
            modifier = Modifier.bounceClick(),
            text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.beta_program)) },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Science, contentDescription = null) },
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                IntentsHelper.openUrl(
                    context = context, url = "https://play.google.com/apps/testing/${activity.packageName}"
                )
            }
        )
        DropdownMenuItem(
            modifier = Modifier.bounceClick(),
            text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.terms_of_service)) },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Description, contentDescription = null) },
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                IntentsHelper.openUrl(context = context, url = "https://sites.google.com/view/d4rk7355608/more/apps/terms-of-service")
            }
        )
        DropdownMenuItem(
            modifier = Modifier.bounceClick(),
            text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.privacy_policy)) },
            leadingIcon = { Icon(imageVector = Icons.Outlined.PrivacyTip, contentDescription = null) },
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                IntentsHelper.openUrl(context = context, url = "https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy")
            }
        )
        DropdownMenuItem(
            modifier = Modifier.bounceClick(),
            text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.oss_license_title)) },
            leadingIcon = { Icon(Icons.Outlined.Balance, contentDescription = null) },
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                IntentsHelper.openLicensesScreen(
                    context = context,
                    eulaHtmlString = eulaHtmlString,
                    changelogHtmlString = changelogHtmlString,
                    appName = context.getString(R.string.app_name),
                    appVersion = BuildConfig.VERSION_NAME,
                    appVersionCode = BuildConfig.VERSION_CODE,
                    appShortDescription = R.string.app_short_description
                )
            }
        )
    }

    if (showDialog.value) {
        VersionInfoAlertDialog(
            onDismiss = { showDialog.value = false },
            copyrightString = R.string.copyright,
            appName = R.string.app_full_name,
            versionName = BuildConfig.VERSION_NAME,
            versionString = com.d4rk.android.libs.apptoolkit.R.string.version
        )
    }
}