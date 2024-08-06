package com.d4rk.cleaner.utils.cleaning

import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

object FileUtils {

    /**
     * Formats a file size in bytes to a human-readable string (e.g., "128 MB").
     *
     * @param size The file size in bytes.
     * @return A formatted string representing the file size.
     */
    fun formatSize(size : Long) : String {
        if (size <= 0) return "0 B"
        val units : Array<String> = arrayOf("B" , "KB" , "MB" , "GB" , "TB")
        val digitGroups : Int = (log10(size.toDouble()) / log10(x = 1024.0)).toInt()
        return String.format(
            Locale.US ,
            format = "%.2f %s" ,
            size / 1024.0.pow(digitGroups.toDouble()) ,
            units[digitGroups]
        )
    }
}