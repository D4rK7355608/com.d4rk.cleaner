package com.d4rk.cleaner.ui.home

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.app.Application
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.utils.FileScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.math.roundToInt

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val progress = MutableLiveData(0f)
    val storageUsed = MutableLiveData<String>()
    val storageTotal = MutableLiveData<String>()
    var fileScanner: FileScanner
    val scannedFiles = MutableLiveData<List<File>>()
    val allFilesSelected = mutableStateOf(false)
    val fileSelectionStates = mutableStateMapOf<File, Boolean>()
    private val dataStoreInstance: DataStore = DataStore(application)
    val showCleaningComposable = MutableLiveData(false)
    val isAnalyzing = MutableLiveData(false)
    val _selectedFileCount = MutableStateFlow(0)
    val selectedFileCount: StateFlow<Int> = _selectedFileCount.asStateFlow()

    init {
        updateStorageInfo()
        fileScanner = FileScanner(dataStoreInstance,application.resources)
    }

    /**
     * Updates storage information asynchronously.
     *
     * This function retrieves and updates storage-related information such as total storage size, used storage size,
     * and storage usage progress.
     * It utilizes the Android StorageManager and StorageStatsManager to fetch storage statistics.
     * The updated storage information is then posted to corresponding LiveData objects for observation.
     *
     * @see android.content.Context.STORAGE_SERVICE
     * @see android.content.Context.STORAGE_STATS_SERVICE
     * @see android.os.storage.StorageManager
     * @see android.os.storage.StorageStatsManager
     * @see android.os.storage.StorageVolume
     * @param viewModelScope The coroutine scope associated with the ViewModel for launching asynchronous tasks.
     */
    private fun updateStorageInfo() {
        viewModelScope.launch {
            val storageManager =
                getApplication<Application>().getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageStatsManager =
                getApplication<Application>().getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageVolume = storageManager.primaryStorageVolume
            val totalSize: Long
            val usedSize: Long
            val uuidStr = storageVolume.uuid
            val uuid: UUID =
                if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(uuidStr)
            totalSize = storageStatsManager.getTotalBytes(uuid)
            usedSize = totalSize - storageStatsManager.getFreeBytes(uuid)
            storageUsed.postValue((usedSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString())
            storageTotal.postValue((totalSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString())
            progress.postValue(usedSize.toFloat() / totalSize.toFloat())
        }
    }

    fun onFileSelectionChange(file: File, isChecked: Boolean) {
        fileSelectionStates[file] = isChecked
        _selectedFileCount.value = fileSelectionStates.count { it.value }
        allFilesSelected.value = fileSelectionStates.all { it.value }
    }

    /**
     * This function is used to select or deselect all files in the scannedFiles list.
     *
     * @param selectAll A boolean value indicating whether to select all files (if true) or deselect all files (if false).
     * When this function is called with true, it will mark all files in the scannedFiles list as selected by adding them to the fileSelectionStates map with a value of true.
     * If called with false, it will mark all files as not selected by adding them to the fileSelectionStates map with a value of false.
     * The function also updates the allFilesSelected LiveData object with the value of the selectAll parameter.
     *
     * Usage:
     * selectAllFiles(true)  // Selects all files
     * selectAllFiles(false) // Deselects all files
     */
    fun selectAllFiles(selectAll: Boolean) {
        scannedFiles.value?.forEach { file ->
            fileSelectionStates[file] = selectAll
        }
        allFilesSelected.value = selectAll
        _selectedFileCount.value = fileSelectionStates.values.count { it }
    }

    /**
     * Initiates the file analysis process.
     *
     * This function checks for necessary permissions and, if granted, triggers the file* scanning process asynchronously.
     *
     * @param activity The Activity instance required to request permissions.
     */
    fun analyze(activity: Activity) {
        if (!hasRequiredPermissions()) {
            requestPermissions(activity)
            return
        }
        isAnalyzing.value = true
        showCleaningComposable.value = true
        viewModelScope.launch {
            fileScanner.startScanning()
            withContext(Dispatchers.Main) {
                scannedFiles.value = fileScanner.getFilteredFiles()
                isAnalyzing.value =false
            }
        }
    }

    /**
     * Initiates the cleaning process if the required permissions are granted.
     *
     * @param activity The Activity instance required to request permissions.
     */
    fun clean(activity: Activity) {
        if (!hasRequiredPermissions()) {
            requestPermissions(activity)
            return
        }

        // TODO: Implement your cleaning logic here
    }

    /**
     * Checks if the app has all the necessary permissions to perform scanning and cleaning.
     *
     * @return True if all required permissions are granted, false otherwise.
     */
    private fun hasRequiredPermissions(): Boolean {
        val hasStoragePermissions = when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q ->
                ContextCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED

            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 ->
                ContextCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED

            else -> true
        }

        val hasManageStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }

        val hasUsageStatsPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isAccessGranted()
        } else {
            true
        }

        val hasMediaPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return hasStoragePermissions && hasManageStoragePermission &&
                hasUsageStatsPermission && hasMediaPermissions
    }

    /**
     * Checks if the app has access to usage statistics.
     *
     * @return True if access is granted, false otherwise.
     */
    private fun isAccessGranted(): Boolean = try {
        val packageManager = getApplication<Application>().packageManager
        val applicationInfo =
            packageManager.getApplicationInfo(getApplication<Application>().packageName, 0)
        val appOpsManager =
            getApplication<Application>().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        @Suppress("DEPRECATION") val mode: Int = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            applicationInfo.uid,
            applicationInfo.packageName
        )
        mode == AppOpsManager.MODE_ALLOWED
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    /**
     * Requests necessary permissions for the app to function correctly.
     *
     * This function checks for and requests the following permissions:
     * - WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE: For accessing and managing files.
     * - MANAGE_EXTERNAL_STORAGE (Android R and above): For managing all files on external storage.
     * - PACKAGE_USAGE_STATS: For gathering app usage statistics.
     * - READ_MEDIA_AUDIO, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO (Android Tiramisu and above):
     *     For accessing media files.
     *
     * It utilizes ActivityCompat.requestPermissions to initiate the permission request process
     * and handles different Android versions to ensure compatibility.
     *
     * @param activity The Activity instance required to request permissions.
     */
    private fun requestPermissions(activity: Activity) {
        val requiredPermissions = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", getApplication<Application>().packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }

            if (!isAccessGranted()) {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                activity.startActivity(intent)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.addAll(
                listOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        }

        ActivityCompat.requestPermissions(activity, requiredPermissions.toTypedArray(), 1)
    }
}