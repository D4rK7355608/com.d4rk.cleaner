package com.d4rk.cleaner.ui.screens.imageoptimizer.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.components.ads.AdBanner
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.ImageOptimizerActivity
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.ui.components.animations.bounceClick
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton

@Composable
fun ImagePickerComposable(
    activity: ImagePickerActivity , viewModel: ImagePickerViewModel
) {
    val context: Context = LocalContext.current
    val view: View = LocalView.current
    val dataStore: DataStore = DataStore.getInstance(context = context)
    val adsState: State<Boolean> = dataStore.ads.collectAsState(initial = true)

    LaunchedEffect(key1 = viewModel.selectedImageUri) {
        viewModel.selectedImageUri?.let { uri ->
            val intent = Intent(context, ImageOptimizerActivity::class.java)
            intent.putExtra("selectedImageUri", uri.toString())
            activity.startActivity(intent)
            viewModel.setSelectedImageUri(null)
        }
    }

    LaunchedEffect(key1 = true) {
        if (!PermissionsUtils.hasMediaPermissions(context)) {
            PermissionsUtils.requestMediaPermissions(context as Activity)
        }
    }


    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.image_optimizer),
        onBackClicked = { activity.finish() }) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (fab: ConstrainedLayoutReference, adView: ConstrainedLayoutReference, imagePrompt: ConstrainedLayoutReference) = createRefs()

            Column(
                modifier = Modifier.constrainAs(imagePrompt) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.ImageSearch,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(id = R.string.summary_select_image))
            }

            ExtendedFloatingActionButton(modifier = Modifier
                .padding(16.dp)
                .bounceClick()
                .constrainAs(fab) {
                    end.linkTo(parent.end)
                    if (adsState.value) {
                        bottom.linkTo(adView.top)
                    } else {
                        bottom.linkTo(parent.bottom)
                    }
                }, text = { Text(text = stringResource(id = R.string.choose_image)) }, onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                activity.selectImage()
            }, icon = {
                Icon(
                    Icons.Outlined.AddPhotoAlternate, contentDescription = null
                )
            })

            AdBanner(modifier = Modifier.constrainAs(adView) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            } , dataStore = dataStore)
        }
    }
}