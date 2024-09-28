package com.d4rk.cleaner.ui.screens.appmanager.repository

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.FileProvider
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import java.io.File

/**
 * Abstract base class for repository implementations related to application management.
 *
 * This class provides common functionality for interacting with installed applications, APK files,
 * and the Android system for app management tasks.
 *
 * @property application The application context.
 */
abstract class AppManagerRepositoryImplementation(val application: Application) {

    /**
     * Retrieves a list of all installed applications on the device.
     *
     * @return A list of [ApplicationInfo] objects representing the installed applications.
     */
    fun getInstalledAppsFromPackageManager(): List<ApplicationInfo> {
        return application.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    /**
     * Retrieves a list of APK files from the device's media store.
     *
     * @return A list of [ApkInfo] objects representing the found APK files.
     */
    fun getApkFilesFromMediaStore(): List<ApkInfo> {
        val apkFiles: MutableList<ApkInfo> = mutableListOf()
        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection: Array<String> = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE
        )
        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
        val selectionArgs: Array<String> = arrayOf("application/vnd.android.package-archive")
        val cursor: Cursor? = application.contentResolver.query(
            uri, projection, selection, selectionArgs, null
        )

        cursor?.use {
            val idColumn: Int = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn: Int = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val sizeColumn: Int = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (it.moveToNext()) {
                val id: Long = it.getLong(idColumn)
                val path: String = it.getString(dataColumn)
                val size: Long = it.getLong(sizeColumn)
                apkFiles.add(ApkInfo(id, path, size))
            }
        }
        return apkFiles
    }

    /**
     * Initiates the installation process for an APK file.
     *
     * @param apkPath The file path of the APK to be installed.
     */
    fun installApk(apkPath: String) {
        val apkFile = File(apkPath)
        val installIntent = Intent(Intent.ACTION_VIEW)
        val contentUri: Uri = FileProvider.getUriForFile(
            application, "${application.packageName}.fileprovider", apkFile
        )
        installIntent.setDataAndType(
            contentUri, "application/vnd.android.package-archive"
        )
        installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        application.startActivity(installIntent)
    }

    /**
     * Creates an intent for sharing an APK file.
     *
     * @param apkPath The file path of the APK to be shared.
     * @return An [Intent] configured for sharing the APK.
     */
    fun prepareShareIntent(apkPath: String): Intent {
        val apkFile = File(apkPath)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/vnd.android.package-archive"
        val contentUri: Uri = FileProvider.getUriForFile(
            application, "${application.packageName}.fileprovider", apkFile
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return shareIntent
    }

    /**
     * Creates an intent for sharing an application via its Play Store link.
     *
     * @param packageName The package name of the application to be shared.
     * @return An [Intent] configured for sharing the app.
     */
    fun shareApp(packageName: String): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app")
        val playStoreLink = "https://play.google.com/store/apps/details?id=$packageName"
        val shareMessage = "Check out this app: ${getAppName(packageName)}\n$playStoreLink"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        return shareIntent
    }

    /**
     * Retrieves the user-friendly name of an application.
     *
     * @param packageName The package name of the application.
     * @return The application name, or the package name if the name cannot be found.
     */
    private fun getAppName(packageName: String): String {
        return try {
            val appInfo: ApplicationInfo =
                application.packageManager.getApplicationInfo(packageName, 0)
            appInfo.loadLabel(application.packageManager).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    /**
     * Opens the application info screen in system settings for a given package.
     *
     * @param packageName The package name of the application.
     */
    fun openAppInfo(packageName: String) {
        val appInfoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageUri: Uri = Uri.fromParts("package", packageName, null)
        appInfoIntent.data = packageUri
        appInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(appInfoIntent)
    }

    /**
     * Initiates the uninstallation process for an application.
     *
     * @param packageName The package name of the application to be uninstalled.
     */
    fun uninstallApp(packageName: String) {
        val uri: Uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Intent.ACTION_DELETE, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }
}