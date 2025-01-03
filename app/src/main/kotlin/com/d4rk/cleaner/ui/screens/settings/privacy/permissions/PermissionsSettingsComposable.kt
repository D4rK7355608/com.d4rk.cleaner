package com.d4rk.cleaner.ui.screens.settings.privacy.permissions

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.components.preferences.PreferenceCategoryItem
import com.d4rk.cleaner.ui.components.preferences.PreferenceItem
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton

@Composable
fun PermissionsSettingsComposable(activity: PermissionsSettingsActivity) {
    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.permissions),
        onBackClicked = { activity.finish() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues),
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(id = R.string.normal))
                PreferenceItem(
                    title = stringResource(id = R.string.ad_id),
                    summary = stringResource(id = R.string.summary_preference_permissions_ad_id),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.internet),
                    summary = stringResource(id = R.string.summary_preference_permissions_internet),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.post_notifications),
                    summary = stringResource(id = R.string.summary_preference_permissions_post_notifications),
                )
            }
            item {
                PreferenceCategoryItem(title = stringResource(id = R.string.runtime))
                PreferenceItem(
                    title = stringResource(id = R.string.access_network_state),
                    summary = stringResource(id = R.string.summary_preference_permissions_access_network_state),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.access_notification_policy),
                    summary = stringResource(id = R.string.summary_preference_permissions_access_notification_policy),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.billing),
                    summary = stringResource(id = R.string.summary_preference_permissions_billing),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.check_license),
                    summary = stringResource(id = R.string.summary_preference_permissions_check_license),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.foreground_service),
                    summary = stringResource(id = R.string.summary_preference_permissions_foreground_service),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.request_delete_packages),
                    summary = stringResource(id = R.string.summary_preference_permissions_request_delete_packages),
                )
            }
            item {
                PreferenceCategoryItem(title = stringResource(id = R.string.storage))
                PreferenceItem(
                    title = stringResource(id = R.string.access_media_location),
                    summary = stringResource(id = R.string.summary_preference_permissions_access_media_location),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.action_open_document_tree),
                    summary = stringResource(id = R.string.summary_preference_permissions_action_open_document_tree),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.manage_external_storage),
                    summary = stringResource(id = R.string.summary_preference_permissions_manage_external_storage),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.package_usage_stats),
                    summary = stringResource(id = R.string.summary_preference_permissions_package_usage_stats),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.query_all_packages),
                    summary = stringResource(id = R.string.summary_preference_permissions_query_all_packages),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.read_external_storage),
                    summary = stringResource(id = R.string.summary_preference_permissions_read_external_storage),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.read_media_audio),
                    summary = stringResource(id = R.string.summary_preference_permissions_read_media_audio),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.read_media_images),
                    summary = stringResource(id = R.string.summary_preference_permissions_read_media_images),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.read_media_video),
                    summary = stringResource(id = R.string.summary_preference_permissions_read_media_video),
                )
                PreferenceItem(
                    title = stringResource(id = R.string.write_external_storage),
                    summary = stringResource(id = R.string.summary_preference_permissions_write_external_storage),
                )
            }
        }
    }
}