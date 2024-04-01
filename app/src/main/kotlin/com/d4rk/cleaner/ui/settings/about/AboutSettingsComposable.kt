package com.d4rk.cleaner.ui.settings.about

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
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
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.PreferenceCategoryItem
import com.d4rk.cleaner.utils.PreferenceItem
import com.d4rk.cleaner.utils.Utils
import com.google.android.gms.oss.licenses.OssLicensesActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSettingsComposable(activity: AboutSettingsActivity) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) ,
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.about)) } ,
                navigationIcon = {
                    IconButton(onClick = {
                        activity.finish()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null)
                    }
                } ,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues),
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(R.string.app_info))
                PreferenceItem(
                    title = stringResource(R.string.app_name) ,
                    summary = stringResource(R.string.copyright),
                )
                PreferenceItem(
                    title = stringResource(R.string.app_build_version) ,
                    summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                )
                PreferenceItem(
                    title = stringResource(com.google.android.gms.oss.licenses.R.string.oss_license_title) ,
                    summary = stringResource(R.string.summary_preference_settings_oss),
                    onClick = {
                        Utils.openActivity(context , OssLicensesMenuActivity::class.java)
                    }
                )
            }
            item {
                PreferenceCategoryItem(title = stringResource(R.string.device_info))
                val version = stringResource(
                    id = R.string.app_build,
                    "${stringResource(R.string.manufacturer)} ${Build.MANUFACTURER}",
                    "${stringResource(R.string.device_model)} ${Build.MODEL}",
                    "${stringResource(R.string.android_version)} ${Build.VERSION.RELEASE}",
                    "${stringResource(R.string.api_level)} ${Build.VERSION.SDK_INT}",
                    "${stringResource(R.string.arch)} ${Build.SUPPORTED_ABIS.joinToString()}"
                )

                PreferenceItem(
                    title = stringResource(id = R.string.device_info),
                    summary = version,
                    onClick = {
                        val clip = ClipData.newPlainText("text", version)
                        clipboardManager.setPrimaryClip(clip)
                        // Show snackbar
                    }
                )
            }
        }
    }
}