package com.d4rk.cleaner.core.utils.helpers

import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

object FileSizeFormatter {
    fun format(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        val value = size / 1024.0.pow(digitGroups.toDouble())
        return String.format(Locale.US, "%.2f %s", value, units[digitGroups])
    }
}
