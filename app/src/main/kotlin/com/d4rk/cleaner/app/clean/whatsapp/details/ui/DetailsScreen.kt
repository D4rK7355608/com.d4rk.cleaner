package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.ui.components.FilePreviewCard
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.SortDialog
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.DetailScreenTopBar
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.DetailsViewModel
import java.io.File
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsScreen(
    title: String,
    files: List<File>,
    onDelete: (List<File>) -> Unit,
    viewModel: DetailsViewModel,
    padding: PaddingValues = PaddingValues()
) {
    val selected = remember { mutableStateListOf<File>() }
    val context = LocalContext.current
    val sortedFiles by viewModel.files.collectAsState()
    val suggested by viewModel.suggested.collectAsState()
    val isGrid by viewModel.isGridView.collectAsState()
    var showSort by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(files) {
        viewModel.setFiles(files)
    }

    Scaffold(topBar = {
        DetailScreenTopBar(
            title = title,
            isGridView = isGrid,
            onToggleView = { viewModel.toggleView() },
            onSortClick = { showSort = true }
        )
    }) { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner).padding(padding)) {
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
                                    showConfirm = true
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
                                selected.clear();
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
                    LazyVerticalGrid(columns = GridCells.Adaptive(96.dp), modifier = Modifier.weight(1f)) {
                        items(sortedFiles) { file ->
                            val checked = file in selected
                            Box(modifier = Modifier.padding(4.dp)) {
                                FilePreviewCard(file = file, modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (checked) selected.remove(file) else selected.add(file)
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
                            Row(modifier = Modifier
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
                    onClick = { showConfirm = true },
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

    if (showSort) {
        SortDialog(
            current = viewModel.sortType.collectAsState().value,
            onDismiss = { showSort = false },
            onSortSelected = { viewModel.applySort(it) }
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
