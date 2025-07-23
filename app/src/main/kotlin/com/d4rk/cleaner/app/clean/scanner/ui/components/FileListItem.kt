package com.d4rk.cleaner.app.clean.scanner.ui.components

import android.content.Context
import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.utils.helpers.getFileIcon
import com.google.common.io.Files.getFileExtension
import java.io.File

@Composable
fun FileListItem(file: File, modifier: Modifier = Modifier) {
    val context: Context = LocalContext.current
    val fileExtension = remember(file.name) { getFileExtension(file.name) }
    val size = remember(file.length()) { Formatter.formatShortFileSize(context, file.length()) }
    val audioExtensions = remember { context.resources.getStringArray(R.array.audio_extensions).toList() }
    val apkExtensions = remember { context.resources.getStringArray(R.array.apk_extensions).toList() }
    val isAudio = remember(fileExtension) { audioExtensions.any { it.equals(fileExtension, ignoreCase = true) } }
    val isApk = remember(fileExtension) { apkExtensions.any { it.equals(fileExtension, ignoreCase = true) } }
    val fileIcon = if (isAudio) R.drawable.ic_audio_file else remember(fileExtension) { getFileIcon(fileExtension, context) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(SizeConstants.SmallSize),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isApk) {
            val icon = remember(file.path) {
                context.packageManager.getPackageArchiveInfo(file.path, 0)?.applicationInfo?.apply {
                    sourceDir = file.path
                    publicSourceDir = file.path
                }?.loadIcon(context.packageManager)
            }
            if (icon != null) {
                AsyncImage(model = icon, contentDescription = null, modifier = Modifier.size(40.dp))
            } else {
                Icon(
                    painter = painterResource(id = fileIcon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        } else {
            Icon(
                painter = painterResource(id = fileIcon),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
        Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = size, style = MaterialTheme.typography.bodySmall)
        }
    }
}
