package com.d4rk.cleaner.ui.imageoptimizer.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ads.BannerAdsComposable
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer.ImageOptimizerActivity
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.compose.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerComposable(
    activity : ImagePickerActivity , viewModel : ImagePickerViewModel
) {
    val context : Context = LocalContext.current
    val dataStore : DataStore = DataStore.getInstance(context)
    val scrollBehavior : TopAppBarScrollBehavior =
            TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val adsState : State<Boolean> = dataStore.ads.collectAsState(initial = true)

    LaunchedEffect(key1 = viewModel.selectedImageUri) {
        viewModel.selectedImageUri?.let { uri ->
            val intent = Intent(context , ImageOptimizerActivity::class.java)
            intent.putExtra("selectedImageUri" , uri.toString())
            activity.startActivity(intent)
            viewModel.setSelectedImageUri(null)
        }
    }

    LaunchedEffect(key1 = true) {
        if (! PermissionsUtils.hasMediaPermissions(context)) {
            PermissionsUtils.requestMediaPermissions(context as Activity)
        }
    }


    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.image_optimizer)) } ,
                       navigationIcon = {
                           IconButton(onClick = {
                               activity.finish()
                           }) {
                               Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null)
                           }
                       } ,
                       scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
        ) {
            val (fab : ConstrainedLayoutReference , adView : ConstrainedLayoutReference , imagePrompt : ConstrainedLayoutReference) = createRefs()

            Column(modifier = Modifier.constrainAs(imagePrompt) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            } ,
                   verticalArrangement = Arrangement.Center ,
                   horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.ImageSearch ,
                    contentDescription = null ,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.summary_select_image))
            }

            ExtendedFloatingActionButton(modifier = Modifier
                    .padding(16.dp)
                    .bounceClick()
                    .constrainAs(fab) {
                        end.linkTo(parent.end)
                        if (adsState.value) {
                            bottom.linkTo(adView.top)
                        }
                        else {
                            bottom.linkTo(parent.bottom)
                        }
                    } , text = { Text(stringResource(R.string.choose_image)) } , onClick = {
                activity.selectImage()
            } , icon = {
                Icon(
                    Icons.Outlined.AddPhotoAlternate , contentDescription = null
                )
            })

            BannerAdsComposable(modifier = Modifier.constrainAs(adView) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            } , dataStore = dataStore)
        }
    }
}