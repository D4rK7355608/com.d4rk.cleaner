package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ApkInfo
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
fun ApkCleanerCard(
    apkFiles: List<ApkInfo>,
    modifier: Modifier = Modifier,
    onCleanClick: () -> Unit
) {
    val preview = apkFiles.take(3)
    val context: Context = LocalContext.current

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
                Icon(imageVector = Icons.Outlined.Android, contentDescription = null)
                Column(modifier = Modifier.padding(start = SizeConstants.MediumSize)) {
                    Text(
                        text = stringResource(id = R.string.apk_card_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    SmallVerticalSpacer()
                    Text(
                        text = stringResource(id = R.string.apk_card_subtitle),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            SmallVerticalSpacer()

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize),
                verticalAlignment = Alignment.CenterVertically
            ) {
                preview.forEach { apk ->
                    val appInfo = remember(apk.path) {
                        context.packageManager.getPackageArchiveInfo(apk.path, 0)?.applicationInfo?.apply {
                            sourceDir = apk.path
                            publicSourceDir = apk.path
                        }
                    }
                    val appName = appInfo?.loadLabel(context.packageManager)?.toString() ?: File(apk.path).name
                    val icon = appInfo?.loadIcon(context.packageManager)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = SizeConstants.ExtraSmallSize)
                        )
                    }
                }
                if (apkFiles.size > preview.size) {
                    Text(
                        text = stringResource(id = R.string.apk_card_more_format, apkFiles.size - preview.size),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            FilledTonalButton(
                onClick = onCleanClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.filledTonalButtonColors()
            ) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                ButtonIconSpacer()
                Text(text = stringResource(id = R.string.clean_apks))
            }
        }
    }
}
