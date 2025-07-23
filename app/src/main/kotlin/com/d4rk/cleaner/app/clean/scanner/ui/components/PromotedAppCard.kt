package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Recommend
import androidx.compose.material.icons.outlined.Shop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.TonalIconButtonWithText
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Recommend,
                    contentDescription = stringResource(id = R.string.cleaner_recommends_icon_description),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(id = R.string.cleaner_recommends),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
            ) {
                AsyncImage(
                    model = app.iconLogo,
                    contentDescription = stringResource(
                        R.string.promoted_app_icon_description,
                        app.name
                    ),
                    modifier = Modifier
                        .size(size = 48.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = app.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )


                TonalIconButtonWithText(
                    label = stringResource(id = R.string.install),
                    icon = Icons.Outlined.Shop,
                    onClick = {
                        IntentsHelper.openUrl(
                            context = context,
                            url = "https://play.google.com/store/apps/details?id=${app.packageName}"
                        )
                    },
                    iconContentDescription = stringResource(R.string.install_button_icon_description)
                )
            }
        }
    }
}