package com.d4rk.cleaner.data.model.ui.screens

import com.d4rk.cleaner.data.model.ui.error.UiErrorModel
import com.d4rk.cleaner.data.model.ui.memorymanager.RamInfo
import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo

data class UiMemoryManagerModel(
    val storageInfo: StorageInfo = StorageInfo(),
    val ramInfo: RamInfo = RamInfo(),
    val listExpanded: Boolean = true,
    val error: UiErrorModel = UiErrorModel(),
)