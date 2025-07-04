package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import com.d4rk.cleaner.app.clean.whatsapp.utils.constants.WhatsAppMediaConstants
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.ui.components.FilePreviewCard
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.WhatsAppMediaSummary
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.WhatsappCleanerSummaryViewModel
import com.d4rk.cleaner.app.clean.whatsapp.utils.helpers.openFile
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.components.CustomTabLayout
import java.io.File
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    title: String,
    onDelete: (List<File>) -> Unit,
    detailsViewModel: DetailsViewModel,
    viewModel: WhatsappCleanerSummaryViewModel,
    activity: Activity
) {

    val state = viewModel.uiState.collectAsState().value
    val summary = state.data?.mediaSummary ?: WhatsAppMediaSummary()
    val localizedTitle = when (title) {
        WhatsAppMediaConstants.IMAGES -> stringResource(id = R.string.images)
        WhatsAppMediaConstants.VIDEOS -> stringResource(id = R.string.videos)
        WhatsAppMediaConstants.DOCUMENTS -> stringResource(id = R.string.documents)
        WhatsAppMediaConstants.AUDIOS -> stringResource(id = R.string.audios)
        WhatsAppMediaConstants.STATUSES -> stringResource(id = R.string.statuses)
        WhatsAppMediaConstants.VOICE_NOTES -> stringResource(id = R.string.voice_notes)
        WhatsAppMediaConstants.VIDEO_NOTES -> stringResource(id = R.string.video_notes)
        WhatsAppMediaConstants.GIFS -> stringResource(id = R.string.gifs)
        WhatsAppMediaConstants.WALLPAPERS -> stringResource(id = R.string.wallpapers)
        WhatsAppMediaConstants.STICKERS -> stringResource(id = R.string.stickers)
        WhatsAppMediaConstants.PROFILE_PHOTOS -> stringResource(id = R.string.profile_photos)
        else -> title
    }
    val files = when (title) {
        WhatsAppMediaConstants.IMAGES -> summary.images.files
        WhatsAppMediaConstants.VIDEOS -> summary.videos.files
        WhatsAppMediaConstants.DOCUMENTS -> summary.documents.files
        WhatsAppMediaConstants.AUDIOS -> summary.audios.files
        WhatsAppMediaConstants.STATUSES -> summary.statuses.files
        WhatsAppMediaConstants.VOICE_NOTES -> summary.voiceNotes.files
        WhatsAppMediaConstants.VIDEO_NOTES -> summary.videoNotes.files
        WhatsAppMediaConstants.GIFS -> summary.gifs.files
        WhatsAppMediaConstants.WALLPAPERS -> summary.wallpapers.files
        WhatsAppMediaConstants.STICKERS -> summary.stickers.files
        WhatsAppMediaConstants.PROFILE_PHOTOS -> summary.profilePhotos.files
        else -> emptyList()
    }

    val selected = remember { mutableStateListOf<File>() }
    val isGrid: Boolean by detailsViewModel.isGridView.collectAsState()
    var showSort: Boolean by remember { mutableStateOf(false) }
    var showConfirm: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(files) {
        detailsViewModel.setFiles(files)
    }

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val sortedFiles by detailsViewModel.files.collectAsState()
    val receivedFiles = remember(sortedFiles) {
        sortedFiles.filterNot { it.path.contains("${File.separator}Sent") || it.path.contains("${File.separator}Private") }
    }
    val sentFiles = remember(sortedFiles) { sortedFiles.filter { it.path.contains("${File.separator}Sent") } }
    val privateFiles = remember(sortedFiles) { sortedFiles.filter { it.path.contains("${File.separator}Private") } }

    val hasSent = sentFiles.isNotEmpty()
    val hasPrivate = privateFiles.isNotEmpty()

    val tabs = listOfNotNull(
        stringResource(id = R.string.received),
        if (hasSent) stringResource(id = R.string.sent) else null,
        if (hasPrivate) stringResource(id = R.string.private_tab) else null
    )

    val tabFiles = buildList<List<File>> {
        add(receivedFiles)
        if (hasSent) add(sentFiles)
        if (hasPrivate) add(privateFiles)
    }

    val pagerState = rememberPagerState { tabs.size }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    LargeTopAppBarWithScaffold(
        actions = {
            IconButton(onClick = { detailsViewModel.toggleView() }) {
                Icon(
                    imageVector = if (isGrid) Icons.AutoMirrored.Filled.ViewList else Icons.Filled.GridView,
                    contentDescription = null
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { showSort = true }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = null)
            }
        },
        title = localizedTitle,
        onBackClicked = {
            activity.finish()
        },
        scrollBehavior = scrollBehavior,
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            if (hasSent || hasPrivate) {
                CustomTabLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    selectedItemIndex = selectedTabIndex,
                    items = tabs,
                    filesPerTab = tabFiles,
                    selectedFiles = selected,
                    onTabSelected = { index ->
                        selectedTabIndex = index
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    onTabCheckedChange = { index, checked ->
                        val files = tabFiles.getOrNull(index) ?: emptyList()
                        if (checked) {
                            files.filterNot { it in selected }.forEach { selected.add(it) }
                        } else {
                            selected.removeAll(files)
                        }
                    }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val list = when (page) {
                    0 -> receivedFiles
                    1 -> if (hasSent) sentFiles else privateFiles
                    else -> privateFiles
                }

                DetailsScreenContent(
                    paddingValues = paddingValues,
                    selected = selected,
                    isGrid = isGrid,
                    onShowConfirmChange = { showConfirm = it },
                    detailsViewModel = detailsViewModel,
                    files = list
                )
            }

            Button(
                onClick = { showConfirm = true },
                enabled = selected.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = stringResource(id = R.string.delete_selected))
            }
        }
    }

    if (showSort) {
        SortDialog(
            current = detailsViewModel.sortType.collectAsState().value,
            descending = detailsViewModel.descending.collectAsState().value,
            startDate = detailsViewModel.startDate.collectAsState().value,
            endDate = detailsViewModel.endDate.collectAsState().value,
            onDismiss = { showSort = false },
            onApply = { type, desc, start, end ->
                detailsViewModel.applySort(type, desc, start, end)
            }
        )
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onDelete(selected.toList())
                    selected.clear()
                }) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
            },
            title = { Text(text = stringResource(id = R.string.delete_confirmation_title)) },
            text = {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.delete_confirmation_message,
                        count = selected.size,
                        selected.size
                    )
                )
            }
        )
    }
}

