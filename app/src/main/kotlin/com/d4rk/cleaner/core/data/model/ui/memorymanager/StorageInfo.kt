package com.d4rk.cleaner.core.data.model.ui.memorymanager

data class StorageInfo(
    val storageUsageProgress : Float = 0f ,
    val freeStorage : Long = 0 ,
    val usedStorage : Long = 0 ,
    val freeSpacePercentage : Int = 0 ,
    val storageBreakdown : Map<String , Long> = emptyMap() ,
    val usedStorageFormatted : String = "" ,
    val totalStorageFormatted : String = "" ,
    val cleanedSpace : String = "0 KB" ,
)