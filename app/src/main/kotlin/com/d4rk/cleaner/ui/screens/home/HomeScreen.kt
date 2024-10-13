package com.d4rk.cleaner.ui.screens.home

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.d4rk.cleaner.data.model.ui.error.UiErrorModel
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.components.CircularDeterminateIndicator
import com.d4rk.cleaner.ui.components.dialogs.ErrorAlertDialog
import com.d4rk.cleaner.utils.PermissionsUtils

@Composable
fun HomeScreen() {
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val viewModel : HomeViewModel = viewModel()


    val uiState : UiHomeModel by viewModel.uiState.collectAsState()
    val uiErrorModel : UiErrorModel by viewModel.uiErrorModel.collectAsState()
    val imageLoader : ImageLoader = remember {
        ImageLoader.Builder(context).memoryCache {
            MemoryCache.Builder(context).maxSizePercent(percent = 0.24).build()
        }.diskCache {
            DiskCache.Builder().directory(context.cacheDir.resolve(relative = "image_cache"))
                    .maxSizePercent(percent = 0.02).build()
        }.build()
    }

    LaunchedEffect(Unit) {
       // viewModel.populateFileTypesData()

        // Permissions
        if (! PermissionsUtils.hasStoragePermissions(context)) {
            PermissionsUtils.requestStoragePermissions(context as Activity)
        }
    }

    if (uiErrorModel.showErrorDialog) {
        ErrorAlertDialog(errorMessage = uiErrorModel.errorMessage ,
                         onDismiss = { viewModel.dismissErrorDialog() })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth()
        ) {

            if (! uiState.analyzeState.isAnalyzeScreenVisible) {
                CircularDeterminateIndicator(progress = uiState.storageUsageProgress ,
                                             modifier = Modifier
                                                     .align(Alignment.TopCenter)
                                                     .offset(y = 98.dp) ,
                                             onClick = {
                                                 viewModel.analyze()
                                             })
            }

            Crossfade(
                targetState = uiState.analyzeState.isAnalyzeScreenVisible ,
                animationSpec = tween(durationMillis = 300) ,
                label = ""
            ) { showCleaningComposable ->
                if (showCleaningComposable) {
                    key(uiState.analyzeState.fileTypesData) {
                        AnalyzeScreen(
                            imageLoader = imageLoader ,
                            view = view ,
                            viewModel = viewModel ,
                            data = uiState
                        )
                    }
                }
            }
        }
    }
}