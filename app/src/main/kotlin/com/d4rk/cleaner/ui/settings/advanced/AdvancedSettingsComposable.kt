package com.d4rk.cleaner.ui.settings.advanced

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.PreferenceCategoryItem
import com.d4rk.cleaner.utils.PreferenceItem
import com.d4rk.cleaner.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsComposable(activity: AdvancedSettingsActivity) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.advanced)) }, navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null
                )
            }
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues),
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(R.string.error_reporting))
                PreferenceItem(title = stringResource(R.string.bug_report),
                               summary = stringResource(R.string.summary_preference_settings_bug_report),
                               onClick = {
                                   Utils.openUrl(
                                       context,
                                       "https://github.com/D4rK7355608/${context.packageName}/issues/new"
                                   )
                               })
            }
        }
    }
}