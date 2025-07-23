package com.d4rk.cleaner.app.clean.whatsapp.details.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.android.libs.apptoolkit.core.ui.components.fields.DatePickerTextField
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.switches.CustomSwitch
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.SortType
import java.util.Date

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
    val selected = remember { mutableStateOf(current) }
    val isDescending = remember { mutableStateOf(descending) }
    var start by remember { mutableStateOf(startDate?.let { Date(it) } ?: Date()) }
    var end by remember { mutableStateOf(endDate?.let { Date(it) } ?: Date()) }

    BasicAlertDialog(
        onDismiss = onDismiss,
        onConfirm = {
            onApply(
                selected.value,
                isDescending.value,
                start.time,
                end.time
            )
            onDismiss()
        },
        icon = Icons.AutoMirrored.Filled.Sort,
        title = stringResource(id = R.string.sort_options),
        confirmButtonText = stringResource(id = R.string.apply),
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

                if (selected.value == SortType.DATE) {
                    SmallVerticalSpacer()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(id = R.string.from_date))
                            SmallVerticalSpacer()
                            DatePickerTextField(date = start) { start = it }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(id = R.string.to_date))
                            SmallVerticalSpacer()
                            DatePickerTextField(date = end) { end = it }
                        }
                    }
                }

                LargeVerticalSpacer()

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
                    Text(text = stringResource(id = R.string.descending), modifier = Modifier.weight(1f))
                    CustomSwitch(
                        checked = isDescending.value,
                        onCheckedChange = { isDescending.value = it }
                    )
                }
            }
        }
    )
}