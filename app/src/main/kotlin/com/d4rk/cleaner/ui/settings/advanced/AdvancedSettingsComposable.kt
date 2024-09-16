package com.d4rk.cleaner.ui.settings.advanced

import android.content.Context
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.IntentUtils
import com.d4rk.cleaner.utils.compose.components.PreferenceCategoryItem
import com.d4rk.cleaner.utils.compose.components.PreferenceItem
import com.d4rk.cleaner.utils.compose.components.TopAppBarScaffold

@Composable
fun AdvancedSettingsComposable(activity : AdvancedSettingsActivity) {
    val context : Context = LocalContext.current
    TopAppBarScaffold(
        title = stringResource(R.string.advanced) ,
        onBackClicked = { activity.finish() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues) ,
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(R.string.error_reporting))
                PreferenceItem(title = stringResource(R.string.bug_report) ,
                               summary = stringResource(R.string.summary_preference_settings_bug_report) ,
                               onClick = {
                                   IntentUtils.openUrl(
                                       context ,
                                       url = "https://github.com/D4rK7355608/${context.packageName}/issues/new"
                                   )
                               })
            }
        }
    }
}