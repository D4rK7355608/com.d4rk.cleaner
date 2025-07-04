package com.d4rk.cleaner.app.clean.whatsapp.utils.constants

object WhatsAppMediaConstants {
    const val IMAGES = "images"
    const val VIDEOS = "videos"
    const val DOCUMENTS = "documents"
    const val AUDIOS = "audios"
    const val STATUSES = "statuses"
    const val VOICE_NOTES = "voice_notes"
    const val VIDEO_NOTES = "video_notes"
    const val GIFS = "gifs"
    const val WALLPAPERS = "wallpapers"
    const val STICKERS = "stickers"
    const val PROFILE_PHOTOS = "profile_photos"

    val DIRECTORIES = mapOf(
        IMAGES to "WhatsApp Images",
        VIDEOS to "WhatsApp Video",
        DOCUMENTS to "WhatsApp Documents",
        AUDIOS to "WhatsApp Audio",
        STATUSES to ".Statuses",
        VOICE_NOTES to "WhatsApp Voice Notes",
        VIDEO_NOTES to "WhatsApp Video Notes",
        GIFS to "WhatsApp Animated Gifs",
        WALLPAPERS to "WallPaper",
        STICKERS to "WhatsApp Stickers",
        PROFILE_PHOTOS to "WhatsApp Profile Photos",
    )
}
