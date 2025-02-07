package com.d4rk.cleaner.ui.screens.home

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import com.d4rk.android.libs.apptoolkit.data.model.ui.error.UiErrorModel
import com.d4rk.android.libs.apptoolkit.ui.components.dialogs.ErrorAlertDialog
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.core.ui.components.progressbars.StorageProgressButton
import com.d4rk.cleaner.ui.screens.analyze.AnalyzeScreen
import com.d4rk.cleaner.utils.helpers.PermissionsHelper

@Composable
fun HomeScreen() {
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val viewModel : HomeViewModel = viewModel()
    val uiState : com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel by viewModel.uiState.collectAsState()
    val uiErrorModel : UiErrorModel by viewModel.uiErrorModel.collectAsState()
    val imageLoader : ImageLoader = remember {
        ImageLoader.Builder(context = context).memoryCache {
            MemoryCache.Builder().maxSizePercent(context = context , percent = 0.24).build()
        }.diskCache {
            DiskCache.Builder().directory(directory = context.cacheDir.resolve(relative = "image_cache")).maxSizePercent(percent = 0.02).build()
        }.build()
    }

    LaunchedEffect(key1 = Unit) {
        if (! PermissionsHelper.hasStoragePermissions(context = context)) {
            PermissionsHelper.requestStoragePermissions(activity = context as Activity)
        }
    }

    if (uiErrorModel.showErrorDialog) {
        ErrorAlertDialog(errorMessage = uiErrorModel.errorMessage , onDismiss = { viewModel.dismissErrorDialog() })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth()
        ) {

            if (! uiState.analyzeState.isAnalyzeScreenVisible) {
                StorageProgressButton(progress = uiState.storageInfo.storageUsageProgress , modifier = Modifier
                        .align(alignment = Alignment.TopCenter)
                        .offset(y = 98.dp) , onClick = {
                    viewModel.analyze()
                })

                ExtraStorageInfo(
                    modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp) ,
                    cleanedSpace = uiState.storageInfo.cleanedSpace ,
                    freeSpace = "${uiState.storageInfo.freeSpacePercentage} %" ,
                )
            }

            Crossfade(
                targetState = uiState.analyzeState.isAnalyzeScreenVisible , animationSpec = tween(durationMillis = 300) , label = ""
            ) { showCleaningComposable ->
                if (showCleaningComposable) {
                    key(uiState.analyzeState.fileTypesData) {
                        AnalyzeScreen(
                            imageLoader = imageLoader , view = view , viewModel = viewModel , data = uiState
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExtraStorageInfo(
    modifier : Modifier = Modifier ,
    cleanedSpace : String ,
    freeSpace : String ,
) {
    Row(
        modifier = modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
                .padding(horizontal = 16.dp , vertical = 8.dp) , horizontalArrangement = Arrangement.SpaceAround , verticalAlignment = Alignment.CenterVertically
    ) {
        InfoColumn(
            title = stringResource(id = R.string.cleaned_space) , value = cleanedSpace , modifier = Modifier.weight(weight = 1f)
        )

        VerticalDivider()

        InfoColumn(
            title = stringResource(id = R.string.free_space) , value = freeSpace , modifier = Modifier.weight(weight = 1f)
        )
    }
}

@Composable
fun InfoColumn(
    title : String , value : String , modifier : Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally , modifier = modifier
    ) {
        Text(text = title , style = MaterialTheme.typography.bodySmall , modifier = Modifier.basicMarquee())
        Text(
            text = value , style = MaterialTheme.typography.bodyMedium , maxLines = 2 , overflow = TextOverflow.Ellipsis
        )
    }
}