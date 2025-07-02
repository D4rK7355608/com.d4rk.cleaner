package com.d4rk.cleaner.app.images.picker.ui

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
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.fab.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.images.compressor.ui.ImageOptimizerActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerComposable(
    activity: ImagePickerActivity,
    viewModel: ImagePickerViewModel
) {
    val context: Context = LocalContext.current
    val view: View = LocalView.current
    val uiState by viewModel.uiState.collectAsState()
    val isFabVisible = uiState.isFabVisible

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val isFabExtended = remember { mutableStateOf(true) }
    LaunchedEffect(scrollBehavior.state.contentOffset) {
        isFabExtended.value = scrollBehavior.state.contentOffset >= 0f
    }

    LaunchedEffect(uiState.selectedImageUri) {
        uiState.selectedImageUri?.let { uri ->
            val intent = Intent(context, ImageOptimizerActivity::class.java)
            intent.putExtra("selectedImageUri", uri.toString())
            activity.startActivity(intent)
            viewModel.setSelectedImageUri(null)
        }
    }

    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.image_optimizer),
        onBackClicked = {
            activity.finish()
            view.playSoundEffect(SoundEffectConstants.CLICK)
        },
        scrollBehavior = scrollBehavior,
        floatingActionButton = {
            AnimatedExtendedFloatingActionButton(
                visible = isFabVisible,
                onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    activity.selectImage()
                },
                text = { Text(text = stringResource(id = R.string.choose_image)) },
                icon = { Icon(
                    modifier = Modifier.size(SizeConstants.ButtonIconSize),
                    imageVector = Icons.Outlined.AddPhotoAlternate,
                    contentDescription = null
                ) },
                expanded = isFabExtended.value,
                modifier = Modifier.bounceClick()
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .safeDrawingPadding(),
            state = rememberLazyListState(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Icon(
                    imageVector = Icons.Outlined.ImageSearch,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp)
                )
                LargeVerticalSpacer()
                Text(text = stringResource(id = R.string.summary_select_image))
            }
        }
    }
}