package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.TonalIconButtonWithText
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import java.io.File

@Composable
fun LargeFilesCard(
    files: List<File>,
    modifier: Modifier = Modifier,
    onOpenClick: () -> Unit
) {
    val preview = files.take(4)

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SizeConstants.ExtraLargeSize),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize),
            verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(start = SizeConstants.MediumSize)) {
                    Text(
                        text = stringResource(id = R.string.large_files_card_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    SmallVerticalSpacer()
                    Text(
                        text = stringResource(id = R.string.large_files_card_subtitle),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            AnimatedVisibility(visible = preview.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .animateContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    preview.forEach { file ->
                        FilePreviewCard(
                            file = file,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    if (files.size > preview.size) {
                        Text(
                            text = pluralStringResource(
                                id = R.plurals.apk_card_more_format,
                                count = files.size - preview.size,
                                files.size - preview.size
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            TonalIconButtonWithText(
                label = stringResource(id = R.string.open_large_files),
                painter = painterResource(id = R.drawable.ic_folder_search),
                onClick = onOpenClick,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
