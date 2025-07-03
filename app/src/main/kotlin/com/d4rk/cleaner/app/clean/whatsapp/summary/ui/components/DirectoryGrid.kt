package com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.DirectoryItem
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components.DirectoryCard

@Composable
fun DirectoryGrid(items: List<DirectoryItem>, onOpenDetails: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(horizontal = SizeConstants.MediumSize)
    ) {
        items.chunked(2).forEach { chunk ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                for (item in chunk) {
                    DirectoryCard(item = item, onOpenDetails = onOpenDetails, modifier = Modifier.weight(1f))
                }
                if (chunk.size == 1) {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