@Composable
fun DetailsScreenContent(
    paddingValues: PaddingValues,
    selected: MutableList<File>,
    isGrid: Boolean,
    onShowConfirmChange: (Boolean) -> Unit,
    detailsViewModel: DetailsViewModel,
    files: List<File>
) {
    val context : Context = LocalContext.current
    val suggested by detailsViewModel.suggested.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        if (files.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = context.getString(R.string.no_files))
            }
        } else {
            if (suggested.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_auto_fix_high),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(id = R.string.smart_suggestions),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(suggested) { file ->
                                val checked = file in selected
                                Box(modifier = Modifier.padding(4.dp)) {
                                    FilePreviewCard(
                                        file = file,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .pointerInput(file) {
                                                detectTapGestures(
                                                    onLongPress = {
                                                        if (checked) selected.remove(file) else selected.add(file)
                                                    },
                                                    onTap = { openFile(context, file) }
                                                )
                                            }
                                    )
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = {
                                            if (checked) selected.remove(file) else selected.add(
                                                file
                                            )
                                        },
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = {
                                selected.clear()
                                selected.addAll(suggested)
                                onShowConfirmChange(true)
                            },
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = stringResource(id = R.string.delete_all_suggested))
                        }
                    }
                }
            }
            if (isGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(96.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(files) { file ->
                        val checked = file in selected
                        Box(modifier = Modifier.padding(4.dp)) {
                            FilePreviewCard(
                                file = file, modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(file) {
                                        detectTapGestures(
                                            onLongPress = {
                                                if (checked) selected.remove(file) else selected.add(file)
                                            },
                                            onTap = { openFile(context, file) }
                                        )
                                    }
                            )
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    if (checked) selected.remove(file) else selected.add(file)
                                },
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(files) { file ->
                        val checked = file in selected
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .pointerInput(file) {
                                    detectTapGestures(
                                        onLongPress = {
                                            if (checked) selected.remove(file) else selected.add(file)
                                        },
                                        onTap = { openFile(context, file) }
                                    )
                                }
                        ) {
                            FilePreviewCard(file = file, modifier = Modifier.weight(1f))
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    if (checked) selected.remove(file) else selected.add(file)
                                },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}
