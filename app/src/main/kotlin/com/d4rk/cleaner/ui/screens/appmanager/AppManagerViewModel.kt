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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppManagerViewModel(application : Application) : BaseViewModel(application) {
    private val repository = AppManagerRepository(application)
    private val _uiState = MutableStateFlow(UiAppManagerModel())
    val uiState : StateFlow<UiAppManagerModel> = _uiState.asStateFlow()

    private val packageRemovedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context : Context? , intent : Intent?) {
            if (intent?.action == Intent.ACTION_PACKAGE_REMOVED) {
                val packageName = intent.data?.schemeSpecificPart
                packageName?.let {
                    _uiState.update { currentState ->
                        val updatedInstalledApps = currentState.installedApps.filterNot { it.packageName == packageName }
                        return@update currentState.copy(installedApps = updatedInstalledApps)
                    }
                }
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
        getApplication<Application>().registerReceiver(packageRemovedReceiver , filter)
    }

    override fun onCleared() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            getApplication<Application>().unregisterReceiver(packageRemovedReceiver)
            super.onCleared()
        }
    }

    private fun loadAppData() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            showLoading()
            getInstalledAppsAndApks()
        }.invokeOnCompletion {
            hideLoading()
        }
    }

    private suspend fun getInstalledAppsAndApks() {
        repository.getInstalledAppsRepository { installedApps ->
            viewModelScope.launch(context = coroutineExceptionHandler) {
                val apkFilesDeferred : Deferred<List<ApkInfo>> = async {
                    var apkFiles : List<ApkInfo> = emptyList()
                    repository.getApkFilesFromStorageRepository { files ->
                        apkFiles = files
                    }
                    return@async apkFiles
                }
                val apkFiles : List<ApkInfo> = apkFilesDeferred.await()
                _uiState.update {
                    it.copy(
                        installedApps = installedApps , apkFiles = apkFiles
                    )
                }
            }
        }
    }

    fun installApk(apkPath : String) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.installApkRepository(apkPath , onSuccess = {})
        }
    }

    fun shareApk(apkPath : String) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.shareApkRepository(apkPath , onSuccess = { })
        }
    }

    fun shareApp(packageName : String) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.shareAppRepository(packageName , onSuccess = { })
        }
    }

    fun openAppInfo(packageName : String) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.openAppInfoRepository(packageName , onSuccess = {})
        }
    }

    fun uninstallApp(packageName : String) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.uninstallAppRepository(packageName , onSuccess = { })
        }
    }
}