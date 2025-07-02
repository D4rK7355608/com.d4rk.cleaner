package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.ButtonDefaults
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
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.WhatsAppMediaSummary
import java.io.File

@Composable
fun WhatsAppCleanerCard(
    mediaSummary: WhatsAppMediaSummary,
    modifier: Modifier = Modifier,
    onCleanClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
    val preview = files.take(9)
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null)
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = SizeConstants.MediumSize)
            )
        }
        Row(
            modifier = Modifier
                .padding(start = SizeConstants.MediumSize)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize),
            verticalAlignment = Alignment.CenterVertically
        ) {
            preview.forEach { file ->
                FilePreviewCard(
                    file = file,
                    modifier = Modifier.size(64.dp)
                )
            }
            if (files.size > preview.size) {
                MorePreviewTile(files.size - preview.size)
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
