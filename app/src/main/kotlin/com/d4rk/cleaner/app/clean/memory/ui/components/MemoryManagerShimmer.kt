package com.d4rk.cleaner.app.clean.memory.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toDp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import com.d4rk.android.libs.apptoolkit.core.ui.components.carousel.DotsIndicator
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.hapticPagerSwipe
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.shimmerEffect
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemoryManagerShimmer(paddingValues : PaddingValues) {
    val pagerState : PagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .hapticPagerSwipe(pagerState = pagerState),
            userScrollEnabled = false,
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) { _ ->
            CarouselShimmerCard()
        }

        LargeVerticalSpacer()

        DotsIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = SizeConstants.SmallSize)
                .shimmerEffect(),
            totalDots = 2,
            selectedIndex = pagerState.currentPage,
            dotSize = SizeConstants.MediumSize / 2,
        )

        LargeVerticalSpacer()

        StorageBreakdownGridShimmer()
    }
}

@Composable
private fun CarouselShimmerCard() {
    val headlineHeight = with(LocalDensity.current) { MaterialTheme.typography.headlineSmall.fontSize.toDp() }
    val bodyHeight = with(LocalDensity.current) { MaterialTheme.typography.bodyMedium.fontSize.toDp() }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SizeConstants.MediumSize),
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(SizeConstants.LargeSize)) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(headlineHeight)
                    .clip(RoundedCornerShape(SizeConstants.SmallSize))
                    .shimmerEffect()
            )

            SmallVerticalSpacer()

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SizeConstants.SmallSize)
                    .clip(RoundedCornerShape(SizeConstants.SmallSize))
                    .shimmerEffect()
            )

            SmallVerticalSpacer()

            repeat(3) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(bodyHeight)
                        .clip(RoundedCornerShape(SizeConstants.SmallSize))
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
private fun StorageBreakdownGridShimmer() {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(3) {
            Row(modifier = Modifier.fillMaxWidth()) {
                StorageBreakdownItemShimmer(modifier = Modifier.weight(1f))
                SmallHorizontalSpacer()
                StorageBreakdownItemShimmer(modifier = Modifier.weight(1f))
            }
            SmallVerticalSpacer()
        }
        StorageBreakdownItemShimmer(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun StorageBreakdownItemShimmer(modifier: Modifier = Modifier) {
    val bodyHeight = with(LocalDensity.current) { MaterialTheme.typography.bodyMedium.fontSize.toDp() }
    val bodySmallHeight = with(LocalDensity.current) { MaterialTheme.typography.bodySmall.fontSize.toDp() }
    Card(
        modifier = modifier
            .padding(all = SizeConstants.ExtraSmallSize),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .shimmerEffect(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {}

            Spacer(modifier = Modifier.padding(horizontal = SizeConstants.ExtraSmallSize))

            Column {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(bodyHeight)
                        .clip(RoundedCornerShape(SizeConstants.SmallSize))
                        .shimmerEffect()
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(bodySmallHeight)
                        .clip(RoundedCornerShape(SizeConstants.SmallSize))
                        .shimmerEffect()
                )
            }
        }
    }
}

