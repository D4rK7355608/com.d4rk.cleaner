package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.os.Environment
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.constants.cleaning.ExtensionsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class HomeRepository(dataStore : DataStore , application : Application) : HomeRepositoryImplementation(application , dataStore) {

    suspend fun getPreferences(): Map<String, Boolean> {
        return withContext(context = Dispatchers.IO) {
                mapOf(
                    ExtensionsConstants.GENERIC_EXTENSIONS to dataStore.genericFilter.first(),
                    ExtensionsConstants.IMAGE_EXTENSIONS to dataStore.deleteImageFiles.first(),
                    ExtensionsConstants.VIDEO_EXTENSIONS to dataStore.deleteVideoFiles.first(),
                    ExtensionsConstants.AUDIO_EXTENSIONS to dataStore.deleteAudioFiles.first(),
                    ExtensionsConstants.OFFICE_EXTENSIONS to dataStore.deleteOfficeFiles.first(),
                    ExtensionsConstants.ARCHIVE_EXTENSIONS to dataStore.deleteArchives.first(),
                    ExtensionsConstants.APK_EXTENSIONS to dataStore.deleteApkFiles.first(),
                    ExtensionsConstants.FONT_EXTENSIONS to dataStore.deleteFontFiles.first(),
                    ExtensionsConstants.WINDOWS_EXTENSIONS to dataStore.deleteWindowsFiles.first(),
                    ExtensionsConstants.EMPTY_FOLDERS to dataStore.deleteEmptyFolders.first(),
                    ExtensionsConstants.OTHER_EXTENSIONS to dataStore.deleteOtherFiles.first()
                )

        }
    }

    suspend fun getStorageInfoRepository(onSuccess : (com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel) -> Unit) {
        withContext(context = Dispatchers.IO) {
            val storageInfo : com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel = getStorageInfoImplementation()
            withContext(context = Dispatchers.Main) { onSuccess(storageInfo) }
        }
    }

    suspend fun getFileTypesRepository(onSuccess : (com.d4rk.cleaner.core.data.model.ui.screens.FileTypesData) -> Unit) {
        withContext(context = Dispatchers.IO) {
            val fileTypesData : com.d4rk.cleaner.core.data.model.ui.screens.FileTypesData = getFileTypesImplementation()
            withContext(context = Dispatchers.Main) { onSuccess(fileTypesData) }
        }
    }

    suspend fun analyzeFiles(onSuccess : (Pair<List<File> , List<File>>) -> Unit) {
        withContext(context = Dispatchers.IO) {
            val result = getAllFilesImplementation()
            withContext(context = Dispatchers.Main) { onSuccess(result) }
        }
    }

    suspend fun getTrashFiles() : List<File> {
        return withContext(context = Dispatchers.IO) {
            val trashDir = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")
            if (trashDir.exists()) trashDir.listFiles()?.toList() ?: emptyList()
            else emptyList()
        }
    }

    suspend fun deleteFilesRepository(filesToDelete : Set<File> , onSuccess : () -> Unit) {
        withContext(context = Dispatchers.IO) {
            deleteFilesImplementation(filesToDelete = filesToDelete)
            withContext(context = Dispatchers.Main) { onSuccess() }
        }
    }

    suspend fun moveToTrashRepository(filesToMove : List<File> , onSuccess : () -> Unit) {
        withContext(context = Dispatchers.IO) {
            moveToTrashImplementation(filesToMove = filesToMove)
            withContext(context = Dispatchers.Main) { onSuccess() }
        }
    }

    suspend fun restoreFromTrashRepository(filesToRestore : Set<File> , onSuccess : () -> Unit) {
        withContext(context = Dispatchers.IO) {
            restoreFromTrashImplementation(filesToRestore = filesToRestore)
            withContext(context = Dispatchers.Main) { onSuccess() }
        }
    }

    suspend fun addTrashSize(size : Long) {
        dataStore.addTrashSize(size = size)
    }

    suspend fun subtractTrashSize(size : Long) {
        dataStore.subtractTrashSize(size = size)
    }
}