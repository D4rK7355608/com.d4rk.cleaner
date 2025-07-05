package com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model

data class WhatsAppMediaSummary(
    val images: DirectorySummary = DirectorySummary(),
    val videos: DirectorySummary = DirectorySummary(),
    val documents: DirectorySummary = DirectorySummary(),
    val audios: DirectorySummary = DirectorySummary(),
    val statuses: DirectorySummary = DirectorySummary(),
    val voiceNotes: DirectorySummary = DirectorySummary(),
    val videoNotes: DirectorySummary = DirectorySummary(),
    val gifs: DirectorySummary = DirectorySummary(),
    val wallpapers: DirectorySummary = DirectorySummary(),
    val stickers: DirectorySummary = DirectorySummary(),
    val profilePhotos: DirectorySummary = DirectorySummary(),
    val formattedTotalSize: String = "0 B",
) {
    val hasData: Boolean
        get() = listOf(
            images,
            videos,
            documents,
            audios,
            statuses,
            voiceNotes,
            videoNotes,
            gifs,
            wallpapers,
            stickers,
            profilePhotos,
        ).any { it.files.isNotEmpty() }

    val totalBytes: Long
        get() = listOf(
            images,
            videos,
            documents,
            audios,
            statuses,
            voiceNotes,
            videoNotes,
            gifs,
            wallpapers,
            stickers,
            profilePhotos,
        ).sumOf { it.totalBytes }
}

data class UiWhatsAppCleanerModel(
    val mediaSummary: WhatsAppMediaSummary = WhatsAppMediaSummary(),
    val totalSize: String = mediaSummary.formattedTotalSize,
)
