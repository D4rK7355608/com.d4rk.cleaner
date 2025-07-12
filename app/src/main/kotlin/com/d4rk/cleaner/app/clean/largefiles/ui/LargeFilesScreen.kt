package com.d4rk.cleaner.app.clean.largefiles.ui

import android.view.View
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.analyze.components.FilesByDateSection
import com.d4rk.cleaner.app.clean.largefiles.domain.actions.LargeFilesEvent
import com.d4rk.cleaner.app.clean.largefiles.domain.data.model.ui.UiLargeFilesModel
import com.d4rk.cleaner.app.clean.scanner.ui.components.TwoRowButtons
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeFilesScreen(activity: LargeFilesActivity) {
    val viewModel: LargeFilesViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val view: View = LocalView.current

    LargeTopAppBarWithScaffold(title = stringResource(id = R.string.large_files), onBackClicked = { activity.finish() }) { padding ->
        ScreenStateHandler(screenState = uiState) { data: UiLargeFilesModel ->
            ConstraintLayout(modifier = Modifier.padding(padding)) {
                val (list, buttons) = createRefs()
                val enabled = data.selectedFileCount > 0
                val filesByDate = data.files.groupBy { file ->
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(file.lastModified()))
                }
                FilesByDateSection(
                    modifier = Modifier.constrainAs(list) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(buttons.top)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                    filesByDate = filesByDate,
                    fileSelectionStates = data.fileSelectionStates,
                    onFileSelectionChange = { file, checked ->
                        viewModel.onEvent(LargeFilesEvent.OnFileSelectionChange(file, checked))
                    },
                    onDateSelectionChange = { files, checked ->
                        files.forEach { viewModel.onEvent(LargeFilesEvent.OnFileSelectionChange(it, checked)) }
                    },
                    view = view
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
                        viewModel.onEvent(LargeFilesEvent.DeleteSelectedFiles)
                    },
                    onStartButtonIcon = Icons.Outlined.DeleteForever,
                    onStartButtonText = R.string.delete_forever,
                    onEndButtonClick = {},
                    onEndButtonIcon = Icons.Outlined.Folder,
                    onEndButtonText = R.string.empty_folders
                )
            }
        }
    }
}
