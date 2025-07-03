package com.d4rk.cleaner.app.clean.whatsapp.summary.data

import android.app.Application
import android.os.Environment
import android.text.format.Formatter
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.DirectorySummary
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.WhatsAppMediaSummary
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.repository.WhatsAppCleanerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class WhatsAppCleanerRepositoryImpl(private val application: Application) : WhatsAppCleanerRepository {

    private fun getWhatsAppMediaDir(): File {
        val scoped = File(Environment.getExternalStorageDirectory(),
            "Android/media/com.whatsapp/WhatsApp/Media")
        val legacy = File(Environment.getExternalStorageDirectory(),
            "WhatsApp/Media")
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
                .toList()
            val size = files.sumOf { it.length() }
            val formatted = Formatter.formatFileSize(application, size)
            return DirectorySummary(files, size, formatted)
        }

        val directories = mapOf(
            "images" to "WhatsApp Images",
            "videos" to "WhatsApp Video",
            "documents" to "WhatsApp Documents",
            "audios" to "WhatsApp Audio",
            "statuses" to ".Statuses",
            "voice_notes" to "WhatsApp Voice Notes",
            "video_notes" to "WhatsApp Video Notes",
            "gifs" to "WhatsApp Animated Gifs",
            "wallpapers" to "WallPaper",
            "stickers" to "WhatsApp Stickers",
            "profile_photos" to "WhatsApp Profile Photos",
        )

        val collected = directories.mapValues { (_, dirName) -> collect(dirName) }

        val images = collected.getValue("images")
        val videos = collected.getValue("videos")
        val docs = collected.getValue("documents")
        val audios = collected.getValue("audios")
        val statuses = collected.getValue("statuses")
        val voiceNotes = collected.getValue("voice_notes")
        val videoNotes = collected.getValue("video_notes")
        val gifs = collected.getValue("gifs")
        val wallpapers = collected.getValue("wallpapers")
        val stickers = collected.getValue("stickers")
        val profile = collected.getValue("profile_photos")

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
}
