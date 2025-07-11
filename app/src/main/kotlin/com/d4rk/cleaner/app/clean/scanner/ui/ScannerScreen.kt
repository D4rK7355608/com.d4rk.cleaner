package com.d4rk.cleaner.app.clean.scanner.ui

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import com.d4rk.android.libs.apptoolkit.core.ui.effects.LifecycleEventsEffect
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHandler
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.app.clean.analyze.ui.AnalyzeScreen
import com.d4rk.cleaner.app.clean.dashboard.ui.ScannerDashboardScreen
import com.d4rk.cleaner.app.clean.scanner.domain.actions.ScannerEvent
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.UiScannerModel
import com.d4rk.cleaner.app.clean.scanner.ui.components.dialogs.HideStreakAlertDialog
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScannerScreen(paddingValues: PaddingValues , snackbarHostState: SnackbarHostState) {
    val context: Context = LocalContext.current
    val view: View = LocalView.current
    val viewModel: ScannerViewModel = koinViewModel()
    val uiState: UiStateScreen<UiScannerModel> by viewModel.uiState.collectAsState()

    val streakDialogVisible = uiState.data?.isHideStreakDialogVisible == true

    LaunchedEffect(key1 = true) {
        if (!PermissionsHelper.hasStoragePermissions(context)) {
            PermissionsHelper.requestStoragePermissions(context as Activity)
        }
    }

    LifecycleEventsEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onEvent(ScannerEvent.RefreshData)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = SizeConstants.LargeSize)
    ) {
        Box(
            modifier = Modifier
                .weight(4f)
                .fillMaxWidth()
        ) {
            Crossfade(
                targetState = uiState.data?.analyzeState?.isAnalyzeScreenVisible,
                animationSpec = tween(durationMillis = 300),
                label = ""
            ) { showAnalyze ->
                if (showAnalyze == true) {
                    uiState.data?.let { data ->
                        key(data.analyzeState.fileTypesData) {
                            AnalyzeScreen(
                                view = view,
                                viewModel = viewModel,
                                data = data
                            )
                        }
                    }
                } else {
                    ScannerDashboardScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    DefaultSnackbarHandler(screenState = uiState , snackbarHostState = snackbarHostState , getDismissEvent = { ScannerEvent.DismissSnackbar } , onEvent = { viewModel.onEvent(event = it) })

    if (streakDialogVisible) {
        HideStreakAlertDialog(
            onHideForNow = { viewModel.onEvent(ScannerEvent.HideStreakForNow) },
            onHidePermanently = { viewModel.onEvent(ScannerEvent.HideStreakPermanently) },
            onDismiss = { viewModel.onEvent(ScannerEvent.SetHideStreakDialogVisibility(false)) }
        )
    }
}