package com.d4rk.cleaner.app.clean.home.utils.helpers

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import java.util.Locale
import java.util.UUID
import kotlin.math.log10
import kotlin.math.pow

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

    fun formatSize(size : Long) : String {
        if (size <= 0) return "0 B"
        val units : Array<String> = arrayOf("B" , "KB" , "MB" , "GB" , "TB")
        val digitGroups : Int = (log10(size.toDouble()) / log10(x = 1024.0)).toInt()
        val value : Double = size / 1024.0.pow(digitGroups.toDouble())

        return String.format(Locale.US , "%.2f %s" , value , units[digitGroups])
    }

    fun formatSizeReadable(size: Long): String {
        if (size < 1024) return "$size B"

        val kb: Double = size / 1024.0
        if (kb < 1024) {
            return String.format(Locale.US, "%.1f KB", kb)
        }

        val mb: Double = kb / 1024.0
        if (mb < 1024) {
            return String.format(Locale.US, "%.1f MB", mb)
        }

        val gb: Double = mb / 1024.0
        return String.format(Locale.US, "%.1f GB", gb)
    }
}