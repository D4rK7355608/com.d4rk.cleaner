package com.d4rk.cleaner.ui.memory

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.ui.memory.model.InternalStorageInfo
import com.d4rk.cleaner.ui.memory.model.RamInfo
import com.d4rk.cleaner.ui.memory.model.StorageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import java.util.UUID
import kotlin.math.log10
import kotlin.math.pow

/**
 * ViewModel for managing and providing information about device memory (RAM and storage).
 */
class MemoryManagerViewModel : ViewModel() {
    private val _storageInfo = MutableStateFlow(StorageInfo())
    val storageInfo: StateFlow<StorageInfo> = _storageInfo.asStateFlow()

    private val _ramInfo = MutableStateFlow(RamInfo())
    val ramInfo: StateFlow<RamInfo> = _ramInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Updates the storage information by fetching the latest data from the device.
     *
     * @param context The application context.
     */
    fun updateStorageInfo(context: Context) {
        viewModelScope.launch {
            try {
                _storageInfo.value = getStorageInfo(context)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Updates the RAM information by fetching the latest data from the device.
     *
     * @param context The application context.
     */
    fun updateRamInfo(context: Context) {
        viewModelScope.launch {
            try {
                _ramInfo.value = getRamInfo(context)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetches the current storage information from the device.
     *
     * @param context The application context.
     * @return A [StorageInfo] object containing details about total, free, and used storage.
     */
    private suspend fun getStorageInfo(context: Context): StorageInfo =
        withContext(Dispatchers.IO) {
            val storageManager =
                context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageStatsManager =
                context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageVolume = storageManager.primaryStorageVolume
            val totalSize: Long
            val usedSize: Long
            val freeSize: Long
            val uuidStr = storageVolume.uuid
            val uuid: UUID = if (uuidStr == null) StorageManager.UUID_DEFAULT
            else UUID.fromString(uuidStr)
            totalSize = storageStatsManager.getTotalBytes(uuid)
            freeSize = storageStatsManager.getFreeBytes(uuid)
            usedSize = totalSize - freeSize
            _isLoading.value = true
            val storageBreakdown = getStorageBreakdown(context)
            StorageInfo(
                totalStorage = totalSize,
                freeStorage = freeSize,
                usedStorage = usedSize,
                storageBreakdown = storageBreakdown
            )
        }

    /**
     * Retrieves information about the internal storage.
     *
     * @return An [InternalStorageInfo] object containing details about total, free, and used internal storage.
     */
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

    /**
     * Calculates a breakdown of storage usage by different categories.
     *
     * @param context The application context.
     * @return A map containing storage usage by category (e.g., "Installed Apps", "Music", etc.).
     */
    private fun getStorageBreakdown(context: Context): Map<String, Long> {
        val breakdown = mutableMapOf<String, Long>()
        val externalStoragePath = Environment.getExternalStorageDirectory().absolutePath

        breakdown["Installed Apps"] = getInstalledAppsSize(context)
        breakdown["System"] = getDirectorySize(Environment.getRootDirectory())
        breakdown["Music"] = getDirectorySize(File(externalStoragePath, "Music"))
        breakdown["Images"] = getDirectorySize(File(externalStoragePath, "DCIM")) +
                getDirectorySize(File(externalStoragePath, "Pictures"))
        breakdown["Documents"] = getDirectorySize(File(externalStoragePath, "Documents"))
        breakdown["Downloads"] = getDirectorySize(File(externalStoragePath, "Download"))
        breakdown["Other Files"] = getOtherFilesSize(breakdown)

        return breakdown
    }

    /**
     * Calculates the total size of installed apps.
     *
     * @param context The application context.
     * @return The total size of installed apps in bytes.
     */
    private fun getInstalledAppsSize(context: Context): Long {
        val packageManager = context.packageManager
        val installedApps = packageManager.getInstalledApplications(0)
        var installedAppsSize = 0L
        for (app in installedApps) {
            installedAppsSize += getApkSize(context, app.packageName)
        }
        return installedAppsSize
    }

    /**
     * Retrieves the size of an APK file for a given package name.
     *
     * @param context The application context.
     * @param packageName The package name of the app.
     * @return The size of the APK file in bytes.
     */
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

    /**
     * Calculates the size of a directory and its contents.
     *
     * @param directory The directory to calculate the size for.
     * @return The size of the directory in bytes.
     */
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

    /**
     * Calculates the size of "other files" by subtracting the calculated sizes of known categories
     * from the total used storage.
     *
     * @param breakdown The current storage breakdown map.
     * @return The size of "other files" in bytes.
     */
    private fun getOtherFilesSize(breakdown: MutableMap<String, Long>): Long {
        val totalUsedStorage = getInternalStorageInfo().usedStorage
        val calculatedSize = breakdown.values.sum()
        return totalUsedStorage - calculatedSize
    }

    /**
     * Fetches the current RAM information from the device.
     *
     * @param context The application context.
     * @return A [RamInfo] object containing details about total, available, and used RAM.
     */
    private fun getRamInfo(context: Context): RamInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return RamInfo(
            totalRam = memoryInfo.totalMem,
            availableRam = memoryInfo.availMem,
            usedRam = memoryInfo.totalMem - memoryInfo.availMem
        )
    }
}

/**
 * Formats a file size in bytes to a human-readable string (e.g., "128 MB").
 *
 * @param size The file size in bytes.
 * @return A formatted string representing the file size.
 */
fun formatSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return String.format(
        Locale.US,
        "%.2f %s",
        size / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}