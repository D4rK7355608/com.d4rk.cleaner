package com.d4rk.cleaner.app.clean.largefiles.domain.data.model.ui

import java.io.File

data class UiLargeFilesModel(
    val files: List<File> = emptyList(),
    /** Map of file paths to selection state */
    val fileSelectionStates: Map<String, Boolean> = emptyMap(),
    val selectedFileCount: Int = 0
)
