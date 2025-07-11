package com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ExtraSmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ExtraLargeIncreasedHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R

@Composable
fun CleanerInfoCard(
    freeUpSizeBytes: Long,
    totalSizeBytes: Long,
    filesCount: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formattedFreeUp = remember(freeUpSizeBytes) {
        Formatter.formatFileSize(context, freeUpSizeBytes)
    }
    val progress = if (totalSizeBytes > 0L) {
        freeUpSizeBytes.toFloat() / totalSizeBytes.toFloat()
    } else {
        0f
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SizeConstants.MediumSize),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CleanerProgressIndicator(
                progress = progress,
                icon = painterResource(id = R.drawable.ic_cleaner_notify),
                size = 100.dp
            )

            ExtraLargeIncreasedHorizontalSpacer()

            Column(verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.SdStorage,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.typography.bodyLarge.fontSize.value.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ButtonIconSpacer()
                    Text(
                        text = stringResource(id = R.string.can_be_freed),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = formattedFreeUp,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 42.sp
                )

                ExtraSmallVerticalSpacer()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.typography.bodyMedium.fontSize.value.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ButtonIconSpacer()
                    Text(
                        text = stringResource(id = R.string.total_files_format, filesCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}