package com.d4rk.cleaner.app.clean.home.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder
import coil3.video.videoFramePercent
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.home.utils.helpers.getFileIcon
import com.d4rk.cleaner.app.clean.home.utils.helpers.loadPdfThumbnail
import com.google.common.io.Files.getFileExtension
import java.io.File

@Composable
fun FilePreviewCard(file: File, modifier: Modifier = Modifier) {
    val context: Context = LocalContext.current
    val fileExtension: String = remember(file.name) { getFileExtension(file.name) }
    val imageExtensions = remember { context.resources.getStringArray(R.array.image_extensions).toList() }
    val videoExtensions = remember { context.resources.getStringArray(R.array.video_extensions).toList() }
    val officeExtensions = remember { context.resources.getStringArray(R.array.microsoft_office_extensions).toList() }
    val imageLoader = LocalContext.current.imageLoader

    Card(modifier = modifier) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)) {
            if (file.isDirectory) {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            } else {
                when (fileExtension.lowercase()) {
                    in imageExtensions -> {
                        AsyncImage(
                            model = remember(file) {
                                ImageRequest.Builder(context).data(file).size(64).crossfade(true).build()
                            },
                            imageLoader = imageLoader,
                            contentDescription = file.name,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    in videoExtensions -> {
                        AsyncImage(
                            model = remember(file) {
                                ImageRequest.Builder(context).data(file).decoderFactory { result, options, _ ->
                                    VideoFrameDecoder(result.source, options)
                                }.videoFramePercent(0.5).crossfade(true).build()
                            },
                            contentDescription = file.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    in officeExtensions -> {
                        if (fileExtension.lowercase() == "pdf") {
                            val pdfBitmap = remember(file) { loadPdfThumbnail(file) }
                            if (pdfBitmap != null) {
                                Image(
                                    bitmap = pdfBitmap.asImageBitmap(),
                                    contentDescription = file.name,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.PictureAsPdf,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    else -> {
                        val fileIcon = remember(fileExtension) { getFileIcon(fileExtension, context) }
                        Icon(
                            painter = painterResource(id = fileIcon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = file.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(all = SizeConstants.SmallSize)
                )
            }
        }
    }
}
