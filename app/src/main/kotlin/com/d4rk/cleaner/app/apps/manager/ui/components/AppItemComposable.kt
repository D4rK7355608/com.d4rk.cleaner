package com.d4rk.cleaner.app.apps.manager.ui.components

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import com.d4rk.android.libs.apptoolkit.core.ui.components.dropdown.CommonDropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.IconButton
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R.string
import com.d4rk.cleaner.app.apps.manager.domain.actions.AppManagerEvent
import com.d4rk.cleaner.app.apps.manager.domain.data.model.AppManagerItem
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import com.d4rk.cleaner.app.clean.analyze.utils.helpers.TimeHelper
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter
import java.io.File

@Composable
fun AppItemComposable(
    app: ApplicationInfo,
    lastUsed: Long?,
    viewModel: AppManagerViewModel,
    modifier: Modifier,
) {
    val context: Context = LocalContext.current

    val packageManager: PackageManager = context.packageManager
    val appName: String = app.loadLabel(packageManager).toString()
    val apkPath: String = app.publicSourceDir
    val apkFile = File(apkPath)
    val sizeInBytes: Long = apkFile.length()
    val appSize: String = FileSizeFormatter.format(sizeInBytes)
    var showMenu: Boolean by remember { mutableStateOf(value = false) }
    val model: Drawable = app.loadIcon(packageManager)
    OutlinedCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SizeConstants.LargeSize)
                .clip(RoundedCornerShape(SizeConstants.LargeSize)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                    .padding(SizeConstants.LargeSize)
                    .weight(1f)
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Row {
                    Text(
                        text = appSize,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    lastUsed?.takeIf { it > 0 }?.let { used ->
                        Text(
                            text = " • ${TimeHelper.formatDate(context, java.util.Date(used))}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
            Box {
                IconButton(
                    icon = Icons.Outlined.MoreVert,
                    onClick = {
                        showMenu = true
                    })

                DropdownMenu(expanded = showMenu, onDismissRequest = {
                    showMenu = false
                }) {
                    CommonDropdownMenuItem(
                        textResId = string.uninstall,
                        icon = Icons.Outlined.DeleteForever,
                        onClick = {
                            viewModel.uninstallApp(app.packageName)
                        }
                    )
                    CommonDropdownMenuItem(
                        textResId = com.d4rk.android.libs.apptoolkit.R.string.share,
                        icon = Icons.Outlined.Share,
                        onClick = {
                            viewModel.onEvent(
                                AppManagerEvent.ShareItem(
                                    AppManagerItem.InstalledApp(app.packageName)
                                )
                            )
                        }
                    )
                    CommonDropdownMenuItem(
                        textResId = string.app_info,
                        icon = Icons.Outlined.Info,
                        onClick = {
                            viewModel.openAppInfo(app.packageName)
                        }
                    )
                }
            }
        }
    }
}