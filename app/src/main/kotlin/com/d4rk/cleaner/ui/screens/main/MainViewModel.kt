package com.d4rk.cleaner.ui.screens.main

import android.app.Activity
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiMainModel
import com.d4rk.cleaner.notifications.managers.AppUpdateNotificationsManager
import com.d4rk.cleaner.ui.screens.main.repository.MainRepository
import com.d4rk.cleaner.ui.screens.startup.StartupActivity
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import com.d4rk.cleaner.utils.IntentUtils
import com.google.android.play.core.appupdate.AppUpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(application : Application) : BaseViewModel(application) {
    private val repository = MainRepository(DataStore(application) , application)
    private val _uiState = MutableStateFlow(UiMainModel())
    val uiState : StateFlow<UiMainModel> = _uiState

    fun loadTrashSize() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getTrashSize { trashSize ->
                _uiState.update { it.copy(trashSize = trashSize) }
            }
        }
    }

    fun checkForUpdates(activity : Activity , appUpdateManager : AppUpdateManager) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.checkForUpdates(
                appUpdateManager = appUpdateManager , activity = activity
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkAndScheduleUpdateNotifications(appUpdateNotificationsManager : AppUpdateNotificationsManager) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.checkAndScheduleUpdateNotifications(appUpdateNotificationsManager)
        }
    }

    fun checkAppUsageNotifications() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.checkAppUsageNotifications()
        }
    }

    fun checkAndHandleStartup() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.checkAndHandleStartup { isFirstTime ->
                if (isFirstTime) {
                    IntentUtils.openActivity(getApplication() , StartupActivity::class.java)
                }
            }
        }
    }

    fun configureSettings() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.setupSettings()
        }
    }
}