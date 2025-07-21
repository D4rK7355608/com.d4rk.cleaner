package com.d4rk.cleaner.app.clean.scanner.domain.`interface`

import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.FileTypesData
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.UiScannerModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ScannerRepositoryInterface {
    suspend fun getStorageInfo() : UiScannerModel
    suspend fun getFileTypes() : FileTypesData
    fun getAllFiles(): Flow<PagingData<File>>
    suspend fun getTrashFiles() : List<File>
    suspend fun getLargestFiles(limit: Int): List<File>
    suspend fun deleteFiles(filesToDelete : Set<File>)
    suspend fun moveToTrash(filesToMove : List<File>)
    suspend fun restoreFromTrash(filesToRestore : Set<File>)
    suspend fun addTrashSize(size : Long)
    suspend fun subtractTrashSize(size : Long)
}