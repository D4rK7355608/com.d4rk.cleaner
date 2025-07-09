package com.d4rk.cleaner.app.clean.whatsapp.details.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.SortType
import java.text.DateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDialog(
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            decorFitsSystemWindows = true
        ),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 64.dp, horizontal = 32.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(8.dp),
                    text = "Sort options",
                    style = MaterialTheme.typography.headlineLarge,
                )

                options.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected.value = type },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selected.value == type,
                            onClick = { selected.value = type },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
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
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DateRangePicker(state = dateState)
                    }
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (selected.value) {
                        SortType.DATE -> {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .pointerInput(Unit) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                showDatePicker = true
                                            }
                                        }
                                    },
                                readOnly = true,
                                value = dateState.selectedStartDateMillis?.let { java.text.DateFormat.getDateInstance().format(it) } ?: "N/A",
                                onValueChange = {},
                                label = { Text("From Date") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .pointerInput(Unit) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                showDatePicker = true
                                            }
                                        }
                                    },
                                readOnly = true,
                                value = dateState.selectedEndDateMillis?.let { java.text.DateFormat.getDateInstance().format(it) } ?: "N/A",
                                onValueChange = {},
                                label = { Text("To Date") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
                                singleLine = true
                            )
                        }
                        else -> {}
                    }
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isDescending.value,
                        onCheckedChange = { isDescending.value = it }
                    )
                    Text(text = "Descending", modifier = Modifier.padding(start = 8.dp))
                }

                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(4.dp),
                    onClick = {
                        onApply(
                            selected.value,
                            isDescending.value,
                            dateState.selectedStartDateMillis,
                            dateState.selectedEndDateMillis
                        )
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Apply",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
