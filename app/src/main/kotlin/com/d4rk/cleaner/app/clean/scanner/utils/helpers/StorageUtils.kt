package com.d4rk.cleaner.app.clean.scanner.utils.helpers

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import java.util.Locale
import java.util.UUID
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter

object StorageUtils {

    fun getStorageInfo(
        context : Context , callback : (used : String , total : String , totalSpace : Long , usageProgress : Float , freeSize : Int) -> Unit
    ) {
        val storageManager : StorageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val totalSize : Long
        val usedSize : Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val storageStatsManager : StorageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageVolume : StorageVolume = storageManager.primaryStorageVolume
            val uuidStr : String? = storageVolume.uuid
            val uuid : UUID = if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(uuidStr)
            totalSize = storageStatsManager.getTotalBytes(uuid)
            usedSize = totalSize - storageStatsManager.getFreeBytes(uuid)
        }
        else {
            val statFs = StatFs(Environment.getExternalStorageDirectory().path)
            totalSize = statFs.blockSizeLong * statFs.blockCountLong
            usedSize = totalSize - (statFs.blockSizeLong * statFs.availableBlocksLong)
        }
        val usedFormatted : String = String.format(
            Locale.US,
            "%.2f",
            usedSize / (1024.0 * 1024.0 * 1024.0)
        )
        val totalFormatted : String = String.format(
            Locale.US,
            "%.2f",
            totalSize / (1024.0 * 1024.0 * 1024.0)
        )
        val usageProgress : Float = usedSize.toFloat() / totalSize.toFloat()
        val freeSize = totalSize - usedSize
        val freeSpacePercentage : Int = ((freeSize.toDouble() / totalSize.toDouble()) * 100).toInt()
        callback(usedFormatted , totalFormatted , totalSize , usageProgress , freeSpacePercentage)
    }

    fun formatSize(size: Long): String = FileSizeFormatter.format(size)

    fun formatSizeReadable(size: Long): String = FileSizeFormatter.format(size)
}