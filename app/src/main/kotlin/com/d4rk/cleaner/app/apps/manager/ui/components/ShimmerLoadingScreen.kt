package com.d4rk.cleaner.app.apps.manager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.WindowItemFit

@Composable
fun ShimmerLoadingScreen(paddingValues: PaddingValues) {
    val itemCount = WindowItemFit.count(
        itemHeight = 80.dp,
        itemSpacing = SizeConstants.ExtraTinySize,
        paddingValues = paddingValues
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = SizeConstants.ExtraTinySize),
        verticalArrangement = Arrangement.spacedBy(space = SizeConstants.ExtraTinySize),
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) {
        items(count = itemCount) {
            AppItemPlaceholder(
                modifier = Modifier.padding(
                    start = SizeConstants.SmallSize,
                    end = SizeConstants.SmallSize,
                    top = SizeConstants.SmallSize
                )
            )
        }
    }
}