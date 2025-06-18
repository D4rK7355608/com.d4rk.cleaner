package com.d4rk.cleaner.app.onboarding.ui.tabs

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
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
    val lifecycleOwner = LocalLifecycleOwner.current

    val storageGranted by dataStore.storagePermissionGranted.collectAsState(initial = false)
    val usageGranted by dataStore.usagePermissionGranted.collectAsState(initial = false)


    LaunchedEffect(Unit) {
        dataStore.saveStoragePermissionGranted(PermissionsHelper.hasStoragePermissions(context))
        dataStore.saveUsagePermissionGranted(PermissionsHelper.hasUsageAccessPermissions(context))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch {
                    dataStore.saveStoragePermissionGranted(
                        PermissionsHelper.hasStoragePermissions(context)
                    )
                    dataStore.saveUsagePermissionGranted(
                        PermissionsHelper.hasUsageAccessPermissions(context)
                    )
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SizeConstants.LargeSize)
            .verticalScroll(rememberScrollState()),
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
            }
        )

        PermissionCard(
            title = stringResource(id = R.string.permission_section_manage_title),
            description = stringResource(id = R.string.permission_section_manage_description),
            granted = storageGranted,
            onClick = {
                PermissionsHelper.requestStoragePermissions(context as Activity)
            },
            note = stringResource(id = R.string.manage_external_storage)
        )

        PermissionCard(
            title = stringResource(id = R.string.permission_section_usage_title),
            description = stringResource(id = R.string.permission_section_usage_description),
            granted = usageGranted,
            onClick = {
                PermissionsHelper.requestUsageAccess(context as Activity)
            },
            note = stringResource(id = R.string.package_usage_stats)
        )
    }
}

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
                OutlinedButton(
                    modifier = Modifier.bounceClick(),
                    onClick = onClick
                ) {
                    Text(text = stringResource(id = R.string.button_grant_permission))
                }
            }
        }
    }
}