package com.d4rk.cleaner.app.clean.analyze.ui

import android.view.View
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
import coil3.ImageLoader
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.analyze.components.DeleteOrTrashConfirmation
import com.d4rk.cleaner.app.clean.analyze.components.StatusRowSelectAll
import com.d4rk.cleaner.app.clean.analyze.components.tabs.TabsContent
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.UiHomeModel
import com.d4rk.cleaner.app.clean.home.ui.HomeViewModel
import com.d4rk.cleaner.app.clean.home.ui.components.TwoRowButtons
import com.d4rk.cleaner.app.clean.nofilesfound.ui.NoFilesFoundScreen
import kotlinx.coroutines.CoroutineScope
import java.io.File

@Composable
fun AnalyzeScreen(
    imageLoader : ImageLoader ,
    view : View ,
    viewModel : HomeViewModel ,
    data : UiHomeModel ,
) {
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val hasSelectedFiles : Boolean = data.analyzeState.selectedFilesCount > 0
    val isLoading : Boolean = data.analyzeState.isAnalyzing
    val groupedFiles : Map<String , List<File>> = data.analyzeState.groupedFiles

    Column(
        modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
                .padding(all = SizeConstants.LargeSize) , horizontalAlignment = Alignment.End
    ) {
        OutlinedCard(
            modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxWidth() ,
        ) {
            when {

                isLoading -> {
                    LoadingScreen()
                }

                groupedFiles.isEmpty() -> {
                    NoFilesFoundScreen(viewModel = viewModel)
                }

                groupedFiles.isNotEmpty() -> {
                    TabsContent(groupedFiles = groupedFiles , imageLoader = imageLoader , viewModel = viewModel , view = view , coroutineScope = coroutineScope , data = data)
                }
            }
        }

        if (groupedFiles.isNotEmpty()) {
            StatusRowSelectAll(data = data , view = view , onClickSelectAll = {
                viewModel.toggleSelectAllFiles()
            })
        }

        TwoRowButtons(modifier = Modifier , enabled = hasSelectedFiles , onStartButtonClick = {
            viewModel.setMoveToTrashConfirmationDialogVisibility(isVisible = true)
        } , onStartButtonIcon = Icons.Outlined.Delete , onStartButtonText = R.string.move_to_trash , onEndButtonClick = {
            viewModel.setDeleteForeverConfirmationDialogVisibility(true)
        } , onEndButtonIcon = Icons.Outlined.DeleteForever , onEndButtonText = R.string.delete_forever , view = view)

        if (data.analyzeState.isDeleteForeverConfirmationDialogVisible || data.analyzeState.isMoveToTrashConfirmationDialogVisible) {
            DeleteOrTrashConfirmation(data = data , viewModel = viewModel)
        }
    }
}