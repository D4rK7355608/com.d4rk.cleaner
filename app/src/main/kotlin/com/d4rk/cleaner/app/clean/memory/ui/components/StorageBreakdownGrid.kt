package com.d4rk.cleaner.app.clean.memory.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants

@Composable
fun StorageBreakdownGrid(
    storageBreakdown: Map<String, Long>,
    onItemClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(horizontal = SizeConstants.MediumSize)
    ) {
        storageBreakdown.entries.toList().chunked(size = 2).forEach { chunk : List<Map.Entry<String , Long>> ->
            Row(modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()) {
                for (item : Map.Entry<String , Long> in chunk) {
                    val (icon : String , size : Long) = item
                    StorageBreakdownItem(
                        icon = icon,
                        size = size,
                        modifier = Modifier.weight(weight = 1f),
                        onClick = { onItemClick(icon) }
                    )
                }
            }
        }
    }
}