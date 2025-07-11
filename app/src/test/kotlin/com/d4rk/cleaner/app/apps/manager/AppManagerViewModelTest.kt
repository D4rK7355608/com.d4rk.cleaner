package com.d4rk.cleaner.app.apps.manager

import android.content.pm.ApplicationInfo
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ApkInfo
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class AppManagerViewModelTest : TestAppManagerViewModelBase() {

    companion object {
        @JvmField
        @RegisterExtension
        val dispatcherExtension = MainDispatcherExtension()
    }

    @Test
    fun `load data - success lists`() = runTest(dispatcherExtension.testDispatcher) {
        val apps = listOf(ApplicationInfo())
        val apkFiles = listOf(ApkInfo("/path/app.apk"))
        val installedFlow = flow {
            emit(DataState.Loading<List<ApplicationInfo>, Errors>())
            emit(DataState.Success<List<ApplicationInfo>, Errors>(apps))
        }
        val apkFlow = flow {
            emit(DataState.Loading<List<ApkInfo>, Errors>())
            emit(DataState.Success<List<ApkInfo>, Errors>(apkFiles))
        }
        setup(installedAppsFlow = installedFlow, apkFilesFlow = apkFlow, testDispatcher = dispatcherExtension.testDispatcher)
        viewModel.uiState.testSuccess(expectedApps = apps.size, expectedApks = apkFiles.size, testDispatcher = dispatcherExtension.testDispatcher)
    }

    @Test
    fun `load data - empty lists`() = runTest(dispatcherExtension.testDispatcher) {
        val installedFlow = flow {
            emit(DataState.Loading<List<ApplicationInfo>, Errors>())
            emit(DataState.Success<List<ApplicationInfo>, Errors>(emptyList()))
        }
        val apkFlow = flow {
            emit(DataState.Loading<List<ApkInfo>, Errors>())
            emit(DataState.Success<List<ApkInfo>, Errors>(emptyList()))
        }
        setup(installedAppsFlow = installedFlow, apkFilesFlow = apkFlow, testDispatcher = dispatcherExtension.testDispatcher)
        viewModel.uiState.testEmpty(testDispatcher = dispatcherExtension.testDispatcher)
    }

    @Test
    fun `install apk triggers use case`() = runTest(dispatcherExtension.testDispatcher) {
        val installedFlow = flow {
            emit(DataState.Loading<List<ApplicationInfo>, Errors>())
            emit(DataState.Success<List<ApplicationInfo>, Errors>(emptyList()))
        }
        val apkFlow = flow {
            emit(DataState.Loading<List<ApkInfo>, Errors>())
            emit(DataState.Success<List<ApkInfo>, Errors>(emptyList()))
        }
        setup(installedAppsFlow = installedFlow, apkFilesFlow = apkFlow, testDispatcher = dispatcherExtension.testDispatcher)
        val path = "some.apk"
        viewModel.installApk(path)
        dispatcherExtension.testDispatcher.scheduler.advanceUntilIdle()
        io.mockk.coVerify { installApkUseCase.invoke(path) }
    }
}
