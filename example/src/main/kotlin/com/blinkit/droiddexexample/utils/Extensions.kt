package com.blinkit.droiddexexample.utils

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorInt
import com.blinkit.droiddex.constants.PerformanceLevel
import com.blinkit.droiddexexample.R

fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density)

@ColorInt
fun PerformanceLevel.getColor(context: Context) = context.getColor(
	when (this) {
		PerformanceLevel.EXCELLENT -> R.color.excellent
		PerformanceLevel.HIGH -> R.color.high
		PerformanceLevel.AVERAGE -> R.color.average
		PerformanceLevel.LOW -> R.color.low
		PerformanceLevel.UNKNOWN -> android.R.color.black
	}
)
