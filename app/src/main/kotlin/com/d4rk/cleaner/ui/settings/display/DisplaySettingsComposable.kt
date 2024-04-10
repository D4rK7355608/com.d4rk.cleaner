package com.d4rk.cleaner.ui.settings.display

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.store.DataStore
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
    val dataStore = DataStore(context)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isDarkMode = dataStore.darkMode.collectAsState(initial = false)
    val themeMode = dataStore.themeMode.collectAsState(initial = "follow_system")
    val isDynamicColors = dataStore.dynamicColors.collectAsState(initial = true)
    val swappedButtons = dataStore.swappedButtons.collectAsState(initial = false)
    val darkModeString = stringResource(R.string.dark_mode)
    val lightModeString = stringResource(R.string.light_mode)
    val systemModeString = stringResource(R.string.follow_system)
    val isSystemDarkTheme = isSystemInDarkTheme()
    val switchState = remember { mutableStateOf(isDarkMode.value) }
    LaunchedEffect(isDarkMode.value) {
        if (themeMode.value != systemModeString) {
            val saveThemeMode = if (isDarkMode.value) darkModeString else lightModeString
            dataStore.saveThemeMode(saveThemeMode)
        } else {
            dataStore.saveDarkMode(isSystemDarkTheme)
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.display)) } ,
                navigationIcon = {
                    IconButton(onClick = {
                        activity.finish()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
                PreferenceCategoryItem(title = stringResource(R.string.appearance))
                SwitchPreferenceItemWithDivider(
                    title = stringResource(R.string.dark_theme),
                    summary = when (themeMode.value) {
                        darkModeString, lightModeString -> "Will never turn on automatically"
                        else -> "Will turn on automatically by the system"
                    },
                    checked = switchState.value,
                    onCheckedChange = { isChecked ->
                        CoroutineScope(Dispatchers.IO).launch {
                            switchState.value = isChecked
                            if (themeMode.value != systemModeString) {
                                dataStore.saveDarkMode(isChecked)
                                if (isChecked) {
                                    dataStore.themeModeState.value = darkModeString
                                } else {
                                    dataStore.themeModeState.value = lightModeString
                                }
                            }
                        }
                    },
                    onClick = {
                        Utils.openActivity(context, ThemeSettingsActivity::class.java)
                    }
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SwitchPreferenceItem(
                        title = "Dynamic colors",
                        summary = "Apply colors from wallpapers to the app theme",
                        checked = isDynamicColors.value,
                    ) { isChecked ->
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveDynamicColors(isChecked)
                        }
                    }
                }
            }
            item {
                PreferenceCategoryItem(title = stringResource(R.string.app_behavior))
                PreferenceItem(
                    title = stringResource(R.string.default_tab),
                    summary = "Set the default tab to be displayed on app startup",
                    onClick = {
                        // TODO: Display the select dialog
                    }
                )
                PreferenceItem(
                    title = stringResource(R.string.bottom_navigation_bar_labels),
                    summary = "Set the visibility of labels in the bottom navigation bar",
                    onClick = {
                        // TODO: Display the select dialog
                    }
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.swap_buttons),
                    summary = stringResource(R.string.summary_preference_settings_swap_buttons),
                    checked = swappedButtons.value,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveSwappedButtons(isChecked)
                    }
                }
            }
            item {
                PreferenceCategoryItem(title = stringResource(R.string.language))
                PreferenceItem(
                    title = stringResource(R.string.language),
                    summary = "Changes the language used in the app",
                    onClick = {
                        // TODO: Display the select dialog
                    }
                )
            }

        }
    }
}