package com.d4rk.cleaner.ui.screens.main

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.data.model.ui.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.notifications.managers.AppUpdateNotificationsManager
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.model.ui.navigation.BottomNavigationScreen
import com.d4rk.cleaner.data.model.ui.screens.UiMainScreen
import com.d4rk.cleaner.ui.screens.main.repository.MainRepository
import com.d4rk.cleaner.ui.screens.startup.StartupActivity
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import com.google.android.play.core.appupdate.AppUpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(application : Application) : BaseViewModel(application) {
    private val repository = MainRepository(
        dataStore = AppCoreManager.dataStore , application = application
    )
    private val _uiState : MutableStateFlow<UiMainScreen> = MutableStateFlow(initializeUiState())
    val uiState : StateFlow<UiMainScreen> = _uiState

    fun loadTrashSize() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.dataStore.trashSize.collect { trashSize ->
                _uiState.update { it.copy(trashSize = StorageUtils.formatSize(trashSize)) }
            }
        }
    }

    fun checkForUpdates(updateResultLauncher : ActivityResultLauncher<IntentSenderRequest> , appUpdateManager : AppUpdateManager) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.checkForUpdates(
                appUpdateManager = appUpdateManager , updateResultLauncher = updateResultLauncher
            )
        }
    }

    private fun initializeUiState() : UiMainScreen {
        return UiMainScreen(
            navigationDrawerItems = listOf(
                NavigationDrawerItem(
                    title = com.d4rk.android.libs.apptoolkit.R.string.settings ,
                    selectedIcon = Icons.Outlined.Settings ,
                ) , NavigationDrawerItem(
                    title = com.d4rk.android.libs.apptoolkit.R.string.help_and_feedback ,
                    selectedIcon = Icons.AutoMirrored.Outlined.HelpOutline ,
                ) , NavigationDrawerItem(
                    title = com.d4rk.android.libs.apptoolkit.R.string.updates ,
                    selectedIcon = Icons.AutoMirrored.Outlined.EventNote ,
                ) , NavigationDrawerItem(
                    title = com.d4rk.android.libs.apptoolkit.R.string.share ,
                    selectedIcon = Icons.Outlined.Share ,
                )
            ) , bottomNavigationItems = listOf(
                BottomNavigationScreen.Home , BottomNavigationScreen.AppManager , BottomNavigationScreen.MemoryManager
            ) , currentBottomNavigationScreen = BottomNavigationScreen.Home
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkAndScheduleUpdateNotifications(appUpdateNotificationsManager : AppUpdateNotificationsManager) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.checkAndScheduleUpdateNotificationsRepository(appUpdateNotificationsManager = appUpdateNotificationsManager)
        }
    }

    fun checkAppUsageNotifications(context : Context) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.checkAppUsageNotificationsRepository(context = context)
        }
    }

    fun checkAndHandleStartup() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.checkAndHandleStartupRepository { isFirstTime ->
                if (isFirstTime) {
                    com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openActivity(
                        context = getApplication() , activityClass = StartupActivity::class.java
                    )
                }
            }
        }
    }

    fun configureSettings() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.setupSettingsRepository()
        }
    }

    fun updateBottomNavigationScreen(newScreen : BottomNavigationScreen) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.value = _uiState.value.copy(currentBottomNavigationScreen = newScreen)
        }
    }
}