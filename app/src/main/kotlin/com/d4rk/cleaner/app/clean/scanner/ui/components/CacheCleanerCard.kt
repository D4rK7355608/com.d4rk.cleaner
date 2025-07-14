package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.constraintlayout.compose.ConstraintLayout
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.TonalIconButtonWithText
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R

@Composable
fun CacheCleanerCard(
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SizeConstants.ExtraLargeSize),
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = SizeConstants.LargeSize),
                verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cached,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.cache_cleaner_card_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(id = R.string.cache_cleaner_card_subtitle),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val (wipText, actionButton) = createRefs()

                Text(
                    text = "W.I.P",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .constrainAs(wipText) {
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        }
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(topEnd = SizeConstants.ExtraLargeSize)
                        )
                        .padding(all = SizeConstants.MediumSize)
                )

                TonalIconButtonWithText(
                    label = stringResource(id = R.string.scan_cache),
                    painter = painterResource(id = R.drawable.ic_folder_search),
                    onClick = onScanClick,
                    modifier = Modifier
                        .constrainAs(actionButton) {
                            end.linkTo(parent.end, margin = SizeConstants.LargeSize)
                            bottom.linkTo(parent.bottom, margin = SizeConstants.LargeSize)
                        }
                )
            }
        }
    }
}