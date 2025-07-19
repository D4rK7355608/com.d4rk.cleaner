package com.d4rk.cleaner.app.contacts.cleaner.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.d4rk.cleaner.app.contacts.cleaner.domain.actions.ContactsCleanerEvent
import com.d4rk.cleaner.app.contacts.cleaner.domain.data.model.RawContactInfo
import com.d4rk.cleaner.app.contacts.cleaner.domain.data.model.UiContactsCleanerModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsCleanerScreen(activity: Activity) {
    val viewModel: ContactsCleanerViewModel = koinViewModel()
    val state = viewModel.uiState.collectAsState().value
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.contacts_cleaner_title),
        onBackClicked = { activity.finish() },
        scrollBehavior = scrollBehavior
    ) { padding ->
        ScreenStateHandler(
            screenState = state,
            onLoading = { LoadingScreen() },
            onEmpty = { NoDataScreen(textMessage = R.string.no_duplicates_found) },
            onSuccess = @Composable { data: UiContactsCleanerModel ->
                ContactsCleanerContent(
                    data = data,
                    viewModel = viewModel,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            }
        )
    }

    LifecycleEventsEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onEvent(ContactsCleanerEvent.LoadDuplicates)
    }
}

@Composable
private fun ContactsCleanerContent(
    data: UiContactsCleanerModel,
    viewModel: ContactsCleanerViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
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
