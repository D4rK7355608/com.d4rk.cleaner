package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.shimmerEffect
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants

@Composable
fun InfoColumn(
    title : String , value : String , modifier : Modifier = Modifier , isLoading : Boolean
) {
    if (isLoading) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally , modifier = modifier
        ) {
            Spacer(
                modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(SizeConstants.MediumSize)
                        .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(SizeConstants.ExtraSmallSize))
            Spacer(
                modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(SizeConstants.MediumSize)
                        .shimmerEffect()
            )
        }
    }
    else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally , modifier = modifier
        ) {
            Text(text = title , style = MaterialTheme.typography.bodySmall , modifier = Modifier.basicMarquee())
            Text(
                text = value , style = MaterialTheme.typography.bodyMedium , maxLines = 2 , overflow = TextOverflow.Ellipsis
            )
        }
    }
}