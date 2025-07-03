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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.ui.components.FilePreviewCard
import java.io.File
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsScreen(
    title: String,
    files: List<File>,
    onDelete: (List<File>) -> Unit,
    padding: PaddingValues = PaddingValues()
) {
    val selected = remember { mutableStateListOf<File>() }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        if (files.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = context.getString(R.string.no_files))
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Adaptive(96.dp), modifier = Modifier.weight(1f)) {
                items(files) { file ->
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
