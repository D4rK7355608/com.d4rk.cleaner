package com.d4rk.cleaner.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource


@Composable
fun RescanAlertDialog(
    onYes: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rescan?") },
        text = { Text("Are you sure you want to scan again?") },
        confirmButton = {
            TextButton(onClick = onYes) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}