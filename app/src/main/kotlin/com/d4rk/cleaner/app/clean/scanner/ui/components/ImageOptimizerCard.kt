package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoSizeSelectLarge
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R

@Composable
fun ImageOptimizerCard(
    modifier: Modifier = Modifier,
    lastOptimized: String? = null,
    onOptimizeClick: () -> Unit,
    onInfoClick: () -> Unit = {},
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth() ,
        border = BorderStroke(width = 1.dp , color = MaterialTheme.colorScheme.outline) ,
        shape = RoundedCornerShape(SizeConstants.ExtraLargeSize) ,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize),
            verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.image_optimizer_card_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    SmallVerticalSpacer()
                    Text(
                        text = stringResource(id = R.string.image_optimizer_card_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                IconButton(onClick = onInfoClick) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.PhotoSizeSelectLarge,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            AnimatedVisibility(visible = lastOptimized != null) {
                lastOptimized?.let { size ->
                    Text(
                        text = stringResource(id = R.string.image_optimizer_last_format, size),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.animateContentSize()
                    )
                }
            }

            FilledTonalButton(
                onClick = onOptimizeClick,
                modifier = Modifier.align(Alignment.End).bounceClick()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                ButtonIconSpacer()
                Text(text = stringResource(id = R.string.optimize_image))
            }
        }
    }
}
