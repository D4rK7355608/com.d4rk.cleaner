package com.d4rk.cleaner.app.onboarding.ui.tabs

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper

@Composable
fun StoragePermissionOnboardingTab() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SizeConstants.LargeSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Storage,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        LargeVerticalSpacer()
        Text(
            text = stringResource(id = R.string.onboarding_permission_storage_title),
            style = MaterialTheme.typography.titleLarge
        )
        SmallVerticalSpacer()
        Text(
            text = stringResource(id = R.string.onboarding_permission_storage_description),
            style = MaterialTheme.typography.bodyMedium
        )
        LargeVerticalSpacer()
        OutlinedButton(onClick = {
            PermissionsHelper.requestStoragePermissions(context as Activity)
            PermissionsHelper.requestUsageAccess(context)
        }, colors = ButtonDefaults.outlinedButtonColors()) {
            Text(text = stringResource(id = R.string.button_grant_permission))
        }
    }
}