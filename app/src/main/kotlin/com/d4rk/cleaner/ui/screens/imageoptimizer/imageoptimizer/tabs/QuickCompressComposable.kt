package com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.cleaner.core.data.model.ui.imageoptimizer.CompressionLevel
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.ImageOptimizerViewModel
import com.d4rk.cleaner.utils.imageoptimizer.getCompressionLevelFromSliderValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun QuickCompressScreen(viewModel: ImageOptimizerViewModel) {
    var sliderValue: Float by remember { mutableFloatStateOf(50f) }
    val selectedCompression: com.d4rk.cleaner.core.data.model.ui.imageoptimizer.CompressionLevel = getCompressionLevelFromSliderValue(sliderValue)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (compressionLevel: com.d4rk.cleaner.core.data.model.ui.imageoptimizer.CompressionLevel in com.d4rk.cleaner.core.data.model.ui.imageoptimizer.CompressionLevel.entries) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            sliderValue = compressionLevel.defaultPercentage.toFloat()
                            viewModel.setQuickCompressValue(sliderValue.toInt())
                        }
                    }, modifier = Modifier.weight(1f), border = BorderStroke(
                        width = 1.dp,
                        color = if (selectedCompression == compressionLevel) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    ), colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (selectedCompression == compressionLevel) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(text = stringResource(compressionLevel.stringRes))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        LargeVerticalSpacer()

        Slider(value = sliderValue, onValueChange = { newValue ->
            coroutineScope.launch {
                sliderValue = newValue
                viewModel.setQuickCompressValue(newValue.toInt())
            }
        }, valueRange = 0f..100f, steps = 99)
    }
}