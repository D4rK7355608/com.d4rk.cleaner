package com.d4rk.cleaner.app.settings.settings.utils.providers

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import com.d4rk.android.libs.apptoolkit.app.settings.general.ui.GeneralSettingsActivity
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsCategory
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsConfig
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsPreference
import com.d4rk.android.libs.apptoolkit.app.settings.utils.constants.SettingsContent
import com.d4rk.android.libs.apptoolkit.app.settings.utils.interfaces.SettingsProvider
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.settings.settings.utils.constants.SettingsConstants

class AppSettingsProvider : SettingsProvider {
    override fun provideSettingsConfig(context : Context) : SettingsConfig {
        return SettingsConfig(
            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.settings) , categories = listOf(
                SettingsCategory(
                    preferences = listOf(
                        SettingsPreference(
                            key = SettingsConstants.KEY_SETTINGS_NOTIFICATION ,
                            icon = Icons.Outlined.Notifications ,
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.notifications) ,
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_settings_notifications) ,
                            action = { IntentsHelper.openAppNotificationSettings(context = context) }) , SettingsPreference(
                            key = SettingsContent.DISPLAY , icon = Icons.Outlined.Palette , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.display) , summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_settings_display) , action = {
                                GeneralSettingsActivity.start(
                                    context = context , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.display) , contentKey = SettingsContent.DISPLAY
                                )
                            })
                    )
                ) ,
                SettingsCategory(
                    preferences = listOf(
                        SettingsPreference(
                            key = SettingsConstants.KEY_SETTINGS_CLEANING , icon = Icons.Outlined.CleaningServices , title = context.getString(R.string.cleaning) , summary = context.getString(R.string.summary_preference_settings_cleaning) , action = {
                                GeneralSettingsActivity.start(
                                    context = context , title = context.getString(R.string.cleaning) , contentKey = SettingsConstants.KEY_SETTINGS_CLEANING
                                )
                            })
                    )
                ) ,

                SettingsCategory(
                    preferences = listOf(
                        SettingsPreference(
                            key = SettingsContent.SECURITY_AND_PRIVACY ,
                            icon = Icons.Outlined.Security ,
                            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.security_and_privacy) ,
                            summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_settings_privacy_and_security) ,
                            action = {
                                GeneralSettingsActivity.start(
                                    context = context , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.security_and_privacy) , contentKey = SettingsContent.SECURITY_AND_PRIVACY
                                )
                            }) , SettingsPreference(
                            key = SettingsContent.ADVANCED , icon = Icons.Outlined.Build , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.advanced) , summary = context.getString(com.d4rk.android.libs.apptoolkit.R.string.summary_preference_settings_advanced) , action = {
                                GeneralSettingsActivity.start(
                                    context = context , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.advanced) , contentKey = SettingsContent.ADVANCED
                                )
                            }) , SettingsPreference(
                            key = SettingsContent.ABOUT , icon = Icons.Outlined.Info , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.about) , summary = context.getString(R.string.summary_preference_settings_about) , action = {
                                GeneralSettingsActivity.start(
                                    context = context , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.about) , contentKey = SettingsContent.ABOUT
                                )
                            })
                    )
                ) ,
            )
        )
    }
}