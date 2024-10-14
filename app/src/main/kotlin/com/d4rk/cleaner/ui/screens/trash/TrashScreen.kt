package com.d4rk.cleaner.ui.screens.trash

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.screens.UiTrashModel
import com.d4rk.cleaner.ui.components.TwoRowButtons
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton
import com.d4rk.cleaner.ui.screens.analyze.FilesByDateSection
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrashScreen(activity: TrashActivity) {
    val viewModel: TrashViewModel = viewModel()
    val view: View = LocalView.current
    val context = LocalContext.current
    val uiState: UiTrashModel by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val enabled = uiState.selectedFileCount > 0
    val imageLoader: ImageLoader = remember {
        ImageLoader.Builder(context).memoryCache {
            MemoryCache.Builder(context).maxSizePercent(percent = 0.24).build()
        }.diskCache {
            DiskCache.Builder().directory(context.cacheDir.resolve(relative = "image_cache"))
                .maxSizePercent(percent = 0.02).build()
        }.build()
    }

    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.trash),
        onBackClicked = { activity.finish() }) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (uiState.trashFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.trash_is_empty))
                }
            } else {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val (list, buttons) = createRefs()

                    TrashItemsList(
                        modifier = Modifier.constrainAs(list) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(buttons.top)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        },
                        trashFiles = uiState.trashFiles,
                        imageLoader,
                        uiState = uiState,
                        viewModel = viewModel,
                        view = view,
                    )

                    TwoRowButtons(
                        modifier = Modifier
                            .padding(16.dp)
                            .constrainAs(buttons) {
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            },
                        enabled = enabled,
                        onStartButtonClick = {
                            viewModel.restoreFromTrash()
                        },
                        onStartButtonIcon = Icons.Outlined.Restore,
                        onStartButtonText = R.string.restore,

                        onEndButtonClick = {
                            viewModel.clean()
                        },
                        onEndButtonIcon = Icons.Outlined.DeleteForever,
                        onEndButtonText = R.string.delete_forever,
                        view = view,
                    )
                }
            }
        }
    }
}

@Composable
fun TrashItemsList(
    modifier: Modifier,
    trashFiles: List<File>,
    imageLoader: ImageLoader,
    uiState: UiTrashModel,
    viewModel: TrashViewModel,
    view: View,
) {
    val filesByDate = remember(trashFiles) {
        trashFiles.groupBy { file ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(file.lastModified()))
        }
    }

    FilesByDateSection(
        modifier = modifier,
        filesByDate = filesByDate,
        fileSelectionStates = uiState.fileSelectionStates,
        imageLoader = imageLoader,
        onFileSelectionChange = viewModel::onFileSelectionChange,
        view = view
    )
}