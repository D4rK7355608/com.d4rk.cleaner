package com.d4rk.cleaner.core.data.model.ui.screens

import com.d4rk.cleaner.core.data.model.ui.memorymanager.RamInfo
import com.d4rk.cleaner.core.data.model.ui.memorymanager.StorageInfo

data class UiMemoryManagerModel(
    val storageInfo : com.d4rk.cleaner.core.data.model.ui.memorymanager.StorageInfo = com.d4rk.cleaner.core.data.model.ui.memorymanager.StorageInfo() ,
    val ramInfo : com.d4rk.cleaner.core.data.model.ui.memorymanager.RamInfo = com.d4rk.cleaner.core.data.model.ui.memorymanager.RamInfo() ,
    val listExpanded : Boolean = true ,
)