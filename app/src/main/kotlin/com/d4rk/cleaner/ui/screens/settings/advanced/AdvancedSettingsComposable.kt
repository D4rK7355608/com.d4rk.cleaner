package com.d4rk.cleaner.ui.screens.settings.advanced

import android.content.Context
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.components.preferences.PreferenceCategoryItem
import com.d4rk.cleaner.ui.components.preferences.PreferenceItem
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton
import com.d4rk.cleaner.utils.helpers.IntentsHelper

@Composable
fun AdvancedSettingsComposable(activity: AdvancedSettingsActivity) {
    val context: Context = LocalContext.current
    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.advanced),
        onBackClicked = { activity.finish() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues),
        ) {
            item(key = "error_reporting_category") {
                PreferenceCategoryItem(title = stringResource(id = R.string.error_reporting))
            }
            item(key = "bug_report") {
                PreferenceItem(
                    title = stringResource(id = R.string.bug_report),
                    summary = stringResource(id = R.string.summary_preference_settings_bug_report),
                    onClick = {
                        IntentsHelper.openUrl(
                            context,
                            url = "https://github.com/D4rK7355608/${context.packageName}/issues/new"
                        )
                    }
                )
            }
        }
    }
}