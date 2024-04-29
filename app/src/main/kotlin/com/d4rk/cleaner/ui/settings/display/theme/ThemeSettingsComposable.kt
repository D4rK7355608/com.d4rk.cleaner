package com.d4rk.cleaner.ui.settings.display.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.store.DataStore
import com.d4rk.cleaner.utils.SwitchCardComposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsComposable(activity: ThemeSettingsActivity) {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val themeMode = dataStore.themeMode.collectAsState(initial = "follow_system")
    val isAmoledMode = dataStore.amoledMode.collectAsState(initial = false)
    val darkModeString = stringResource(R.string.dark_mode)
    LaunchedEffect(themeMode.value) {
        val isDarkMode = themeMode.value == darkModeString
        dataStore.saveDarkMode(isDarkMode)
    }

    val themeOptions = listOf(
        stringResource(R.string.follow_system),
        stringResource(R.string.dark_mode),
        stringResource(R.string.light_mode),
        stringResource(R.string.auto_battery_mode)
    )
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(
            title = { Text(stringResource(R.string.dark_theme)) },
            navigationIcon = {
                IconButton(onClick = {
                    activity.finish()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                item {
                    SwitchCardComposable(
                        title = stringResource(R.string.amoled_mode), switchState = isAmoledMode
                    ) { isChecked ->
                        scope.launch(Dispatchers.IO) {
                            dataStore.saveAmoledMode(isChecked)
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        themeOptions.forEach { text ->
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = (text == themeMode.value), onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        dataStore.saveThemeMode(text)
                                        dataStore.themeModeState.value = text
                                    }
                                })
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodyMedium.merge(),
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(stringResource(R.string.summary_dark_theme))
                    }
                }
            }
        }
    }
}