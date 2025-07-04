package com.d4rk.cleaner.app.clean.whatsapp.summary.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.fab.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.WhatsAppDetailsActivity
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.actions.WhatsAppCleanerEvent
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.DirectoryItem
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.UiWhatsAppCleanerModel
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components.CleanerProgressIndicator
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components.DirectoryGrid
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsappCleanerSummaryScreen(activity: Activity) {
    val viewModel: WhatsappCleanerSummaryViewModel = koinViewModel()
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.whatsapp_cleaner),
        onBackClicked = {
            activity.finish()
        },
        scrollBehavior = scrollBehavior,
        floatingActionButton = {
            AnimatedExtendedFloatingActionButton(
                onClick = { viewModel.onEvent(WhatsAppCleanerEvent.CleanAll) },
                icon = {
                    Icon(
                        modifier = Modifier.size(SizeConstants.ButtonIconSize),
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
        WhatsappCleanerSummaryScreenContent(
            paddingValues = paddingValues,
            onOpenDetails = { type ->
                val intent = Intent(activity, WhatsAppDetailsActivity::class.java)
                intent.putExtra(WhatsAppDetailsActivity.EXTRA_TYPE, type)
                activity.startActivity(intent)
            }
        )
    }
}


@Composable
fun WhatsappCleanerSummaryScreenContent(
    paddingValues: PaddingValues,
    onOpenDetails: (String) -> Unit,
    viewModel: WhatsappCleanerSummaryViewModel = koinViewModel(),
) {
    val state: UiStateScreen<UiWhatsAppCleanerModel> by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(WhatsAppCleanerEvent.LoadMedia)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ScreenStateHandler(
        screenState = state,
        onLoading = {
            LoadingScreen()
        },
        onEmpty = {
            NoDataScreen(
                icon = Icons.Outlined.FolderOff,
                showRetry = true,
                onRetry = { viewModel.onEvent(WhatsAppCleanerEvent.LoadMedia) },
            )
        },
        onSuccess = { data ->
            WhatsappCleanerSummaryScreenSuccessContent(
                uiModel = data,
                paddingValues = paddingValues,
                onOpenDetails = { type -> onOpenDetails(type) }
            )
        }
    )
}

@Composable
private fun WhatsappCleanerSummaryScreenSuccessContent(
    uiModel: UiWhatsAppCleanerModel,
    paddingValues: PaddingValues,
    onOpenDetails: (String) -> Unit
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
                type = "images",
                name = images,
                icon = R.drawable.ic_image,
                count = summary.images.files.size,
                size = summary.images.formattedSize
            ),
            DirectoryItem(
                type = "videos",
                name = videos,
                icon = R.drawable.ic_video_file,
                count = summary.videos.files.size,
                size = summary.videos.formattedSize
            ),
            DirectoryItem(
                type = "documents",
                name = docs,
                icon = R.drawable.ic_description,
                count = summary.documents.files.size,
                size = summary.documents.formattedSize
            ),
            DirectoryItem(
                type = "audios",
                name = audios,
                icon = R.drawable.ic_audio_file,
                count = summary.audios.files.size,
                size = summary.audios.formattedSize
            ),
            DirectoryItem(
                type = "statuses",
                name = statuses,
                icon = R.drawable.ic_web_stories,
                count = summary.statuses.files.size,
                size = summary.statuses.formattedSize
            ),
            DirectoryItem(
                type = "voice_notes",
                name = voiceNotes,
                icon = R.drawable.ic_voice_selection,
                count = summary.voiceNotes.files.size,
                size = summary.voiceNotes.formattedSize
            ),
            DirectoryItem(
                type = "video_notes",
                name = videoNotes,
                icon = R.drawable.ic_video_file,
                count = summary.videoNotes.files.size,
                size = summary.videoNotes.formattedSize
            ),
            DirectoryItem(
                type = "gifs",
                name = gifs,
                icon = R.drawable.ic_gif,
                count = summary.gifs.files.size,
                size = summary.gifs.formattedSize
            ),
            DirectoryItem(
                type = "wallpapers",
                name = wallpapers,
                icon = R.drawable.ic_wallpaper,
                count = summary.wallpapers.files.size,
                size = summary.wallpapers.formattedSize
            ),
            DirectoryItem(
                type = "stickers",
                name = stickers,
                icon = R.drawable.ic_ar_stickers,
                count = summary.stickers.files.size,
                size = summary.stickers.formattedSize
            ),
            DirectoryItem(
                type = "profile_photos",
                name = profiles,
                icon = R.drawable.ic_person_pin,
                count = summary.profilePhotos.files.size,
                size = summary.profilePhotos.formattedSize
            ),
        ).filter { it.size != "0 B" }
    }
    val totalFiles = remember(directoryList) { directoryList.sumOf { it.count } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CleanerInfoCard(
                freeUpSizeBytes = freeUpBytes,
                totalSizeBytes = totalDeviceBytes,
                filesCount = totalFiles
            )
        }
        item { DirectoryGrid(items = directoryList, onOpenDetails = onOpenDetails) }
    }
}

@Composable
fun CleanerInfoCard(
    freeUpSizeBytes: Long,
    totalSizeBytes: Long,
    filesCount: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formattedFreeUp = remember(freeUpSizeBytes) {
        android.text.format.Formatter.formatFileSize(context, freeUpSizeBytes)
    }
    val progress = if (totalSizeBytes > 0L) {
        freeUpSizeBytes.toFloat() / totalSizeBytes.toFloat()
    } else {
        0f
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CleanerProgressIndicator(
                progress = progress,
                icon = painterResource(id = R.drawable.ic_cleaner_notify),
                size = 100.dp
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column(verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.SdStorage,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.typography.bodyLarge.fontSize.value.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ButtonIconSpacer()
                    Text(
                        text = stringResource(id = R.string.can_be_freed),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = formattedFreeUp,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 42.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.typography.bodyMedium.fontSize.value.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ButtonIconSpacer()
                    Text(
                        text = stringResource(id = R.string.total_files_format, filesCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}