package com.d4rk.cleaner.ui.screens.settings.privacy.permissions

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.PreferenceCategoryItem
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.PreferenceItem
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton

@Composable
fun PermissionsSettingsScreen(activity : AppCompatActivity) {
    TopAppBarScaffoldWithBackButton(title = activity.getString(com.d4rk.android.libs.apptoolkit.R.string.permissions) , onBackClicked = {
        activity.finish()
    }) { paddingValues ->
        PermissionsSettingsList(paddingValues = paddingValues)
    }
}

@Composable
fun PermissionsSettingsList(paddingValues : PaddingValues) {
    LazyColumn(
        modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues) ,
    ) {
        item {
            PreferenceCategoryItem(title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.normal))
            PreferenceItem(
                title = stringResource(id = R.string.ad_id) ,
                summary = stringResource(id = R.string.summary_preference_permissions_ad_id) ,
            )
            PreferenceItem(
                title = stringResource(id = R.string.internet) ,
                summary = stringResource(id = R.string.summary_preference_permissions_internet) ,
            )
            PreferenceItem(
                title = stringResource(id = R.string.post_notifications) ,
                summary = stringResource(id = R.string.summary_preference_permissions_post_notifications) ,
            )
        }
        item {
            PreferenceCategoryItem(title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.runtime))
            PreferenceItem(
                title = stringResource(id = R.string.access_network_state) ,
                summary = stringResource(id = R.string.summary_preference_permissions_access_network_state) ,
            )
            PreferenceItem(
                title = stringResource(id = R.string.access_notification_policy) ,
                summary = stringResource(id = R.string.summary_preference_permissions_access_notification_policy) ,
            )
            PreferenceItem(
                title = stringResource(id = R.string.billing) ,
                summary = stringResource(id = R.string.summary_preference_permissions_billing) ,
            )
            PreferenceItem(
                title = stringResource(id = R.string.check_license) ,
                summary = stringResource(id = R.string.summary_preference_permissions_check_license) ,
            )
            PreferenceItem(
                title = stringResource(id = R.string.foreground_service) ,
                summary = stringResource(id = R.string.summary_preference_permissions_foreground_service) ,
            )
        }
    }
}