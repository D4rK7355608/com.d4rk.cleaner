package com.d4rk.cleaner.app.clean.whatsapp.summary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import com.d4rk.cleaner.BuildConfig
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.shimmerEffect
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.actions.WhatsAppCleanerEvent
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.DirectoryItem
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.UiWhatsAppCleanerModel
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components.DirectoryGrid
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WhatsAppCleanerScreen(
    paddingValues: PaddingValues,
    onOpenDetails: (String) -> Unit,
    viewModel: WhatsAppCleanerViewModel = koinViewModel(),
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

    Scaffold(topBar = { HomeTopBar() }) { innerPadding ->
        val combinedPadding = PaddingValues(
            start = paddingValues.calculateStartPadding(layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr),
            top = paddingValues.calculateTopPadding() + innerPadding.calculateTopPadding(),
            end = paddingValues.calculateEndPadding(layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr),
            bottom = paddingValues.calculateBottomPadding() + innerPadding.calculateBottomPadding()
        )

        ScreenStateHandler(
            screenState = state,
            onLoading = {
                LoadingContent(
                    paddingValues = combinedPadding,
                    onClean = { viewModel.onEvent(WhatsAppCleanerEvent.CleanAll) },
                    onOpenDetails = { type ->
                        onOpenDetails(type)
                    }
                )
            },
            onEmpty = {
                LoadingContent(
                    paddingValues = combinedPadding,
                    onClean = { viewModel.onEvent(WhatsAppCleanerEvent.CleanAll) },
                    onOpenDetails = { type -> onOpenDetails(type) }
                )
            },
            onSuccess = { data ->
                SuccessContent(
                    uiModel = data,
                    paddingValues = combinedPadding,
                    onClean = { viewModel.onEvent(WhatsAppCleanerEvent.CleanAll) },
                    onOpenDetails = { type -> onOpenDetails(type) }
                )
            },
            onError = {
                ErrorContent(
                    paddingValues = combinedPadding,
                    onClean = { viewModel.onEvent(WhatsAppCleanerEvent.CleanAll) }
                )
            }
        )
    }
}

@Composable

private fun SuccessContent(
    uiModel: UiWhatsAppCleanerModel,
    paddingValues: PaddingValues,
    onClean: () -> Unit,
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

    val freeUp = uiModel.totalSize

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
                icon = R.drawable.ic_apk_document,
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
                icon = R.drawable.ic_image,
                count = summary.statuses.files.size,
                size = summary.statuses.formattedSize
            ),
            DirectoryItem(
                type = "voice_notes",
                name = voiceNotes,
                icon = R.drawable.ic_audio_file,
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
                icon = R.drawable.ic_image,
                count = summary.gifs.files.size,
                size = summary.gifs.formattedSize
            ),
            DirectoryItem(
                type = "wallpapers",
                name = wallpapers,
                icon = R.drawable.ic_image,
                count = summary.wallpapers.files.size,
                size = summary.wallpapers.formattedSize
            ),
            DirectoryItem(
                type = "stickers",
                name = stickers,
                icon = R.drawable.ic_image,
                count = summary.stickers.files.size,
                size = summary.stickers.formattedSize
            ),
            DirectoryItem(
                type = "profile_photos",
                name = profiles,
                icon = R.drawable.ic_image,
                count = summary.profilePhotos.files.size,
                size = summary.profilePhotos.formattedSize
            ),
        )
    }
    val total = remember(directoryList) { directoryList.sumOf { it.count } }

    Column(
        Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.free_up_format, freeUp),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { ListSizeHeader(Modifier, total.toString()) }
            item { DirectoryGrid(items = directoryList, onOpenDetails = onOpenDetails) }
        }

        Button(onClick = onClean, modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = R.string.clean_whatsapp))
        }

        if (BuildConfig.DEBUG)
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp),
                text = "Debug Build ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall,
            )
    }
}

