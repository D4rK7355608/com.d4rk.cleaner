package com.d4rk.cleaner.ui.appmanager.repository

import android.app.Application
import android.content.pm.ApplicationInfo
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppManagerRepository(application: Application) : AppManagerRepositoryImplementation(application) {

    suspend fun getInstalledApps(onSuccess: (List<ApplicationInfo>) -> Unit) {
        withContext(Dispatchers.IO) {
            val installedApps = getInstalledAppsFromPackageManager()
            withContext(Dispatchers.Main) {
                onSuccess(installedApps)
            }
        }
    }

    suspend fun getApkFilesFromStorage(onSuccess: (List<ApkInfo>) -> Unit) {
        withContext(Dispatchers.IO) {
            val apkFiles = getApkFilesFromMediaStore()
            withContext(Dispatchers.Main) {
                onSuccess(apkFiles)
            }
        }
    }
}