package com.d4rk.cleaner.utils.cleaning

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

object StorageUtils {

    fun getStorageInfo(
        context : Context ,
        callback : (used : String , total : String , usageProgress : Float) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val storageManager : StorageManager =
                    context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageStatsManager : StorageStatsManager =
                    context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageVolume : StorageVolume = storageManager.primaryStorageVolume
            val totalSize : Long
            val usedSize : Long
            val uuidStr : String? = storageVolume.uuid
            val uuid : UUID =
                    if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(uuidStr)
            totalSize = storageStatsManager.getTotalBytes(uuid)
            usedSize = totalSize - storageStatsManager.getFreeBytes(uuid)
            val usedFormatted : String =
                    (usedSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString()
            val totalFormatted : String =
                    (totalSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString()
            val usageProgress : Float = usedSize.toFloat() / totalSize.toFloat()

            withContext(Dispatchers.Main) {
                callback(usedFormatted , totalFormatted , usageProgress)
            }
        }
    }

    fun formatSize(size : Long) : String {
        if (size <= 0) return "0 B"
        val units : Array<String> = arrayOf("B" , "KB" , "MB" , "GB" , "TB")
        val digitGroups : Int = (log10(size.toDouble()) / log10(x= 1024.0)).toInt()
        val value : Double = size / 1024.0.pow(digitGroups.toDouble())

        return if (value.compareTo(value.toLong()) == 0) {
            String.format(Locale.US , format = "%d %s" , value.toLong() , units[digitGroups])
        }
        else {
            val decimalPart : Int = (value * 100).toInt() % 100
            if (decimalPart == 0) {
                String.format(Locale.US , format = "%d %s" , value.toLong() , units[digitGroups])
            }
            else {
                String.format(Locale.US , format = "%.2f %s" , value , units[digitGroups])
            }
        }
    }
}