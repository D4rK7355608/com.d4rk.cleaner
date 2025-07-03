package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp

@Composable
fun DetailScreenTopBar(
    title: String,
    isGridView: Boolean,
    onToggleView: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title) },
        actions = {
            IconButton(onClick = onToggleView) {
                Icon(
                    imageVector = if (isGridView) Icons.Filled.ViewList else Icons.Filled.GridView,
                    contentDescription = null
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onSortClick) {
                Icon(imageVector = Icons.Filled.Sort, contentDescription = null)
            }
        }
    )
}
