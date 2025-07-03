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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val isGrid by viewModel.isGridView.collectAsState()
    var showSort by remember { mutableStateOf(false) }

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
                    onClick = { onDelete(selected.toList()) },
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
}
