package com.d4rk.cleaner.app.apps.manager.data

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ApkInfo
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.ApkFileManager
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import com.d4rk.cleaner.core.utils.helpers.DirectoryScanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApkFileManagerImpl(private val application: Application) : ApkFileManager {
    override fun getApkFilesFromStorage(): Flow<DataState<List<ApkInfo>, Errors>> = flow {
        runCatching {
            val apkFiles: MutableList<ApkInfo> = mutableListOf()
            val addedPaths: MutableSet<String> = mutableSetOf()
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
                    addedPaths.add(path)
                }
            }

            DirectoryScanner.scan(Environment.getExternalStorageDirectory()) { file ->
                if (file.extension.equals("apk", ignoreCase = true)) {
                    val path = file.absolutePath
                    if (addedPaths.add(path)) {
                        apkFiles.add(ApkInfo(file.hashCode().toLong(), path, file.length()))
                    }
                }
            }
            apkFiles
        }.onSuccess { apkFiles: MutableList<ApkInfo> ->
            emit(value = DataState.Success(data = apkFiles))
        }.onFailure { throwable: Throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_GET_APK_FILES)))
        }
    }
}
