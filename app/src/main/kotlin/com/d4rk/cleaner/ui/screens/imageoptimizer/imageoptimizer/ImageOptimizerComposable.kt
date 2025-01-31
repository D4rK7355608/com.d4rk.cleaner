package com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.ui.components.ads.AdBanner
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.imageoptimizer.ImageOptimizerState
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.tabs.FileSizeScreen
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.tabs.ManualModeScreen
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.tabs.QuickCompressScreen
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ImageOptimizerComposable(
    activity: ImageOptimizerActivity , viewModel: ImageOptimizerViewModel
) {
    val context: Context = LocalContext.current
    val dataStore: DataStore = AppCoreManager.dataStore
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val adsState: State<Boolean> = dataStore.ads.collectAsState(initial = true)
    val tabs: List<String> = listOf(
        stringResource(id = R.string.quick_compress),
        stringResource(id = R.string.file_size),
        stringResource(id = R.string.manual),
    )
    val pagerState: PagerState = rememberPagerState(pageCount = { tabs.size })
    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.image_optimizer),
        onBackClicked = { activity.finish() }) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (imageCardView: ConstrainedLayoutReference, tabLayout: ConstrainedLayoutReference, viewPager: ConstrainedLayoutReference, compressButton: ConstrainedLayoutReference, adView: ConstrainedLayoutReference) = createRefs()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(imageCardView) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(tabLayout.top)
                    }
                    .padding(24.dp),
            ) {
                ImageDisplay(viewModel)
            }

            TabRow(selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.constrainAs(tabLayout) {
                    top.linkTo(imageCardView.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(text = title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        })
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.constrainAs(viewPager) {
                top.linkTo(tabLayout.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(compressButton.top)
                height = Dimension.fillToConstraints
            }) { page ->
                when (page) {
                    0 -> QuickCompressScreen(viewModel)
                    1 -> FileSizeScreen(viewModel)
                    2 -> ManualModeScreen(viewModel)
                }
            }

            OutlinedButton(onClick = {}, modifier = Modifier
                .constrainAs(compressButton) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    if (adsState.value) {
                        bottom.linkTo(adView.top)
                    } else {
                        bottom.linkTo(parent.bottom)
                    }
                }
                .padding(12.dp)) {
                Text(text = stringResource(id = R.string.optimize_image))
            }

            AdBanner(modifier = Modifier.constrainAs(adView) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
        }
    }
}


@Composable
fun ImageDisplay(viewModel: ImageOptimizerViewModel) {
    val state: State<ImageOptimizerState> = viewModel.uiState.collectAsState()
    val showCompressedImage: MutableState<Boolean> = remember { mutableStateOf(value = false) }

    LaunchedEffect(key1 = state.value.compressedImageUri) {
        if (state.value.compressedImageUri != null) {
            showCompressedImage.value = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 1f),
        contentAlignment = Alignment.Center
    ) {
        if (state.value.isLoading) {
            CircularProgressIndicator()
        } else {
            if (showCompressedImage.value) {
                state.value.compressedImageUri?.let { imageUri ->
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}