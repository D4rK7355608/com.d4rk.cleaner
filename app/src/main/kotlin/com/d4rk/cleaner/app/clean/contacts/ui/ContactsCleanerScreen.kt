package com.d4rk.cleaner.app.clean.contacts.ui

import android.app.Activity
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import com.d4rk.android.libs.apptoolkit.core.ui.effects.LifecycleEventsEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.contacts.domain.actions.ContactsCleanerEvent
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.RawContactInfo
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.UiContactsCleanerModel
import org.koin.compose.viewmodel.koinViewModel

private enum class ContactsPermissionState { CHECKING, GRANTED, RATIONALE, DENIED }

private fun openAppSettings(activity: Activity) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", activity.packageName, null)
    )
    activity.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsCleanerScreen(activity: Activity) {
    val viewModel: ContactsCleanerViewModel = koinViewModel()
    val state = viewModel.uiState.collectAsState().value
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val context = LocalContext.current
    var permissionState by remember { mutableStateOf(ContactsPermissionState.CHECKING) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }
        permissionState = if (granted) {
            ContactsPermissionState.GRANTED
        } else {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_CONTACTS
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.WRITE_CONTACTS
            )
            if (showRationale) ContactsPermissionState.RATIONALE else ContactsPermissionState.DENIED
        }
    }

    LaunchedEffect(Unit) {
        if (PermissionsHelper.hasContactsPermissions(context)) {
            permissionState = ContactsPermissionState.GRANTED
        } else {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_CONTACTS
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.WRITE_CONTACTS
            )
            if (showRationale) {
                permissionState = ContactsPermissionState.RATIONALE
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    )
                )
            }
        }
    }

    when (permissionState) {
        ContactsPermissionState.GRANTED -> {
            LargeTopAppBarWithScaffold(
                title = stringResource(id = R.string.contacts_cleaner_title),
                onBackClicked = { activity.finish() },
                scrollBehavior = scrollBehavior
            ) { paddingValues ->
                ScreenStateHandler(
                    screenState = state,
                    onLoading = {
                        println("ContactsCleanerScreen: Loading state")
                        LoadingScreen()
                    },
                    onEmpty = {
                        println("ContactsCleanerScreen: Empty state")
                        NoDataScreen(textMessage = R.string.no_duplicates_found)
                    },
                    onSuccess = { data: UiContactsCleanerModel ->
                        println("ContactsCleanerScreen: Success state")
                        ContactsCleanerContent(
                            data = data,
                            viewModel = viewModel,
                            paddingValues = paddingValues
                        )
                    }
                )
            }

            LifecycleEventsEffect(Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(ContactsCleanerEvent.LoadDuplicates)
            }
        }

        ContactsPermissionState.RATIONALE -> {
            PermissionRationaleDialog(onRequest = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    )
                )
            }) {
                permissionState = ContactsPermissionState.DENIED
            }
        }

        ContactsPermissionState.DENIED -> {
            PermissionDeniedScreen(onOpenSettings = { openAppSettings(activity) })
        }

        ContactsPermissionState.CHECKING -> Unit
    }
}

@Composable
private fun ContactsCleanerContent(
    data: UiContactsCleanerModel,
    viewModel: ContactsCleanerViewModel,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(SizeConstants.LargeSize)
    ) {
        data.duplicates.forEach { group ->
            ContactGroupItem(
                group = group,
                onDelete = { viewModel.onEvent(ContactsCleanerEvent.DeleteOlder(group)) },
                onMerge = { viewModel.onEvent(ContactsCleanerEvent.MergeAll(group)) }
            )
        }
    }
}

@Composable
private fun ContactGroupItem(
    group: List<RawContactInfo>,
    onDelete: () -> Unit,
    onMerge: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SizeConstants.LargeSize),
        verticalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize)
    ) {
        Text(text = group.firstOrNull()?.displayName ?: "")
        RowActions(onDelete = onDelete, onMerge = onMerge)
    }
}

@Composable
private fun RowActions(onDelete: () -> Unit, onMerge: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize)) {
        Button(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.keep_newest_remove_older))
        }
        Button(onClick = onMerge, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.merge_all_duplicates))
        }
    }
}

@Composable
private fun PermissionRationaleDialog(onRequest: () -> Unit, onDismiss: () -> Unit) {
    BasicAlertDialog(
        onDismiss = onDismiss,
        onConfirm = onRequest,
        onCancel = onDismiss,
        icon = Icons.Outlined.Contacts,
        title = stringResource(id = R.string.contacts_cleaner_title),
        confirmButtonText = stringResource(id = R.string.button_grant_permission),
        content = { Text(text = stringResource(id = R.string.contacts_permission_rationale)) },
    )
}

@Composable
private fun PermissionDeniedScreen(onOpenSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SizeConstants.LargeSize),
        verticalArrangement = Arrangement.spacedBy(SizeConstants.LargeSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Contacts,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(id = R.string.contacts_permission_denied),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = onOpenSettings) {
            Text(text = stringResource(id = R.string.open_settings))
        }
    }
}
