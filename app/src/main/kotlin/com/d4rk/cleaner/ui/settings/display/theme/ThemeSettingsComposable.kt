package com.d4rk.cleaner.ui.settings.display.theme

import android.content.Context
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.utils.compose.bounceClick
import com.d4rk.cleaner.utils.compose.components.SwitchCardComposable
import com.d4rk.cleaner.utils.compose.components.TopAppBarScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ThemeSettingsComposable(activity : ThemeSettingsActivity) {
    val context : Context = LocalContext.current
    val dataStore : DataStore = DataStore.getInstance(context)
    val scope : CoroutineScope = rememberCoroutineScope()
    val themeMode : String = dataStore.themeMode.collectAsState(initial = "follow_system").value
    val isAmoledMode : State<Boolean> = dataStore.amoledMode.collectAsState(initial = false)

    val themeOptions : List<String> = listOf(
        stringResource(id = R.string.follow_system),
        stringResource(id = R.string.dark_mode),
        stringResource(id = R.string.light_mode),
    )
    TopAppBarScaffold(
        title = stringResource(id = R.string.dark_theme),
        onBackClicked = { activity.finish() }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) ,
            ) {
                item {
                    SwitchCardComposable(
                        title = stringResource(id = R.string.amoled_mode),
                        switchState = isAmoledMode
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
                                Modifier.fillMaxWidth() ,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    modifier = Modifier.bounceClick() ,
                                    selected = (text == themeMode) ,
                                    onClick = {
                                        scope.launch(Dispatchers.IO) {
                                            dataStore.saveThemeMode(text)
                                            dataStore.themeModeState.value = text
                                        }
                                    })
                                Text(
                                    text = text ,
                                    style = MaterialTheme.typography.bodyMedium.merge() ,
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
                        Icon(imageVector = Icons.Outlined.Info , contentDescription = null)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(stringResource(id = R.string.summary_dark_theme))
                    }
                }
            }
        }
    }
}