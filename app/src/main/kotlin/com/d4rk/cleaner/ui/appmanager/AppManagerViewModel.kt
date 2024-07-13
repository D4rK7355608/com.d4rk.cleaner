package com.d4rk.cleaner.ui.appmanager

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppManagerViewModel(private val application: Application) : ViewModel() {
    private val _installedApps = MutableStateFlow<List<ApplicationInfo>>(emptyList())
    val installedApps: StateFlow<List<ApplicationInfo>> = _installedApps.asStateFlow()

    private val _apkFiles = MutableStateFlow<List<ApkInfo>>(emptyList())
    val apkFiles: StateFlow<List<ApkInfo>> = _apkFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading : StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAppData()
    }

    private fun loadAppData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                awaitAll(
                    async { loadInstalledApps() },
                    async { loadApkFiles() }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _installedApps.value = getInstalledApps()
        }
    }

    private suspend fun getInstalledApps(): List<ApplicationInfo> {
        return withContext(Dispatchers.IO) {
            application.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
    }

    private fun loadApkFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            _apkFiles.value = getApkFilesFromStorage()
        }
    }

    private suspend fun getApkFilesFromStorage(): List<ApkInfo> {
        return withContext(Dispatchers.IO) {
            val apkFiles = mutableListOf<ApkInfo>()
            val uri: Uri = MediaStore.Files.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE
            )
            val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
            val selectionArgs = arrayOf("application/vnd.android.package-archive")
            val cursor: Cursor? = application.contentResolver.query(
                uri, projection, selection, selectionArgs, null
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val path = it.getString(dataColumn)
                    val size = it.getLong(sizeColumn)
                    apkFiles.add(ApkInfo(id, path, size))
                }
            }
            apkFiles
        }
    }
}