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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) { _ ->
            CarouselShimmerItem()
        }

        LargeVerticalSpacer()

        DotsIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = SizeConstants.SmallSize),
            totalDots = 2,
            selectedIndex = pagerState.currentPage,
            dotSize = SizeConstants.MediumSize / 2,
        )

        LargeVerticalSpacer()

        Column(modifier = Modifier.fillMaxWidth()) {
            repeat(3) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(SizeConstants.MediumSize))
                            .shimmerEffect()
                    )
                    SmallHorizontalSpacer()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(SizeConstants.MediumSize))
                            .shimmerEffect()
                    )
                }
                SmallVerticalSpacer()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(SizeConstants.MediumSize))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun CarouselShimmerItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SizeConstants.MediumSize))
            .padding(SizeConstants.LargeSize)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(SizeConstants.MediumSize)
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

        repeat(3) {
            SmallVerticalSpacer()
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(SizeConstants.MediumSize)
                    .clip(RoundedCornerShape(SizeConstants.SmallSize))
                    .shimmerEffect()
            )
        }
    }
}

