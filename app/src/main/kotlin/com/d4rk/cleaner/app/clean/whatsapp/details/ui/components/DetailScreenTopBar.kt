package com.d4rk.cleaner.app.clean.whatsapp.details.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants

@OptIn(ExperimentalMaterial3Api::class)
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
                    modifier = Modifier.size(SizeConstants.ButtonIconSize),
                    imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Filled.GridView,
                    contentDescription = null
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onSortClick) {
                Icon(modifier = Modifier.size(SizeConstants.ButtonIconSize), imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = null)
            }
        }
    )
}
