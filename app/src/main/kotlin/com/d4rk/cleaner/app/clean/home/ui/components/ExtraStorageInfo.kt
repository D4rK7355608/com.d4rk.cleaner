package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R

@Composable
fun ExtraStorageInfo(
    modifier : Modifier = Modifier ,
    cleanedSpace : String ,
    freeSpace : String ,
    isCleanedSpaceLoading : Boolean ,
    isFreeSpaceLoading : Boolean ,
) {
    Row(
        modifier = modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
                .padding(horizontal = SizeConstants.LargeSize , vertical = SizeConstants.SmallSize) , horizontalArrangement = Arrangement.SpaceAround , verticalAlignment = Alignment.CenterVertically
    ) {

        InfoColumn(
            title = stringResource(id = R.string.cleaned_space) , value = cleanedSpace , modifier = Modifier.weight(weight = 1f) , isLoading = isCleanedSpaceLoading
        )

        VerticalDivider()

        InfoColumn(
            title = stringResource(id = R.string.free_space) , value = freeSpace , modifier = Modifier.weight(weight = 1f) , isLoading = isFreeSpaceLoading
        )
    }
}