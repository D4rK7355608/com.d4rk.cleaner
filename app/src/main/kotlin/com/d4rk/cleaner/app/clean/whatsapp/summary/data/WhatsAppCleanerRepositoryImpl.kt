package com.d4rk.cleaner.app.clean.whatsapp.summary.data

import android.app.Application
import android.os.Environment
import android.text.format.Formatter
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.DirectorySummary
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.WhatsAppMediaSummary
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.repository.WhatsAppCleanerRepository
import com.d4rk.cleaner.app.clean.whatsapp.utils.constants.WhatsAppMediaConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class WhatsAppCleanerRepositoryImpl(private val application: Application) :
    WhatsAppCleanerRepository {

    private fun getWhatsAppMediaDir(): File {
        val scoped = File(
            Environment.getExternalStorageDirectory(),
            "Android/media/com.whatsapp/WhatsApp/Media"
        )
        val legacy = File(
            Environment.getExternalStorageDirectory(),
            "WhatsApp/Media"
        )
        return when {
            scoped.exists() -> scoped
            legacy.exists() -> legacy
            else -> scoped
        }
    }

    override suspend fun getMediaSummary(): WhatsAppMediaSummary = withContext(Dispatchers.IO) {
        val base = getWhatsAppMediaDir()

        fun collect(name: String): DirectorySummary {
            val dir = File(base, name)
            if (!dir.exists()) return DirectorySummary()
            val files = dir.walkTopDown()
                .filter { it.isFile && it.name != ".nomedia" }
            var count = 0
            var size = 0L
            files.forEach {
                count++
                size += it.length()
            }
            val formatted = Formatter.formatFileSize(application, size)
            return DirectorySummary(count, size, formatted)
        }

        val directories = WhatsAppMediaConstants.DIRECTORIES

        val collected = directories.mapValues { (_, dirName) -> collect(dirName) }

        val images = collected.getValue(WhatsAppMediaConstants.IMAGES)
        val videos = collected.getValue(WhatsAppMediaConstants.VIDEOS)
        val docs = collected.getValue(WhatsAppMediaConstants.DOCUMENTS)
        val audios = collected.getValue(WhatsAppMediaConstants.AUDIOS)
        val statuses = collected.getValue(WhatsAppMediaConstants.STATUSES)
        val voiceNotes = collected.getValue(WhatsAppMediaConstants.VOICE_NOTES)
        val videoNotes = collected.getValue(WhatsAppMediaConstants.VIDEO_NOTES)
        val gifs = collected.getValue(WhatsAppMediaConstants.GIFS)
        val wallpapers = collected.getValue(WhatsAppMediaConstants.WALLPAPERS)
        val stickers = collected.getValue(WhatsAppMediaConstants.STICKERS)
        val profile = collected.getValue(WhatsAppMediaConstants.PROFILE_PHOTOS)

        val totalSize = collected.values.sumOf { it.totalBytes }
        val totalFormatted = Formatter.formatFileSize(application, totalSize)

        WhatsAppMediaSummary(
            images = images,
            videos = videos,
            documents = docs,
            audios = audios,
            statuses = statuses,
            voiceNotes = voiceNotes,
            videoNotes = videoNotes,
            gifs = gifs,
            wallpapers = wallpapers,
            stickers = stickers,
            profilePhotos = profile,
            formattedTotalSize = totalFormatted,
        )
    }

    override suspend fun deleteFiles(files: List<File>) = withContext(Dispatchers.IO) {
        files.forEach { file ->
            if (file.exists()) file.deleteRecursively()
        }
    }

    override suspend fun listMediaFiles(type: String, offset: Int, limit: Int): List<File> =
        withContext(Dispatchers.IO) {
            val base = getWhatsAppMediaDir()
            val dirName =
                WhatsAppMediaConstants.DIRECTORIES[type] ?: return@withContext emptyList<File>()
            val dir = File(base, dirName)
            if (!dir.exists()) return@withContext emptyList<File>()
            dir.walkTopDown()
                .filter { it.isFile && it.name != ".nomedia" }
                .drop(offset)
                .take(limit)
                .toList()
        }

}
