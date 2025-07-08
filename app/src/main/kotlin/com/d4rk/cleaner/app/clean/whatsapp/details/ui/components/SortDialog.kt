package com.d4rk.cleaner.app.clean.whatsapp.details.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.SortType
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog

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
            ); onDismiss()
        },
        onCancel = onDismiss,
        confirmButtonText = "OK",
        dismissButtonText = "Cancel",
        title = "Sort options",
        content = {
            Column {
                options.forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selected.value == type,
                            onClick = { selected.value = type }
                        )
                        Text(text = type.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = isDescending.value, onCheckedChange = { isDescending.value = it })
                    Text(text = "Descending")
                }
                DateRangePicker(state = dateState)
            }
        }
    )
}
