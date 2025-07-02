package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
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
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.WhatsAppMediaSummary
import java.io.File

@Composable
fun WhatsAppCleanerCard(
    mediaSummary: WhatsAppMediaSummary,
    modifier: Modifier = Modifier,
    onCleanClick: () -> Unit
) {
    if (!mediaSummary.hasData) return

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(SizeConstants.LargeSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize),
            verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.Chat , contentDescription = null)
                Column(modifier = Modifier.padding(start = SizeConstants.MediumSize)) {
                    Text(
                        text = stringResource(id = R.string.whatsapp_card_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    SmallVerticalSpacer()
                    Text(
                        text = stringResource(id = R.string.whatsapp_card_subtitle),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            AnimatedVisibility(visible = mediaSummary.images.isNotEmpty()) {
                CategoryRow(
                    icon = Icons.Outlined.Image,
                    label = stringResource(id = R.string.images),
                    files = mediaSummary.images
                )
            }
            AnimatedVisibility(visible = mediaSummary.videos.isNotEmpty()) {
                CategoryRow(
                    icon = Icons.Outlined.Videocam,
                    label = stringResource(id = R.string.videos),
                    files = mediaSummary.videos
                )
            }
            AnimatedVisibility(visible = mediaSummary.documents.isNotEmpty()) {
                CategoryRow(
                    icon = Icons.Outlined.Description,
                    label = stringResource(id = R.string.documents),
                    files = mediaSummary.documents
                )
            }

            FilledTonalButton(
                onClick = onCleanClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.filledTonalButtonColors()
            ) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                ButtonIconSpacer()
                Text(text = stringResource(id = R.string.clean_whatsapp))
            }
        }
    }
}

@Composable
private fun CategoryRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    files: List<File>
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val maxSlots = remember(configuration.screenWidthDp) {
        val screenPx = with(density) { configuration.screenWidthDp.dp.toPx() }
        val slotPx = with(density) { (64.dp + SizeConstants.SmallSize).toPx() }
        val raw = (screenPx / slotPx).toInt().coerceAtLeast(1).coerceAtMost(5)
        raw
    }
    val previewCount = if (files.size > maxSlots) maxSlots - 1 else minOf(files.size, maxSlots)
    val preview = files.take(previewCount)
    val remaining = files.size - preview.size

    Column(verticalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null)
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = SizeConstants.MediumSize)
            )
        }
        Row(
            modifier = Modifier
                .padding(start = SizeConstants.MediumSize)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize),
            verticalAlignment = Alignment.CenterVertically
        ) {
            preview.forEach { file ->
                FilePreviewCard(
                    file = file,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (remaining > 0) {
                MorePreviewTile(remaining)
            }
        }
    }
}

@Composable
private fun MorePreviewTile(count: Int) {
    Card {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.apk_card_more_format, count),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
