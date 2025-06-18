package com.d4rk.cleaner.app.clean.memory.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.d4rk.android.libs.apptoolkit.core.ui.components.carousel.DotsIndicator
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.hapticPagerSwipe
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.shimmerEffect
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemoryManagerShimmer(paddingValues: PaddingValues) {
    val pagerState: PagerState = rememberPagerState(pageCount = { 2 })

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
        ) { page ->
            val pageOffset = remember(pagerState.currentPage, page) {
                (pagerState.currentPage - page).absoluteValue.toFloat()
            }
            CarouselShimmerCard(pageOffset = pageOffset)
        }

        if (false) HorizontalPager(state = pagerState) {}

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

        ListHeaderShimmer()

        SmallVerticalSpacer()

        StorageBreakdownGridShimmer()
    }
}

@Composable
private fun ListHeaderShimmer() {
    val density = LocalDensity.current
    val headlineSmallHeight =
        with(density) { MaterialTheme.typography.headlineSmall.fontSize.toDp() }

    val iconButtonSize = 40.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SizeConstants.LargeSize),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(headlineSmallHeight)
                .clip(RoundedCornerShape(SizeConstants.SmallSize))
                .shimmerEffect()
        )

        SmallHorizontalSpacer()

        Spacer(
            modifier = Modifier
                .size(iconButtonSize)
                .clip(CircleShape)
                .shimmerEffect()
        )
    }
}

@Composable
private fun CarouselShimmerCard(pageOffset: Float) {
    val density = LocalDensity.current
    val titleHeight = with(density) { MaterialTheme.typography.titleLarge.fontSize.toDp() + 4.dp }
    val bodyHeight = with(density) { MaterialTheme.typography.bodyMedium.fontSize.toDp() + 4.dp }
    val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
    val scale = animateFloatAsState(
        targetValue = lerp(0.95f, 1f, 1f - pageOffset.coerceIn(0f, 1f)),
        animationSpec = tween(250),
        label = "Carousel Item Scale for Page $pageOffset"
    ).value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
        shape = RoundedCornerShape(SizeConstants.MediumSize),
    ) {
        Column(modifier = Modifier.padding(SizeConstants.LargeSize)) {

            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(titleHeight)
                    .clip(RoundedCornerShape(SizeConstants.SmallSize))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(SizeConstants.MediumSize))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SizeConstants.SmallSize)
                    .clip(RoundedCornerShape(SizeConstants.SmallSize))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(SizeConstants.MediumSize))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(bodyHeight)
                    .clip(RoundedCornerShape(SizeConstants.SmallSize))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(SizeConstants.SmallSize))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(bodyHeight)
                    .clip(RoundedCornerShape(SizeConstants.SmallSize))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(SizeConstants.SmallSize))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(bodyHeight)
                    .clip(RoundedCornerShape(SizeConstants.SmallSize))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun StorageBreakdownGridShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(horizontal = SizeConstants.MediumSize)
    ) {
        repeat(3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                StorageBreakdownItemShimmer(modifier = Modifier.weight(1f))

                StorageBreakdownItemShimmer(modifier = Modifier.weight(1f))
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StorageBreakdownItemShimmer(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun StorageBreakdownItemShimmer(modifier: Modifier = Modifier) {
    val bodyHeight =
        with(LocalDensity.current) { MaterialTheme.typography.bodyMedium.fontSize.toDp() }
    val bodySmallHeight =
        with(LocalDensity.current) { MaterialTheme.typography.bodySmall.fontSize.toDp() }
    Card(
        modifier = modifier
            .padding(all = SizeConstants.ExtraSmallSize),
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
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {}

            Spacer(modifier = Modifier.padding(horizontal = SizeConstants.ExtraSmallSize))

            Column {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(bodyHeight)
                        .clip(RoundedCornerShape(SizeConstants.SmallSize))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.padding(vertical = SizeConstants.ExtraTinySize))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(bodySmallHeight)
                        .clip(RoundedCornerShape(SizeConstants.SmallSize))
                        .shimmerEffect()
                )
            }
        }
    }
}