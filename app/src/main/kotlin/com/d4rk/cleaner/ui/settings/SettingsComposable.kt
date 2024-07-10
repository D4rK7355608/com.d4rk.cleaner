package com.d4rk.cleaner.ui.settings

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SafetyCheck
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.settings.about.AboutSettingsActivity
import com.d4rk.cleaner.ui.settings.advanced.AdvancedSettingsActivity
import com.d4rk.cleaner.ui.settings.cleaning.CleaningSettingsActivity
import com.d4rk.cleaner.ui.settings.display.DisplaySettingsActivity
import com.d4rk.cleaner.ui.settings.privacy.PrivacySettingsActivity
import com.d4rk.cleaner.utils.compose.components.PreferenceItem
import com.d4rk.cleaner.utils.IntentUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsComposable(activity: SettingsActivity) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.settings)) }, navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues),
        ) {
            item {
                PreferenceItem(Icons.Outlined.Palette,
                    title = stringResource(R.string.display),
                    summary = stringResource(R.string.summary_preference_settings_display),
                    onClick = {
                        IntentUtils.openActivity(context, DisplaySettingsActivity::class.java)
                    })
            }
            item {
                PreferenceItem(Icons.Outlined.CleaningServices,
                    title = stringResource(R.string.cleaning),
                    summary = stringResource(R.string.summary_preference_settings_cleaning),
                    onClick = {
                        IntentUtils.openActivity(
                            context, CleaningSettingsActivity::class.java
                        )
                    })
            }
            item {
                PreferenceItem(Icons.Outlined.Notifications,
                    title = stringResource(R.string.notifications),
                    summary = stringResource(R.string.summary_preference_settings_notifications),
                    onClick = {
                        IntentUtils.openAppNotificationSettings(context)
                    })
            }
            item {
                PreferenceItem(Icons.Outlined.Build,
                    title = stringResource(R.string.advanced),
                    summary = stringResource(R.string.summary_preference_settings_advanced),
                    onClick = {
                        IntentUtils.openActivity(
                            context, AdvancedSettingsActivity::class.java
                        )
                    })
            }
            item {
                PreferenceItem(Icons.Outlined.SafetyCheck,
                    title = stringResource(R.string.security_and_privacy),
                    summary = stringResource(R.string.summary_preference_settings_privacy_and_security),
                    onClick = {
                        IntentUtils.openActivity(context, PrivacySettingsActivity::class.java)
                    })
            }
            item {
                PreferenceItem(Icons.Outlined.Info,
                    title = stringResource(R.string.about),
                    summary = stringResource(R.string.summary_preference_settings_about),
                    onClick = {
                        IntentUtils.openActivity(context, AboutSettingsActivity::class.java)
                    })
            }
        }
    }
}