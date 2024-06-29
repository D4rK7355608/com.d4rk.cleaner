package com.d4rk.cleaner.ui.memory

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cleaner.ui.memory.model.RamInfo
import com.d4rk.cleaner.ui.memory.model.StorageInfo
import com.d4rk.cleaner.utils.StorageProgressBar
import kotlin.math.absoluteValue

val StorageIcons = mapOf(
    "Installed Apps" to Icons.Outlined.Apps,
    "System" to Icons.Outlined.Android,
    "Music" to Icons.Outlined.MusicNote,
    "Images" to Icons.Outlined.Image,
    "Documents" to Icons.Outlined.FolderOpen,
    "Downloads" to Icons.Outlined.Download,
    "Other Files" to Icons.Outlined.FolderOpen
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemoryManagerComposable() {
    val viewModel = viewModel<MemoryManagerViewModel>()
    val storageInfo by viewModel.storageInfo.collectAsState()
    val ramInfo by viewModel.ramInfo.collectAsState()
    val context = LocalContext.current

    var listExpanded by remember { mutableStateOf(true) }

    val pagerState = rememberPagerState {
        2
    }

    LaunchedEffect(Unit) {
        viewModel.updateStorageInfo(context)
        viewModel.updateRamInfo(context)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState, modifier = Modifier
                .fillMaxWidth()
        ) { page ->
            val pageOffset = (page - pagerState.currentPage).absoluteValue
            val scale by animateFloatAsState(
                targetValue = lerp(
                    start = 0.9f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0, 1)
                ), label = ""
            )

            Card(modifier = Modifier
                .padding(16.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = lerp(
                        start = 0.5f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0, 1)
                    )
                }
                .offset {
                    val pageOffsetState = (page - pagerState.currentPage)
                    IntOffset(
                        x = (10.dp * pageOffsetState.toFloat()).roundToPx(), y = 0
                    )
                }
                .fillMaxWidth()) {
                when (page) {
                    0 -> StorageInfoCard(storageInfo)
                    1 -> RamInfoCard(ramInfo)
                }
            }
        }
        DotsIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            totalDots = 2,
            selectedIndex = pagerState.currentPage,
            dotSize = 6.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.animateContentSize()) {
                    if (listExpanded) {
                        LazyColumn {
                            items(storageInfo.storageBreakdown.entries.toList()) { entry ->
                                StorageBreakdownItem(icon = entry.key, size = entry.value)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { listExpanded = !listExpanded }) {
                Icon(
                    imageVector = if (listExpanded) Icons.Outlined.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft,
                    contentDescription = if (listExpanded) "Collapse" else "Expand"
                )
            }
        }
    }
}

@Composable
fun StorageInfoCard(storageInfo: StorageInfo) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Storage Information",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { storageInfo.usedStorage.toFloat() / storageInfo.totalStorage.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        StorageInfoText(label = "Used:", size = storageInfo.usedStorage)
        StorageInfoText(label = "Free:", size = storageInfo.freeStorage)
        StorageInfoText(label = "Total:", size = storageInfo.totalStorage)
    }
}

@Composable
fun StorageBreakdownItem(icon: String, size: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = StorageIcons[icon] ?: Icons.Filled.Info,
                        contentDescription = icon,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 16.dp))

            Column {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = formatSize(size), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun StorageInfoText(label: String, size: Long) {
    Text(text = "$label ${formatSize(size)}", style = MaterialTheme.typography.bodyMedium)
}


@Composable
fun RamInfoCard(ramInfo: RamInfo) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "RAM Information",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        StorageProgressBar(
            StorageInfo(
                totalStorage = ramInfo.totalRam,
                usedStorage = ramInfo.usedRam,
                freeStorage = ramInfo.availableRam
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        StorageInfoText(label = "Used RAM:", size = ramInfo.usedRam)
        StorageInfoText(label = "Free RAM:", size = ramInfo.availableRam)
        StorageInfoText(label = "Total RAM:", size = ramInfo.totalRam)
    }
}

@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unSelectedColor: Color = Color.Gray,
    dotSize: Dp,
) {
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        items(totalDots) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor else unSelectedColor,
                size = dotSize
            )

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
fun IndicatorDot(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}