package com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ads.BannerAdsComposable
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer.tabs.FileSizeScreen
import com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer.tabs.ManualModeScreen
import com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer.tabs.QuickCompressScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class , ExperimentalFoundationApi::class)
@Composable
fun ImageOptimizerComposable(
    activity : ImageOptimizerActivity ,
    viewModel : ImageOptimizerViewModel
) {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val adsState = dataStore.ads.collectAsState(initial = true)
    val tabs = listOf(
        stringResource(R.string.quick_compress) ,
        stringResource(R.string.file_size) ,
        stringResource(R.string.manual) ,
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.image_optimizer)) } ,
                       navigationIcon = {
                           IconButton(onClick = {
                               activity.finish()
                           }) {
                               Icon(
                                   Icons.AutoMirrored.Filled.ArrowBack ,
                                   contentDescription = null
                               )
                           }
                       } ,
                       scrollBehavior = scrollBehavior
        )
    }) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
        ) {
            val (imageCardView , tabLayout , viewPager , compressButton , adView) = createRefs()

            Card(
                modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(imageCardView) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(tabLayout.top)
                        }
                        .padding(24.dp) ,
            ) {
                ImageDisplay(viewModel)
            }

            TabRow(selectedTabIndex = pagerState.currentPage ,
                   modifier = Modifier.constrainAs(tabLayout) {
                               top.linkTo(imageCardView.bottom)
                               start.linkTo(parent.start)
                               end.linkTo(parent.end)
                           }) {
                tabs.forEachIndexed { index , title ->
                    Tab(text = { Text(title) } ,
                        selected = pagerState.currentPage == index ,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        })
                }
            }

            HorizontalPager(state = pagerState , modifier = Modifier.constrainAs(viewPager) {
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

            OutlinedButton(onClick = {

            } , modifier = Modifier
                    .constrainAs(compressButton) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        if (adsState.value) {
                            bottom.linkTo(adView.top)
                        }
                        else {
                            bottom.linkTo(parent.bottom)
                        }
                    }
                    .padding(12.dp)) {
                Text(stringResource(R.string.optimize_image))
            }

            BannerAdsComposable(
                modifier = Modifier.constrainAs(adView) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        } , dataStore = dataStore
            )
        }
    }
}


@Composable
fun ImageDisplay(viewModel : ImageOptimizerViewModel) {
    val state = viewModel.uiState.collectAsState()
    val showCompressedImage = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = state.value.compressedImageUri) {
        if (state.value.compressedImageUri != null) {
            showCompressedImage.value = true
        }
    }

    Box(
        modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) , contentAlignment = Alignment.Center
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