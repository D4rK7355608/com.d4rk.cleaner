package com.d4rk.cleaner.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R

@Composable
fun RescanAlertDialog(
    onYes: () -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.rescan_title)) },
        text = { Text(stringResource(id = R.string.rescan_message)) },
        confirmButton = {
            TextButton(onClick = {
                onYes()
            }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(stringResource(android.R.string.cancel))
            }
        })
}