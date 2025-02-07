package com.d4rk.cleaner.core.ui.components.layouts

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.cleaner.core.ui.components.modifiers.hapticPagerSwipe
import kotlin.math.absoluteValue

@Composable
fun <T> CarouselLayout(
    items : List<T> , sidePadding : Dp , pagerState : PagerState , itemContent : @Composable (item : T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState ,
            modifier = Modifier.fillMaxWidth().hapticPagerSwipe(pagerState) ,
            contentPadding = PaddingValues(horizontal = sidePadding) ,
        ) { page ->
            val pageOffset = remember(pagerState.currentPage , page) {
                (pagerState.currentPage - page).absoluteValue.toFloat()
            }
            CarouselItem(item = items[page] , pageOffset = pageOffset , itemContent = itemContent)
        }

        LargeVerticalSpacer()

        DotsIndicator(
            modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp) ,
            totalDots = items.size ,
            selectedIndex = pagerState.currentPage ,
            dotSize = 6.dp ,
        )
    }
}

@Composable
fun <T> CarouselItem(
    item : T , pageOffset : Float , itemContent : @Composable (item : T) -> Unit
) {
    val scale = animateFloatAsState(
        targetValue = lerp(0.95f , 1f , 1f - pageOffset.coerceIn(0f , 1f)) , animationSpec = tween(250) , label = "Carousel Item Scale for Page $pageOffset"
    ).value

    val alpha = lerp(0.5f , 1f , 1f - pageOffset.coerceIn(0f , 1f))

    Card(modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }) {
        itemContent(item)
    }
}

@Composable
fun DotsIndicator(
    modifier : Modifier = Modifier , totalDots : Int , selectedIndex : Int , selectedColor : Color = MaterialTheme.colorScheme.primary , unSelectedColor : Color = Color.Gray , dotSize : Dp , animationDuration : Int = 300
) {
    val transition : Transition<Int> = updateTransition(targetState = selectedIndex , label = "Dot Transition")

    LazyRow(
        modifier = modifier
                .wrapContentWidth()
                .height(dotSize) , verticalAlignment = Alignment.CenterVertically
    ) {
        items(count = totalDots , key = { index -> index }) { index ->
            val animatedDotSize : Dp by transition.animateDp(transitionSpec = {
                tween(durationMillis = animationDuration , easing = FastOutSlowInEasing)
            } , label = "Dot Size Animation") {
                if (it == index) dotSize else dotSize / 1.4f
            }

            val isSelected : Boolean = index == selectedIndex
            val size : Dp = if (isSelected) animatedDotSize else animatedDotSize

            IndicatorDot(
                color = if (isSelected) selectedColor else unSelectedColor , size = size
            )

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
fun IndicatorDot(
    modifier : Modifier = Modifier ,
    size : Dp ,
    color : Color ,
) {
    Box(
        modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(color)
    )
}