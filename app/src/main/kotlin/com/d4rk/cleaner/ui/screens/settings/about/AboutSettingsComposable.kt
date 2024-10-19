package com.d4rk.cleaner.ui.screens.settings.about

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.components.PreferenceCategoryItem
import com.d4rk.cleaner.ui.components.PreferenceItem
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton
import com.d4rk.cleaner.utils.IntentUtils
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

@Composable
fun AboutSettingsComposable(activity: AboutSettingsActivity) {
    val context: Context = LocalContext.current
    val clipboardManager: ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var showSnackbar: Boolean by remember { mutableStateOf(value = false) }
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage: String = stringResource(id = R.string.snack_device_info_copied)

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage, duration = SnackbarDuration.Short
            )
            showSnackbar = false
        }
    }

    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.about),
        onBackClicked = { activity.finish() }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                item(key = "app_info_category") {
                    PreferenceCategoryItem(title = stringResource(id = R.string.app_info))
                }
                item(key = "app_name") {
                    PreferenceItem(
                        title = stringResource(id = R.string.app_name),
                        summary = stringResource(id = R.string.copyright),
                    )
                }
                item(key = "app_build_version") {
                    PreferenceItem(
                        title = stringResource(id = R.string.app_build_version),
                        summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                    )
                }
                item(key = "oss_licenses") {
                    PreferenceItem(
                        title = stringResource(com.google.android.gms.oss.licenses.R.string.oss_license_title),
                        summary = stringResource(id = R.string.summary_preference_settings_oss),
                        onClick = {
                            IntentUtils.openActivity(
                                context, OssLicensesMenuActivity::class.java
                            )
                        }
                    )
                }
                item(key = "device_info_category") {
                    PreferenceCategoryItem(title = stringResource(id = R.string.device_info))
                }
                item(key = "device_info") {
                    val version: String = stringResource(
                        id = R.string.app_build,
                        "${stringResource(id = R.string.manufacturer)} ${Build.MANUFACTURER}",
                        "${stringResource(id = R.string.device_model)} ${Build.MODEL}",
                        "${stringResource(id = R.string.android_version)} ${Build.VERSION.RELEASE}",
                        "${stringResource(id = R.string.api_level)} ${Build.VERSION.SDK_INT}",
                        "${stringResource(id = R.string.arch)} ${Build.SUPPORTED_ABIS.joinToString()}",
                        if (BuildConfig.DEBUG) stringResource(id = R.string.debug) else stringResource(id = R.string.release)
                    )

                    PreferenceItem(
                        title = stringResource(id = R.string.device_info),
                        summary = version,
                        onClick = {
                            val clip: ClipData = ClipData.newPlainText("text", version)
                            clipboardManager.setPrimaryClip(clip)
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                showSnackbar = true
                            }
                        }
                    )
                }
            }
            SnackbarHost(
                hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}