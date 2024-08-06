package com.d4rk.cleaner.utils.cleaning

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
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



           //  val storageBreakdown = getStorageBreakdown(context)
            StorageInfo(
                totalStorage = totalSize ,
                // freeStorage = freeSize ,
                usedStorage = usedSize ,
                // storageBreakdown = storageBreakdown
            )

            withContext(Dispatchers.Main) {
                callback(usedFormatted , totalFormatted , usageProgress)
            }
        }
    }
}