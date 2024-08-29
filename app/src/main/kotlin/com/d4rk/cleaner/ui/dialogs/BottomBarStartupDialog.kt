package com.d4rk.cleaner.ui.dialogs

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.ui.bottombar.BottomBarRoutes
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.utils.haptic.weakHapticFeedback
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun BottomBarStartupDialog(
    dataStore: DataStore,
    onDismiss: () -> Unit,
    onStartupSelected: (String) -> Unit
) {
    val defaultPage: MutableState<String> = remember { mutableStateOf(BottomBarRoutes.HOME) }
    val startupEntries: List<String> =
        stringArrayResource(R.array.preference_startup_entries).toList()
    val startupValues: List<String> =
        stringArrayResource(R.array.preference_startup_values).toList()
    val view: View = LocalView.current
    AlertDialog(onDismissRequest = onDismiss, text = {
        BottomBarStartupDialogContent(
            defaultPage, dataStore, startupEntries, startupValues
        )
    }, icon = {
        Icon(Icons.Outlined.Home, contentDescription = null)
    }, confirmButton = {
        TextButton(onClick = {
            view.weakHapticFeedback()
            onStartupSelected(defaultPage.value)
            onDismiss()
        }) {
            Text(stringResource(android.R.string.ok))
        }
    }, dismissButton = {
        view.weakHapticFeedback()
        TextButton(onClick = onDismiss) {
            Text(stringResource(android.R.string.cancel))
        }
    })
}

@Composable
fun BottomBarStartupDialogContent(
    selectedPage: MutableState<String>,
    dataStore: DataStore,
    startupEntries: List<String>,
    startupValues: List<String>
) {
    val view: View = LocalView.current
    LaunchedEffect(Unit) {
        selectedPage.value = dataStore.getStartupPage().firstOrNull() ?: BottomBarRoutes.HOME
    }

    Column {
        Text(stringResource(id = R.string.dialog_startup_subtitle))
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LazyColumn {
                items(startupEntries.size) { index ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(selected = selectedPage.value == startupValues[index],
                            onClick = {
                                view.weakHapticFeedback()
                                selectedPage.value = startupValues[index]
                            })
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = startupEntries[index],
                            style = MaterialTheme.typography.bodyMedium.merge()
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(id = R.string.dialog_info_startup))
    }

    LaunchedEffect(selectedPage.value) {
        dataStore.saveStartupPage(selectedPage.value)
    }
}