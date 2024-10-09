package com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.imageoptimizer.ImageOptimizerState
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.ImageOptimizerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ManualModeScreen(viewModel: ImageOptimizerViewModel) {
    val state: State<ImageOptimizerState> = viewModel.uiState.collectAsState()
    var widthText: String by remember { mutableStateOf(state.value.manualWidth.toString()) }
    var heightText: String by remember { mutableStateOf(state.value.manualHeight.toString()) }
    var qualityValue: Float by remember { mutableFloatStateOf(state.value.manualQuality.toFloat()) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = widthText,
                onValueChange = { newValue ->
                    widthText = newValue
                    coroutineScope.launch {
                        viewModel.setManualCompressSettings(
                            width = newValue.toIntOrNull() ?: 0,
                            height = heightText.toIntOrNull() ?: 0,
                            qualityValue.toInt()
                        )
                    }
                },
                label = { Text(text = stringResource(id = R.string.width)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            OutlinedTextField(
                value = heightText,
                onValueChange = { newValue ->
                    heightText = newValue
                    coroutineScope.launch {
                        viewModel.setManualCompressSettings(
                            width = widthText.toIntOrNull() ?: 0,
                            height = newValue.toIntOrNull() ?: 0,
                            qualityValue.toInt()
                        )
                    }
                },
                label = { Text(stringResource(id = R.string.height)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.quality), style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    R.string.image_compressor_percentage_format, qualityValue.toInt()
                )
            )
            Slider(
                value = qualityValue,
                onValueChange = { newValue ->
                    coroutineScope.launch {
                        qualityValue = newValue
                        viewModel.setManualCompressSettings(
                            width = widthText.toIntOrNull() ?: 0,
                            height = heightText.toIntOrNull() ?: 0,
                            newValue.toInt()
                        )
                    }
                },
            )
        }
    }
}