package com.d4rk.cleaner.ui.memory

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.math.log10
import kotlin.math.pow

class MemoryManagerViewModel : ViewModel() {
    private val _storageInfo = MutableStateFlow(StorageInfo())
    val storageInfo : StateFlow<StorageInfo> = _storageInfo.asStateFlow()

    fun updateStorageInfo(context : Context) {
        viewModelScope.launch {
            _storageInfo.value = getStorageInfo(context)
        }
    }

    private suspend fun getStorageInfo(context : Context) : StorageInfo =
            withContext(Dispatchers.IO) {
                val storageManager =
                        context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                val storageStatsManager =
                        context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                val storageVolume = storageManager.primaryStorageVolume
                val totalSize : Long
                val usedSize : Long
                val freeSize : Long
                val uuidStr = storageVolume.uuid
                val uuid : UUID = if (uuidStr == null) StorageManager.UUID_DEFAULT
                else UUID.fromString(uuidStr)
                totalSize = storageStatsManager.getTotalBytes(uuid)
                freeSize = storageStatsManager.getFreeBytes(uuid)
                usedSize = totalSize - freeSize
                val storageBreakdown = getStorageBreakdown(context)
                StorageInfo(
                    totalStorage = totalSize ,
                    freeStorage = freeSize ,
                    usedStorage = usedSize ,
                    storageBreakdown = storageBreakdown
                )
            }


    private fun getInternalStorageInfo(): InternalStorageInfo {
        val statFs = StatFs(Environment.getDataDirectory().path)
        val blockSizeBytes = statFs.blockSizeLong
        val totalBlocks = statFs.blockCountLong
        val availableBlocks = statFs.availableBlocksLong

        val totalStorage = totalBlocks * blockSizeBytes
        val freeStorage = availableBlocks * blockSizeBytes
        val usedStorage = totalStorage - freeStorage

        return InternalStorageInfo(totalStorage, freeStorage, usedStorage)
    }

    private fun getStorageBreakdown(context: Context): Map<String, Long> {
        val breakdown = mutableMapOf<String, Long>()
        val externalStoragePath = Environment.getExternalStorageDirectory().absolutePath

        breakdown["Installed Apps"] = getInstalledAppsSize(context)
        breakdown["System"] = getDirectorySize(Environment.getRootDirectory())
        breakdown["Music"] = getDirectorySize(File(externalStoragePath, "Music"))
        breakdown["Images"] = getDirectorySize(File(externalStoragePath, "DCIM")) +
                getDirectorySize(File(externalStoragePath, "Pictures"))
        breakdown["Documents"] = getDirectorySize(File(externalStoragePath, "Documents"))
        breakdown["Downloads"] = getDirectorySize(File(externalStoragePath, "Download")) // Use "Download"
        breakdown["Other Files"] = getOtherFilesSize(breakdown)

        return breakdown
    }

    private fun getInstalledAppsSize(context: Context): Long {
        val packageManager = context.packageManager
        val installedApps = packageManager.getInstalledApplications(0)
        var installedAppsSize = 0L
        for (app in installedApps) {
            installedAppsSize += getApkSize(context, app.packageName)
        }
        return installedAppsSize
    }

    private fun getApkSize(context: Context, packageName: String): Long {
        return try {
            context.packageManager.getApplicationInfo(
                packageName,
                0
            ).sourceDir.let { File(it).length() }
        } catch (e: Exception) {
            0L
        }
    }

    private fun getDirectorySize(directory: File?): Long {
        if (directory == null || !directory.exists() || !directory.isDirectory) return 0
        var size = 0L
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                size += if (file.isDirectory) {
                    getDirectorySize(file)
                } else {
                    file.length()
                }
            }
        }
        return size
    }

    private fun getOtherFilesSize(breakdown : MutableMap<String , Long>): Long {
        val totalUsedStorage = getInternalStorageInfo().usedStorage
        val calculatedSize = breakdown.values.sum()
        return totalUsedStorage - calculatedSize
    }
}

data class InternalStorageInfo(
    val totalStorage: Long,
    val freeStorage: Long,
    val usedStorage: Long
)

data class StorageInfo(
    val totalStorage: Long = 0,
    val freeStorage: Long = 0,
    val usedStorage: Long = 0,
    val storageBreakdown: Map<String, Long> = emptyMap()
)

fun formatSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return String.format("%.2f %s", size / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}