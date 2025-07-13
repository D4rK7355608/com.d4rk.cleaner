package com.d4rk.cleaner.app.clean.largefiles.ui

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.OutlinedIconButtonWithText
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.analyze.ui.components.FilesByDateSection
import com.d4rk.cleaner.app.clean.largefiles.domain.actions.LargeFilesEvent
import com.d4rk.cleaner.app.clean.largefiles.domain.data.model.ui.UiLargeFilesModel
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeFilesScreen(activity: LargeFilesActivity) {
    val viewModel: LargeFilesViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val view: View = LocalView.current

    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.large_files),
        onBackClicked = { activity.finish() }) { padding ->
        ScreenStateHandler(
            screenState = uiState,
            onLoading = {
                LoadingScreen()
                println("LoadingScreen")
            },
            onEmpty = {
                println("onEmpty")
                NoDataScreen(textMessage = R.string.no_large_files, icon = Icons.Outlined.Folder)
            },
            onSuccess = { data: UiLargeFilesModel ->
                println("onSuccess")
                ConstraintLayout(modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()) {
                    val (list, buttons) = createRefs()
                    val enabled = data.selectedFileCount > 0
                    val filesByDate = data.files.groupBy { file ->
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(Date(file.lastModified()))
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
                            files.forEach {
                                viewModel.onEvent(
                                    LargeFilesEvent.OnFileSelectionChange(
                                        it,
                                        checked
                                    )
                                )
                            }
                        },
                        view = view
                    )

                    OutlinedIconButtonWithText(
                        modifier = Modifier
                            .padding(all = SizeConstants.LargeSize)
                            .constrainAs(buttons) {
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            },
                        onClick = {
                            viewModel.onEvent(event = LargeFilesEvent.DeleteSelectedFiles)
                        },
                        enabled = enabled,
                        icon = Icons.Outlined.DeleteForever,
                        iconContentDescription = stringResource(id = R.string.move_to_trash_icon_description),
                        label = stringResource(id = R.string.delete_forever)
                    )
                }
            })
    }
}