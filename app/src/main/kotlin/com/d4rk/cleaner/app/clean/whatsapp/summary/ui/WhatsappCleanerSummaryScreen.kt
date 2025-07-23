package com.d4rk.cleaner.app.clean.whatsapp.summary.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.fab.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.effects.LifecycleEventsEffect
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.WhatsAppDetailsActivity
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.actions.WhatsAppCleanerEvent
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.DirectoryItem
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.UiWhatsAppCleanerModel
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components.CleanerInfoCard
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components.DirectoryGrid
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components.WhatsAppEmptyState
import com.d4rk.cleaner.app.clean.whatsapp.utils.constants.WhatsAppMediaConstants
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsappCleanerSummaryScreen(activity: Activity) {
    val viewModel: WhatsappCleanerSummaryViewModel = koinViewModel()
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var showCleanDialog by remember { mutableStateOf(false) }

    val state: UiStateScreen<UiWhatsAppCleanerModel> by viewModel.uiState.collectAsState()

    LifecycleEventsEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onEvent(WhatsAppCleanerEvent.LoadMedia)
    }

    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.whatsapp_cleaner),
        onBackClicked = {
            activity.finish()
        },
        scrollBehavior = scrollBehavior,
        floatingActionButton = {
            AnimatedExtendedFloatingActionButton(
                visible = state.data?.mediaSummary?.totalBytes != 0L,
                onClick = { showCleanDialog = true },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.DeleteSweep,
                        contentDescription = null
                    )
                },
                text = {
                    Text(text = stringResource(id = R.string.clean_whatsapp))
                }
            )
        }
    ) { paddingValues ->
        ScreenStateHandler(
            screenState = state,
            onLoading = {
                LoadingScreen()
            },
            onEmpty = {
                WhatsAppEmptyState(paddingValues)
            },
            onSuccess = { data ->
                WhatsappCleanerSummaryScreenContent(
                    uiModel = data,
                    paddingValues = paddingValues,
                    onOpenDetails = { type ->
                        val intent = Intent(activity, WhatsAppDetailsActivity::class.java)
                        intent.putExtra(WhatsAppDetailsActivity.EXTRA_TYPE, type)
                        activity.startActivity(intent)
                    }
                )
            }
        )
    }

    if (showCleanDialog) {
        BasicAlertDialog(
            onDismiss = { showCleanDialog = false },
            onConfirm = {
                showCleanDialog = false
                viewModel.onEvent(WhatsAppCleanerEvent.CleanAll)
            },
            icon = Icons.Outlined.DeleteSweep,
            onCancel = { showCleanDialog = false },
            title = stringResource(id = R.string.clean_whatsapp_warning_title),
            content = { Text(text = stringResource(id = R.string.clean_whatsapp_warning_message)) },
            confirmButtonText = stringResource(id = R.string.clean_whatsapp),
            dismissButtonText = stringResource(id = R.string.cancel)
        )
    }
}

@Composable
private fun WhatsappCleanerSummaryScreenContent(
    uiModel: UiWhatsAppCleanerModel,
    paddingValues: PaddingValues,
    onOpenDetails: (String) -> Unit,
    adsConfig: AdsConfig = koinInject(qualifier = named(name = "large_banner"))
) {
    val summary = uiModel.mediaSummary

    val videos = stringResource(id = R.string.videos)
    val docs = stringResource(id = R.string.documents)
    val images = stringResource(id = R.string.images)
    val audios = stringResource(id = R.string.audios)
    val statuses = stringResource(id = R.string.statuses)
    val voiceNotes = stringResource(id = R.string.voice_notes)
    val videoNotes = stringResource(id = R.string.video_notes)
    val gifs = stringResource(id = R.string.gifs)
    val wallpapers = stringResource(id = R.string.wallpapers)
    val stickers = stringResource(id = R.string.stickers)
    val profiles = stringResource(id = R.string.profile_photos)

    val freeUpBytes = summary.totalBytes
    val totalDeviceBytes = remember {
        val stat = android.os.StatFs(android.os.Environment.getDataDirectory().path)
        stat.blockCountLong * stat.blockSizeLong
    }

    val directoryList = remember(summary) {
        listOf(
            DirectoryItem(
                type = WhatsAppMediaConstants.IMAGES,
                name = images,
                icon = R.drawable.ic_image,
                count = summary.images.count,
                size = summary.images.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.VIDEOS,
                name = videos,
                icon = R.drawable.ic_video_file,
                count = summary.videos.count,
                size = summary.videos.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.DOCUMENTS,
                name = docs,
                icon = R.drawable.ic_description,
                count = summary.documents.count,
                size = summary.documents.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.AUDIOS,
                name = audios,
                icon = R.drawable.ic_audio_file,
                count = summary.audios.count,
                size = summary.audios.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.STATUSES,
                name = statuses,
                icon = R.drawable.ic_web_stories,
                count = summary.statuses.count,
                size = summary.statuses.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.VOICE_NOTES,
                name = voiceNotes,
                icon = R.drawable.ic_voice_selection,
                count = summary.voiceNotes.count,
                size = summary.voiceNotes.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.VIDEO_NOTES,
                name = videoNotes,
                icon = R.drawable.ic_video_file,
                count = summary.videoNotes.count,
                size = summary.videoNotes.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.GIFS,
                name = gifs,
                icon = R.drawable.ic_gif,
                count = summary.gifs.count,
                size = summary.gifs.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.WALLPAPERS,
                name = wallpapers,
                icon = R.drawable.ic_wallpaper,
                count = summary.wallpapers.count,
                size = summary.wallpapers.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.STICKERS,
                name = stickers,
                icon = R.drawable.ic_ar_stickers,
                count = summary.stickers.count,
                size = summary.stickers.formattedSize
            ),
            DirectoryItem(
                type = WhatsAppMediaConstants.PROFILE_PHOTOS,
                name = profiles,
                icon = R.drawable.ic_person_pin,
                count = summary.profilePhotos.count,
                size = summary.profilePhotos.formattedSize
            ),
        ).filter { it.size != "0 B" }
    }
    val totalFiles = remember(directoryList) { directoryList.sumOf { it.count } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize)
    ) {
        SmallVerticalSpacer()

        CleanerInfoCard(
            freeUpSizeBytes = freeUpBytes,
            totalSizeBytes = totalDeviceBytes,
            filesCount = totalFiles
        )

        AdBanner(
            modifier = Modifier.padding(vertical = SizeConstants.MediumSize),
            adsConfig = adsConfig
        )

        DirectoryGrid(items = directoryList, onOpenDetails = onOpenDetails)
    }
}