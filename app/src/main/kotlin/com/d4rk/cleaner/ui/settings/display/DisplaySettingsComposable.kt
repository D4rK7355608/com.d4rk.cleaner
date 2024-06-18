package com.d4rk.cleaner.ui.settings.display

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.ui.dialogs.LanguageDialog
import com.d4rk.cleaner.ui.settings.display.theme.ThemeSettingsActivity
import com.d4rk.cleaner.utils.PreferenceCategoryItem
import com.d4rk.cleaner.utils.PreferenceItem
import com.d4rk.cleaner.utils.SwitchPreferenceItem
import com.d4rk.cleaner.utils.SwitchPreferenceItemWithDivider
import com.d4rk.cleaner.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplaySettingsComposable(activity: DisplaySettingsActivity) {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showLanguageDialog by remember { mutableStateOf(false) }
    val themeMode = dataStore.themeMode.collectAsState(initial = "follow_system").value
    val darkModeString = stringResource(R.string.dark_mode)
    val lightModeString = stringResource(R.string.light_mode)
    val themeSummary = when (themeMode) {
        darkModeString, lightModeString -> stringResource(R.string.will_never_turn_on_automatically)
        else -> stringResource(R.string.will_turn_on_automatically_by_system)
    }
    val switchState = remember { mutableStateOf(themeMode == darkModeString) }

    val isDynamicColors = dataStore.dynamicColors.collectAsState(initial = true)
    val scope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.display)) }, navigationIcon = {
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
                PreferenceCategoryItem(title = stringResource(R.string.appearance))
                SwitchPreferenceItemWithDivider(title = stringResource(R.string.dark_theme),
                                                summary = themeSummary,
                                                checked = switchState.value,
                                                onCheckedChange = { isChecked ->
                                                    switchState.value = isChecked
                                                },
                                                onSwitchClick = { isChecked ->
                                                    scope.launch(Dispatchers.IO) {
                                                        if (isChecked) {
                                                            dataStore.saveThemeMode(darkModeString)
                                                            dataStore.themeModeState.value =
                                                                    darkModeString
                                                        } else {
                                                            dataStore.saveThemeMode(lightModeString)
                                                            dataStore.themeModeState.value =
                                                                    lightModeString
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    Utils.openActivity(
                                                        context, ThemeSettingsActivity::class.java
                                                    )
                                                })

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SwitchPreferenceItem(
                        title = stringResource(R.string.dynamic_colors),
                        summary = stringResource(R.string.summary_preference_settings_dynamic_colors),
                        checked = isDynamicColors.value,
                    ) { isChecked ->
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveDynamicColors(isChecked)
                        }
                    }
                }
            }
            item {
                PreferenceCategoryItem(title = stringResource(R.string.language))
                PreferenceItem(title = stringResource(R.string.language),
                               summary = stringResource(id = R.string.summary_preference_settings_language),
                               onClick = {
                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                       val localeIntent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).setData(
                                           Uri.fromParts("package", context.packageName, null)
                                       )
                                       val detailsIntent =
                                               Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                                                   Uri.fromParts("package", context.packageName, null)
                                               )
                                       when {
                                           context.packageManager.resolveActivity(
                                               localeIntent, 0
                                           ) != null -> context.startActivity(localeIntent)

                                           context.packageManager.resolveActivity(
                                               detailsIntent, 0
                                           ) != null -> context.startActivity(detailsIntent)

                                           else -> {
                                               showLanguageDialog = true
                                           }
                                       }
                                   } else {
                                       showLanguageDialog = true
                                   }
                               })
                if (showLanguageDialog) {
                    LanguageDialog(
                        dataStore = dataStore,
                        onDismiss = { showLanguageDialog = false },
                        onLanguageSelected = { newLanguageCode ->
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(
                                    newLanguageCode
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}