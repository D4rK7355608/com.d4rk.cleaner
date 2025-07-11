package com.d4rk.cleaner.app.images.compressor.ui.components.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ExtraSmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.images.compressor.domain.data.model.ui.UiImageOptimizerState
import com.d4rk.cleaner.app.images.compressor.ui.ImageOptimizerViewModel
import kotlinx.coroutines.delay

@Composable
fun ManualModeTab(viewModel : ImageOptimizerViewModel) {
    val state : State<UiImageOptimizerState> = viewModel.uiState.collectAsState()
    val focusManager : FocusManager = LocalFocusManager.current

    val defaultWidth : Int = if (state.value.manualWidth != 0) state.value.manualWidth else 640
    val defaultHeight : Int = if (state.value.manualHeight != 0) state.value.manualHeight else 480

    var widthText : String by remember { mutableStateOf(value = defaultWidth.toString()) }
    var heightText : String by remember { mutableStateOf(value = defaultHeight.toString()) }
    var qualityValue : Float by remember { mutableFloatStateOf(value = state.value.manualQuality.toFloat()) }

    var widthFocused : Boolean by remember { mutableStateOf(value = false) }
    var heightFocused : Boolean by remember { mutableStateOf(value = false) }

    val aspectRatio : Float = defaultWidth.toFloat() / defaultHeight.toFloat()

    LaunchedEffect(key1 = widthText , key2 = heightText) {
        delay(timeMillis = 400L)
        if (! widthFocused && ! heightFocused) {
            viewModel.setManualCompressSettings(
                width = widthText.toIntOrNull() ?: defaultWidth , height = heightText.toIntOrNull() ?: defaultHeight , quality = qualityValue.toInt()
            )
        }
    }

    Column(modifier = Modifier.padding(all = SizeConstants.LargeSize)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = widthText , onValueChange = { newValue : String ->
                widthText = newValue
                val newWidth : Int? = newValue.toIntOrNull()
                if (newWidth != null && defaultWidth != 0) {
                    val newHeight : Int = (newWidth / aspectRatio).toInt()
                    heightText = newHeight.toString()
                }
            } , label = { Text(text = stringResource(id = R.string.width)) } , singleLine = true , keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number , imeAction = ImeAction.Done) , keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                viewModel.setManualCompressSettings(
                    width = widthText.toIntOrNull() ?: defaultWidth , height = heightText.toIntOrNull() ?: defaultHeight , quality = qualityValue.toInt()
                )
            }) , modifier = Modifier
                    .weight(weight = 1f)
                    .padding(end = SizeConstants.SmallSize)
                    .onFocusChanged { focusState -> widthFocused = focusState.isFocused })
            OutlinedTextField(value = heightText , onValueChange = { newValue : String ->
                heightText = newValue
                val newHeight : Int? = newValue.toIntOrNull()
                if (newHeight != null && defaultHeight != 0) {
                    val newWidth : Int = (newHeight * aspectRatio).toInt()
                    widthText = newWidth.toString()
                }
            } , label = { Text(text = stringResource(id = R.string.height)) } , singleLine = true , keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number , imeAction = ImeAction.Done) , keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                viewModel.setManualCompressSettings(
                    width = widthText.toIntOrNull() ?: defaultWidth , height = heightText.toIntOrNull() ?: defaultHeight , quality = qualityValue.toInt()
                )
            }) , modifier = Modifier
                    .weight(weight = 1f)
                    .onFocusChanged { focusState -> heightFocused = focusState.isFocused })
        }
        SmallVerticalSpacer()
        Text(
            text = stringResource(id = R.string.quality) , style = MaterialTheme.typography.bodyLarge
        )
        ExtraSmallVerticalSpacer()
        Row(
            modifier = Modifier.fillMaxWidth() , verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${qualityValue.toInt()}%")
            Slider(value = qualityValue , onValueChange = { newValue : Float -> qualityValue = newValue } , onValueChangeFinished = {
                viewModel.setManualCompressSettings(
                    width = widthText.toIntOrNull() ?: defaultWidth , height = heightText.toIntOrNull() ?: defaultHeight , quality = qualityValue.toInt()
                )
            } , valueRange = 0f..100f , steps = 99 , modifier = Modifier.weight(1f))
        }
    }
}