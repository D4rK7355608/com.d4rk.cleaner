package com.d4rk.cleaner.app.auto

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.FileTypesData
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.AnalyzeFilesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.DeleteFilesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.GetFileTypesUseCase
import com.d4rk.cleaner.app.settings.cleaning.utils.constants.ExtensionsConstants
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.utils.helpers.CleaningEventBus
import com.d4rk.cleaner.core.utils.extensions.md5
import com.d4rk.cleaner.app.images.utils.ImageHashUtils
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.days
import java.io.File

class AutoCleanWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val analyzeFiles: AnalyzeFilesUseCase by inject()
    private val deleteFiles: DeleteFilesUseCase by inject()
    private val getFileTypes: GetFileTypesUseCase by inject()
    private val dataStore: DataStore by inject()

    override suspend fun doWork(): Result {
        if (!dataStore.autoCleanEnabled.first()) return Result.success()

        val frequency = dataStore.autoCleanFrequencyDays.first()
        val lastScan = dataStore.lastScanTimestamp.first() ?: 0L
        val now = System.currentTimeMillis()
        if (frequency <= 0 || now - lastScan < frequency.days.inWholeMilliseconds) {
            return Result.success()
        }

        val filesState = analyzeFiles().first { it !is DataState.Loading }
        if (filesState !is DataState.Success) return Result.success()
        val (files, emptyFolders) = filesState.data

        val typesState = getFileTypes().first { it !is DataState.Loading }
        val types = if (typesState is DataState.Success) typesState.data else FileTypesData()

        val prefs = mapOf(
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
        val includeDuplicates = dataStore.deleteDuplicateFiles.first()
        val deepDuplicateSearch = dataStore.deepDuplicateSearch.first()
        val toDelete = computeFilesToClean(files, emptyFolders, types, prefs, includeDuplicates, deepDuplicateSearch)
        if (toDelete.isEmpty()) return Result.success()

        deleteFiles(filesToDelete = toDelete.toSet()).collect {}
        dataStore.saveLastScanTimestamp(now)
        CleaningEventBus.notifyCleaned()
        return Result.success()
    }

    private fun computeFilesToClean(
        scannedFiles: List<File>,
        emptyFolders: List<File>,
        fileTypesData: FileTypesData,
        preferences: Map<String, Boolean>,
        includeDuplicates: Boolean,
        deepDuplicateSearch: Boolean
    ): List<File> {
        val knownExtensions = (fileTypesData.imageExtensions + fileTypesData.videoExtensions +
            fileTypesData.audioExtensions + fileTypesData.officeExtensions + fileTypesData.archiveExtensions +
            fileTypesData.apkExtensions + fileTypesData.fontExtensions + fileTypesData.windowsExtensions).toSet()
        val result = mutableListOf<File>()

        val duplicateGroups = if (includeDuplicates) findDuplicateGroups(scannedFiles, deepDuplicateSearch, fileTypesData.imageExtensions) else emptyList()
        val duplicateFiles = if (includeDuplicates) duplicateGroups.flatten().toSet() else emptySet()

        scannedFiles.forEach { file ->
            if (includeDuplicates && file in duplicateFiles) {
                result.add(file)
            } else {
                val extension = file.extension.lowercase()
                val match = when (extension) {
                    in fileTypesData.imageExtensions -> preferences[ExtensionsConstants.IMAGE_EXTENSIONS] == true
                    in fileTypesData.videoExtensions -> preferences[ExtensionsConstants.VIDEO_EXTENSIONS] == true
                    in fileTypesData.audioExtensions -> preferences[ExtensionsConstants.AUDIO_EXTENSIONS] == true
                    in fileTypesData.officeExtensions -> preferences[ExtensionsConstants.OFFICE_EXTENSIONS] == true
                    in fileTypesData.archiveExtensions -> preferences[ExtensionsConstants.ARCHIVE_EXTENSIONS] == true
                    in fileTypesData.apkExtensions -> preferences[ExtensionsConstants.APK_EXTENSIONS] == true
                    in fileTypesData.fontExtensions -> preferences[ExtensionsConstants.FONT_EXTENSIONS] == true
                    in fileTypesData.windowsExtensions -> preferences[ExtensionsConstants.WINDOWS_EXTENSIONS] == true
                    else -> !knownExtensions.contains(extension) && preferences[ExtensionsConstants.OTHER_EXTENSIONS] == true
                }
                if (match) result.add(file)
            }
        }
        if (preferences[ExtensionsConstants.EMPTY_FOLDERS] == true) {
            result.addAll(emptyFolders)
        }
        return result
    }

    private fun findDuplicateGroups(
        files: List<File>,
        deepSearch: Boolean,
        imageExtensions: List<String>
    ): List<List<File>> {
        val hashMap = mutableMapOf<String, MutableList<File>>()
        files.filter { it.isFile }.forEach { file ->
            val extension = file.extension.lowercase()
            val hash = if (deepSearch && extension in imageExtensions) {
                ImageHashUtils.perceptualHash(file)
            } else {
                file.md5()
            } ?: return@forEach
            hashMap.getOrPut(hash) { mutableListOf() }.add(file)
        }
        return hashMap.values.filter { it.size > 1 }
    }
}
