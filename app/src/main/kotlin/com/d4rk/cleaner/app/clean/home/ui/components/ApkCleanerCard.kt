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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ApkInfo
import java.io.File

@Composable
fun ApkCleanerCard(
    apkFiles: List<ApkInfo>,
    modifier: Modifier = Modifier,
    onCleanClick: () -> Unit
) {
    val preview = apkFiles.take(3)

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

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize),
                verticalAlignment = Alignment.CenterVertically
            ) {
                preview.forEach { apk ->
                    Text(
                        text = File(apk.path).name,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (apkFiles.size > preview.size) {
                    Text(
                        text = stringResource(id = R.string.apk_card_more_format, apkFiles.size - preview.size),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(modifier = Modifier.align(Alignment.End), onClick = onCleanClick) {
                Text(text = stringResource(id = R.string.clean_apks))
            }
        }
    }
}
