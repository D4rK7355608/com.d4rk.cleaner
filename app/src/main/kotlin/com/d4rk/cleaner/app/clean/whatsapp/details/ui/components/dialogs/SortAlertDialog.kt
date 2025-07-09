package com.d4rk.cleaner.app.clean.whatsapp.details.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.SortType
import java.text.DateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortAlertDialog(
    current: SortType,
    descending: Boolean,
    startDate: Long?,
    endDate: Long?,
    onDismiss: () -> Unit,
    onApply: (SortType, Boolean, Long?, Long?) -> Unit
) {
    val options = SortType.entries
    var showDatePicker by remember { mutableStateOf(false) }
    val selected = remember { mutableStateOf(current) }
    val isDescending = remember { mutableStateOf(descending) }
    val dateState: DateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startDate,
        initialSelectedEndDateMillis = endDate
    )

    BasicAlertDialog(
        onDismiss = onDismiss,
        onConfirm = {
            onApply(
                selected.value,
                isDescending.value,
                dateState.selectedStartDateMillis,
                dateState.selectedEndDateMillis
            )
            onDismiss()
        },
        icon = Icons.AutoMirrored.Filled.Sort,
        title = "Sort options",
        confirmButtonText = "Apply",
        content = {
            Column {
                options.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .clickable { selected.value = type },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selected.value == type,
                            onClick = { selected.value = type },
                        )
                        Text(
                            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text(stringResource(id = android.R.string.ok))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text(stringResource(id = android.R.string.cancel))
                            }
                        }
                    ) {
                        DateRangePicker(state = dateState)
                    }
                }

                if (selected.value == SortType.DATE) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReadOnlyDateTextField(
                            label = "From Date",
                            value = dateState.selectedStartDateMillis,
                            modifier = Modifier.weight(1f),
                            onClick = { showDatePicker = true }
                        )

                        ReadOnlyDateTextField(
                            label = "To Date",
                            value = dateState.selectedEndDateMillis,
                            modifier = Modifier.weight(1f),
                            onClick = { showDatePicker = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { isDescending.value = !isDescending.value }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Descending", modifier = Modifier.weight(1f))
                    Switch(
                        checked = isDescending.value,
                        onCheckedChange = { isDescending.value = it }
                    )
                }
            }
        }
    )
}

@Composable
private fun ReadOnlyDateTextField(
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