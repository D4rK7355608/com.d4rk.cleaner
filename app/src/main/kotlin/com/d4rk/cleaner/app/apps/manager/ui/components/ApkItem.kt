package com.d4rk.cleaner.app.apps.manager.ui.components

import android.content.Context
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
import androidx.compose.material.icons.outlined.InstallMobile
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.domain.actions.AppManagerEvent
import com.d4rk.cleaner.app.apps.manager.domain.data.model.AppManagerItem
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import java.io.File

@Composable
fun ApkItem(apkPath : String , viewModel : AppManagerViewModel , modifier : Modifier) {
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val apkFile = File(apkPath)
    var showMenu : Boolean by remember { mutableStateOf(value = false) }

    val model = remember(apkPath) {
        context.packageManager.getPackageArchiveInfo(apkPath , 0)?.applicationInfo?.loadIcon(context.packageManager)
    }

    OutlinedCard(modifier = modifier) {

        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(SizeConstants.LargeSize)
                    .clip(RoundedCornerShape(SizeConstants.LargeSize)) , verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = model , contentDescription = null , modifier = Modifier.size(48.dp) , contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                        .padding(SizeConstants.LargeSize)
                        .weight(1f)
            ) {
                Text(
                    text = apkFile.name ,
                    style = MaterialTheme.typography.titleMedium ,
                )
                Text(
                    text = "%.2f MB".format(apkFile.length() / 1024 / 1024.toFloat()) , style = MaterialTheme.typography.bodyMedium
                )
            }

            Box {
                IconButton(modifier = Modifier.bounceClick() , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    showMenu = true
                }) {
                    Icon(modifier = Modifier.size(SizeConstants.ButtonIconSize), imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                }

                DropdownMenu(expanded = showMenu , onDismissRequest = {
                    showMenu = false
                }) {
                    DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.share)) } , leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Share , contentDescription = null)
                    } , onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        viewModel.onEvent(AppManagerEvent.ShareItem(AppManagerItem.ApkFile(apkPath)))
                    })

                    DropdownMenuItem(modifier = Modifier.bounceClick() , text = { Text(stringResource(id = R.string.install)) } , leadingIcon = {
                        Icon(imageVector = Icons.Outlined.InstallMobile , contentDescription = null)
                    } , onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        viewModel.installApk(apkPath)
                    })
                }
            }
        }
    }
}