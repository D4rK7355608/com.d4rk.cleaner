package com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer.tabs

import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.imageoptimizer.ImageOptimizerState
import com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer.ImageOptimizerViewModel
import com.d4rk.cleaner.utils.haptic.weakHapticFeedback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileSizeScreen(viewModel: ImageOptimizerViewModel) {
    val view: View = LocalView.current
    val state: State<ImageOptimizerState> = viewModel.uiState.collectAsState()
    var fileSizeText: String by remember { mutableStateOf(state.value.fileSizeKB.toString()) }
    var expanded: Boolean by remember { mutableStateOf(value = false) }
    val presetSizes: List<String> = stringArrayResource(R.array.file_sizes).toList()
    var selectedPresetSize: String by remember { mutableStateOf(value = "") }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = fileSizeText,
                onValueChange = { newValue ->
                    fileSizeText = newValue
                    coroutineScope.launch {
                        viewModel.setFileSize(newValue.toIntOrNull() ?: 0)
                    }
                },
                label = { Text(stringResource(R.string.file_size)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    Text(text = stringResource(R.string.enter_a_value))
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                isError = fileSizeText.isNotEmpty() && fileSizeText.toFloatOrNull() == null,
                modifier = Modifier
                    .menuAnchor() // FIXME: 'menuAnchor(): Modifier' is deprecated. Use overload that takes MenuAnchorType and enabled parameters
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                presetSizes.forEach { size ->
                    DropdownMenuItem(text = { Text(text = "$size KB") }, onClick = {
                        view.weakHapticFeedback()
                        selectedPresetSize = size
                        fileSizeText = size
                        coroutineScope.launch {
                            viewModel.setFileSize(size.toIntOrNull() ?: 0)
                        }
                        expanded = false
                    })
                }
            }
        }
    }
}