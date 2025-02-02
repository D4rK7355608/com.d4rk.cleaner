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

    suspend fun getInstalledApps(onSuccess : (List<ApplicationInfo>) -> Unit) {
        withContext(Dispatchers.IO) {
            val installedApps : List<ApplicationInfo> = getInstalledAppsFromPackageManager()
            withContext(Dispatchers.Main) {
                onSuccess(installedApps)
            }
        }
    }

    suspend fun getApkFilesFromStorage(onSuccess : (List<ApkInfo>) -> Unit) {
        withContext(Dispatchers.IO) {
            val apkFiles : List<ApkInfo> = getApkFilesFromMediaStore()
            withContext(Dispatchers.Main) {
                onSuccess(apkFiles)
            }
        }
    }

    suspend fun installApk(apkPath : String , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            installApk(apkPath)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun shareApk(apkPath : String , onSuccess : () -> Unit) {
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

    suspend fun shareApp(packageName : String , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            val shareIntent : Intent = shareApp(packageName)
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

    suspend fun openAppInfo(packageName : String , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            openAppInfo(packageName)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun uninstallApp(packageName : String , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            uninstallApp(packageName)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}