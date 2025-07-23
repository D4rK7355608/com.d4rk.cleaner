package com.d4rk.cleaner.app.clean.memory.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter.format as formatSize

@Composable
fun StorageInfoText(label: String, size: Long) {
    Text(text = "$label ${formatSize(size)}", style = MaterialTheme.typography.bodyMedium)
}