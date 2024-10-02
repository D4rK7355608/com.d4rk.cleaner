package com.d4rk.cleaner.ui.screens.trash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.screens.UiTrashModel
import com.d4rk.cleaner.ui.components.TwoRowButtons
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton
import com.d4rk.cleaner.ui.screens.home.FilesByDateSection
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrashScreen(activity : TrashActivity) {
    val viewModel : TrashViewModel = viewModel()
    val context = LocalContext.current
    val uiState : UiTrashModel by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val enabled = uiState.selectedFileCount > 0
    val imageLoader : ImageLoader = remember {
        ImageLoader.Builder(context).memoryCache {
            MemoryCache.Builder(context).maxSizePercent(percent = 0.24).build()
        }.diskCache {
            DiskCache.Builder().directory(context.cacheDir.resolve(relative = "image_cache"))
                    .maxSizePercent(percent = 0.02).build()
        }.build()
    }

    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.trash) ,
        onBackClicked = { activity.finish() }) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) ,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else {
            if (uiState.trashFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues) ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.trash_is_empty))
                }
            }
            else {
                Column(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                ) {
                    TrashItemsList(
                        trashFiles = uiState.trashFiles ,
                        imageLoader ,
                        uiState = uiState ,
                    )

                    TwoRowButtons(enabled = enabled , onStartButtonClick = {
                        viewModel.restoreFromTrash()
                    } , onStartButtonIcon = Icons.Outlined.Delete , onEndButtonClick = {
                        // TODO:
                    } , onEndButtonIcon = Icons.Outlined.DeleteForever)
                }
            }
        }
    }
}

@Composable
fun TrashItemsList(
    trashFiles : List<File> ,
    imageLoader : ImageLoader ,
    uiState : UiTrashModel ,
) {
    val filesByDate = remember(trashFiles) {
        trashFiles.groupBy { file ->
            SimpleDateFormat("yyyy-MM-dd" , Locale.getDefault()).format(Date(file.lastModified()))
        }
    }

    FilesByDateSection(
        filesByDate = filesByDate ,
        fileSelectionStates = uiState.fileSelectionStates ,
        imageLoader = imageLoader
    )
}