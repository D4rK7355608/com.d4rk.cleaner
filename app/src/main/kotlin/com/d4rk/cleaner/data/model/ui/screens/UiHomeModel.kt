package com.d4rk.cleaner.data.model.ui.screens

import java.io.File

data class UiHomeModel(
    val storageUsageProgress : Float = 0f ,
    val usedStorageFormatted : String = "" ,
    val totalStorageFormatted : String = "" ,
    var analyzeState : UiAnalyzeModel = UiAnalyzeModel() ,
    val isRescanDialogVisible : Boolean = false ,
)

data class UiAnalyzeModel(
    val isAnalyzeScreenVisible : Boolean = false ,
    val scannedFileList : List<File> = emptyList() ,
    val emptyFolderList : List<File> = emptyList() ,
    val areAllFilesSelected : Boolean = false ,
    val fileSelectionMap : Map<File , Boolean> = emptyMap() ,
    val selectedFilesCount : Int = 0 ,
    val isFileScanEmpty : Boolean = false ,
    var fileTypesData : FileTypesData = FileTypesData() ,
)

data class FileTypesData(
    val apkExtensions : List<String> = emptyList() ,
    val imageExtensions : List<String> = emptyList() ,
    val videoExtensions : List<String> = emptyList() ,
    val audioExtensions : List<String> = emptyList() ,
    val archiveExtensions : List<String> = emptyList() ,
    val fileTypesTitles : List<String> = emptyList() ,
)