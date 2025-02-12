package com.d4rk.cleaner.ui.screens.appmanager.repository

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppManagerRepository(application : Application) :
    AppManagerRepositoryImplementation(application) {

    suspend fun getInstalledAppsRepository(onSuccess : (List<ApplicationInfo>) -> Unit) {
        withContext(Dispatchers.IO) {
            val installedApps : List<ApplicationInfo> = getInstalledAppsImplementation()
            withContext(Dispatchers.Main) {
                onSuccess(installedApps)
            }
        }
    }

    suspend fun getApkFilesFromStorageRepository(onSuccess : (List<ApkInfo>) -> Unit) {
        withContext(Dispatchers.IO) {
            val apkFiles : List<ApkInfo> = getApkFilesFromStorageImplementation()
            withContext(Dispatchers.Main) {
                onSuccess(apkFiles)
            }
        }
    }

    suspend fun installApkRepository(apkPath : String , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            installApkImplementation(apkPath)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun shareApkRepository(apkPath : String , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            val shareIntent : Intent = prepareShareIntent(apkPath)
            withContext(Dispatchers.Main) {
                val chooserIntent : Intent = Intent.createChooser(
                    shareIntent , application.getString(R.string.share_apk)
                )
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                application.startActivity(chooserIntent)
                onSuccess()
            }
        }
    }

    suspend fun shareAppRepository(packageName : String , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            val shareIntent : Intent = shareAppImplementation(packageName)
            withContext(Dispatchers.Main) {
                val chooserIntent : Intent = Intent.createChooser(
                    shareIntent , application.getString(com.d4rk.android.libs.apptoolkit.R.string.share)
                )
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                application.startActivity(chooserIntent)
                onSuccess()
            }
        }
    }

    suspend fun openAppInfoRepository(packageName : String , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            openAppInfoImplementation(packageName)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun uninstallAppRepository(packageName : String , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            uninstallAppImplementation(packageName)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}