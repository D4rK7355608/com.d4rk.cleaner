package com.d4rk.cleaner.app.clean.analyze.ui

import android.view.View
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.analyze.ui.components.CleaningAnimationScreen
import com.d4rk.cleaner.app.clean.analyze.ui.components.StatusRowSelectAll
import com.d4rk.cleaner.app.clean.analyze.ui.components.dialogs.DeleteOrTrashConfirmation
import com.d4rk.cleaner.app.clean.analyze.ui.components.tabs.TabsContent
import com.d4rk.cleaner.app.clean.nofilesfound.ui.NoFilesFoundScreen
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.CleaningState
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.FileEntry
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.UiScannerModel
import com.d4rk.cleaner.app.clean.scanner.ui.ScannerViewModel
import com.d4rk.cleaner.app.clean.scanner.ui.components.TwoRowButtons
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnalyzeScreen(
    view: View,
    viewModel: ScannerViewModel,
    data: UiScannerModel,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val hasSelectedFiles: Boolean = data.analyzeState.selectedFilesCount > 0
    val groupedFiles: Map<String, List<FileEntry>> = data.analyzeState.groupedFiles

    Column(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .padding(vertical = SizeConstants.LargeSize), horizontalAlignment = Alignment.End
    ) {
        OutlinedCard(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
        ) {
            when (data.analyzeState.state) {

                CleaningState.Analyzing -> {
                    LoadingScreen()
                }

                CleaningState.Cleaning -> {
                    CleaningAnimationScreen()
                }

                CleaningState.ReadyToClean -> {
                    if (groupedFiles.isNotEmpty()) {
                        TabsContent(
                            groupedFiles = groupedFiles,
                            viewModel = viewModel,
                            view = view,
                            coroutineScope = coroutineScope,
                            data = data,
                        )
                    }
                }

                CleaningState.Result -> {
                    NoFilesFoundScreen(viewModel = viewModel)
                }

                CleaningState.Error -> {
                    if (groupedFiles.isEmpty()) {
                        NoFilesFoundScreen(viewModel = viewModel)
                    }
                }

                CleaningState.Idle -> {}
            }
        }
        if (groupedFiles.isNotEmpty()) {
            StatusRowSelectAll(data = data, view = view, onClickSelectAll = {
                viewModel.toggleSelectAllFiles()
            })
        }

        TwoRowButtons(
            modifier = Modifier,
            enabled = hasSelectedFiles,
            onStartButtonClick = {
                viewModel.setMoveToTrashConfirmationDialogVisibility(isVisible = true)
            },
            onStartButtonIcon = Icons.Outlined.Delete,
            onStartButtonText = R.string.move_to_trash,
            onEndButtonClick = {
                viewModel.setDeleteForeverConfirmationDialogVisibility(true)
            },
            onEndButtonIcon = Icons.Outlined.DeleteForever,
            onEndButtonText = R.string.delete_forever,
        )

        if (data.analyzeState.isDeleteForeverConfirmationDialogVisible || data.analyzeState.isMoveToTrashConfirmationDialogVisible) {
            DeleteOrTrashConfirmation(data = data, viewModel = viewModel)
        }
    }
}