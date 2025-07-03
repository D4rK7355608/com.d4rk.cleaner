/*
 * Copyright (C) 2025 Vishnu Sanal T
 *
 * This file is part of WhatsAppCleaner.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.vishnu.whatsappcleaner.model

import com.vishnu.whatsappcleaner.R
import java.io.Serializable

data class ListDirectory(
    val name: String,
    val path: String,
    val icon: Int,
    val hasSent: Boolean = true,
    val hasPrivate: Boolean = true,
    var size: String = "0 B"
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = -5435756175248173106L

        fun getDirectoryList(homePath: String): List<ListDirectory> = listOf(
            ListDirectory(
                "Images",
                "$homePath/Media/WhatsApp Images",
                R.drawable.image
            ),
            ListDirectory(
                "Videos",
                "$homePath/Media/WhatsApp Video",
                R.drawable.video
            ),
            ListDirectory(
                "Documents",
                "$homePath/Media/WhatsApp Documents",
                R.drawable.document
            ),

            ListDirectory(
                "Audios",
                "$homePath/Media/WhatsApp Audio",
                R.drawable.audio
            ),
            ListDirectory(
                "Statuses",
                "$homePath/Media/.Statuses",
                R.drawable.status,
                false,
                false
            ),

            ListDirectory(
                "Voice Notes",
                "$homePath/Media/WhatsApp Voice Notes",
                R.drawable.voice,
                false,
                false
            ),
            ListDirectory(
                "Video Notes",
                "$homePath/Media/WhatsApp Video Notes",
                R.drawable.video_notes,
                false,
                false
            ),

            ListDirectory(
                "GIFs",
                "$homePath/Media/WhatsApp Animated Gifs",
                R.drawable.gif
            ),
            ListDirectory(
                "Wallpapers",
                "$homePath/Media/WallPaper",
                R.drawable.wallpaper,
                false,
                false
            ),
            ListDirectory(
                "Stickers",
                "$homePath/Media/WhatsApp Stickers",
                R.drawable.sticker,
                false,
                false
            ),
            ListDirectory(
                "Profile Photos",
                "$homePath/Media/WhatsApp Profile Photos",
                R.drawable.profile,
                false,
                false
            ),
        )
    }
}
