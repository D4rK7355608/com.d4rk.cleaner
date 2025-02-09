package com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import com.d4rk.android.libs.apptoolkit.ui.components.snackbar.Snackbar
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.imageoptimizer.ImageOptimizerState
import com.d4rk.cleaner.ui.components.ads.AdBanner
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.tabs.FileSizeTab
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.tabs.ManualModeTab
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.tabs.QuickCompressTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ImageOptimizerScreen(
    activity : ImageOptimizerActivity , viewModel : ImageOptimizerViewModel
) {
    val uiState : ImageOptimizerState by viewModel.uiState.collectAsState()

    val dataStore : DataStore = AppCoreManager.dataStore
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val adsState : State<Boolean> = dataStore.ads.collectAsState(initial = true)
    val tabs : List<String> = listOf(
        stringResource(id = R.string.quick_compress) ,
        stringResource(id = R.string.file_size) ,
        stringResource(id = R.string.manual) ,
    )
    val pagerState : PagerState = rememberPagerState(pageCount = { tabs.size })

    LaunchedEffect(key1 = pagerState.currentPage) {
        viewModel.setCurrentTab(pagerState.currentPage)
    }

    TopAppBarScaffoldWithBackButton(title = stringResource(id = R.string.image_optimizer) , onBackClicked = { activity.finish() }) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
        ) {
            val (imageCardView : ConstrainedLayoutReference , tabLayout : ConstrainedLayoutReference , viewPager : ConstrainedLayoutReference , compressButton : ConstrainedLayoutReference , adView : ConstrainedLayoutReference) = createRefs()

            Card(
                modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(imageCardView) {
                            top.linkTo(anchor = parent.top)
                            start.linkTo(anchor = parent.start)
                            end.linkTo(anchor = parent.end)
                            bottom.linkTo(anchor = tabLayout.top)
                        }
                        .padding(all = 24.dp) ,
            ) {
                ImageDisplay(viewModel)
            }

            TabRow(selectedTabIndex = pagerState.currentPage , modifier = Modifier.constrainAs(ref = tabLayout) {
                top.linkTo(anchor = imageCardView.bottom)
                start.linkTo(anchor = parent.start)
                end.linkTo(anchor = parent.end)
            }) {
                tabs.forEachIndexed { index , title ->
                    Tab(text = { Text(text = title) } , selected = pagerState.currentPage == index , onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = index)
                        }
                    })
                }
            }

            HorizontalPager(state = pagerState , modifier = Modifier.constrainAs(ref = viewPager) {
                top.linkTo(anchor = tabLayout.bottom)
                start.linkTo(anchor = parent.start)
                end.linkTo(anchor = parent.end)
                bottom.linkTo(anchor = compressButton.top)
                height = Dimension.fillToConstraints
            }) { page ->
                when (page) {
                    0 -> QuickCompressTab(viewModel = viewModel)
                    1 -> FileSizeTab(viewModel = viewModel)
                    2 -> ManualModeTab(viewModel = viewModel)
                }
            }

            OutlinedButton(onClick = {
                coroutineScope.launch {
                    viewModel.optimizeImage()
                }
            } , enabled = if (pagerState.currentPage == 1) {
                uiState.fileSizeKB != 0
            }
            else {
                true
            } , modifier = Modifier
                    .constrainAs(ref = compressButton) {
                        start.linkTo(anchor = parent.start)
                        end.linkTo(anchor = parent.end)
                        if (adsState.value) {
                            bottom.linkTo(anchor = adView.top)
                        }
                        else {
                            bottom.linkTo(anchor = parent.bottom)
                        }
                    }
                    .padding(all = 12.dp)) {
                Text(text = stringResource(id = R.string.optimize_image))
            }

            AdBanner(modifier = Modifier.constrainAs(ref = adView) {
                bottom.linkTo(anchor = parent.bottom)
                start.linkTo(anchor = parent.start)
                end.linkTo(anchor = parent.end)
            })
        }
        Snackbar(message = "Imagine salvată în folderul Optimized images" , showSnackbar = uiState.showSaveSnackbar , onDismiss = { coroutineScope.launch { viewModel.updateShowSaveSnackbar(false) } })
    }
}

@Composable
fun ImageDisplay(viewModel : ImageOptimizerViewModel) {
    val state : State<ImageOptimizerState> = viewModel.uiState.collectAsState()
    val showCompressedImage : MutableState<Boolean> = remember { mutableStateOf(value = false) }

    LaunchedEffect(key1 = state.value.compressedImageUri) {
        if (state.value.compressedImageUri != null) {
            showCompressedImage.value = true
        }
    }

    Box(
        modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1f) , contentAlignment = Alignment.Center
    ) {
        if (state.value.isLoading) {
            CircularProgressIndicator()
        }
        else {
            if (showCompressedImage.value) {
                state.value.compressedImageUri?.let { imageUri ->
                    AsyncImage(
                        model = imageUri ,
                        contentDescription = "Selected Image" ,
                        modifier = Modifier.fillMaxSize() ,
                        contentScale = ContentScale.Crop ,
                    )
                }
            }
        }
    }
}