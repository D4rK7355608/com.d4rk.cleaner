package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.IconButtonWithText
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.OutlinedIconButtonWithText
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.cleaner.R

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
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        OutlinedIconButtonWithText(
            modifier = Modifier.weight(1f),
            onClick = onStartButtonClick,
            enabled = enabled,
            icon = onStartButtonIcon,
            iconContentDescription = stringResource(id = R.string.move_to_trash_icon_description),
            label = stringResource(id = onStartButtonText)
        )

        SmallHorizontalSpacer()

        IconButtonWithText(
            modifier = Modifier.weight(1f),
            onClick = onEndButtonClick,
            enabled = enabled,
            icon = onEndButtonIcon,
            iconContentDescription = stringResource(id = R.string.delete_forever_icon_description),
            label = stringResource(id = onEndButtonText)
        )
    }
}