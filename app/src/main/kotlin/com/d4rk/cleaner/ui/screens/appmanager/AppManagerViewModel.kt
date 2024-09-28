package com.d4rk.cleaner.ui.screens.appmanager

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import com.d4rk.cleaner.data.model.ui.screens.UiAppManagerModel
import com.d4rk.cleaner.ui.screens.appmanager.repository.AppManagerRepository
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppManagerViewModel(application: Application) : BaseViewModel(application) {
    private val repository = AppManagerRepository(application)
    private val _uiState = MutableStateFlow(UiAppManagerModel())
    val uiState: StateFlow<UiAppManagerModel> = _uiState.asStateFlow()

    private val packageRemovedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_PACKAGE_REMOVED) {
                loadAppData()
            }
        }
    }

    init {
        loadAppData()
        registerPackageRemovedReceiver()
    }

    private fun registerPackageRemovedReceiver() {
        val filter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED)
        filter.addDataScheme("package")
        getApplication<Application>().registerReceiver(packageRemovedReceiver, filter)
    }

    override fun onCleared() {
        getApplication<Application>().unregisterReceiver(packageRemovedReceiver)
        super.onCleared()
    }

    private fun loadAppData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()
            loadInstalledAppsAndApks()
        }.invokeOnCompletion {
            hideLoading()
        }
    }

    private suspend fun loadInstalledAppsAndApks() {
        repository.getInstalledApps { installedApps ->
            viewModelScope.launch {
                val apkFilesDeferred: Deferred<List<ApkInfo>> = async {
                    var apkFiles: List<ApkInfo> = emptyList()
                    repository.getApkFilesFromStorage { files ->
                        apkFiles = files
                    }
                    apkFiles
                }
                val apkFiles: List<ApkInfo> = apkFilesDeferred.await()
                _uiState.value = UiAppManagerModel(installedApps, apkFiles)
            }
        }
    }

    fun installApk(apkPath: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.installApk(apkPath, onSuccess = {})
        }
    }

    fun shareApk(apkPath: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.shareApk(apkPath, onSuccess = { })
        }
    }

    fun shareApp(packageName: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.shareApp(packageName, onSuccess = { })
        }
    }

    fun openAppInfo(packageName: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.openAppInfo(packageName, onSuccess = {})
        }
    }

    fun uninstallApp(packageName: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.uninstallApp(packageName, onSuccess = { })
        }
    }
}