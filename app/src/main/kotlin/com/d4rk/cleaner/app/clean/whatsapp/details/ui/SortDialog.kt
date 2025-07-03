package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SortDialog(
    current: SortType,
    onDismiss: () -> Unit,
    onSortSelected: (SortType) -> Unit
) {
    val options = SortType.values().toList()
    val selected = remember { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSortSelected(selected.value); onDismiss() }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Sort by") },
        text = {
            Column {
                options.forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected.value = type }
                    ) {
                        RadioButton(
                            selected = selected.value == type,
                            onClick = { selected.value = type }
                        )
                        Text(
                            text = type.name.lowercase().replaceFirstChar { it.uppercase() }
                        )
                    }
                }
            }
        }
    )
}
