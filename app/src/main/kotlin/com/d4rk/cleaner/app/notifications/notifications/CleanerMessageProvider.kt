package com.d4rk.cleaner.app.notifications.notifications

import android.content.Context
import com.d4rk.cleaner.R

object CleanerMessageProvider {

    private fun randomOf(vararg options: String): String = options.random()

    fun getStorageText(context: Context, usagePercent: Int): String = when {
        usagePercent >= 95 -> randomOf(
            context.getString(R.string.cleanup_storage_very_high),
            context.getString(R.string.cleanup_storage_very_high_2),
            context.getString(R.string.cleanup_storage_very_high_3),
            context.getString(R.string.cleanup_storage_very_high_4),
            context.getString(R.string.cleanup_storage_very_high_5),
            context.getString(R.string.cleanup_storage_very_high_6)
        )

        usagePercent >= 90 -> randomOf(
            context.getString(R.string.cleanup_storage_high, usagePercent),
            context.getString(R.string.cleanup_storage_high_2, usagePercent),
            context.getString(R.string.cleanup_storage_high_3, usagePercent),
            context.getString(R.string.cleanup_storage_high_4, usagePercent),
            context.getString(R.string.cleanup_storage_high_5, usagePercent),
            context.getString(R.string.cleanup_storage_high_6, usagePercent)
        )

        usagePercent >= 80 -> randomOf(
            context.getString(R.string.cleanup_storage_medium),
            context.getString(R.string.cleanup_storage_medium_2),
            context.getString(R.string.cleanup_storage_medium_3),
            context.getString(R.string.cleanup_storage_medium_4),
            context.getString(R.string.cleanup_storage_medium_5),
            context.getString(R.string.cleanup_storage_medium_6)
        )

        else -> randomOf(
            context.getString(R.string.cleanup_storage_ok),
            context.getString(R.string.cleanup_storage_ok_2),
            context.getString(R.string.cleanup_storage_ok_3),
            context.getString(R.string.cleanup_storage_ok_4),
            context.getString(R.string.cleanup_storage_ok_5),
            context.getString(R.string.cleanup_storage_ok_6)
        )
    }

    fun getTitleVariants(context: Context): List<String> = listOf(
        context.getString(R.string.cleanup_title_variant1),
        context.getString(R.string.cleanup_title_variant2),
        context.getString(R.string.cleanup_title_variant3),
        context.getString(R.string.cleanup_title_variant4),
        context.getString(R.string.cleanup_title_variant5),
        context.getString(R.string.cleanup_title_variant6),
        context.getString(R.string.cleanup_title_variant7),
        context.getString(R.string.cleanup_title_variant8),
        context.getString(R.string.cleanup_title_variant9),
        context.getString(R.string.cleanup_title_variant10),
        context.getString(R.string.cleanup_title_variant11),
        context.getString(R.string.cleanup_title_variant12),
        context.getString(R.string.cleanup_title_variant13),
        context.getString(R.string.cleanup_title_variant14),
        context.getString(R.string.cleanup_title_variant15)
    )

    fun getRandomQuickScanTip(context: Context): String = randomOf(
        context.getString(R.string.quick_scan_summary_tip),
        context.getString(R.string.quick_scan_summary_tip_2),
        context.getString(R.string.quick_scan_summary_tip_3),
        context.getString(R.string.quick_scan_summary_tip_4),
        context.getString(R.string.quick_scan_summary_tip_5),
        context.getString(R.string.quick_scan_summary_tip_6),
        context.getString(R.string.quick_scan_summary_tip_7),
        context.getString(R.string.quick_scan_summary_tip_8)
    )
}