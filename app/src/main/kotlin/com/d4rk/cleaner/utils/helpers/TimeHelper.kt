package com.d4rk.cleaner.utils.helpers

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeHelper {

    fun formatDate(date: Date): String {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)

        val calendar = Calendar.getInstance()
        calendar.time = date

        return when {
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> {
                "Today"
            }
            calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) -> {
                "Yesterday"
            }
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
                SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(date)
            }
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) - 1 -> {
                "a year ago"
            }
            else -> {
                val yearsAgo = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)
                "$yearsAgo years ago"
            }
        }
    }
}