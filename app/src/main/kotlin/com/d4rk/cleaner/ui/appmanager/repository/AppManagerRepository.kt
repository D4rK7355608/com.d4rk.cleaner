package com.d4rk.cleaner.ui.appmanager.repository

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppManagerRepository(private val application: Application) {
    suspend fun getInstalledApps(): List<ApplicationInfo> =
        withContext(Dispatchers.IO) {
            application.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }

    suspend fun getApkFilesFromStorage(): List<ApkInfo> =
        withContext(Dispatchers.IO) {
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
            apkFiles
        }
}