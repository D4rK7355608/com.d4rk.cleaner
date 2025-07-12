package com.d4rk.cleaner.app.clean.largefiles.domain.data.model.ui

import java.io.File

data class UiLargeFilesModel(
    val files: List<File> = emptyList(),
    val fileSelectionStates: Map<File, Boolean> = emptyMap(),
    val selectedFileCount: Int = 0
)
