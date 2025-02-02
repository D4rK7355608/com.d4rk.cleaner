package com.d4rk.cleaner.utils.helpers

import android.content.Context
import com.d4rk.cleaner.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeHelper {

    fun formatDate(context: Context, date: Date): String {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)

        val calendar = Calendar.getInstance()
        calendar.time = date

        return when {
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> {
                context.getString(R.string.today)
            }
            calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) -> {
                context.getString(R.string.yesterday)
            }
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
                SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(date)
            }
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) - 1 -> {
                context.getString(R.string.a_year_ago)
            }
            else -> {
                val yearsAgo = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)
                context.resources.getQuantityString(R.plurals.years_ago, yearsAgo, yearsAgo)
            }
        }
    }
}