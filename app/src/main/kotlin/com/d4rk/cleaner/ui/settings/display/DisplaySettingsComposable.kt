package com.d4rk.cleaner.ui.settings.display

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.ui.dialogs.BottomBarStartupDialog
import com.d4rk.cleaner.ui.dialogs.LanguageDialog
import com.d4rk.cleaner.ui.settings.display.theme.ThemeSettingsActivity
import com.d4rk.cleaner.utils.IntentUtils
import com.d4rk.cleaner.utils.compose.components.PreferenceCategoryItem
import com.d4rk.cleaner.utils.compose.components.PreferenceItem
import com.d4rk.cleaner.utils.compose.components.SwitchPreferenceItem
import com.d4rk.cleaner.utils.compose.components.SwitchPreferenceItemWithDivider
import com.d4rk.cleaner.utils.compose.components.TopAppBarScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DisplaySettingsComposable(activity : DisplaySettingsActivity) {
    val context : Context = LocalContext.current
    val dataStore : DataStore = DataStore.getInstance(context)
    var showLanguageDialog : Boolean by remember { mutableStateOf(value = false) }
    var showStartupDialog : Boolean by remember { mutableStateOf(value = false) }
    val themeMode : String = dataStore.themeMode.collectAsState(initial = "follow_system").value
    val darkModeString: String = stringResource(id = R.string.dark_mode)
    val lightModeString: String = stringResource(id = R.string.light_mode)
    val themeSummary : String = when (themeMode) {
        darkModeString, lightModeString -> stringResource(id = R.string.will_never_turn_on_automatically)
        else -> stringResource(id = R.string.will_turn_on_automatically_by_system)
    }
    val switchState : MutableState<Boolean> =
            remember { mutableStateOf(value = themeMode == darkModeString) }
    val bouncyButtons : Boolean by dataStore.bouncyButtons.collectAsState(initial = true)


    val isDynamicColors : State<Boolean> = dataStore.dynamicColors.collectAsState(initial = true)
    val scope : CoroutineScope = rememberCoroutineScope()

    TopAppBarScaffold(
        title = stringResource(id = R.string.display),
                      onBackClicked = { activity.finish() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues) ,
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(id = R.string.appearance))
                SwitchPreferenceItemWithDivider(
                    title = stringResource(id = R.string.dark_theme),
                                                summary = themeSummary ,
                                                checked = switchState.value ,
                                                onCheckedChange = { isChecked ->
                                                    switchState.value = isChecked
                                                } ,
                                                onSwitchClick = { isChecked ->
                                                    scope.launch(Dispatchers.IO) {
                                                        if (isChecked) {
                                                            dataStore.saveThemeMode(darkModeString)
                                                            dataStore.themeModeState.value =
                                                                    darkModeString
                                                        }
                                                        else {
                                                            dataStore.saveThemeMode(lightModeString)
                                                            dataStore.themeModeState.value =
                                                                    lightModeString
                                                        }
                                                    }
                                                } ,
                                                onClick = {
                                                    IntentUtils.openActivity(
                                                        context , ThemeSettingsActivity::class.java
                                                    )
                                                })

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SwitchPreferenceItem(
                        title = stringResource(id = R.string.dynamic_colors),
                        summary = stringResource(id = R.string.summary_preference_settings_dynamic_colors),
                        checked = isDynamicColors.value ,
                    ) { isChecked ->
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveDynamicColors(isChecked)
                        }
                    }
                }
            }
            item {
                PreferenceCategoryItem(title = stringResource(id = R.string.app_behavior))
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.bounce_buttons),
                    summary = stringResource(id = R.string.summary_preference_settings_bounce_buttons),
                    checked = bouncyButtons ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveBouncyButtons(isChecked)
                    }
                }
            }
            item {
                PreferenceCategoryItem(title = stringResource(id = R.string.navigation))
                PreferenceItem(
                    title = stringResource(id = R.string.startup_page),
                    summary = stringResource(id = R.string.summary_preference_settings_startup_page),
                               onClick = { showStartupDialog = true })

                if (showStartupDialog) {
                    BottomBarStartupDialog(dataStore = dataStore ,
                                           onDismiss = { showStartupDialog = false } ,
                                           onStartupSelected = { selectedStartup ->
                                               scope.launch {
                                                   dataStore.saveStartupPage(selectedStartup)
                                               }
                                           })
                }
            }
            item {
                PreferenceCategoryItem(title = stringResource(id = R.string.language))
                PreferenceItem(
                    title = stringResource(id = R.string.language),
                               summary = stringResource(id = R.string.summary_preference_settings_language) ,
                               onClick = {
                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                       val localeIntent : Intent =
                                               Intent(Settings.ACTION_APP_LOCALE_SETTINGS).setData(
                                                   Uri.fromParts(
                                                       "package" , context.packageName , null
                                                   )
                                               )
                                       val detailsIntent : Intent =
                                               Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                                                   Uri.fromParts(
                                                       "package" , context.packageName , null
                                                   )
                                               )
                                       when {
                                           context.packageManager.resolveActivity(
                                               localeIntent , 0
                                           ) != null -> context.startActivity(localeIntent)

                                           context.packageManager.resolveActivity(
                                               detailsIntent , 0
                                           ) != null -> context.startActivity(detailsIntent)

                                           else -> {
                                               showLanguageDialog = true
                                           }
                                       }
                                   }
                                   else {
                                       showLanguageDialog = true
                                   }
                               })
                if (showLanguageDialog) {
                    LanguageDialog(dataStore = dataStore ,
                                   onDismiss = { showLanguageDialog = false } ,
                                   onLanguageSelected = { newLanguageCode ->
                                       AppCompatDelegate.setApplicationLocales(
                                           LocaleListCompat.forLanguageTags(
                                               newLanguageCode
                                           )
                                       )
                                   })
                }
            }
        }
    }
}