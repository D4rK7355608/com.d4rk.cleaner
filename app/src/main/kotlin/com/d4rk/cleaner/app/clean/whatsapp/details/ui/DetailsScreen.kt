package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
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
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.ui.components.FilePreviewCard
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.WhatsAppMediaSummary
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.WhatsappCleanerSummaryViewModel
import java.io.File

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
        "images" -> stringResource(id = R.string.images)
        "videos" -> stringResource(id = R.string.videos)
        "documents" -> stringResource(id = R.string.documents)
        "audios" -> stringResource(id = R.string.audios)
        "statuses" -> stringResource(id = R.string.statuses)
        "voice_notes" -> stringResource(id = R.string.voice_notes)
        "video_notes" -> stringResource(id = R.string.video_notes)
        "gifs" -> stringResource(id = R.string.gifs)
        "wallpapers" -> stringResource(id = R.string.wallpapers)
        "stickers" -> stringResource(id = R.string.stickers)
        "profile_photos" -> stringResource(id = R.string.profile_photos)
        else -> title
    }
    val files = when (title) {
        "images" -> summary.images.files
        "videos" -> summary.videos.files
        "documents" -> summary.documents.files
        "audios" -> summary.audios.files
        "statuses" -> summary.statuses.files
        "voice_notes" -> summary.voiceNotes.files
        "video_notes" -> summary.videoNotes.files
        "gifs" -> summary.gifs.files
        "wallpapers" -> summary.wallpapers.files
        "stickers" -> summary.stickers.files
        "profile_photos" -> summary.profilePhotos.files
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
        DetailsScreenContent(
            paddingValues = paddingValues,
            selected = selected,
            isGrid = isGrid,
            onShowConfirmChange = { showConfirm = it },
            detailsViewModel = detailsViewModel
        )
    }

    if (showSort) {
        SortDialog(
            current = detailsViewModel.sortType.collectAsState().value,
            onDismiss = { showSort = false },
            onSortSelected = { detailsViewModel.applySort(it) }
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
    detailsViewModel: DetailsViewModel) {
    val context : Context = LocalContext.current
    val sortedFiles by detailsViewModel.files.collectAsState()
    val suggested by detailsViewModel.suggested.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        if (sortedFiles.isEmpty()) {
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
                        Text(
                            text = stringResource(id = R.string.smart_suggestions),
                            style = MaterialTheme.typography.titleMedium
                        )
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(suggested) { file ->
                                FilePreviewCard(
                                    file = file,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(64.dp)
                                )
                            }
                        }
                        Button(
                            onClick = {
                                selected.clear()
                                selected.addAll(suggested)
                                onShowConfirmChange(true)
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = stringResource(id = R.string.delete_all_suggested))
                        }
                    }
                }
            }
            val allSelected = selected.size == sortedFiles.size && sortedFiles.isNotEmpty()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Checkbox(
                    checked = allSelected,
                    onCheckedChange = {
                        if (allSelected) selected.clear() else {
                            selected.clear()
                            selected.addAll(sortedFiles)
                        }
                    }
                )
                Text(
                    text = if (allSelected)
                        stringResource(id = R.string.deselect_all)
                    else
                        stringResource(id = R.string.select_all)
                )
            }
            if (isGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(96.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(sortedFiles) { file ->
                        val checked = file in selected
                        Box(modifier = Modifier.padding(4.dp)) {
                            FilePreviewCard(
                                file = file, modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (checked) selected.remove(file) else selected.add(
                                            file
                                        )
                                    })
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
                    items(sortedFiles) { file ->
                        val checked = file in selected
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .clickable {
                                    if (checked) selected.remove(file) else selected.add(file)
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
            Button(
                onClick = { onShowConfirmChange(true) },
                enabled = selected.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = context.getString(R.string.delete_selected))
            }
        }
    }
}