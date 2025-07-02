package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.PromotedApp

@Composable
fun PromotedAppCard(app: PromotedApp, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SizeConstants.ExtraLargeSize)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize),
            verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
        ) {
            Text(
                text = stringResource(id = R.string.cleaner_recommends),
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
            ) {
                AsyncImage(
                    model = app.iconLogo,
                    contentDescription = null,
                    modifier = Modifier.padding(end = SizeConstants.MediumSize).size(48.dp)
                )
                Text(
                    text = app.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                FilledTonalButton(
                    onClick = {
                        IntentsHelper.openUrl(
                            context = context,
                            url = "https://play.google.com/store/apps/details?id=${app.packageName}"
                        )
                    },
                    modifier = Modifier.bounceClick()
                ) {
                    Text(text = stringResource(id = R.string.install))
                }
            }
        }
    }
}
