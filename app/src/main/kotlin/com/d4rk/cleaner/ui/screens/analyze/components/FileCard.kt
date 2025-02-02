package com.d4rk.cleaner.ui.screens.analyze.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.SoundEffectConstants
import android.view.View
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
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder
import coil3.video.videoFramePercent
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.cleaning.getFileIcon
import com.google.common.io.Files.getFileExtension
import java.io.File

@Composable
fun FileCard(
    file : File , imageLoader : ImageLoader , onCheckedChange : (Boolean) -> Unit ,
    isChecked : Boolean ,
    view : View ,
    modifier : Modifier = Modifier ,
) {
    val isFolder : Boolean = file.isDirectory
    val context : Context = LocalContext.current
    val fileExtension : String = remember(key1 = file.name) { getFileExtension(file.name) }

    val imageExtensions : List<String> = remember { context.resources.getStringArray(R.array.image_extensions).toList() }
    val videoExtensions : List<String> = remember { context.resources.getStringArray(R.array.video_extensions).toList() }

    Card(
        modifier = modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1f)
                .bounceClick()
                .clickable {
                    if (! file.isDirectory) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val uri : Uri = FileProvider.getUriForFile(
                            context , "${context.packageName}.fileprovider" , file
                        )
                        intent.setDataAndType(uri , context.contentResolver.getType(uri))
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.startActivity(intent)
                    }
                } ,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isFolder) {
                Icon(
                    imageVector = Icons.Outlined.Folder , contentDescription = null , modifier = Modifier
                            .size(size = 24.dp)
                            .align(alignment = Alignment.Center)
                )
            }
            else {
                when (fileExtension) {
                    in imageExtensions -> {
                        AsyncImage(
                            model = remember(key1 = file) {
                                ImageRequest.Builder(context = context).data(data = file).size(size = 64).crossfade(enable = true).build()
                            } ,
                            contentScale = ContentScale.FillWidth ,
                            contentDescription = file.name ,
                            modifier = Modifier.fillMaxWidth() ,
                            imageLoader = imageLoader ,
                        )
                    }

                    in videoExtensions -> {
                        AsyncImage(model = remember(file) {
                            ImageRequest.Builder(context = context).data(data = file).decoderFactory { result , options , _ ->
                                VideoFrameDecoder(source = result.source , options = options)
                            }.videoFramePercent(framePercent = 0.5).crossfade(enable = true).build()
                        } , imageLoader = imageLoader , contentDescription = file.name , contentScale = ContentScale.Crop , modifier = Modifier.fillMaxSize())
                    }

                    else -> {
                        val fileIcon : Int = remember(key1 = fileExtension) {
                            getFileIcon(
                                extension = fileExtension , context = context
                            )
                        }
                        Icon(
                            painter = painterResource(id = fileIcon) , contentDescription = null , modifier = Modifier
                                    .size(size = 24.dp)
                                    .align(alignment = Alignment.Center)
                        )
                    }
                }
            }

            Checkbox(checked = isChecked , onCheckedChange = { checked ->
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onCheckedChange(checked)
            } , modifier = Modifier.align(alignment = Alignment.TopEnd))

            Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.Black.copy(alpha = 0.4f)
                        )
                        .align(alignment = Alignment.BottomCenter)
            ) {
                Text(
                    text = file.name , maxLines = 1 , overflow = TextOverflow.Ellipsis , modifier = Modifier
                            .basicMarquee()
                            .padding(all = 8.dp)
                )
            }
        }
    }
}