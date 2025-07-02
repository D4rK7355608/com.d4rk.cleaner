package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R

@Composable
fun ClipboardCleanerCard(
    clipboardText: String?,
    modifier: Modifier = Modifier,
    onCleanClick: () -> Unit,
    onSeeMoreClick: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth() ,
        border = BorderStroke(width = 1.dp , color = MaterialTheme.colorScheme.outline) ,
        shape = RoundedCornerShape(SizeConstants.ExtraLargeSize) ,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize),
            verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.ContentPaste, contentDescription = null)
                Column(modifier = Modifier.padding(start = SizeConstants.MediumSize)) {
                    Text(
                        text = stringResource(id = R.string.clipboard_card_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    SmallVerticalSpacer()
                    Text(
                        text = stringResource(id = R.string.clipboard_card_subtitle),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (!clipboardText.isNullOrBlank()) {
                Text(
                    text = stringResource(id = R.string.clipboard_current_format, clipboardText),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
            ) {
                FilledTonalButton(onClick = onCleanClick, modifier = Modifier.weight(1f).bounceClick()) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                    ButtonIconSpacer()
                    Text(text = stringResource(id = R.string.clean_clipboard))
                }
                FilledTonalButton(onClick = onSeeMoreClick, modifier = Modifier.weight(1f).bounceClick()) {
                    Icon(imageVector = Icons.Outlined.Visibility, contentDescription = null)
                    ButtonIconSpacer()
                    Text(text = stringResource(id = R.string.see_more))
                }
            }
        }
    }
}
