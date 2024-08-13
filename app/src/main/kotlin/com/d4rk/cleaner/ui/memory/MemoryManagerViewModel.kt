package com.d4rk.cleaner.ui.memory

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.memorymanager.InternalStorageInfo
import com.d4rk.cleaner.data.model.ui.memorymanager.RamInfo
import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for managing and providing information about device memory (RAM and storage).
 */
class MemoryManagerViewModel : ViewModel() {
    private val _storageInfo = MutableStateFlow(StorageInfo())
    val storageInfo : StateFlow<StorageInfo> = _storageInfo.asStateFlow()

    private val _ramInfo = MutableStateFlow(RamInfo())
    val ramInfo : StateFlow<RamInfo> = _ramInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(value = true)
    val isLoading : StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _listExpanded = MutableStateFlow(value = true)
    val listExpanded : StateFlow<Boolean> = _listExpanded.asStateFlow()

    /**
     * Updates the storage information by fetching the latest data from the device.
     *
     * @param context The application context.
     */
    fun updateStorageInfo(context : Context) {
        viewModelScope.launch {
            try {
                StorageUtils.getStorageInfo(context) { used, total, _ ->

                    val usedStorageBytes : Double =
                            (used.toDoubleOrNull() ?: 0.0) * 1024 * 1024 * 1024
                    val totalStorageBytes : Double =
                            (total.toDoubleOrNull() ?: 0.0) * 1024 * 1024 * 1024

                    val storageBreakdown : Map<String , Long> = getStorageBreakdown(context)
                    _storageInfo.value = StorageInfo(
                        totalStorage = totalStorageBytes.toLong() ,
                        freeStorage = (totalStorageBytes - usedStorageBytes).toLong() ,
                        usedStorage = usedStorageBytes.toLong() ,
                        storageBreakdown = storageBreakdown
                    )
                }
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
    fun updateRamInfo(context : Context) {
        viewModelScope.launch {
            _ramInfo.value = getRamInfo(context)
        }
    }

    /**
     * Retrieves information about the internal storage.
     *
     * @return An [InternalStorageInfo] object containing details about total, free, and used internal storage.
     */
    private fun getInternalStorageInfo() : InternalStorageInfo {
        val statFs = StatFs(Environment.getDataDirectory().path)
        val blockSizeBytes : Long = statFs.blockSizeLong
        val totalBlocks : Long = statFs.blockCountLong
        val availableBlocks : Long = statFs.availableBlocksLong

        val totalStorage : Long = totalBlocks * blockSizeBytes
        val freeStorage : Long = availableBlocks * blockSizeBytes
        val usedStorage : Long = totalStorage - freeStorage

        return InternalStorageInfo(totalStorage , freeStorage , usedStorage)
    }

    /**
     * Calculates a breakdown of storage usage by different categories.
     *
     * @param context The application context.
     * @return A map containing storage usage by category (e.g., "Installed Apps", "Music", etc.).
     */
    private fun getStorageBreakdown(context : Context) : Map<String , Long> {
        val breakdown : MutableMap<String , Long> = mutableMapOf()
        val externalStoragePath : String = Environment.getExternalStorageDirectory().absolutePath

        breakdown[context.getString(R.string.installed_apps)] = getInstalledAppsSize(context)
        breakdown[context.getString(R.string.system)] =
                getDirectorySize(Environment.getRootDirectory())
        breakdown[context.getString(R.string.music)] =
                getDirectorySize(File(externalStoragePath , "Music"))
        breakdown[context.getString(R.string.images)] =
                getDirectorySize(File(externalStoragePath , "DCIM")) + getDirectorySize(
                    File(
                        externalStoragePath , "Pictures"
                    )
                )
        breakdown[context.getString(R.string.documents)] =
                getDirectorySize(File(externalStoragePath , "Documents"))
        breakdown[context.getString(R.string.downloads)] =
                getDirectorySize(File(externalStoragePath , "Download"))
        breakdown[context.getString(R.string.other_files)] = getOtherFilesSize(breakdown)

        return breakdown
    }

    /**
     * Calculates the total size of installed apps.
     *
     * @param context The application context.
     * @return The total size of installed apps in bytes.
     */
    private fun getInstalledAppsSize(context : Context) : Long {
        val packageManager : PackageManager = context.packageManager
        val installedApps : MutableList<ApplicationInfo> =
                packageManager.getInstalledApplications(0)
        var installedAppsSize = 0L
        for (app : ApplicationInfo in installedApps) {
            installedAppsSize += getApkSize(context , app.packageName)
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
    private fun getApkSize(context : Context , packageName : String) : Long {
        return try {
            context.packageManager.getApplicationInfo(
                packageName , 0
            ).sourceDir.let { File(it).length() }
        } catch (e : Exception) {
            0L
        }
    }

    /**
     * Calculates the size of a directory and its contents.
     *
     * @param directory The directory to calculate the size for.
     * @return The size of the directory in bytes.
     */
    private fun getDirectorySize(directory : File?) : Long {
        if (directory == null || ! directory.exists() || ! directory.isDirectory) return 0
        var size = 0L
        val files : Array<out File>? = directory.listFiles()
        if (files != null) {
            for (file : File in files) {
                size += if (file.isDirectory) {
                    getDirectorySize(file)
                }
                else {
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
    private fun getOtherFilesSize(breakdown : MutableMap<String , Long>) : Long {
        val totalUsedStorage : Long = getInternalStorageInfo().usedStorage
        val calculatedSize : Long = breakdown.values.sum()
        return totalUsedStorage - calculatedSize
    }

    /**
     * Fetches the current RAM information from the device.
     *
     * @param context The application context.
     * @return A [RamInfo] object containing details about total, available, and used RAM.
     */
    private fun getRamInfo(context : Context) : RamInfo {
        val activityManager : ActivityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return RamInfo(
            totalRam = memoryInfo.totalMem ,
            availableRam = memoryInfo.availMem ,
            usedRam = memoryInfo.totalMem - memoryInfo.availMem
        )
    }

    /**
     * Toggles the expansion state of the storage breakdown list.
     */
    fun toggleListExpanded() {
        _listExpanded.value = ! _listExpanded.value
    }
}