package com.d4rk.cleaner.ui.dialogs

import android.view.View
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.haptic.weakHapticFeedback


@Composable
fun RescanAlertDialog(
    onYes : () -> Unit , onDismiss : () -> Unit
) {
    val view : View = LocalView.current
    AlertDialog(onDismissRequest = onDismiss ,
                title = { Text(stringResource(id = R.string.rescan_title)) } ,
                text = { Text(stringResource(id = R.string.rescan_message)) } ,
                confirmButton = {
                    TextButton(onClick = {
                        view.weakHapticFeedback()
                        onYes()
                    }) {
                        Text(stringResource(android.R.string.ok))
                    }
                } ,
                dismissButton = {
                    TextButton(onClick = {
                        view.weakHapticFeedback()
                        onDismiss()
                    }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                })
}