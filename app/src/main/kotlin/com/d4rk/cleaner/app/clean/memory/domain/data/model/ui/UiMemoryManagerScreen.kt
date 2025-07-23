package com.d4rk.cleaner.app.clean.memory.domain.data.model.ui

import com.d4rk.cleaner.app.clean.memory.domain.data.model.RamInfo
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo

data class UiMemoryManagerScreen(
    val ramInfo: RamInfo? = null,
    val storageInfo: StorageInfo? = null,
    val errorMessage: String? = null,
    val listExpanded: Boolean = true
)