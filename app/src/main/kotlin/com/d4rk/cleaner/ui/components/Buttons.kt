package com.d4rk.cleaner.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.ui.components.animations.bounceClick

@Composable
fun TwoRowButtons(
    modifier: Modifier,
    enabled: Boolean,
    onStartButtonClick: () -> Unit,
    onStartButtonIcon: ImageVector,
    onStartButtonText: Int,
    onEndButtonClick: () -> Unit,
    onEndButtonIcon: ImageVector,
    onEndButtonText: Int,
    view: View
) {

    Row(
        modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
    ) {
        OutlinedButton(
            enabled = enabled,
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onStartButtonClick()
            },
            modifier = Modifier
                .weight(1f)
                .bounceClick(),
        ) {
            Icon(
                imageVector = onStartButtonIcon,
                contentDescription = "Move to trash",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = onStartButtonText))
        }

        Spacer(Modifier.width(8.dp))

        Button(
            enabled = enabled,
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onEndButtonClick()
            },
            modifier = Modifier
                .weight(1f)
                .bounceClick(),
        ) {
            Icon(
                imageVector = onEndButtonIcon,
                contentDescription = "Delete forever",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = onEndButtonText))
        }
    }
}