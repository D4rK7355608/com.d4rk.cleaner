package com.d4rk.cleaner.app.apps.manager.data

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.ApkInstaller
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.ApkSharer
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppInfoOpener
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppSharer
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppUninstaller
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class AppPackageManagerImpl(private val application : Application) : ApkInstaller , ApkSharer , AppSharer , AppInfoOpener , AppUninstaller {

    override fun installApk(apkPath : String) : Flow<DataState<Unit , Errors>> = flow {
        runCatching {
            val apkFile = File(apkPath)
            val installIntent = Intent(Intent.ACTION_VIEW)
            val contentUri : Uri = FileProvider.getUriForFile(
                application , "${application.packageName}.fileprovider" , apkFile
            )
            installIntent.setDataAndType(
                contentUri , "application/vnd.android.package-archive"
            )
            installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            application.startActivity(installIntent)
        }.onSuccess {
            emit(value = DataState.Success(data = Unit))
        }.onFailure { throwable : Throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_INSTALL_APK)))
        }
    }

    override fun prepareShareIntent(apkPath : String) : Flow<DataState<Intent , Errors>> = flow {
        runCatching {
            val apkFile = File(apkPath)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/vnd.android.package-archive"
            val contentUri : Uri = FileProvider.getUriForFile(
                application , "${application.packageName}.fileprovider" , apkFile
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM , contentUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent
        }.onSuccess { intent : Intent ->
            emit(value = DataState.Success(data = intent))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_SHARE_APK)))
        }
    }

    override fun shareApp(packageName : String) : Flow<DataState<Intent , Errors>> = flow {
        runCatching {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT , "Check out this app")
            val playStoreLink = "https://play.google.com/store/apps/details?id=$packageName"
            val shareMessage = "Check out this app: ${getAppName(packageName)}\n$playStoreLink"
            shareIntent.putExtra(Intent.EXTRA_TEXT , shareMessage)
            shareIntent
        }.onSuccess { intent : Intent ->
            emit(value = DataState.Success(data = intent))
        }.onFailure { throwable : Throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_SHARE_APP)))
        }
    }

    private fun getAppName(packageName : String) : String {
        return runCatching {
            val appInfo : android.content.pm.ApplicationInfo = application.packageManager.getApplicationInfo(packageName , 0)
            appInfo.loadLabel(application.packageManager).toString()
        }.getOrDefault(defaultValue = packageName)
    }

    override fun openAppInfo(packageName : String) : Flow<DataState<Unit , Errors>> = flow {
        runCatching {
            val appInfoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val packageUri : Uri = Uri.fromParts("package" , packageName , null)
            appInfoIntent.data = packageUri
            appInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(appInfoIntent)
        }.onSuccess {
            emit(value = DataState.Success(data = Unit))
        }.onFailure { throwable : Throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_OPEN_APP_INFO)))
        }
    }

    override fun uninstallApp(packageName : String) : Flow<DataState<Unit , Errors>> = flow {
        runCatching {
            val uri : Uri = Uri.fromParts("package" , packageName , null)
            val intent = Intent(Intent.ACTION_DELETE , uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(intent)
        }.onSuccess {
            emit(value = DataState.Success(Unit))
        }.onFailure { throwable : Throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_UNINSTALL_APP)))
        }
    }
}