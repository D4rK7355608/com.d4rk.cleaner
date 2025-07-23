package com.d4rk.cleaner.app.clean.trash.domain.data.model.ui

import java.io.File

data class UiTrashModel(
    val trashFiles: List<File> = emptyList(),
    /** Map of file paths to selection state */
    val fileSelectionStates: Map<String, Boolean> = emptyMap(),
    val selectedFileCount: Int = 0,
    val trashSize: Long = 0L,
    val trashFileOriginalPaths: Set<String> = emptySet(),
)