package com.d4rk.cleaner.app.clean.analyze.ui.components

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder
import coil3.video.videoFramePercent
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.utils.helpers.getFileIcon
import com.d4rk.cleaner.app.clean.scanner.utils.helpers.loadPdfThumbnail
import com.d4rk.cleaner.core.utils.helpers.FileManagerHelper
import com.google.common.io.Files.getFileExtension
import java.io.File

@Composable
fun FileCard(
    modifier: Modifier = Modifier,
    file: File, onCheckedChange: (Boolean) -> Unit,
    isChecked: Boolean,
    isOriginal: Boolean = false,
    view: View,
) {
    val isFolder: Boolean = file.isDirectory
    val context: Context = LocalContext.current
    val fileExtension: String = remember(key1 = file.name) { getFileExtension(file.name) }

    val imageExtensions: List<String> =
        remember { context.resources.getStringArray(R.array.image_extensions).toList() }
    val videoExtensions: List<String> =
        remember { context.resources.getStringArray(R.array.video_extensions).toList() }
    val officeExtensions: List<String> =
        remember { context.resources.getStringArray(R.array.microsoft_office_extensions).toList() }
    val apkExtensions: List<String> =
        remember { context.resources.getStringArray(R.array.apk_extensions).toList() }

    Card(
        modifier = modifier
            .aspectRatio(ratio = 1f)
            .bounceClick()
            .clickable {
                if (!file.isDirectory) {
                    FileManagerHelper.openFile(context, file)
                }
            },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isFolder) {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = 24.dp)
                        .align(alignment = Alignment.Center)
                )
            } else {
                when (fileExtension) {
                    in imageExtensions -> {
                        AsyncImage(
                            model = remember(key1 = file) {
                                ImageRequest.Builder(context = context).data(data = file)
                                    .size(size = 64).crossfade(enable = true).build()
                            },
                            contentScale = ContentScale.FillWidth,
                            contentDescription = file.name,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    in videoExtensions -> {
                        AsyncImage(model = remember(file) {
                            ImageRequest.Builder(context = context).data(data = file)
                                .decoderFactory { result, options, _ ->
                                    VideoFrameDecoder(source = result.source, options = options)
                                }.videoFramePercent(framePercent = 0.5).crossfade(enable = true)
                                .build()
                        },
                            contentDescription = file.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize())
                    }

                    in apkExtensions -> {
                        val icon = remember(file.path) {
                            context.packageManager.getPackageArchiveInfo(
                                file.path,
                                0
                            )?.applicationInfo?.apply {
                                sourceDir = file.path
                                publicSourceDir = file.path
                            }?.loadIcon(context.packageManager)
                        }
                        if (icon != null) {
                            AsyncImage(
                                model = icon,
                                contentDescription = file.name,
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_apk_document),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center)
                            )
                        }
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
                                    contentDescription = null, modifier = Modifier
                                        .size(size = 24.dp)
                                        .align(alignment = Alignment.Center)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null, modifier = Modifier
                                    .size(size = 24.dp)
                                    .align(alignment = Alignment.Center)
                            )
                        }
                    }

                    else -> {
                        val fileIcon: Int = remember(key1 = fileExtension) {
                            getFileIcon(
                                extension = fileExtension, context = context
                            )
                        }
                        Icon(
                            painter = painterResource(id = fileIcon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(size = 24.dp)
                                .align(alignment = Alignment.Center)
                        )
                    }
                }
            }

            Checkbox(checked = isChecked, onCheckedChange = { checked ->
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onCheckedChange(checked)
            }, modifier = Modifier.align(alignment = Alignment.TopEnd))

            if (isOriginal) {
                Text(
                    text = stringResource(id = R.string.original),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color.Red.copy(alpha = 0.7f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Black.copy(alpha = 0.4f)
                    )
                    .align(alignment = Alignment.BottomCenter)
            ) {
                Text(
                    text = file.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .basicMarquee()
                        .padding(all = SizeConstants.SmallSize)
                )
            }
        }
    }
}