package com.d4rk.cleaner.ui.screens.analyze.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.screens.home.HomeViewModel

/**
 * Composable function for selecting or deselecting all items.
 *
 * This composable displays a filter chip labeled "Select All". When tapped, it toggles the
 * selection state and invokes the `onCheckedChange` callback.
 *
 * @param checked A boolean value indicating whether all items are currently selected.
 * @param onCheckedChange A callback function that is invoked when the user taps the chip to change the selection state.
 */
@Composable
fun SelectAllComposable(
    viewModel : HomeViewModel ,
    view : View ,
) {
    val uiState : UiHomeModel by viewModel.uiState.collectAsState()

    Row(
        modifier = Modifier
                .fillMaxWidth()
                .animateContentSize() , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.End
    ) {
        val interactionSource : MutableInteractionSource = remember { MutableInteractionSource() }
        FilterChip(
            modifier = Modifier.bounceClick() ,
            selected = uiState.analyzeState.areAllFilesSelected ,
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                viewModel.toggleSelectAllFiles()
            } ,
            label = { Text(text = stringResource(id = R.string.select_all)) } ,
            leadingIcon = {
                AnimatedContent(
                    targetState = uiState.analyzeState.areAllFilesSelected , label = "Checkmark Animation"
                ) { targetChecked ->
                    if (targetChecked) {
                        Icon(
                            imageVector = Icons.Filled.Check , contentDescription = null , modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } ,
            interactionSource = interactionSource ,
        )
    }
}