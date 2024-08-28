package com.d4rk.cleaner.ui.memory

import android.app.Activity
import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.SnippetFolder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.memorymanager.RamInfo
import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.cleaning.StorageUtils.formatSize
import com.d4rk.cleaner.utils.compose.bounceClick
import com.d4rk.cleaner.utils.compose.components.StorageProgressBar
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemoryManagerComposable() {
    val viewModel : MemoryManagerViewModel = viewModel<MemoryManagerViewModel>()
    val storageInfo : StorageInfo by viewModel.storageInfo.collectAsState()
    val ramInfo : RamInfo by viewModel.ramInfo.collectAsState()
    val isLoading : Boolean by viewModel.isLoading.collectAsState()
    val listExpanded : Boolean by viewModel.listExpanded.collectAsState()
    val context : Context = LocalContext.current

    val transition : Transition<Boolean> =
            updateTransition(targetState = ! isLoading , label = "LoadingTransition")

    val progressAlpha : Float by transition.animateFloat(label = "Progress Alpha") {
        if (it) 0f else 1f
    }
    val contentAlpha : Float by transition.animateFloat(label = "Content Alpha") {
        if (it) 1f else 0f
    }

    val pagerState : PagerState = rememberPagerState { 2 }

    LaunchedEffect(Unit) {
        viewModel.updateStorageInfo(context)
        viewModel.updateRamInfo(context)
        if (! PermissionsUtils.hasStoragePermissions(context)) {
            PermissionsUtils.requestStoragePermissions(context as Activity)
        }

        if (! PermissionsUtils.hasUsageAccessPermissions(context)) {
            PermissionsUtils.requestUsageAccess(context as Activity)
        }

        while (true) {
            delay(5000)
            viewModel.updateRamInfo(context)
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
                    .alpha(progressAlpha) ,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else {
        Column(
            modifier = Modifier
                    .fillMaxSize()
                    .alpha(contentAlpha)
        ) {
            CarouselLayout(
                items = listOf(storageInfo , ramInfo) ,
                peekPreviewWidth = 24.dp ,
                pagerState = pagerState
            ) { item ->
                when (item) {
                    is StorageInfo -> StorageInfoCard(item)
                    is RamInfo -> RamInfoCard(item)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DotsIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally) ,
                totalDots = 2 ,
                selectedIndex = pagerState.currentPage ,
                dotSize = 6.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .padding(horizontal = 16.dp) ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.categories) ,
                    modifier = Modifier.weight(1f) ,
                    style = MaterialTheme.typography.headlineSmall ,
                )

                Spacer(modifier = Modifier.width(8.dp))
                IconButton(modifier = Modifier.bounceClick() , onClick = {
                    viewModel.toggleListExpanded()
                }) {
                    Icon(
                        imageVector = if (listExpanded) Icons.Outlined.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft ,
                        contentDescription = if (listExpanded) "Collapse" else "Expand"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (listExpanded) {
                StorageBreakdownGrid(storageInfo.storageBreakdown)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> CarouselLayout(
    items : List<T> ,
    peekPreviewWidth : Dp ,
    pagerState : PagerState ,
    itemContent : @Composable (item : T) -> Unit
) {
    HorizontalPager(
        state = pagerState ,
        modifier = Modifier.fillMaxWidth() ,
        contentPadding = PaddingValues(horizontal = peekPreviewWidth)
    ) { page ->
        val pageOffset : Float = (pagerState.currentPage - page).toFloat().absoluteValue

        val scale : Float by animateFloatAsState(
            targetValue = lerp(
                start = 0.95f , stop = 1f , fraction = 1f - pageOffset.coerceIn(0f , 1f)
            ) ,
            animationSpec = tween(durationMillis = 250) ,
            label = "Carousel Item Scale Animation"
        )
        val alpha : Float =
                lerp(start = 0.5f , stop = 1f , fraction = 1f - pageOffset.coerceIn(0f , 1f))

        Card(modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }) {
            itemContent(items[page])
        }
    }
}

@Composable
fun StorageInfoCard(storageInfo : StorageInfo) {

    val progress : Float = if (storageInfo.totalStorage == 0L) {
        0f
    }
    else {
        storageInfo.usedStorage.toFloat() / storageInfo.totalStorage.toFloat()
    }

    Column(
        modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
    ) {
        Text(
            text = stringResource(id = R.string.storage_information) ,
            style = MaterialTheme.typography.headlineSmall ,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress } ,
            modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp) ,
            color = MaterialTheme.colorScheme.primary ,
        )
        Spacer(modifier = Modifier.height(8.dp))
        StorageInfoText(
            label = stringResource(id = R.string.used) , size = storageInfo.usedStorage
        )
        StorageInfoText(
            label = stringResource(id = R.string.free) , size = storageInfo.freeStorage
        )
        StorageInfoText(
            label = stringResource(id = R.string.total) , size = storageInfo.totalStorage
        )
    }
}

@Composable
fun StorageBreakdownGrid(storageBreakdown : Map<String , Long>) {
    val items : List<Map.Entry<String , Long>> = storageBreakdown.entries.toList()
    val chunkSize = 2

    LazyColumn(
        modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(horizontal = 16.dp)
    ) {
        items(items = items.chunked(chunkSize) ,
              key = { chunk -> chunk.firstOrNull()?.key ?: "" }) { chunk ->
            Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
            ) {
                for (item in chunk) {
                    val (icon , size) = item
                    StorageBreakdownItem(icon = icon , size = size , modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun StorageBreakdownItem(icon : String , size : Long , modifier : Modifier = Modifier) {
    val storageIcons : Map<String , ImageVector> = mapOf(
        stringResource(id = R.string.installed_apps) to Icons.Outlined.Apps ,
        stringResource(id = R.string.system) to Icons.Outlined.Android ,
        stringResource(id = R.string.music) to Icons.Outlined.MusicNote ,
        stringResource(id = R.string.images) to Icons.Outlined.Image ,
        stringResource(id = R.string.documents) to Icons.Outlined.FolderOpen ,
        stringResource(id = R.string.downloads) to Icons.Outlined.Download ,
        stringResource(id = R.string.other_files) to Icons.Outlined.FolderOpen ,
    )
    Card(
        modifier = modifier
                .padding(vertical = 4.dp , horizontal = 4.dp)
                .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(16.dp) ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(48.dp) ,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) ,
            ) {
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = storageIcons[icon] ?: Icons.Outlined.SnippetFolder ,
                        contentDescription = icon ,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            Column {
                Text(
                    text = icon ,
                    style = MaterialTheme.typography.bodyMedium ,
                    fontWeight = FontWeight.Bold
                )
                Text(text = formatSize(size) , style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun StorageInfoText(label : String , size : Long) {
    Text(text = "$label ${formatSize(size)}" , style = MaterialTheme.typography.bodyMedium)
}


@Composable
fun RamInfoCard(ramInfo : RamInfo) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.ram_information) ,
            style = MaterialTheme.typography.headlineSmall ,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        StorageProgressBar(
            StorageInfo(
                totalStorage = ramInfo.totalRam ,
                usedStorage = ramInfo.usedRam ,
                freeStorage = ramInfo.availableRam
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        StorageInfoText(label = stringResource(id = R.string.used_ram) , size = ramInfo.usedRam)
        StorageInfoText(
            label = stringResource(id = R.string.free_ram) , size = ramInfo.availableRam
        )
        StorageInfoText(label = stringResource(id = R.string.total_ram) , size = ramInfo.totalRam)
    }
}

@Composable
fun DotsIndicator(
    modifier : Modifier = Modifier ,
    totalDots : Int ,
    selectedIndex : Int ,
    selectedColor : Color = MaterialTheme.colorScheme.primary ,
    unSelectedColor : Color = Color.Gray ,
    dotSize : Dp ,
    animationDuration : Int = 300
) {
    val transition : Transition<Int> =
            updateTransition(targetState = selectedIndex , label = "Dot Transition")

    LazyRow(
        modifier = modifier
                .wrapContentWidth()
                .height(dotSize) ,
        verticalAlignment = Alignment.CenterVertically
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