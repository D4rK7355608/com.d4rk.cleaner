package com.blinkit.droiddex.memory

import android.content.Context
import com.blinkit.droiddex.constants.PerformanceClass
import com.blinkit.droiddex.constants.PerformanceLevel
import com.blinkit.droiddex.factory.base.PerformanceManager
import com.blinkit.droiddex.factory.providers.PerformanceManagerProvider
import com.blinkit.droiddex.utils.getApproxHeapLimitInMB
import com.blinkit.droiddex.utils.getApproxHeapRemainingInMB
import com.blinkit.droiddex.utils.getAvailableRamInGB
import com.blinkit.droiddex.utils.getMemoryInfo

internal class MemoryPerformanceManager(private val applicationContext: Context): PerformanceManager() {

	override fun getPerformanceClass() = PerformanceClass.MEMORY

	override fun getDelayInSecs() = DELAY_IN_SECS

	override fun measurePerformanceLevel(): PerformanceLevel {
		if (getMemoryInfo(applicationContext, logger).lowMemory) {
			logInfo("DEVICE HAS LOW MEMORY")
			return PerformanceLevel.LOW
		}

		val availableRamInGB = getAvailableRamInGB(applicationContext, logger)

		val approxHeapLimitInMB = getApproxHeapLimitInMB(logger)

		val approxHeapRemainingInMB = getApproxHeapRemainingInMB(logger)

		return if (approxHeapRemainingInMB <= 64 || approxHeapLimitInMB < 128) {
			PerformanceLevel.LOW
		} else if (approxHeapRemainingInMB <= 128 || approxHeapLimitInMB < 256 || availableRamInGB <= 2) {
			PerformanceLevel.AVERAGE
		} else if (approxHeapRemainingInMB <= 256 || availableRamInGB <= 3) {
			PerformanceLevel.HIGH
		} else {
			PerformanceLevel.EXCELLENT
		}
	}

	companion object: PerformanceManagerProvider {

		override fun create(applicationContext: Context): PerformanceManager =
			MemoryPerformanceManager(applicationContext)

		private const val DELAY_IN_SECS = 10F
	}
}
