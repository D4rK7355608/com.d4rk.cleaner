package com.d4rk.cleaner.app.clean.home.ui

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHandler
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ui.UiAppManagerModel
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import com.d4rk.cleaner.app.clean.home.ui.components.ApkCleanerCard
import com.d4rk.cleaner.app.clean.analyze.ui.AnalyzeScreen
import com.d4rk.cleaner.app.clean.home.domain.actions.HomeEvent
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.UiHomeModel
import com.d4rk.cleaner.app.clean.home.ui.components.QuickScanSummaryCard
import com.d4rk.cleaner.app.clean.home.ui.components.WhatsAppCleanerCard
import com.d4rk.cleaner.app.clean.home.ui.components.ClipboardCleanerCard
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(paddingValues: PaddingValues, snackbarHostState: SnackbarHostState) {
    val context: Context = LocalContext.current
    val view: View = LocalView.current
    val viewModel: HomeViewModel = koinViewModel()
    val appManagerViewModel: AppManagerViewModel = koinViewModel()
    val uiState: UiStateScreen<UiHomeModel> by viewModel.uiState.collectAsState()
    val appManagerState: UiStateScreen<UiAppManagerModel> by appManagerViewModel.uiState.collectAsState()
    val whatsappSummary by viewModel.whatsAppMediaSummary.collectAsState()
    val clipboardText by viewModel.clipboardPreview.collectAsState()
    val clipboardSensitive by viewModel.clipboardDetectedSensitive.collectAsState()

    LaunchedEffect(key1 = true) {
        if (!PermissionsHelper.hasStoragePermissions(context)) {
            PermissionsHelper.requestStoragePermissions(context as Activity)
        }
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(SizeConstants.LargeSize),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        QuickScanSummaryCard(
                            cleanedSize = uiState.data?.storageInfo?.cleanedSpace ?: "",
                            freePercent = uiState.data?.storageInfo?.freeSpacePercentage ?: 0,
                            usedPercent = ((uiState.data?.storageInfo?.storageUsageProgress ?: 0f) * 100).toInt(),
                            progress = uiState.data?.storageInfo?.storageUsageProgress ?: 0f,
                            onQuickScanClick = { viewModel.onEvent(HomeEvent.ToggleAnalyzeScreen(true)) }
                        )
                        AnimatedVisibility(visible = appManagerState.data?.apkFiles?.isNotEmpty() == true) {
                            ApkCleanerCard(
                                apkFiles = appManagerState.data?.apkFiles ?: emptyList(),
                                onCleanClick = {
                                    val files = appManagerState.data?.apkFiles?.map { java.io.File(it.path) } ?: emptyList()
                                    viewModel.onCleanApks(files)
                                }
                            )
                        }
                        AnimatedVisibility(visible = whatsappSummary.hasData) {
                            WhatsAppCleanerCard(
                                mediaSummary = whatsappSummary,
                                onCleanClick = { viewModel.onEvent(HomeEvent.CleanWhatsAppFiles) }
                            )
                        }
                        AnimatedVisibility(visible = clipboardText != null || clipboardSensitive) {
                            ClipboardCleanerCard(
                                clipboardText = clipboardText,
                                onCleanClick = { viewModel.onClipboardClear() },
                                onSeeMoreClick = {}
                            )
                        }
                    }
                }
            }
        }
    }

    DefaultSnackbarHandler(screenState = uiState , snackbarHostState = snackbarHostState , getDismissEvent = { HomeEvent.DismissSnackbar } , onEvent = { viewModel.onEvent(event = it) })
}