@Composable
private fun LoadingContent(
    paddingValues: PaddingValues,
    onClean: () -> Unit,
    onOpenDetails: (String) -> Unit
) {
    val videos = stringResource(id = R.string.videos)
    val docs = stringResource(id = R.string.documents)
    val images = stringResource(id = R.string.images)

    val context = LocalContext.current
    val freeUp = remember {
        android.text.format.Formatter.formatShortFileSize(context, 0)
    }

    Column(
        Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val modifier = Modifier.shimmerEffect()

        Text(
            text = stringResource(id = R.string.free_up_format, freeUp),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { ListSizeHeader(modifier, "0") }
            item {
                DirectoryGrid(
                    items = listOf(
                        DirectoryItem(
                            type = "images",
                            name = images,
                            icon = R.drawable.ic_image,
                            count = 0,
                            size = "0 B"
                        ),
                    DirectoryItem(
                        type = "videos",
                        name = videos,
                        icon = R.drawable.ic_video_file,
                        count = 0,
                        size = "0 B"
                    ),
                    DirectoryItem(
                        type = "documents",
                        name = docs,
                        icon = R.drawable.ic_apk_document,
                        count = 0,
                        size = "0 B"
                    ),
                    DirectoryItem(
                        type = "audios",
                        name = audios,
                        icon = R.drawable.ic_audio_file,
                        count = 0,
                        size = "0 B"
                    ),
                    DirectoryItem(
                        type = "statuses",
                        name = statuses,
                        icon = R.drawable.ic_image,
                        count = 0,
                        size = "0 B"
                    ),
                    DirectoryItem(
                        type = "voice_notes",
                        name = voiceNotes,
                        icon = R.drawable.ic_audio_file,
                        count = 0,
                        size = "0 B"
                    ),
                    DirectoryItem(
                        type = "video_notes",
                        name = videoNotes,
                        icon = R.drawable.ic_video_file,
                        count = 0,
                        size = "0 B"
                    ),
                    DirectoryItem(
                        type = "gifs",
                        name = gifs,
                        icon = R.drawable.ic_image,
                        count = 0,
                        size = "0 B"
                    ),
                    DirectoryItem(
                        type = "wallpapers",
                        name = wallpapers,
                        icon = R.drawable.ic_image,
                        count = 0,
                        size = "0 B"
                    ),
                    DirectoryItem(
                        type = "stickers",
                        name = stickers,
                        icon = R.drawable.ic_image,
                        count = 0,
                        size = "0 B"
                    ),
                    DirectoryItem(
                        type = "profile_photos",
                        name = profiles,
                        icon = R.drawable.ic_image,
                        count = 0,
                        size = "0 B"
                    )
                ),
                    onOpenDetails = onOpenDetails
                )
            }
        }

        Button(onClick = onClean, modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = R.string.clean_whatsapp))
        }

        if (BuildConfig.DEBUG)
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp),
                text = "Debug Build ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall,
            )
    }
}

@Composable
private fun ErrorContent(
    paddingValues: PaddingValues,
    onClean: () -> Unit
) {
    Column(
        Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = "Error",
            style = MaterialTheme.typography.labelSmall
        )

        Button(onClick = onClean, modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = R.string.clean_whatsapp))
        }

        if (BuildConfig.DEBUG)
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp),
                text = "Debug Build ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall,
            )
    }
}

@Composable
private fun ListSizeHeader(modifier: Modifier, total: String) = Banner(
    modifier.padding(16.dp),
    buildAnnotatedString {
        withStyle(SpanStyle(fontSize = 24.sp)) { append(total) }
        withStyle(SpanStyle(fontSize = 18.sp)) { append(" files") }
    }
)

@Composable


@Composable
private fun Title(modifier: Modifier, text: String) {
    Text(
        modifier = modifier.padding(8.dp),
        text = text,
        fontSize = 24.sp,
        textAlign = TextAlign.Start,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun Banner(modifier: Modifier, text: AnnotatedString) {
    val bgColor = MaterialTheme.colorScheme.primaryContainer
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(0.4f)
                .aspectRatio(1f)
                .shadow(elevation = 16.dp, shape = CircleShape)
                .background(bgColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {
            Title(modifier = Modifier, text = stringResource(R.string.app_name))
        }
    )
}
