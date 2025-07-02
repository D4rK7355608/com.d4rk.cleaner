package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import coil3.video.videoFramePercent
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
private fun CategoryRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, files: List<File>) {
    val preview = files.take(3)
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null)
        Row(
            modifier = Modifier
                .padding(start = SizeConstants.MediumSize)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize),
            verticalAlignment = Alignment.CenterVertically
        ) {
            preview.forEach { file ->
                val ext = file.extension.lowercase()
                if (icon == Icons.Outlined.Image || icon == Icons.Outlined.Videocam) {
                    val request = remember(file) {
                        if (icon == Icons.Outlined.Videocam) {
                            ImageRequest.Builder(context)
                                .data(file)
                                .decoderFactory { result, options, _ ->
                                    VideoFrameDecoder(result.source, options)
                                }
                                .videoFramePercent(0.5)
                                .build()
                        } else {
                            ImageRequest.Builder(context).data(file).build()
                        }
                    }
                    AsyncImage(
                        model = request,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    if (ext == "pdf") {
                        Icon(
                            imageVector = Icons.Outlined.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        AssistChip(onClick = {}, label = { Text(file.name) }, enabled = false)
                    }
                }
            }
            if (files.size > preview.size) {
                Text(
                    text = stringResource(id = R.string.apk_card_more_format, files.size - preview.size),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
