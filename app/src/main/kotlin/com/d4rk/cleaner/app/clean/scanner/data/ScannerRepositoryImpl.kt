package com.d4rk.cleaner.app.clean.scanner.data


import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.FileTypesData
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.UiScannerModel
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import com.d4rk.cleaner.app.clean.scanner.utils.helpers.StorageUtils
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.utils.extensions.clearClipboardCompat
import com.d4rk.cleaner.core.utils.extensions.partialMd5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import com.d4rk.cleaner.core.utils.helpers.DirectoryScanner
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ScannerRepositoryImpl(
    private val application : Application , private val dataStore : DataStore
) : ScannerRepositoryInterface {

    private val trashDir : File = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

    override suspend fun getStorageInfo() : UiScannerModel {
        return suspendCoroutine { continuation ->
            StorageUtils.getStorageInfo(context = application) { _ , _ , _ , usageProgress , freeSpacePercentage ->
                continuation.resume(
                    UiScannerModel(storageInfo = StorageInfo(storageUsageProgress = usageProgress , freeSpacePercentage = freeSpacePercentage))
                )
            }
        }
    }

    override suspend fun getFileTypes() : FileTypesData {
        return suspendCoroutine { continuation ->
            val apkExtensions : List<String> = application.resources.getStringArray(R.array.apk_extensions).toList()
            val imageExtensions : List<String> = application.resources.getStringArray(R.array.image_extensions).toList()
            val videoExtensions : List<String> = application.resources.getStringArray(R.array.video_extensions).toList()
            val audioExtensions : List<String> = application.resources.getStringArray(R.array.audio_extensions).toList()
            val fontExtensions : List<String> = application.resources.getStringArray(R.array.font_extensions).toList()
            val windowsExtensions : List<String> = application.resources.getStringArray(R.array.windows_extensions).toList()
            val archiveExtensions : List<String> = application.resources.getStringArray(R.array.archive_extensions).toList()
            val officeExtensions : List<String> = application.resources.getStringArray(R.array.microsoft_office_extensions).toList()
            val genericExtensions : List<String> = application.resources.getStringArray(R.array.generic_extensions).toList()
            val fileTypesTitles : List<String> = application.resources.getStringArray(R.array.file_types_titles).toList()

            val knownExtensions = mutableSetOf<String>().apply {
                addAll(elements = apkExtensions.map { it.lowercase() })
                addAll(elements = imageExtensions.map { it.lowercase() })
                addAll(elements = videoExtensions.map { it.lowercase() })
                addAll(elements = audioExtensions.map { it.lowercase() })
                addAll(elements = fontExtensions.map { it.lowercase() })
                addAll(elements = windowsExtensions.map { it.lowercase() })
                addAll(elements = archiveExtensions.map { it.lowercase() })
                addAll(elements = officeExtensions.map { it.lowercase() })
                addAll(elements = genericExtensions.map { it.lowercase() })
            }

            val allFoundExtensions : MutableSet<String> = mutableSetOf()
            DirectoryScanner.scan(Environment.getExternalStorageDirectory()) { file ->
                val ext : String = file.extension.lowercase()
                if (ext.isNotEmpty()) {
                    allFoundExtensions.add(ext)
                }
            }

            val otherExtensions : List<String> = (allFoundExtensions - knownExtensions).toList().sorted()

            val fileTypesData = FileTypesData(
                apkExtensions = apkExtensions ,
                imageExtensions = imageExtensions ,
                videoExtensions = videoExtensions ,
                audioExtensions = audioExtensions ,
                archiveExtensions = archiveExtensions ,
                fileTypesTitles = fileTypesTitles ,
                fontExtensions = fontExtensions ,
                windowsExtensions = windowsExtensions ,
                officeExtensions = officeExtensions ,
                otherExtensions = otherExtensions
            )
            continuation.resume(value = fileTypesData)
        }
    }

    override suspend fun getAllFiles() : Pair<List<File> , List<File>> {
        val files : MutableList<File> = mutableListOf()
        val emptyFolders : MutableList<File> = mutableListOf()
        val stack : ArrayDeque<File> = ArrayDeque()
        val root : File = Environment.getExternalStorageDirectory()
        stack.addFirst(element = root)

        val trashDir = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

        while (stack.isNotEmpty()) {
            val currentFile : File = stack.removeFirst()
            if (currentFile.isDirectory) {
                if (! currentFile.absolutePath.startsWith(trashDir.absolutePath)) {
                    currentFile.listFiles()?.let { children ->
                        if (children.isEmpty()) {
                            emptyFolders.add(currentFile)
                        }
                        else {
                            children.forEach { child ->
                                if (child.isDirectory) {
                                    stack.addLast(child)
                                }
                                else {
                                    files.add(child)
                                }
                            }
                        }
                    }
                }
            }
            else {
                files.add(currentFile)
            }
        }
        return Pair(files , emptyFolders)
    }

    override suspend fun getTrashFiles() : List<File> {
        return withContext(context = Dispatchers.IO) { // Assuming you have Dispatchers available or inject them
            val trashDir = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")
            if (trashDir.exists()) trashDir.listFiles()?.toList() ?: emptyList()
            else emptyList()
        }
    }

    override suspend fun deleteFiles(filesToDelete : Set<File>) {
        var totalSize = 0L
        filesToDelete.forEach { file ->
            if (file.exists()) {
                totalSize += file.length()
                file.deleteRecursively()
            }
        }
        if (totalSize > 0) {
            dataStore.addCleanedSpace(space = totalSize)
        }
        clearClipboardImplementation()
    }

    private fun clearClipboardImplementation() {
        val clipboardManager : ClipboardManager = application.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return

        runCatching {
            clipboardManager.clearClipboardCompat()
        }
    }

    override suspend fun moveToTrash(filesToMove : List<File>) {
        if (! trashDir.exists()) {
            trashDir.mkdirs()
        }

        filesToMove.forEach { file ->
            if (file.exists()) {
                val destination = File(trashDir , file.name)

                if (file.renameTo(destination)) {
                    dataStore.addTrashFileOriginalPath(originalPath = file.absolutePath)
                    dataStore.addTrashSize(size = file.length())
                    MediaScannerConnection.scanFile(
                        application , arrayOf(destination.absolutePath , file.absolutePath) , null , null
                    )
                }
            }
        }
    }

    override suspend fun restoreFromTrash(filesToRestore : Set<File>) {
        val originalPaths : Set<String> = dataStore.trashFileOriginalPaths.first()
        filesToRestore.forEach { file ->
            if (file.exists()) {
                val originalPath : String? = originalPaths.firstOrNull { File(it).name == file.name }
                if (originalPath != null) {
                    val destinationFile = File(originalPath)
                    val destinationParent : File? = destinationFile.parentFile

                    if (destinationParent?.exists() == false) {
                        destinationParent.mkdirs()
                    }

                    if (file.renameTo(destinationFile)) {
                        dataStore.removeTrashFileOriginalPath(originalPath)
                        dataStore.subtractTrashSize(size = file.length())
                        MediaScannerConnection.scanFile(
                            application , arrayOf(destinationFile.absolutePath , file.absolutePath) , null , null
                        )
                    }
                }
                else {
                    val downloadsDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val destinationFile = File(downloadsDir , file.name)

                    if (file.renameTo(destinationFile)) {
                        dataStore.removeTrashFileOriginalPath(file.absolutePath)
                        dataStore.subtractTrashSize(size = file.length())
                        MediaScannerConnection.scanFile(
                            application , arrayOf(destinationFile.absolutePath , file.absolutePath) , null , null
                        )
                    }
                }
            }
        }
    }

    override suspend fun addTrashSize(size : Long) {
        dataStore.addTrashSize(size = size)
    }

    override suspend fun subtractTrashSize(size : Long) {
        dataStore.subtractTrashSize(size = size)
    }

    override suspend fun getLargestFiles(limit: Int): List<File> {
        return withContext(Dispatchers.IO) {
            val root = Environment.getExternalStorageDirectory()
            val minSize = 100L * 1024 * 1024 // 100MB threshold
            val trashed = dataStore.trashFileOriginalPaths.first()

            // Load extension lists for basic type detection
            val apkExt = application.resources.getStringArray(R.array.apk_extensions).map { it.lowercase() }
            val videoExt = application.resources.getStringArray(R.array.video_extensions).map { it.lowercase() }
            val imageExt = application.resources.getStringArray(R.array.image_extensions).map { it.lowercase() }
            val audioExt = application.resources.getStringArray(R.array.audio_extensions).map { it.lowercase() }
            val archiveExt = application.resources.getStringArray(R.array.archive_extensions).map { it.lowercase() }
            val officeExt = application.resources.getStringArray(R.array.microsoft_office_extensions).map { it.lowercase() }

            fun fileType(file: File): String = when (file.extension.lowercase()) {
                in videoExt -> "video"
                in archiveExt -> "archive"
                in apkExt -> "apk"
                in imageExt -> "image"
                in audioExt -> "audio"
                in officeExt -> "doc"
                else -> "other"
            }

            val groups = mutableMapOf<String, MutableList<File>>()
            val seenHashes = mutableSetOf<String>()

            DirectoryScanner.scan(
                root = root,
                skipDir = { dir ->
                    dir.absolutePath.startsWith(trashDir.absolutePath) ||
                        dir.absolutePath.startsWith(File(root, "Android").absolutePath) ||
                        dir.isHidden
                }
            ) { file ->
                if (file.length() >= minSize && file.absolutePath !in trashed) {
                    val hash = file.partialMd5() ?: return@scan
                    if (seenHashes.add(hash)) {
                        val type = fileType(file)
                        groups.getOrPut(type) { mutableListOf() }.add(file)
                    }
                }
            }

            groups.values.forEach { list ->
                list.sortWith(compareByDescending<File> { it.length() }.thenByDescending { it.lastModified() })
            }

            val result = mutableListOf<File>()
            var index = 0
            while (result.size < limit) {
                var added = false
                for (list in groups.values) {
                    if (index < list.size && result.size < limit) {
                        result.add(list[index])
                        added = true
                    }
                }
                if (!added) break
                index++
            }
            result
        }
    }
}
