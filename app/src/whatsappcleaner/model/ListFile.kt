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

import java.io.File

data class ListFile(
    val filePath: String,
    var size: String = "0 B",
    var isSelected: Boolean = false,
) : File(filePath) {
    companion object {
        private const val serialVersionUID: Long = 8425722975465458623L
    }

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ListFile

        if (filePath != other.filePath) return false
        if (size != other.size) return false
        if (isSelected != other.isSelected) return false

        return true
    }
}
