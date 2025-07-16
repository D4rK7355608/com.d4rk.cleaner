package com.d4rk.cleaner.app.clean.nofilesfound.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.OutlinedIconButtonWithText
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.domain.actions.ScannerEvent
import com.d4rk.cleaner.app.clean.scanner.ui.ScannerViewModel

@Composable
fun NoFilesFoundScreen(viewModel : ScannerViewModel) {
    Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.FolderOff , contentDescription = null , modifier = Modifier.size(size = 64.dp) , tint = MaterialTheme.colorScheme.onSurface
            )
            LargeVerticalSpacer()
            Text(
                text = stringResource(id = R.string.no_files_found) , style = MaterialTheme.typography.bodyLarge , color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedIconButtonWithText(
                onClick = { viewModel.onEvent(ScannerEvent.ToggleAnalyzeScreen(true)) },
                modifier = Modifier,
                icon = Icons.Outlined.Refresh,
                iconContentDescription = null,
                label = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.try_again)
            )
        }
    }
}