package com.d4rk.cleaner.ui.screens.analyze

import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.components.buttons.TwoRowButtons
import com.d4rk.cleaner.ui.screens.analyze.components.DeleteOrTrashConfirmation
import com.d4rk.cleaner.ui.screens.analyze.components.SelectAllComposable
import com.d4rk.cleaner.ui.screens.analyze.components.TabsContent
import com.d4rk.cleaner.ui.screens.home.HomeViewModel
import com.d4rk.cleaner.ui.screens.nofilesfound.NoFilesFoundScreen
import kotlinx.coroutines.CoroutineScope
import java.io.File

/**
 * Composable function that displays the Analyze screen.
 * This screen shows the results of a file scan, grouped by file type.
 * It allows users to select files and perform actions like moving to trash or permanently deleting them.
 *
 * @param imageLoader The ImageLoader instance used to load images.
 * @param view The underlying Android View instance.
 * @param viewModel The ViewModel that handles the logic of this screen.
 * @param data The UI data model for the home screen.
 * @author Mihai-Cristian Condrea
 */
@Composable
fun AnalyzeScreen(
    imageLoader : ImageLoader ,
    view : View ,
    viewModel : HomeViewModel ,
    data : UiHomeModel ,
) {
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val hasSelectedFiles : Boolean = data.analyzeState.selectedFilesCount > 0
    val isLoading : Boolean by viewModel.isLoading.collectAsState()
    val groupedFiles : Map<String , List<File>> = data.analyzeState.groupedFiles

    Column(
        modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
                .padding(all = 16.dp) , horizontalAlignment = Alignment.End
    ) {
        OutlinedCard(
            modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxWidth() ,
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                groupedFiles.isEmpty() -> {
                    NoFilesFoundScreen(viewModel = viewModel)
                }

                else -> {
                    TabsContent(
                        groupedFiles = groupedFiles , imageLoader = imageLoader , viewModel = viewModel , view = view , coroutineScope = coroutineScope , data = data
                    )
                }
            }
        }

        if (data.analyzeState.scannedFileList.isNotEmpty() || data.analyzeState.emptyFolderList.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth() ,
                verticalAlignment = Alignment.CenterVertically ,
                horizontalArrangement = Arrangement.SpaceBetween ,
            ) {
                val statusText : String = if (data.analyzeState.selectedFilesCount > 0) {
                    pluralStringResource(
                        id = R.plurals.status_selected_files , count = data.analyzeState.selectedFilesCount , data.analyzeState.selectedFilesCount
                    )
                }
                else {
                    stringResource(id = R.string.status_no_files_selected)
                }
                val statusColor : Color by animateColorAsState(
                    targetValue = if (data.analyzeState.selectedFilesCount > 0) {
                        MaterialTheme.colorScheme.primary
                    }
                    else {
                        MaterialTheme.colorScheme.secondary
                    } , animationSpec = tween() , label = "Selected Files Status Color Animation"
                )

                Text(
                    text = statusText , color = statusColor , modifier = Modifier.animateContentSize()
                )
                SelectAllComposable(viewModel = viewModel , view = view)
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
}