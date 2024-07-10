package com.d4rk.cleaner.data.model.ui

data class StorageInfo(
    val totalStorage: Long = 0,
    val freeStorage: Long = 0,
    val usedStorage: Long = 0,
    val storageBreakdown: Map<String, Long> = emptyMap()
)