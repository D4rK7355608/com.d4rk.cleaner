package com.d4rk.cleaner.app.apps.manager.ui.components

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.SoundEffectConstants
import android.view.View
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R.string
import com.d4rk.cleaner.app.apps.manager.domain.actions.AppManagerEvent
import com.d4rk.cleaner.app.apps.manager.domain.data.model.AppManagerItem
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import java.io.File

@Composable
fun AppItemComposable(
    app: ApplicationInfo, viewModel: AppManagerViewModel, modifier: Modifier
) {
    val context: Context = LocalContext.current

    @OptIn(ExperimentalMaterial3Api::class) val view: View = LocalView.current
    val packageManager: PackageManager = context.packageManager
    val appName: String = app.loadLabel(packageManager).toString()
    val apkPath: String = app.publicSourceDir
    val apkFile = File(apkPath)
    val sizeInBytes: Long = apkFile.length()
    val sizeInKB: Long = sizeInBytes / 1024
    val sizeInMB: Long = sizeInKB / 1024
    val appSize: String = "%.2f MB".format(sizeInMB.toFloat())
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
                Text(
                    text = appSize, style = MaterialTheme.typography.bodyMedium
                )
            }

            Box {
                IconButton(modifier = Modifier.bounceClick(), onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    showMenu = true
                }) {
                    Icon(modifier = Modifier.size(SizeConstants.ButtonIconSize), imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = {
                    showMenu = false
                }) {
                    DropdownMenuItem(leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.DeleteForever,
                            contentDescription = null
                        )
                    }, modifier = Modifier.bounceClick(), text = {
                        Text(text = stringResource(id = string.uninstall))
                    }, onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        viewModel.uninstallApp(app.packageName)
                    })
                    DropdownMenuItem(leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null
                        )
                    }, modifier = Modifier.bounceClick(), text = {
                        Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.share))
                    }, onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        viewModel.onEvent(AppManagerEvent.ShareItem(AppManagerItem.InstalledApp(app.packageName)))
                    })
                    DropdownMenuItem(leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null
                        )
                    }, modifier = Modifier.bounceClick(), text = {
                        Text(text = stringResource(id = string.app_info))
                    }, onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        viewModel.openAppInfo(app.packageName)
                    })
                }
            }
        }
    }
}