package com.d4rk.cleaner.app.apps.manager

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ApkInfo
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ui.UiAppManagerModel
import com.d4rk.cleaner.app.apps.manager.domain.usecases.GetApkFilesFromStorageUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.GetInstalledAppsUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.InstallApkUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.OpenAppInfoUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.ShareApkUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.ShareAppUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.UninstallAppUseCase
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import com.d4rk.cleaner.core.domain.model.network.Errors
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import org.junit.jupiter.api.Assertions.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
open class TestAppManagerViewModelBase {
    protected lateinit var dispatcherProvider: TestDispatchers
    protected lateinit var viewModel: AppManagerViewModel

    private lateinit var getInstalledAppsUseCase: GetInstalledAppsUseCase
    private lateinit var getApkFilesFromStorageUseCase: GetApkFilesFromStorageUseCase
    protected lateinit var installApkUseCase: InstallApkUseCase
    protected lateinit var shareApkUseCase: ShareApkUseCase
    protected lateinit var shareAppUseCase: ShareAppUseCase
    protected lateinit var openAppInfoUseCase: OpenAppInfoUseCase
    protected lateinit var uninstallAppUseCase: UninstallAppUseCase

    protected fun setup(
        installedAppsFlow: Flow<DataState<List<ApplicationInfo>, Errors>>,
        apkFilesFlow: Flow<DataState<List<ApkInfo>, Errors>>,
        testDispatcher: TestDispatcher
    ) {
        dispatcherProvider = TestDispatchers(testDispatcher)

        val application: Application = mockk(relaxed = true)
        every { application.applicationContext } returns application
        every { application.registerReceiver(any(), any()) } returns Intent()
        every { application.unregisterReceiver(any()) } returns Unit

        getInstalledAppsUseCase = mockk()
        getApkFilesFromStorageUseCase = mockk()
        installApkUseCase = mockk(relaxed = true)
        shareApkUseCase = mockk(relaxed = true)
        shareAppUseCase = mockk(relaxed = true)
        openAppInfoUseCase = mockk(relaxed = true)
        uninstallAppUseCase = mockk(relaxed = true)

        coEvery { getInstalledAppsUseCase.invoke() } returns installedAppsFlow
        coEvery { getApkFilesFromStorageUseCase.invoke() } returns apkFilesFlow

        viewModel = AppManagerViewModel(
            application,
            getInstalledAppsUseCase,
            getApkFilesFromStorageUseCase,
            installApkUseCase,
            shareApkUseCase,
            shareAppUseCase,
            openAppInfoUseCase,
            uninstallAppUseCase,
            dispatcherProvider
        )
    }

    protected suspend fun Flow<UiStateScreen<UiAppManagerModel>>.testSuccess(
        expectedApps: Int,
        expectedApks: Int,
        testDispatcher: TestDispatcher
    ) {
        this.test {
            val first = awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val second = awaitItem()
            assertEquals(expectedApps, second.data?.installedApps?.size)
            assertEquals(expectedApks, second.data?.apkFiles?.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    protected suspend fun Flow<UiStateScreen<UiAppManagerModel>>.testEmpty(testDispatcher: TestDispatcher) {
        this.test {
            val first = awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val second = awaitItem()
            assertEquals(0, second.data?.installedApps?.size)
            assertEquals(0, second.data?.apkFiles?.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
