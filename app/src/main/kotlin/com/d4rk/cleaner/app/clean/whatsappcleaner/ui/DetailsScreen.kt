package com.d4rk.cleaner.app.clean.whatsappcleaner.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun DetailsScreen(title: String, files: List<File>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = title)
        LazyColumn {
            items(files) { file ->
                Text(text = file.name)
            }
        }
    }
}
