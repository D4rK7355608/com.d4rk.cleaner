package com.d4rk.cleaner.app.images.compressor.ui.components.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.images.compressor.domain.data.model.ImageOptimizerState
import com.d4rk.cleaner.app.images.compressor.ui.ImageOptimizerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileSizeTab(viewModel : ImageOptimizerViewModel) {
    val state : State<ImageOptimizerState> = viewModel.uiState.collectAsState()
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    var originalSizeKB : Int by remember { mutableIntStateOf(value = 0) }

    LaunchedEffect(key1 = state.value.selectedImageUri) {
        state.value.selectedImageUri?.let { uri ->
            originalSizeKB = viewModel.getOriginalSizeInKB(uri = uri)
        }
    }

    val presetSizes : List<String> = if (originalSizeKB > 0) {
        viewModel.generateDynamicPresets(originalSizeKB)
    }
    else {
        stringArrayResource(id = R.array.file_sizes).toList()
    }

    var fileSizeText : String = if (state.value.fileSizeKB == 0) {
        stringResource(id = R.string.default_value)
    }
    else {
        state.value.fileSizeKB.toString()
    }

    var expanded : Boolean by remember { mutableStateOf(value = false) }
    var selectedPresetSize : String by remember { mutableStateOf(value = "") }

    Column(modifier = Modifier.padding(all = SizeConstants.LargeSize)) {
        ExposedDropdownMenuBox(expanded = expanded , onExpandedChange = { expanded = ! expanded }) {
            OutlinedTextField(
                value = fileSizeText ,
                              onValueChange = { newValue ->
                                  fileSizeText = newValue
                                  coroutineScope.launch {
                                      viewModel.setFileSize(size = newValue.toIntOrNull() ?: 0)
                                  }
                              } ,
                              label = { Text(text = stringResource(id = R.string.file_size)) } ,
                              singleLine = true ,
                              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) ,
                              supportingText = { Text(text = stringResource(id = R.string.enter_a_value)) } ,
                              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) } ,
                              isError = fileSizeText.isNotEmpty() && fileSizeText.toFloatOrNull() == null ,
                              modifier = Modifier
                                      .menuAnchor(type = MenuAnchorType.PrimaryNotEditable , enabled = true)
                                      .fillMaxWidth()
                                      .padding(top = SizeConstants.MediumSize))
            ExposedDropdownMenu(expanded = expanded , onDismissRequest = { expanded = false }) {
                presetSizes.forEach { size ->
                    DropdownMenuItem(text = { Text(text = "$size KB") } , onClick = {
                        selectedPresetSize = size
                        fileSizeText = size
                        coroutineScope.launch {
                            viewModel.setFileSize(size = size.toIntOrNull() ?: 0)
                        }
                        expanded = false
                    })
                }
            }
        }
    }
}