package com.d4rk.cleaner.ui.screens.nofilesfound

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.components.modifiers.bounceClick
import com.d4rk.cleaner.ui.screens.home.HomeViewModel

@Composable
fun NoFilesFoundScreen(viewModel : HomeViewModel) {
    Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.FolderOff ,
                contentDescription = null ,
                modifier = Modifier.size(64.dp) ,
                tint = MaterialTheme.colorScheme.onSurface
            )
            LargeVerticalSpacer()
            Text(
                text = stringResource(id = R.string.no_files_found) ,
                style = MaterialTheme.typography.bodyLarge ,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedButton(modifier = Modifier.bounceClick() , onClick = {
                viewModel.rescanFiles()
            }) {
                Icon(
                    modifier = Modifier.size(ButtonDefaults.IconSize) ,
                    imageVector = Icons.Outlined.Refresh ,
                    contentDescription = "Close"
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = R.string.try_again))
            }
        }
    }
}