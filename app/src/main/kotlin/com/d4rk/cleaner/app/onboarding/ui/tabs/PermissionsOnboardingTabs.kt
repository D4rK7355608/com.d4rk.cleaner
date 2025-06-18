package com.d4rk.cleaner.app.onboarding.ui.tabs

import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun StoragePermissionOnboardingTab() {
    val context = LocalContext.current
    val dataStore: DataStore = koinInject()
    val coroutineScope = rememberCoroutineScope()

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
            PermissionsHelper.requestUsageAccess(context as Activity)
            coroutineScope.launch {
                val granted = PermissionsHelper.hasStoragePermissions(context)
                val usage = PermissionsHelper.hasUsageAccessPermissions(context)
                dataStore.saveStoragePermissionGranted(granted)
                dataStore.saveUsagePermissionGranted(usage)
            }
        }, colors = ButtonDefaults.outlinedButtonColors()) {
            Text(text = stringResource(id = R.string.button_grant_permission))
        }
    }
}

@Composable
fun NotificationPermissionOnboardingTab() {
    val context = LocalContext.current
    val dataStore: DataStore = koinInject()
    val coroutineScope = rememberCoroutineScope()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        coroutineScope.launch {
            dataStore.saveNotificationsPermissionGranted(granted)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SizeConstants.LargeSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        LargeVerticalSpacer()
        Text(
            text = stringResource(id = R.string.onboarding_permission_notifications_title),
            style = MaterialTheme.typography.titleLarge
        )
        SmallVerticalSpacer()
        Text(
            text = stringResource(id = R.string.onboarding_permission_notifications_description),
            style = MaterialTheme.typography.bodyMedium
        )
        LargeVerticalSpacer()
        OutlinedButton(onClick = {
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }, colors = ButtonDefaults.outlinedButtonColors()) {
            Text(text = stringResource(id = R.string.button_grant_permission))
        }
    }
}
