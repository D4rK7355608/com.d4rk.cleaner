package com.d4rk.cleaner.app.apps.manager.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.dismissSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.domain.actions.AppManagerAction
import com.d4rk.cleaner.app.apps.manager.domain.actions.AppManagerEvent
import com.d4rk.cleaner.app.apps.manager.domain.data.model.AppManagerItem
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ui.UiAppManagerModel
import com.d4rk.cleaner.app.apps.manager.domain.usecases.GetApkFilesFromStorageUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.GetAppsLastUsedUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.GetInstalledAppsUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.InstallApkUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.OpenAppInfoUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.ShareApkUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.ShareAppUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.UninstallAppUseCase
import com.d4rk.cleaner.core.utils.helpers.CleaningEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppManagerViewModel(
    application : Application ,
    private val getInstalledAppsUseCase : GetInstalledAppsUseCase ,
    private val getApkFilesFromStorageUseCase : GetApkFilesFromStorageUseCase ,
    private val getAppsLastUsedUseCase: GetAppsLastUsedUseCase,
    private val installApkUseCase : InstallApkUseCase ,
    private val shareApkUseCase : ShareApkUseCase ,
    private val shareAppUseCase : ShareAppUseCase ,
    private val openAppInfoUseCase : OpenAppInfoUseCase ,
    private val uninstallAppUseCase : UninstallAppUseCase ,
    private val dispatchers : DispatcherProvider
) : ScreenViewModel<UiAppManagerModel , AppManagerEvent , AppManagerAction>(initialState = UiStateScreen(data = UiAppManagerModel())) {

    private val applicationContext : Context = application.applicationContext

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var pendingUninstallPackage: String? = null
    private var pendingInstallPackage: String? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private val packageRemovedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_PACKAGE_REMOVED) {
                val packageName = intent.data?.schemeSpecificPart
                val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                packageName?.let {
                    _uiState.update { currentState ->
                        val updatedInstalledApps = currentState.data?.installedApps?.filterNot { it.packageName == packageName } ?: emptyList()
                        currentState.copy(data = currentState.data?.copy(installedApps = updatedInstalledApps))
                    }

                    if (packageName == pendingUninstallPackage && !replacing) {
                        pendingUninstallPackage = null
                        postSnackbar(
                            message = UiTextHelper.StringResource(R.string.app_uninstalled_successfully),
                            isError = false
                        )
                    }

                    onEvent(AppManagerEvent.LoadAppData)
                }
            }
        }
    }

    private val packageAddedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_PACKAGE_ADDED) {
                val packageName = intent.data?.schemeSpecificPart
                val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                packageName?.let {
                    if (packageName == pendingInstallPackage && !replacing) {
                        pendingInstallPackage = null
                        postSnackbar(
                            message = UiTextHelper.StringResource(R.string.apk_installed_successfully),
                            isError = false
                        )
                    }
                    onEvent(AppManagerEvent.LoadAppData)
                }
            }
        }
    }

    init {
        onEvent(AppManagerEvent.LoadAppData)
        registerPackageRemovedReceiver()
        registerPackageAddedReceiver()
        launch(dispatchers.io) {
            CleaningEventBus.events.collectLatest {
                onEvent(AppManagerEvent.LoadAppData)
            }
        }
    }

    override fun onEvent(event : AppManagerEvent) {
        when (event) {
            AppManagerEvent.LoadAppData -> loadAppData()
            is AppManagerEvent.ShareItem -> handleShareItem(event.item)
            is AppManagerEvent.DismissSnackbar -> screenState.dismissSnackbar()
        }
    }

    private fun registerPackageRemovedReceiver() {
        val filter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED)
        filter.addDataScheme("package")
        applicationContext.registerReceiver(packageRemovedReceiver , filter)
    }

    private fun registerPackageAddedReceiver() {
        val filter = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
        filter.addDataScheme("package")
        applicationContext.registerReceiver(packageAddedReceiver, filter)
    }

    override fun onCleared() {
        applicationContext.unregisterReceiver(packageRemovedReceiver)
        applicationContext.unregisterReceiver(packageAddedReceiver)
        super.onCleared()
    }

    private fun loadAppData() {
        launch {
            val installedAppsFlow = getInstalledAppsUseCase().flowOn(dispatchers.default).onEach { result ->
                _uiState.update { currentState ->
                    when (result) {
                        is DataState.Loading -> currentState.copy(data = currentState.data?.copy(userAppsLoading = true , systemAppsLoading = true))

                        is DataState.Success -> {
                            currentState.copy(data = currentState.data?.copy(installedApps = result.data.sortedBy {
                                applicationContext.packageManager.getApplicationLabel(it).toString().lowercase()
                            } , userAppsLoading = false , systemAppsLoading = false))
                        }

                        is DataState.Error -> currentState.copy(data = currentState.data?.copy(userAppsLoading = false , systemAppsLoading = false))
                    }
                }
            }

            val apkFilesFlow = getApkFilesFromStorageUseCase().flowOn(dispatchers.default).onEach { result ->
                _uiState.update { currentState ->
                    when (result) {
                        is DataState.Loading -> currentState.copy(data = currentState.data?.copy(apkFilesLoading = true))

                        is DataState.Success -> currentState.copy(data = currentState.data?.copy(apkFiles = result.data.sortedBy {
                            it.path.substringAfterLast('/').lowercase()
                        } , apkFilesLoading = false))

                        is DataState.Error -> currentState.copy(data = currentState.data?.copy(apkFilesLoading = false))
                    }
                }
            }

            val usageStatsFlow = getAppsLastUsedUseCase().flowOn(dispatchers.default).onEach { result ->
                _uiState.update { currentState ->
                    when (result) {
                        is DataState.Success -> currentState.copy(data = currentState.data?.copy(appUsageStats = result.data))
                        else -> currentState
                    }
                }
            }

            launch(dispatchers.io) { installedAppsFlow.collectLatest {} }
            launch(dispatchers.io) { apkFilesFlow.collectLatest {} }
            launch(dispatchers.io) { usageStatsFlow.collectLatest {} }
        }
    }

    fun installApk(apkPath : String) {
        launch(context = dispatchers.io) {
            val packageName = applicationContext.packageManager.getPackageArchiveInfo(apkPath, 0)?.packageName
            pendingInstallPackage = packageName
            installApkUseCase(apkPath).collectLatest { result ->
                when (result) {
                    is DataState.Error -> {
                        pendingInstallPackage = null
                        postSnackbar(message = UiTextHelper.StringResource(R.string.failed_to_install_apk) , isError = true)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleShareItem(item : AppManagerItem) {
        launch(dispatchers.io) {
            when (item) {
                is AppManagerItem.ApkFile -> {
                    shareApkUseCase(item.path).collectLatest { result ->
                        when (result) {
                            is DataState.Success -> sendAction(AppManagerAction.LaunchShareIntent(result.data))
                            is DataState.Error -> postSnackbar(message = UiTextHelper.StringResource(R.string.share_apk_failed) , isError = true)
                            else -> {}
                        }
                    }
                }

                is AppManagerItem.InstalledApp -> {
                    shareAppUseCase(item.packageName).collectLatest { result ->
                        when (result) {
                            is DataState.Success -> sendAction(AppManagerAction.LaunchShareIntent(result.data))
                            is DataState.Error -> postSnackbar(message = UiTextHelper.StringResource(R.string.share_app_failed) , isError = true)
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    fun openAppInfo(packageName : String) {
        launch(context = dispatchers.io) {
            openAppInfoUseCase(packageName).collectLatest { result ->
                when (result) {
                    is DataState.Error -> postSnackbar(message = UiTextHelper.StringResource(R.string.failed_to_open_app_info) , isError = true)
                    else -> {}
                }
            }
        }
    }

    fun uninstallApp(packageName : String) {
        launch(context = dispatchers.io) {
            pendingUninstallPackage = packageName
            uninstallAppUseCase(packageName).collectLatest { result ->
                when (result) {
                    is DataState.Error -> {
                        pendingUninstallPackage = null
                        postSnackbar(message = UiTextHelper.StringResource(R.string.failed_to_uninstall_app) , isError = true)
                    }
                    else -> {}
                }
            }
        }
    }

    fun getLastUsedTime(packageName: String): Long? {
        return _uiState.value.data?.appUsageStats?.get(packageName)
    }

    private fun postSnackbar(message : UiTextHelper , isError : Boolean) {
        screenState.showSnackbar(snackbar = UiSnackbar(message = message , isError = isError , timeStamp = System.currentTimeMillis() , type = ScreenMessageType.SNACKBAR))
    }
}