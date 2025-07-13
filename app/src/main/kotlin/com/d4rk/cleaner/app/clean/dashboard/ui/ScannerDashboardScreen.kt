package com.d4rk.cleaner.app.clean.dashboard.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import kotlin.collections.buildList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.animations.rememberAnimatedVisibilityState
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.animateVisibility
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ui.UiAppManagerModel
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import com.d4rk.cleaner.app.clean.scanner.domain.actions.ScannerEvent
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.CleaningState
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.CleaningType
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.UiScannerModel
import com.d4rk.cleaner.app.clean.scanner.ui.ScannerViewModel
import com.d4rk.cleaner.app.clean.scanner.ui.components.ApkCleanerCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.CacheCleanerCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.ClipboardCleanerCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.ImageOptimizerCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.PromotedAppCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.QuickScanSummaryCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.WeeklyCleanStreakCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.WhatsAppCleanerCard
import com.d4rk.cleaner.app.clean.scanner.ui.components.LargeFilesCard
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.WhatsAppCleanerActivity
import com.d4rk.cleaner.app.images.picker.ui.ImagePickerActivity
import com.d4rk.cleaner.app.clean.largefiles.ui.LargeFilesActivity
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named
import java.io.File

@Composable
fun ScannerDashboardScreen(
    uiState : UiStateScreen<UiScannerModel> ,
    viewModel : ScannerViewModel ,
) {
    val appManagerViewModel: AppManagerViewModel = koinViewModel()
    val context : Context = LocalContext.current

    val promotedApp = uiState.data?.promotedApp
    val mediumRectAdsConfig: AdsConfig = koinInject(qualifier = named(name = "banner_medium_rectangle"))
    val largeBannerAdsConfig: AdsConfig = koinInject(qualifier = named(name = "large_banner"))
    val leaderboard: AdsConfig = koinInject(qualifier = named(name = "leaderboard"))
    val bannerAdsConfig: AdsConfig = koinInject()

    val appManagerState: UiStateScreen<UiAppManagerModel> by appManagerViewModel.uiState.collectAsState()
    val whatsappSummary by viewModel.whatsAppMediaSummary.collectAsState()
    val whatsappLoaded by viewModel.whatsAppMediaLoaded.collectAsState()
    val whatsappInstalled by viewModel.isWhatsAppInstalled.collectAsState()
    val clipboardText by viewModel.clipboardPreview.collectAsState()
    val largeFiles by viewModel.largestFiles.collectAsState()
    val streakDays by viewModel.cleanStreak.collectAsState()
    val showStreakCard by viewModel.showStreakCard.collectAsState()
    val streakHideUntil by viewModel.streakHideUntil.collectAsState()

    val showApkCard by remember(appManagerState) {
        derivedStateOf {
            appManagerState.data?.apkFilesLoading == false &&
                    appManagerState.data?.apkFiles?.isNotEmpty() == true
        }
    }
    val showWhatsAppCard by remember(whatsappLoaded, whatsappInstalled, whatsappSummary) {
        derivedStateOf {
            whatsappLoaded && whatsappInstalled && whatsappSummary.hasData
        }
    }
    val showClipboardCard by remember(clipboardText) {
        derivedStateOf { !clipboardText.isNullOrBlank() }
    }
    val showLargeFilesCard by remember(largeFiles) {
        derivedStateOf { largeFiles.isNotEmpty() }
    }

    val dataLoaded = appManagerState.data?.apkFilesLoading == false && whatsappLoaded
    val cleanerCardsCount = if (dataLoaded) {
        listOf(showWhatsAppCard, showApkCard, showClipboardCard, showLargeFilesCard).count { it }
    } else 0

    val listState: LazyListState = rememberLazyListState()

    // Pre-compute ad configurations so they do not change while the UI is building
    val topAdConfig = remember(cleanerCardsCount) {
        if (cleanerCardsCount > 1) mediumRectAdsConfig else largeBannerAdsConfig
    }
    val midAdConfig = remember(cleanerCardsCount) {
        if (cleanerCardsCount >= 2) mediumRectAdsConfig else largeBannerAdsConfig
    }
    val endAdConfig = remember(cleanerCardsCount, promotedApp) {
        if (promotedApp == null) bannerAdsConfig else leaderboard
    }

    val showAdTop = dataLoaded && cleanerCardsCount > 0
    val showAdMid = dataLoaded && cleanerCardsCount > 0
    val showAdEnd = dataLoaded && (promotedApp == null || cleanerCardsCount >= 1)

    val itemsSize: Int = remember(
        showAdTop,
        showAdMid,
        showAdEnd,
        showStreakCard,
        streakHideUntil,
        showWhatsAppCard,
        showApkCard,
        showClipboardCard,
        promotedApp
    ) {
        buildList {
            // Quick scan card
            add(true)

            // Streak card or quiet banner
            if (showStreakCard || streakHideUntil > System.currentTimeMillis()) add(true)

            // Top ad
            if (showAdTop) add(true)

            // Cleaner cards
            if (showWhatsAppCard) add(true)
            if (showApkCard) add(true)
            if (showClipboardCard) add(true)
            if (showLargeFilesCard) add(true)

            // Middle ad
            if (showAdMid) add(true)

            // Always visible cleaner options
            add(true) // image optimizer
            add(true) // cache cleaner

            // Promoted app card
            if (promotedApp != null) add(true)

            // End ad
            if (showAdEnd) add(true)
        }.size
    }

    val (visibilityStates: SnapshotStateList<Boolean>) = rememberAnimatedVisibilityState(
        listState = listState,
        itemCount = itemsSize
    )

    var itemIndex = 0
    val nextIndex: () -> Int = { itemIndex++ }

    Column(
        modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) , verticalArrangement = Arrangement.spacedBy(SizeConstants.LargeSize) , horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val quickScanIndex = nextIndex()
        AnimatedVisibility(
            visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            QuickScanSummaryCard(
                modifier = Modifier.animateVisibility(
                    visible = visibilityStates.getOrElse(index = quickScanIndex) { false },
                    index = quickScanIndex
                ).animateContentSize(),
                cleanedSize = uiState.data?.storageInfo?.cleanedSpace ?: "" ,
                freePercent = uiState.data?.storageInfo?.freeSpacePercentage ?: 0 ,
                usedPercent = ((uiState.data?.storageInfo?.storageUsageProgress ?: 0f) * 100).toInt() ,
                progress = uiState.data?.storageInfo?.storageUsageProgress ?: 0f ,
                onQuickScanClick = { viewModel.onEvent(event = ScannerEvent.ToggleAnalyzeScreen(visible = true)) })
        }
        if (showStreakCard) {
            AnimatedVisibility(
                visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val streakIndex = nextIndex()
                WeeklyCleanStreakCard(
                    modifier = Modifier.animateVisibility(
                        visible = visibilityStates.getOrElse(index = streakIndex) { false },
                        index = streakIndex
                    ).animateContentSize(),
                    streakDays = streakDays , onDismiss = { viewModel.onEvent(ScannerEvent.SetHideStreakDialogVisibility(true)) })
            }
        }
        else if (streakHideUntil > System.currentTimeMillis()) {
            AnimatedVisibility(
                visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth() , shape = RoundedCornerShape(SizeConstants.ExtraLargeSize)
                ) {
                    val streakQuietIndex = nextIndex()
                    Text(
                        modifier = Modifier.animateVisibility(
                            visible = visibilityStates.getOrElse(index = streakQuietIndex) { false },
                            index = streakQuietIndex
                        ).fillMaxWidth()
                                .padding(SizeConstants.LargeSize) , text = stringResource(id = R.string.streak_quiet_banner) , style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showAdTop,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            AdBanner(
                modifier = Modifier.padding(bottom = SizeConstants.MediumSize),
                adsConfig = topAdConfig
            )
        }

        if (showWhatsAppCard) {
            AnimatedVisibility(
                visible = showWhatsAppCard,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val whatsappIndex = nextIndex()
                WhatsAppCleanerCard(
                    modifier = Modifier.animateVisibility(
                        visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false &&
                                visibilityStates.getOrElse(index = whatsappIndex) { false },
                        index = whatsappIndex
                    ).animateContentSize(),
                    mediaSummary = whatsappSummary , onCleanClick = {
                        IntentsHelper.openActivity(
                            context = context , activityClass = WhatsAppCleanerActivity::class.java
                        )
                    })
            }
        }

        if (showApkCard) {
            AnimatedVisibility(
                visible = showApkCard,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val isCleaningApks = uiState.data?.analyzeState?.state == CleaningState.Cleaning && uiState.data?.analyzeState?.cleaningType == CleaningType.DELETE && uiState.data?.analyzeState?.isAnalyzeScreenVisible == false
                val apkIndex = nextIndex()
                ApkCleanerCard(
                    modifier = Modifier.animateVisibility(
                        visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false &&
                                visibilityStates.getOrElse(index = apkIndex) { false },
                        index = apkIndex
                    ).animateContentSize(),
                    apkFiles = appManagerState.data?.apkFiles ?: emptyList() , isLoading = isCleaningApks , onCleanClick = { selected ->
                        val files = selected.map { File(it.path) }
                        viewModel.onCleanApks(files)
                    })
            }
        }

        if (showClipboardCard) {
            AnimatedVisibility(
                visible = showClipboardCard,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val clipboardIndex = nextIndex()
                ClipboardCleanerCard(
                    modifier = Modifier.animateVisibility(
                        visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false &&
                                visibilityStates.getOrElse(index = clipboardIndex) { false },
                        index = clipboardIndex
                    ).animateContentSize(),
                    clipboardText = clipboardText , onCleanClick = { viewModel.onClipboardClear() })
            }
        }

        if (showLargeFilesCard) {
            AnimatedVisibility(
                visible = showLargeFilesCard,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val largeFilesIndex = nextIndex()
                LargeFilesCard(
                    modifier = Modifier.animateVisibility(
                        visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false &&
                                visibilityStates.getOrElse(index = largeFilesIndex) { false },
                        index = largeFilesIndex
                    ).animateContentSize(),
                    files = largeFiles,
                    onOpenClick = {
                        IntentsHelper.openActivity(
                            context = context,
                            activityClass = LargeFilesActivity::class.java
                        )
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = showAdMid,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            AdBanner(
                modifier = Modifier.padding(bottom = SizeConstants.MediumSize),
                adsConfig = midAdConfig
            )
        }

        val imageOptimizerIndex = nextIndex()
        AnimatedVisibility(
            visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ImageOptimizerCard(
                modifier = Modifier.animateVisibility(
                    visible = visibilityStates.getOrElse(index = imageOptimizerIndex) { false },
                    index = imageOptimizerIndex
                ).animateContentSize(),
                onOptimizeClick = {
                    IntentsHelper.openActivity(
                        context = context , activityClass = ImagePickerActivity::class.java
                    )
                })
        }

        val cacheIndex = nextIndex()
        AnimatedVisibility(
            visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CacheCleanerCard(
                modifier = Modifier.animateVisibility(
                    visible = visibilityStates.getOrElse(index = cacheIndex) { false },
                    index = cacheIndex
                ).animateContentSize(),
                onScanClick = {
                    viewModel.onEvent(ScannerEvent.CleanCache)
                })
        }

        promotedApp?.let { app ->
            AnimatedVisibility(
                visible = uiState.data?.analyzeState?.isAnalyzeScreenVisible == false,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val promotedIndex = nextIndex()
                PromotedAppCard(modifier = Modifier.animateVisibility(
                    visible = visibilityStates.getOrElse(index = promotedIndex) { false },
                    index = promotedIndex
                ).animateContentSize(),app = app)
            }
        }

        AnimatedVisibility(
            visible = showAdEnd,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            AdBanner(
                modifier = Modifier.padding(bottom = SizeConstants.MediumSize),
                adsConfig = endAdConfig
            )
        }
        LargeVerticalSpacer()
    }
}