package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoSizeSelectLarge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize),
            verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.Image, contentDescription = null)
                Column(modifier = Modifier.padding(start = SizeConstants.MediumSize)) {
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
            }

            Row(horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize)) {
                Icon(imageVector = Icons.Outlined.Image, contentDescription = null)
                Icon(imageVector = Icons.Outlined.PhotoSizeSelectLarge, contentDescription = null)
                Icon(imageVector = Icons.Outlined.HighQuality, contentDescription = null)
            }

            lastOptimized?.let { size ->
                Text(
                    text = stringResource(id = R.string.image_optimizer_last_format, size),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize),
            ) {
                FilledTonalButton(onClick = onOptimizeClick, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Outlined.Image, contentDescription = null)
                    ButtonIconSpacer()
                    Text(text = stringResource(id = R.string.optimize_image))
                }
                FilledTonalButton(onClick = onInfoClick, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                    ButtonIconSpacer()
                    Text(text = stringResource(id = R.string.learn_more))
                }
            }

            Text(
                text = stringResource(id = R.string.image_optimizer_card_footer),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
