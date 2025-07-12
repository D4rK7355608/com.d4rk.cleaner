package com.d4rk.cleaner.app.main.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.main.domain.usecases.PerformInAppUpdateUseCase
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.Errors
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.successData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter
import com.d4rk.cleaner.app.clean.trash.domain.usecases.GetTrashSizeUseCase
import com.d4rk.cleaner.app.main.domain.actions.MainAction
import com.d4rk.cleaner.app.main.domain.actions.MainEvent
import com.d4rk.cleaner.app.main.domain.model.UiMainScreen

class MainViewModel(
    private val performInAppUpdateUseCase : PerformInAppUpdateUseCase,
    private val getTrashSizeUseCase: GetTrashSizeUseCase,
) : ScreenViewModel<UiMainScreen , MainEvent , MainAction>(initialState = UiStateScreen(data = UiMainScreen())) {

    init {
        onEvent(event = MainEvent.LoadNavigation)
    }

    override fun onEvent(event : MainEvent) {
        when (event) {
            is MainEvent.LoadNavigation -> loadNavigationItems()
            is MainEvent.CheckForUpdates -> checkAppUpdate()
        }
    }

    private fun checkAppUpdate() {
        launch {
            performInAppUpdateUseCase(param = Unit).collect { _ : DataState<Int , Errors> -> }
        }
    }

    private fun loadNavigationItems() {
        launch {
            val trashSize = getTrashSizeUseCase()
            val trashBadge = if (trashSize > 0) FileSizeFormatter.format(trashSize) else ""

            screenState.successData {
                copy(
                    navigationDrawerItems = listOf(
                        NavigationDrawerItem(
                            title = com.d4rk.cleaner.R.string.image_optimizer ,
                            selectedIcon = Icons.Outlined.Photo ,
                        ) , NavigationDrawerItem(
                            title = com.d4rk.cleaner.R.string.trash ,
                            selectedIcon = Icons.Outlined.Delete ,
                            badgeText = trashBadge ,
                        ) , NavigationDrawerItem(
                            title = com.d4rk.cleaner.R.string.large_files ,
                            selectedIcon = Icons.Outlined.Folder ,
                        ) , NavigationDrawerItem(
                            title = R.string.settings ,
                            selectedIcon = Icons.Outlined.Settings ,
                        ) , NavigationDrawerItem(
                            title = R.string.help_and_feedback ,
                            selectedIcon = Icons.AutoMirrored.Outlined.HelpOutline ,
                        ) , NavigationDrawerItem(
                            title = R.string.updates ,
                            selectedIcon = Icons.AutoMirrored.Outlined.EventNote ,
                        ) , NavigationDrawerItem(
                            title = R.string.share ,
                            selectedIcon = Icons.Outlined.Share ,
                        )
                    )
                )
            }
        }
    }
}