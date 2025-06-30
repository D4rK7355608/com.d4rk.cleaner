package com.d4rk.cleaner.app.notifications.notifications

import android.content.Context
import com.d4rk.cleaner.R

object CleanerMessageProvider {
    fun getStorageText(context: Context, usagePercent: Int): String = when {
        usagePercent >= 95 -> context.getString(R.string.cleanup_storage_very_high)
        usagePercent >= 90 -> context.getString(R.string.cleanup_storage_high, usagePercent)
        usagePercent >= 80 -> context.getString(R.string.cleanup_storage_medium)
        else -> context.getString(R.string.cleanup_storage_ok)
    }

    fun getTitleVariants(context: Context): List<String> = listOf(
        context.getString(R.string.cleanup_title_variant1),
        context.getString(R.string.cleanup_title_variant2),
        context.getString(R.string.cleanup_title_variant3),
        context.getString(R.string.cleanup_title_variant4),
        context.getString(R.string.cleanup_title_variant5)
    )
}