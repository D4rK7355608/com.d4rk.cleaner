package com.d4rk.cleaner.app.clean.memory.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
fun ShimmeringCarousel(sidePadding: Dp) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = sidePadding) // Apply padding here
        ) {
            CarouselShimmerCard()
        }

        Spacer(modifier = Modifier.height(SizeConstants.MediumSize)) // Adjusted spacer based on your original CustomCarousel

        ShimmerDotsIndicator(
            modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(bottom = SizeConstants.SmallSize),
            totalDots = 3, // Example: Show 3 shimmer dots
            dotSize = SizeConstants.MediumSize / 2,
        )
    }
}

@Composable
private fun CarouselShimmerCard() {
    val density = LocalDensity.current
    val titleHeight = with(density) { MaterialTheme.typography.titleLarge.fontSize.toDp() + 4.dp } // Add a bit for line height
    val bodyHeight = with(density) { MaterialTheme.typography.bodyMedium.fontSize.toDp() + 4.dp } // Add a bit for line height
    val progressBarHeight = 8.dp // Typical height for a progress bar

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SizeConstants.MediumSize),
    ) {
        Column(modifier = Modifier.padding(SizeConstants.LargeSize)) {
            // Title Placeholder
            Spacer(
                modifier = Modifier
                        .fillMaxWidth(0.7f) // Adjust fraction as needed
                        .height(titleHeight)
                        .clip(RoundedCornerShape(SizeConstants.SmallSize))
                        .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(SizeConstants.MediumSize)) // Space between title and progress bar

            // Progress Bar Placeholder
            Spacer(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(progressBarHeight)
                        .clip(RoundedCornerShape(SizeConstants.SmallSize))
                        .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(SizeConstants.MediumSize)) // Space between progress bar and text lines

            // Text Lines Placeholder (Used, Free, Total)
            repeat(3) {
                Spacer(
                    modifier = Modifier
                            .fillMaxWidth(0.5f) // Adjust fraction as needed
                            .height(bodyHeight)
                            .clip(RoundedCornerShape(SizeConstants.SmallSize))
                            .shimmerEffect()
                )
                if (it < 2) { // Don't add spacer after the last item
                    Spacer(modifier = Modifier.height(SizeConstants.SmallSize))
                }
            }
        }
    }
}

@Composable
fun ShimmerDotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    dotSize: Dp,
    spacing: Dp = SizeConstants.SmallSize
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(totalDots) {
            Box(
                modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .shimmerEffect()
            )
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

