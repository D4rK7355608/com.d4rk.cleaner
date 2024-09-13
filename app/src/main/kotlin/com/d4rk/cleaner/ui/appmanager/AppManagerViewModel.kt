package com.d4rk.cleaner.ui.appmanager

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.error.ErrorType
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import com.d4rk.cleaner.data.model.ui.error.UiErrorModel
import com.d4rk.cleaner.data.model.ui.screens.UiAppManagerModel
import com.d4rk.cleaner.ui.appmanager.repository.AppManagerRepository
import com.d4rk.cleaner.utils.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppManagerViewModel(application: Application) : BaseViewModel(application) {
    private val repository = AppManagerRepository(application)
    private val _uiState = MutableStateFlow(UiAppManagerModel())
    val uiState: StateFlow<UiAppManagerModel> = _uiState.asStateFlow()

    private val packageRemovedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context? , intent: Intent?) {
            if (intent?.action == Intent.ACTION_PACKAGE_REMOVED) {
                viewModelScope.launch {
                    loadAppData()
                }
            }
        }
    }

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            loadAppData()
        }

        val filter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED)
        filter.addDataScheme("package")
        getApplication<Application>().registerReceiver(packageRemovedReceiver, filter)
    }

    override fun onCleared() {
        getApplication<Application>().unregisterReceiver(packageRemovedReceiver)
        super.onCleared()
    }

    private suspend fun loadAppData() {
        showLoading()
        try {
            val installedApps: List<ApplicationInfo> = loadInstalledApps()
            val apkFiles: List<ApkInfo> = loadApkFiles()
            _uiState.value = UiAppManagerModel(installedApps, apkFiles)
        } catch (e: Exception) {
            handleError(ErrorType.APP_LOADING_ERROR, e)
        } finally {
            hideLoading()
        }
    }

    private suspend fun loadInstalledApps(): List<ApplicationInfo> {
        var installedApps: List<ApplicationInfo> = emptyList()
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getInstalledApps { apps ->
                installedApps = apps
            }
        }
        return installedApps
    }

    private suspend fun loadApkFiles(): List<ApkInfo> {
        var apkFiles: List<ApkInfo> = emptyList()
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getApkFilesFromStorage { files ->
                apkFiles = files
            }
        }
        return apkFiles
    }

    override fun handleError(errorType: ErrorType, exception: Throwable) {
        when (errorType) {
            ErrorType.APP_LOADING_ERROR -> _uiState.value = _uiState.value.copy(
                error = UiErrorModel(
                    showErrorDialog = true,
                    errorMessage = exception.message
                        ?: getApplication<Application>().getString(R.string.app_loading_error)
                )
            )
            else -> {
                _uiState.value = _uiState.value.copy(
                    error = UiErrorModel(
                        showErrorDialog = true,
                        errorMessage = getApplication<Application>().getString(R.string.unknown_error)
                    )
                )
            }
        }
    }
}