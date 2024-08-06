package com.d4rk.cleaner.ui.settings.privacy

import android.content.Context
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.settings.privacy.ads.AdsSettingsActivity
import com.d4rk.cleaner.ui.settings.privacy.permissions.PermissionsSettingsActivity
import com.d4rk.cleaner.ui.settings.privacy.usage.UsageAndDiagnosticsActivity
import com.d4rk.cleaner.utils.IntentUtils
import com.d4rk.cleaner.utils.compose.components.PreferenceCategoryItem
import com.d4rk.cleaner.utils.compose.components.PreferenceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsComposable(activity : PrivacySettingsActivity) {
    val context : Context = LocalContext.current
    val scrollBehavior : TopAppBarScrollBehavior =
            TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.security_and_privacy)) } ,
                       navigationIcon = {
                           IconButton(onClick = {
                               activity.finish()
                           }) {
                               Icon(
                                   Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null
                               )
                           }
                       } ,
                       scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues) ,
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(R.string.privacy))
                PreferenceItem(title = stringResource(R.string.privacy_policy) ,
                               summary = stringResource(id = R.string.summary_preference_settings_privacy_policy) ,
                               onClick = {
                                   IntentUtils.openUrl(
                                       context ,
                                       url = "https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy"
                                   )
                               })
                PreferenceItem(title = stringResource(R.string.terms_of_service) ,
                               summary = stringResource(id = R.string.summary_preference_settings_terms_of_service) ,
                               onClick = {
                                   IntentUtils.openUrl(
                                       context ,
                                       url = "https://sites.google.com/view/d4rk7355608/more/apps/terms-of-service"
                                   )
                               })
                PreferenceItem(title = stringResource(R.string.code_of_conduct) ,
                               summary = stringResource(id = R.string.summary_preference_settings_code_of_conduct) ,
                               onClick = {
                                   IntentUtils.openUrl(
                                       context ,
                                       url = "https://sites.google.com/view/d4rk7355608/more/code-of-conduct"
                                   )
                               })
                PreferenceItem(title = stringResource(R.string.permissions) ,
                               summary = stringResource(id = R.string.summary_preference_settings_permissions) ,
                               onClick = {
                                   IntentUtils.openActivity(
                                       context , PermissionsSettingsActivity::class.java
                                   )
                               })
                PreferenceItem(title = stringResource(R.string.ads) ,
                               summary = stringResource(id = R.string.summary_preference_settings_ads) ,
                               onClick = {
                                   IntentUtils.openActivity(
                                       context , AdsSettingsActivity::class.java
                                   )
                               })
                PreferenceItem(title = stringResource(R.string.usage_and_diagnostics) ,
                               summary = stringResource(id = R.string.summary_preference_settings_usage_and_diagnostics) ,
                               onClick = {
                                   IntentUtils.openActivity(
                                       context , UsageAndDiagnosticsActivity::class.java
                                   )
                               })
            }
            item {
                PreferenceCategoryItem(title = stringResource(R.string.legal))
                PreferenceItem(title = stringResource(R.string.legal_notices) ,
                               summary = stringResource(id = R.string.summary_preference_settings_legal_notices) ,
                               onClick = {
                                   IntentUtils.openUrl(
                                       context ,
                                       url = "https://sites.google.com/view/d4rk7355608/more/apps/legal-notices"
                                   )
                               })
                PreferenceItem(title = stringResource(R.string.license) ,
                               summary = stringResource(R.string.summary_preference_settings_license) ,
                               onClick = {
                                   IntentUtils.openUrl(
                                       context , url = "https://www.gnu.org/licenses/gpl-3.0"
                                   )
                               })
            }
        }
    }
}