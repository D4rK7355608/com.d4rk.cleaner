package com.d4rk.cleaner.app.clean.memory.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.SnippetFolder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter.format as formatSize

@Composable
fun StorageBreakdownItem(
    icon: String,
    size: Long,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val storageIcons : Map<String , ImageVector> = mapOf(
        stringResource(id = R.string.installed_apps) to Icons.Outlined.Apps ,
        stringResource(id = R.string.system) to Icons.Outlined.Android ,
        stringResource(id = R.string.music) to Icons.Outlined.MusicNote ,
        stringResource(id = R.string.images) to Icons.Outlined.Image ,
        stringResource(id = R.string.documents) to Icons.Outlined.FolderOpen ,
        stringResource(id = R.string.downloads) to Icons.Outlined.Download ,
        stringResource(id = R.string.other_files) to Icons.Outlined.FolderOpen ,
    )
    Card(
        modifier = modifier
            .padding(all = SizeConstants.ExtraSmallSize)
            .animateContentSize()
            .bounceClick()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(all = SizeConstants.LargeSize) , verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(size = 48.dp) ,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) ,
            ) {
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
                    Icon(
                        modifier = Modifier.bounceClick() ,
                        imageVector = storageIcons[icon] ?: Icons.Outlined.SnippetFolder , contentDescription = icon , tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = SizeConstants.ExtraSmallSize))

            Column {
                Text(
                    text = icon ,
                    style = MaterialTheme.typography.bodyMedium ,
                    fontWeight = FontWeight.Bold ,
                    modifier = Modifier.basicMarquee() ,
                )
                Text(text = formatSize(size) , style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}