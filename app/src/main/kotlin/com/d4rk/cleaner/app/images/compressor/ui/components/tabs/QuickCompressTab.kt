package com.d4rk.cleaner.app.images.compressor.ui.components.tabs

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
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.app.images.compressor.domain.data.model.CompressionLevel
import com.d4rk.cleaner.app.images.compressor.ui.ImageOptimizerViewModel
import com.d4rk.cleaner.app.images.utils.getCompressionLevelFromSliderValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun QuickCompressTab(viewModel : ImageOptimizerViewModel) {
    var sliderValue : Float by remember { mutableFloatStateOf(value = 50f) }
    val selectedCompression : CompressionLevel = getCompressionLevelFromSliderValue(sliderValue = sliderValue)
    val coroutineScope : CoroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(all = SizeConstants.LargeSize)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (compressionLevel in CompressionLevel.entries) {
                OutlinedButton(onClick = {
                    coroutineScope.launch {
                        sliderValue = compressionLevel.defaultPercentage.toFloat()
                        viewModel.setQuickCompressValue(sliderValue.toInt())
                    }
                } , modifier = Modifier.weight(weight = 1f) , border = BorderStroke(
                    width = 1.dp , color = if (selectedCompression == compressionLevel) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                ) , colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (selectedCompression == compressionLevel) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )) {
                    Text(text = stringResource(compressionLevel.stringRes))
                }
                SmallHorizontalSpacer()
            }
        }

        LargeVerticalSpacer()

        Slider(value = sliderValue , onValueChange = { newValue -> sliderValue = newValue } , onValueChangeFinished = {
            coroutineScope.launch {
                viewModel.setQuickCompressValue(value = sliderValue.toInt())
            }
        } , valueRange = 0f..100f , steps = 99)
    }
}