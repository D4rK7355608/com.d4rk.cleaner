package com.d4rk.cleaner.app.clean.scanner.ui

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Row
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ui.UiAppManagerModel
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import com.d4rk.cleaner.app.clean.analyze.ui.AnalyzeScreen
import com.d4rk.cleaner.app.clean.scanner.domain.actions.ScannerEvent
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.CleaningState
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.CleaningType
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.UiScannerModel
import com.d4rk.cleaner.app.clean.scanner.ui.components.ApkCleanerCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.CacheCleanerCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.ClipboardCleanerCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.ImageOptimizerCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.PromotedAppCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.QuickScanSummaryCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.WeeklyCleanStreakCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.WhatsAppCleanerCard
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.WhatsAppCleanerActivity
import com.d4rk.cleaner.app.images.picker.ui.ImagePickerActivity
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Composable
fun ScannerScreen(paddingValues: PaddingValues , snackbarHostState: SnackbarHostState) {
    val context: Context = LocalContext.current
    val view: View = LocalView.current
    val viewModel: ScannerViewModel = koinViewModel()
    val appManagerViewModel: AppManagerViewModel = koinViewModel()
    val uiState: UiStateScreen<UiScannerModel> by viewModel.uiState.collectAsState()
    val appManagerState: UiStateScreen<UiAppManagerModel> by appManagerViewModel.uiState.collectAsState()
    val whatsappSummary by viewModel.whatsAppMediaSummary.collectAsState()
    val clipboardText by viewModel.clipboardPreview.collectAsState()
    val streakDays by viewModel.cleanStreak.collectAsState()
    val showStreakCard by viewModel.showStreakCard.collectAsState()
    val streakHideUntil by viewModel.streakHideUntil.collectAsState()
    val streakDialogVisible = uiState.data?.isHideStreakDialogVisible == true
    val promotedApp = uiState.data?.promotedApp
    val mediumRectAdsConfig: AdsConfig = koinInject(qualifier = named(name = "banner_medium_rectangle"))
    val largeBannerAdsConfig: AdsConfig = koinInject(qualifier = named(name = "large_banner"))
    val bannerAdsConfig: AdsConfig = koinInject()

    val showApkCard = appManagerState.data?.apkFiles?.isNotEmpty() == true
    val showWhatsAppCard = whatsappSummary.hasData
    val showClipboardCard = !clipboardText.isNullOrBlank()

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
                    val cleanerCardsCount = listOf(showWhatsAppCard, showApkCard, showClipboardCard).count { it }

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
                            onQuickScanClick = { viewModel.onEvent(event = ScannerEvent.ToggleAnalyzeScreen(visible = true)) }
                        )

                        promotedApp?.let { app ->
                            PromotedAppCard(app = app)
                        }

                        if (showStreakCard) {
                            WeeklyCleanStreakCard(
                                streakDays = streakDays,
                                onDismiss = { viewModel.onEvent(ScannerEvent.SetHideStreakDialogVisibility(true)) }
                            )
                        } else if (streakHideUntil > System.currentTimeMillis()) {
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(SizeConstants.ExtraLargeSize)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SizeConstants.LargeSize),
                                    text = stringResource(id = R.string.streak_quiet_banner),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        if (cleanerCardsCount > 0) {
                            val topAdConfig = if (cleanerCardsCount > 1) largeBannerAdsConfig else bannerAdsConfig
                            AdBanner(
                                modifier = Modifier.padding(bottom = SizeConstants.MediumSize),
                                adsConfig = topAdConfig
                            )
                        }

                        if (showWhatsAppCard) {
                            AnimatedVisibility(visible = showWhatsAppCard) {
                                WhatsAppCleanerCard(
                                    mediaSummary = whatsappSummary,
                                    onCleanClick = {
                                        IntentsHelper.openActivity(
                                            context = context,
                                            activityClass = WhatsAppCleanerActivity::class.java
                                        )
                                    }
                                )
                            }
                        }

                        if (showApkCard) {
                            AnimatedVisibility(visible = showApkCard) {
                                val isCleaningApks = uiState.data?.analyzeState?.state == CleaningState.Cleaning &&
                                        uiState.data?.analyzeState?.cleaningType == CleaningType.DELETE &&
                                        uiState.data?.analyzeState?.isAnalyzeScreenVisible == false
                                ApkCleanerCard(
                                    apkFiles = appManagerState.data?.apkFiles ?: emptyList(),
                                    isLoading = isCleaningApks,
                                    onCleanClick = { selected ->
                                        val files = selected.map { java.io.File(it.path) }
                                        viewModel.onCleanApks(files)
                                    }
                                )
                            }
                        }

                        if (showClipboardCard) {
                            AnimatedVisibility(visible = showClipboardCard) {
                                ClipboardCleanerCard(
                                    clipboardText = clipboardText,
                                    onCleanClick = { viewModel.onClipboardClear() }
                                )
                            }
                        }

                        if (cleanerCardsCount > 0) {
                            val midAdConfig = if (cleanerCardsCount >= 2) mediumRectAdsConfig else bannerAdsConfig
                            AdBanner(
                                modifier = Modifier.padding(bottom = SizeConstants.MediumSize),
                                adsConfig = midAdConfig
                            )
                        }

                        ImageOptimizerCard(
                            onOptimizeClick = {
                                IntentsHelper.openActivity(
                                    context = context,
                                    activityClass = ImagePickerActivity::class.java
                                )
                            }
                        )

                        CacheCleanerCard(
                            onScanClick = {
                                viewModel.onEvent(ScannerEvent.CleanCache)
                            }
                        )

                        if (promotedApp == null || cleanerCardsCount >= 2) {
                            val endAdConfig = if (promotedApp == null) largeBannerAdsConfig else bannerAdsConfig
                            AdBanner(
                                modifier = Modifier.padding(bottom = SizeConstants.MediumSize),
                                adsConfig = endAdConfig
                            )
                        }

                        LargeVerticalSpacer()
                    }
                }
            }
        }
    }

    DefaultSnackbarHandler(screenState = uiState , snackbarHostState = snackbarHostState , getDismissEvent = { ScannerEvent.DismissSnackbar } , onEvent = { viewModel.onEvent(event = it) })

    if (streakDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ScannerEvent.SetHideStreakDialogVisibility(false)) },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize)) {
                    TextButton(onClick = { viewModel.onEvent(ScannerEvent.HideStreakForNow) }) {
                        Text(text = stringResource(id = R.string.hide_for_now))
                    }
                    TextButton(onClick = { viewModel.onEvent(ScannerEvent.HideStreakPermanently) }) {
                        Text(text = stringResource(id = R.string.dont_show_again))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(ScannerEvent.SetHideStreakDialogVisibility(false)) }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            title = { Text(text = stringResource(id = R.string.hide_clean_streak_title)) },
            text = { Text(text = stringResource(id = R.string.hide_clean_streak_message)) }
        )
    }
}