package com.d4rk.cleaner.data.model.ui.screens

import java.io.File

data class UiHomeModel(
    val progress: Float = 0f,
    val storageUsed: String = "",
    val storageTotal: String = "",
    val showCleaningComposable: Boolean = false,
    val isAnalyzing: Boolean = false,
    val scannedFiles: List<File> = emptyList(),
    val allFilesSelected: Boolean = false,
    val fileSelectionStates: Map<File, Boolean> = emptyMap(),
    val selectedFileCount: Int = 0,
    val showRescanDialog: Boolean = false,
)