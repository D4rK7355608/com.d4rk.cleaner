package com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui

/** State of the cleaning process. */

import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo

data class UiScannerModel(
    val storageInfo : StorageInfo = StorageInfo() ,
    var analyzeState : UiAnalyzeModel = UiAnalyzeModel() ,
    var daysFromLastScan : Int = 0 ,
    var promotedApp : PromotedApp? = null ,
    var isRescanDialogVisible : Boolean = false ,
    var isHideStreakDialogVisible: Boolean = false,
)

data class UiAnalyzeModel(
    var state : CleaningState = CleaningState.Idle ,
    var cleaningType : CleaningType = CleaningType.NONE ,
    var isAnalyzeScreenVisible : Boolean = false ,
    var scannedFileList : List<FileEntry> = emptyList() ,
    var emptyFolderList : List<FileEntry> = emptyList() ,
    var areAllFilesSelected : Boolean = false ,
    /** Map of file paths to selection state */
    var fileSelectionMap : Map<String , Boolean> = emptyMap() ,
    var selectedFilesCount : Int = 0 ,
    var groupedFiles : Map<String , List<FileEntry>> = emptyMap() ,
    /** Set of original files when duplicates are detected */
    var duplicateOriginals : Set<FileEntry> = emptySet() ,
    /** Groups of duplicate files starting with the original */
    var duplicateGroups: List<List<FileEntry>> = emptyList(),
    var fileTypesData : FileTypesData = FileTypesData() ,
    var isDeleteForeverConfirmationDialogVisible : Boolean = false ,
    var isMoveToTrashConfirmationDialogVisible : Boolean = false ,
)

data class FileTypesData(
    var fileTypesTitles : List<String> = emptyList() ,
    var apkExtensions : List<String> = emptyList() ,
    var imageExtensions : List<String> = emptyList() ,
    var videoExtensions : List<String> = emptyList() ,
    var audioExtensions : List<String> = emptyList() ,
    var archiveExtensions : List<String> = emptyList() ,
    var fontExtensions : List<String> = emptyList() ,
    var windowsExtensions : List<String> = emptyList() ,
    var officeExtensions : List<String> = emptyList() ,
    var otherExtensions : List<String> = emptyList() ,)