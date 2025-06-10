package com.d4rk.cleaner.app.clean.home.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmationAlertDialog(
    confirmationTitle : String , confirmationMessage : String , confirmationConfirmButtonText : String , confirmationDismissButtonText : String , onConfirm : () -> Unit , onDismiss : () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss , title = { Text(text = confirmationTitle) } , text = { Text(text = confirmationMessage) } , confirmButton = {
        TextButton(onClick = onConfirm) {
            Text(text = confirmationConfirmButtonText)
        }
    } , dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = confirmationDismissButtonText)
        }
    })
}