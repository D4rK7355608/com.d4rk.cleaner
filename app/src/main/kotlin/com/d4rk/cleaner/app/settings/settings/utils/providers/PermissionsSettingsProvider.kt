package com.d4rk.cleaner.app.settings.settings.utils.providers

import android.content.Context
import com.d4rk.android.libs.apptoolkit.app.permissions.utils.interfaces.PermissionsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsCategory
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsConfig
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsPreference
import com.d4rk.cleaner.R

class PermissionsSettingsProvider : PermissionsProvider {
    override fun providePermissionsConfig(context: Context): SettingsConfig {
        return SettingsConfig(
            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.permissions),
            categories = listOf(
                SettingsCategory(
                    title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.normal),
                    preferences = listOf(
                        SettingsPreference(
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.access_network_state),
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_permissions_access_network_state),
                        ),
                        SettingsPreference(
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.ad_id),
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_permissions_ad_id),
                        ),
                        SettingsPreference(
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.billing),
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_permissions_billing),
                        ),
                        SettingsPreference(
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.check_license),
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_permissions_check_license),
                        ),
                        SettingsPreference(
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.foreground_service),
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_permissions_foreground_service),
                        ),
                        SettingsPreference(
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.internet),
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_permissions_internet),
                        ),
                        SettingsPreference(
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.wake_lock),
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_permissions_wake_lock),
                        ),
                    ),
                ), SettingsCategory(
                    title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.runtime),
                    preferences = listOf(
                        SettingsPreference(
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.post_notifications),
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_permissions_post_notifications),
                        ),
                    ),
                ), SettingsCategory(
                    title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.special),
                    preferences = listOf(
                        SettingsPreference(
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.access_notification_policy),
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_permissions_access_notification_policy),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.request_delete_packages),
                            summary = context.getString(R.string.summary_preference_permissions_request_delete_packages),
                        ),
                    ),
                ), SettingsCategory(
                    title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.storage),
                    preferences = listOf(
                        SettingsPreference(
                            title = context.getString(R.string.access_media_location),
                            summary = context.getString(R.string.summary_preference_permissions_access_media_location),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.action_open_document_tree),
                            summary = context.getString(R.string.summary_preference_permissions_action_open_document_tree),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.manage_external_storage),
                            summary = context.getString(R.string.summary_preference_permissions_manage_external_storage),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.package_usage_stats),
                            summary = context.getString(R.string.summary_preference_permissions_package_usage_stats),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.query_all_packages),
                            summary = context.getString(R.string.summary_preference_permissions_query_all_packages),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.read_external_storage),
                            summary = context.getString(R.string.summary_preference_permissions_read_external_storage),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.read_media_audio),
                            summary = context.getString(R.string.summary_preference_permissions_read_media_audio),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.read_media_images),
                            summary = context.getString(R.string.summary_preference_permissions_read_media_images),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.read_media_video),
                            summary = context.getString(R.string.summary_preference_permissions_read_media_video),
                        ),
                        SettingsPreference(
                            title = context.getString(R.string.write_external_storage),
                            summary = context.getString(R.string.summary_preference_permissions_write_external_storage),
                        ),
                    ),
                )
            )
        )
    }
}