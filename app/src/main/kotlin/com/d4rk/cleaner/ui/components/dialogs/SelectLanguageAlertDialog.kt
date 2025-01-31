package com.d4rk.cleaner.ui.components.dialogs

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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.MediumVerticalSpacer
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.datastore.DataStore
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun SelectLanguageAlertDialog(
    dataStore: DataStore, onDismiss: () -> Unit, onLanguageSelected: (String) -> Unit
) {
    val selectedLanguage: MutableState<String> = remember { mutableStateOf(value = "") }
    val languageEntries: List<String> =
        stringArrayResource(R.array.preference_language_entries).toList()
    val languageValues: List<String> =
        stringArrayResource(R.array.preference_language_values).toList()

    AlertDialog(onDismissRequest = onDismiss, text = {
        SelectLanguageAlertDialogContent(
            selectedLanguage, dataStore, languageEntries, languageValues
        )
    }, icon = {
        Icon(Icons.Outlined.Language, contentDescription = null)
    }, confirmButton = {
        TextButton(onClick = {
            onLanguageSelected(selectedLanguage.value)
            onDismiss()
        }) {
            Text(text = stringResource(id = android.R.string.ok))
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = stringResource(id = android.R.string.cancel))
        }
    })
}

@Composable
fun SelectLanguageAlertDialogContent(
    selectedLanguage: MutableState<String>,
    dataStore: DataStore,
    languageEntries: List<String>,
    languageValues: List<String>
) {
    LaunchedEffect(Unit) {
        selectedLanguage.value = dataStore.getLanguage().firstOrNull() ?: ""
    }

    Column {
        Text(text = stringResource(id = R.string.dialog_language_subtitle))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn {
                items(languageEntries.size) { index ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(selected = selectedLanguage.value == languageValues[index],
                            onClick = {
                                selectedLanguage.value = languageValues[index]
                            })
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = languageEntries[index],
                            style = MaterialTheme.typography.bodyMedium.merge()
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
        MediumVerticalSpacer()
        Text(text = stringResource(id = R.string.dialog_info_language))
    }

    LaunchedEffect(selectedLanguage.value) {
        dataStore.saveLanguage(selectedLanguage.value)
    }
}