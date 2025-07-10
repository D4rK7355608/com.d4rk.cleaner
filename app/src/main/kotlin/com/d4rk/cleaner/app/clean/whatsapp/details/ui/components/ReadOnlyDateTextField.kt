package com.d4rk.cleaner.app.clean.whatsapp.details.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import java.text.DateFormat

@Composable
fun ReadOnlyDateTextField(
    label: String,
    value: Long?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        onClick()
                    }
                }
            },
        readOnly = true,
        value = value?.let { DateFormat.getDateInstance().format(it) } ?: "",
        onValueChange = {},
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
        singleLine = true
    )
}