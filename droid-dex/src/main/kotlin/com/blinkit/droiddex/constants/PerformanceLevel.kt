package com.blinkit.droiddex.constants

import androidx.annotation.Keep

@Keep
public enum class PerformanceLevel(public val level: Int) {

	UNKNOWN(0), LOW(1), AVERAGE(2), HIGH(3), EXCELLENT(4);

	public companion object {

		internal fun getPerformanceLevel(level: Int? = null): PerformanceLevel =
			entries.find { it.level == level } ?: UNKNOWN
	}
}
