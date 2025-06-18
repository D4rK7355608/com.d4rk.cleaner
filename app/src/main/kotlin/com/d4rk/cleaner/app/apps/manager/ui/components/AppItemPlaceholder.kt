package com.d4rk.cleaner.app.apps.manager.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.shimmerEffect
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants

@Composable
fun AppItemPlaceholder(modifier: Modifier) {
    OutlinedCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SizeConstants.LargeSize)
                .clip(RoundedCornerShape(SizeConstants.LargeSize)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )

            Column(
                modifier = Modifier
                    .padding(start = SizeConstants.LargeSize)
                    .weight(1f)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .clip(CircleShape)
                        .height(MaterialTheme.typography.titleMedium.fontSize.value.dp)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(SizeConstants.SmallSize / 2))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .clip(CircleShape)
                        .height(MaterialTheme.typography.bodyMedium.fontSize.value.dp)
                        .shimmerEffect()
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect(),
            )
        }
    }
}