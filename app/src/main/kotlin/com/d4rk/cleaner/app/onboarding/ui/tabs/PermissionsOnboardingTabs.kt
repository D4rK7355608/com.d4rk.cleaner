package com.d4rk.cleaner.app.onboarding.ui.tabs

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
private fun PermissionCard(
    title: String,
    description: String,
    granted: Boolean,
    onClick: () -> Unit,
    note: String? = null
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(SizeConstants.LargeSize)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    SmallVerticalSpacer()
                    Text(text = description, style = MaterialTheme.typography.bodySmall)
                    note?.let {
                        SmallVerticalSpacer()
                        Text(text = it, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (granted) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (!granted) {
                LargeVerticalSpacer()
                OutlinedButton(onClick = onClick, colors = ButtonDefaults.outlinedButtonColors()) {
                    Text(text = stringResource(id = R.string.button_grant_permission))
                }
            }
        }
    }
}

@Composable
fun StoragePermissionOnboardingTab() {
    val context = LocalContext.current
    val dataStore: DataStore = koinInject()
    val coroutineScope = rememberCoroutineScope()

    val storageGranted by dataStore.storagePermissionGranted.collectAsState(initial = false)
    val usageGranted by dataStore.usagePermissionGranted.collectAsState(initial = false)
    val treeGranted by dataStore.documentTreePermissionGranted.collectAsState(initial = false)

    val documentTreeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            coroutineScope.launch {
                dataStore.saveDocumentTreeUri(uri.toString())
                dataStore.saveDocumentTreePermissionGranted(true)
            }
        }
    }

    LaunchedEffect(Unit) {
        dataStore.saveStoragePermissionGranted(PermissionsHelper.hasStoragePermissions(context))
        dataStore.saveUsagePermissionGranted(PermissionsHelper.hasUsageAccessPermissions(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SizeConstants.LargeSize),
        verticalArrangement = Arrangement.spacedBy(SizeConstants.LargeSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Storage,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(id = R.string.onboarding_permission_storage_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.onboarding_permission_storage_description),
            style = MaterialTheme.typography.bodyMedium
        )

        PermissionCard(
            title = stringResource(id = R.string.permission_section_public_title),
            description = stringResource(id = R.string.permission_section_public_description),
            granted = storageGranted,
            onClick = {
                PermissionsHelper.requestStoragePermissions(context as Activity)
                coroutineScope.launch {
                    dataStore.saveStoragePermissionGranted(PermissionsHelper.hasStoragePermissions(context))
                }
            }
        )

        PermissionCard(
            title = stringResource(id = R.string.permission_section_manage_title),
            description = stringResource(id = R.string.permission_section_manage_description),
            granted = storageGranted,
            onClick = {
                PermissionsHelper.requestStoragePermissions(context as Activity)
                coroutineScope.launch {
                    dataStore.saveStoragePermissionGranted(PermissionsHelper.hasStoragePermissions(context))
                }
            },
            note = stringResource(id = R.string.manage_external_storage)
        )

        PermissionCard(
            title = stringResource(id = R.string.permission_section_usage_title),
            description = stringResource(id = R.string.permission_section_usage_description),
            granted = usageGranted,
            onClick = {
                PermissionsHelper.requestUsageAccess(context as Activity)
                coroutineScope.launch {
                    dataStore.saveUsagePermissionGranted(PermissionsHelper.hasUsageAccessPermissions(context))
                }
            },
            note = stringResource(id = R.string.package_usage_stats)
        )

        PermissionCard(
            title = stringResource(id = R.string.permission_section_saf_title),
            description = stringResource(id = R.string.permission_section_saf_description),
            granted = treeGranted,
            onClick = { documentTreeLauncher.launch(null) },
            note = stringResource(id = R.string.action_open_document_tree)
        )
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
