package com.d4rk.cleaner.app.clean.home.domain.`interface`

import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.FileTypesData
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.UiHomeModel
import java.io.File

interface HomeRepositoryInterface {
    suspend fun getStorageInfo() : UiHomeModel
    suspend fun getFileTypes() : FileTypesData
    suspend fun getAllFiles() : Pair<List<File> , List<File>>
    suspend fun getTrashFiles() : List<File>
    suspend fun deleteFiles(filesToDelete : Set<File>)
    suspend fun moveToTrash(filesToMove : List<File>)
    suspend fun restoreFromTrash(filesToRestore : Set<File>)
    suspend fun addTrashSize(size : Long)
    suspend fun subtractTrashSize(size : Long)
}