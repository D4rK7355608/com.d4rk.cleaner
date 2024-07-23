package com.d4rk.cleaner.ui.settings.privacy.permissions

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.compose.components.PreferenceCategoryItem
import com.d4rk.cleaner.utils.compose.components.PreferenceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsSettingsComposable(activity : PermissionsSettingsActivity) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.permissions)) } , navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null
                )
            }
        } , scrollBehavior = scrollBehavior
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues) ,
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(R.string.normal))
                PreferenceItem(
                    title = stringResource(R.string.ad_id) ,
                    summary = stringResource(R.string.summary_preference_permissions_ad_id) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.internet) ,
                    summary = stringResource(R.string.summary_preference_permissions_internet) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.post_notifications) ,
                    summary = stringResource(R.string.summary_preference_permissions_post_notifications) ,
                )
            }
            item {
                PreferenceCategoryItem(title = stringResource(R.string.runtime))
                PreferenceItem(
                    title = stringResource(R.string.access_network_state) ,
                    summary = stringResource(R.string.summary_preference_permissions_access_network_state) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.access_notification_policy) ,
                    summary = stringResource(R.string.summary_preference_permissions_access_notification_policy) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.billing) ,
                    summary = stringResource(R.string.summary_preference_permissions_billing) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.check_license) ,
                    summary = stringResource(R.string.summary_preference_permissions_check_license) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.foreground_service) ,
                    summary = stringResource(R.string.summary_preference_permissions_foreground_service) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.request_delete_packages) ,
                    summary = stringResource(R.string.summary_preference_permissions_request_delete_packages) ,
                )
            }
            item {
                PreferenceCategoryItem(title = stringResource(R.string.storage))
                PreferenceItem(
                    title = stringResource(R.string.access_media_location) ,
                    summary = stringResource(R.string.summary_preference_permissions_access_media_location) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.action_open_document_tree) ,
                    summary = stringResource(R.string.summary_preference_permissions_action_open_document_tree) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.manage_external_storage) ,
                    summary = stringResource(R.string.summary_preference_permissions_manage_external_storage) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.package_usage_stats) ,
                    summary = stringResource(R.string.summary_preference_permissions_package_usage_stats) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.query_all_packages) ,
                    summary = stringResource(R.string.summary_preference_permissions_query_all_packages) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.read_external_storage) ,
                    summary = stringResource(R.string.summary_preference_permissions_read_external_storage) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.read_media_audio) ,
                    summary = stringResource(R.string.summary_preference_permissions_read_media_audio) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.read_media_images) ,
                    summary = stringResource(R.string.summary_preference_permissions_read_media_images) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.read_media_video) ,
                    summary = stringResource(R.string.summary_preference_permissions_read_media_video) ,
                )
                PreferenceItem(
                    title = stringResource(R.string.write_external_storage) ,
                    summary = stringResource(R.string.summary_preference_permissions_write_external_storage) ,
                )
            }
        }
    }
}