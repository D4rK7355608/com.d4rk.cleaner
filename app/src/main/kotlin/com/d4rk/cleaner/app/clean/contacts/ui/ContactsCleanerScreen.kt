package com.d4rk.cleaner.app.clean.contacts.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.AnimatedIconButtonDirection
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.effects.LifecycleEventsEffect
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.contacts.domain.actions.ContactsCleanerEvent
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.DuplicateContactGroup
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.RawContactInfo
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.UiContactsCleanerModel
import com.d4rk.cleaner.app.clean.contacts.ui.components.states.ContactsEmptyState
import com.d4rk.cleaner.app.clean.contacts.ui.components.states.ContactsErrorState
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper
import org.koin.compose.viewmodel.koinViewModel

private enum class ContactsPermissionState { CHECKING, GRANTED, RATIONALE, DENIED }
private enum class SelectionState { SINGLE, SAME_GROUP, MULTIPLE_GROUPS }

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

    val selectedCount by remember(state.data) {
        derivedStateOf {
            state.data?.duplicates
                ?.flatMap { it.contacts }
                ?.count { it.isSelected } ?: 0
        }
    }

    val selectionState by remember(state.data) {
        derivedStateOf {
            val groups = state.data?.duplicates ?: emptyList()
            val selectedGroups = groups.filter { group -> group.contacts.any { it.isSelected } }
            val total =
                selectedGroups.sumOf { group -> group.contacts.count { contact -> contact.isSelected } }
            when {
                total == 1 -> SelectionState.SINGLE
                selectedGroups.size == 1 -> SelectionState.SAME_GROUP
                else -> SelectionState.MULTIPLE_GROUPS
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = {
                Text(
                    modifier = Modifier.animateContentSize(),
                    text = stringResource(id = R.string.contacts_cleaner_title)
                )
            }, navigationIcon = {
                AnimatedIconButtonDirection(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.go_back),
                    onClick = { activity.finish() })
            }, scrollBehavior = scrollBehavior)
        },
        bottomBar = {
            AnimatedVisibility(
                visible = selectedCount > 0,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomAppBar(
                    actions = {
                        Text(
                            modifier = Modifier.weight(weight = 1f, fill = true),
                            text = pluralStringResource(
                                R.plurals.items_selected,
                                selectedCount,
                                selectedCount
                            ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        SmallHorizontalSpacer()
                        when (selectionState) {
                            SelectionState.SINGLE -> {
                                FilledTonalButton(
                                    onClick = { viewModel.onEvent(ContactsCleanerEvent.DeleteSelectedContacts) }
                                ) { Text(text = stringResource(id = R.string.delete)) }
                            }

                            SelectionState.SAME_GROUP -> {
                                FilledTonalButton(
                                    onClick = { viewModel.onEvent(ContactsCleanerEvent.MergeSelectedContacts) },
                                    enabled = selectedCount >= 2
                                ) { Text(text = stringResource(id = R.string.merge)) }
                                SmallHorizontalSpacer()
                                FilledTonalButton(
                                    onClick = { viewModel.onEvent(ContactsCleanerEvent.DeleteSelectedContacts) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null
                                    )
                                    SmallHorizontalSpacer()
                                    Text(text = stringResource(id = R.string.smart_clean))
                                }
                            }

                            SelectionState.MULTIPLE_GROUPS -> {
                                val canMerge = state.data?.duplicates?.any { group ->
                                    group.contacts.count { it.isSelected } >= 2
                                } ?: false
                                FilledTonalButton(
                                    onClick = { viewModel.onEvent(ContactsCleanerEvent.MergeSelectedContacts) },
                                    enabled = canMerge
                                ) { Text(text = stringResource(id = R.string.merge_groups)) }
                                SmallHorizontalSpacer()
                                FilledTonalButton(
                                    onClick = { viewModel.onEvent(ContactsCleanerEvent.DeleteSelectedContacts) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null
                                    )
                                    SmallHorizontalSpacer()
                                    Text(text = stringResource(id = R.string.smart_clean_all))
                                }
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        when (permissionState) {
            ContactsPermissionState.GRANTED -> {
                ScreenStateHandler(
                    screenState = state,
                    onLoading = {
                        LoadingScreen()
                    },
                    onEmpty = {
                        ContactsEmptyState(paddingValues = paddingValues)
                    },
                    onSuccess = { data: UiContactsCleanerModel ->
                        ContactsCleanerContent(
                            data = data,
                            viewModel = viewModel,
                            paddingValues = paddingValues
                        )
                    },
                    onError = {
                        ContactsErrorState(paddingValues = paddingValues) {
                            viewModel.onEvent(ContactsCleanerEvent.LoadDuplicates)
                        }
                    }
                )

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
}

@Composable
private fun ContactsCleanerContent(
    data: UiContactsCleanerModel,
    viewModel: ContactsCleanerViewModel,
    paddingValues: PaddingValues,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(SizeConstants.LargeSize),
        contentPadding = PaddingValues(vertical = SizeConstants.LargeSize)
    ) {
        stickyHeader {
            val allSelected = data.duplicates.flatMap { it.contacts }.all { it.isSelected }
            val noneSelected = data.duplicates.flatMap { it.contacts }.none { it.isSelected }
            val toggleState = when {
                allSelected -> ToggleableState.On
                noneSelected -> ToggleableState.Off
                else -> ToggleableState.Indeterminate
            }
            val haptic = LocalHapticFeedback.current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SizeConstants.LargeSize),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TriStateCheckbox(
                    state = toggleState,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.onEvent(ContactsCleanerEvent.ToggleSelectAll)
                    }
                )
                Text(text = stringResource(id = R.string.select_all))
            }
        }
        items(
            items = data.duplicates,
            key = { group ->
                group.contacts.firstOrNull()?.rawContactId ?: group.hashCode()
            }) { group ->
            ContactGroupItem(group = group, viewModel = viewModel)
        }
    }
}

@Composable
private fun ContactGroupItem(
    group: DuplicateContactGroup,
    viewModel: ContactsCleanerViewModel
) {
    var isExpanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SizeConstants.LargeSize)
            .animateContentSize(),
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.onEvent(
                            ContactsCleanerEvent.ToggleGroupSelection(group.contacts)
                        )
                    }
                    .padding(SizeConstants.MediumSize),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val allSelected = group.contacts.all { it.isSelected }
                Checkbox(
                    checked = allSelected,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.onEvent(
                            ContactsCleanerEvent.ToggleGroupSelection(group.contacts)
                        )
                    }
                )
                Column(modifier = Modifier.padding(start = SizeConstants.MediumSize)) {
                    Text(text = group.contacts.firstOrNull()?.displayName ?: "")
                    AssistChip(
                        onClick = {},
                        label = { Text(text = "${group.contacts.size} ${stringResource(id = R.string.duplicates)}") })
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SizeConstants.MediumSize),
                    verticalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize)
                ) {
                    group.contacts.forEach { contact ->
                        ContactDetailRow(contact = contact, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactDetailRow(contact: RawContactInfo, viewModel: ContactsCleanerViewModel) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onEvent(
                    ContactsCleanerEvent.ToggleContactSelection(contact)
                )
            }
            .padding(horizontal = SizeConstants.LargeSize),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = contact.isSelected,
            onCheckedChange = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onEvent(
                    ContactsCleanerEvent.ToggleContactSelection(contact)
                )
            }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = SizeConstants.MediumSize)
        ) {
            Text(text = contact.displayName)
            contact.phones.firstOrNull()?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    SmallHorizontalSpacer()
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            contact.emails.firstOrNull()?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    SmallHorizontalSpacer()
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        val icon = when {
            contact.accountType?.contains("sim", true) == true -> Icons.Default.SimCard
            contact.accountType?.contains("google", true) == true -> Icons.Default.AccountCircle
            else -> Icons.Default.Person
        }
        Icon(imageVector = icon, contentDescription = null)
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