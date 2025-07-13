package com.d4rk.cleaner.app.clean.whatsapp.details.ui.components

import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.analyze.ui.components.SelectAllComposable

@Composable
fun DetailsStatusRow(
    modifier: Modifier = Modifier,
    selectedCount: Int,
    allSelected: Boolean,
    view: View,
    onClickSelectAll: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val statusText = if (selectedCount > 0) {
            pluralStringResource(
                id = R.plurals.status_selected_files,
                count = selectedCount,
                selectedCount
            )
        } else {
            stringResource(id = R.string.status_no_files_selected)
        }
        val statusColor: Color by animateColorAsState(
            targetValue = if (selectedCount > 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            },
            animationSpec = tween(),
            label = "Selected Files Status Color Animation"
        )

        Text(
            text = statusText,
            color = statusColor,
            modifier = Modifier.animateContentSize()
        )
        SelectAllComposable(
            selected = allSelected,
            view = view,
            onClickSelectAll = onClickSelectAll
        )
    }
}