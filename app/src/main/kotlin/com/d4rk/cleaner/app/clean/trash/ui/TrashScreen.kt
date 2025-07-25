package com.d4rk.cleaner.app.clean.trash.ui

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.analyze.ui.components.FilesByDateSection
import com.d4rk.cleaner.app.clean.scanner.ui.components.TwoRowButtons
import com.d4rk.cleaner.app.clean.trash.domain.actions.TrashEvent
import com.d4rk.cleaner.app.clean.trash.domain.data.model.ui.UiTrashModel
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(activity: TrashActivity) {

    val viewModel: TrashViewModel = koinViewModel()

    val view: View = LocalView.current
    val uiStateScreen: UiStateScreen<UiTrashModel> by viewModel.uiState.collectAsState()

    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.trash),
        onBackClicked = { activity.finish() }) { paddingValues ->
        ScreenStateHandler(screenState = uiStateScreen, onLoading = {
            LoadingScreen()
        }, onEmpty = {
            NoDataScreen(
                textMessage = R.string.trash_is_empty,
                icon = Icons.Outlined.RestoreFromTrash
            )
        }, onSuccess = { trashModel ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val (list, buttons) = createRefs()
                val enabled = trashModel.selectedFileCount > 0

                TrashItemsList(
                    modifier = Modifier.constrainAs(list) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(buttons.top)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                    trashFiles = trashModel.trashFiles,
                    uiState = trashModel,
                    viewModel = viewModel,
                    view = view,
                )

                TwoRowButtons(
                    modifier = Modifier
                        .padding(SizeConstants.LargeSize)
                        .constrainAs(buttons) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                    enabled = enabled,
                    onStartButtonClick = {
                        viewModel.onEvent(TrashEvent.RestoreSelectedFiles)
                    },
                    onStartButtonIcon = Icons.Outlined.Restore,
                    onStartButtonText = R.string.restore,
                    onEndButtonClick = {
                        viewModel.onEvent(TrashEvent.DeleteSelectedFilesPermanently)
                    },
                    onEndButtonIcon = Icons.Outlined.DeleteForever,
                    onEndButtonText = R.string.delete_forever,
                )
            }
        })
    }
}

@Composable
fun TrashItemsList(
    modifier: Modifier,
    trashFiles: List<File>,
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
        fileSelectionStates = uiState.fileSelectionStates.mapKeys { File(it.key) },
        onFileSelectionChange = { file, isChecked ->
            viewModel.onEvent(TrashEvent.OnFileSelectionChange(file, isChecked))
        },
        onDateSelectionChange = { files, checked ->
            files.forEach { file ->
                viewModel.onEvent(TrashEvent.OnFileSelectionChange(file, checked))
            }
        },
        view = view
    )
}