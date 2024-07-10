package com.d4rk.cleaner.ui.imageoptimizer.imagepicker

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ads.BannerAdsComposable
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer.ImageOptimizerActivity
import com.d4rk.cleaner.utils.compose.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerComposable(
    activity: ImagePickerActivity,
    viewModel: ImagePickerViewModel
) {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val adsState = dataStore.ads.collectAsState(initial = true)
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(
            title = { Text(stringResource(R.string.image_optimizer)) },
            navigationIcon = {
                IconButton(onClick = {
                    activity.finish()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            scrollBehavior = scrollBehavior
        )
    }) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (fab, adView) = createRefs()

            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .bounceClick()
                    .constrainAs(fab) {
                        end.linkTo(parent.end)
                        if (adsState.value) {
                            bottom.linkTo(adView.top)
                        } else {
                            bottom.linkTo(parent.bottom)
                        }
                    },
                text = { Text(stringResource(R.string.choose_image)) },
                onClick = {
                    activity.selectImage()
                },
                icon = {
                    Icon(
                        Icons.Outlined.ImageSearch, contentDescription = null
                    )
                }
            )

            BannerAdsComposable(
                modifier = Modifier
                    .constrainAs(adView) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                dataStore = dataStore
            )
        }
    }

    viewModel.selectedImageUri?.let { uri ->
        val intent = Intent(context, ImageOptimizerActivity::class.java)
        intent.putExtra("imageUri", uri.toString())
        activity.startActivity(intent)
        viewModel.setSelectedImageUri(null)
    }
}