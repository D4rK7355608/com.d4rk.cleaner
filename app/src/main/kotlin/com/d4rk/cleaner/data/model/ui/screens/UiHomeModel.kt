package com.d4rk.cleaner.data.model.ui.screens

import java.io.File

data class UiHomeModel(
    var storageUsageProgress : Float = 0f ,
    var usedStorageFormatted : String = "" ,
    var totalStorageFormatted : String = "" ,
    var analyzeState : UiAnalyzeModel = UiAnalyzeModel() ,
    var isRescanDialogVisible : Boolean = false ,
)

data class UiAnalyzeModel(
    var isAnalyzeScreenVisible : Boolean = false ,
    var scannedFileList : List<File> = emptyList() ,
    var emptyFolderList : List<File> = emptyList() ,
    var areAllFilesSelected : Boolean = false ,
    var fileSelectionMap : Map<File , Boolean> = emptyMap() ,
    var selectedFilesCount : Int = 0 ,
    var isFileScanEmpty : Boolean = false ,
    var fileTypesData : FileTypesData = FileTypesData() ,
    var isDeleteForeverConfirmationDialogVisible: Boolean = false,
    var isMoveToTrashConfirmationDialogVisible: Boolean = false,
)

data class FileTypesData(
    var apkExtensions : List<String> = emptyList() ,
    var imageExtensions : List<String> = emptyList() ,
    var videoExtensions : List<String> = emptyList() ,
    var audioExtensions : List<String> = emptyList() ,
    var archiveExtensions : List<String> = emptyList() ,
    var fileTypesTitles : List<String> = emptyList() ,
)