package com.d4rk.cleaner.app.clean.scanner.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.cleaner.R

@Composable
fun HideStreakAlertDialog(
    onHideForNow: () -> Unit,
    onHidePermanently: () -> Unit,
    onDismiss: () -> Unit
) {
    var hidePermanently by remember { mutableStateOf(false) }

    BasicAlertDialog(
        onDismiss = onDismiss,
        onConfirm = {
            if (hidePermanently) {
                onHidePermanently()
            } else {
                onHideForNow()
            }
        },
        onCancel = onDismiss,
        icon = Icons.Outlined.VisibilityOff,
        title = stringResource(id = R.string.hide_clean_streak_title),
        confirmButtonText = stringResource(id = R.string.hide_streak_action_hide),
        content = {
            Column {
                Text(text = stringResource(id = R.string.hide_clean_streak_message))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { hidePermanently = !hidePermanently }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hidePermanently,
                        onCheckedChange = { isChecked -> hidePermanently = isChecked }
                    )
                    Text(
                        text = stringResource(id = R.string.dont_show_again),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
    )
}