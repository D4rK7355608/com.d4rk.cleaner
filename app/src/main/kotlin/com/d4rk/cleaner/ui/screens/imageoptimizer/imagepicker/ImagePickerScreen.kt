package com.d4rk.cleaner.ui.screens.imageoptimizer.imagepicker

import android.content.Context
import android.content.Intent
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.components.ads.AdBanner
import com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer.ImageOptimizerActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerComposable(
    activity : ImagePickerActivity , viewModel : ImagePickerViewModel
) {
    val scrollBehavior : TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val isFabVisible : Boolean by viewModel.isFabVisible.collectAsState()

    val isFabExtended : MutableState<Boolean> = remember { mutableStateOf(value = true) }
    LaunchedEffect(key1 = scrollBehavior.state.contentOffset) {
        isFabExtended.value = scrollBehavior.state.contentOffset >= 0f
    }

    LaunchedEffect(key1 = viewModel.selectedImageUri) {
        viewModel.selectedImageUri?.let { uri ->
            val intent = Intent(context , ImageOptimizerActivity::class.java)
            intent.putExtra("selectedImageUri" , uri.toString())
            activity.startActivity(intent)
            viewModel.setSelectedImageUri(null)
        }
    }

    val scrollBehaviorState : TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())

    Scaffold(modifier = Modifier.nestedScroll(connection = scrollBehaviorState.nestedScrollConnection) , topBar = {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.image_optimizer)) } ,
            navigationIcon = {
                IconButton(modifier = Modifier.bounceClick() , onClick = {
                    activity.finish()
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null)
                }
            } ,
            scrollBehavior = scrollBehaviorState ,
        )
    } , floatingActionButton = {
        AnimatedExtendedFloatingActionButton(visible = isFabVisible , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            activity.selectImage()
        } , text = { Text(text = stringResource(id = R.string.choose_image)) } , icon = {
            Icon(
                imageVector = Icons.Outlined.AddPhotoAlternate , contentDescription = null
            )
        } , expanded = isFabExtended.value , modifier = Modifier.bounceClick())
    } , bottomBar = {
        AdBanner()
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize()
                    .safeDrawingPadding() , state = rememberLazyListState() , horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.Center
        ) {
            item {
                Icon(
                    imageVector = Icons.Outlined.ImageSearch , contentDescription = null , modifier = Modifier.size(size = 72.dp)
                )
                LargeVerticalSpacer()
                Text(text = stringResource(id = R.string.summary_select_image))
            }
        }
    }
}