package com.blinkit.droiddex.factory.factory

import android.content.Context
import androidx.lifecycle.LiveData
import com.blinkit.droiddex.battery.BatteryPerformanceManager
import com.blinkit.droiddex.constants.PerformanceClass
import com.blinkit.droiddex.constants.PerformanceLevel
import com.blinkit.droiddex.cpu.CpuPerformanceManager
import com.blinkit.droiddex.factory.base.PerformanceManager
import com.blinkit.droiddex.memory.MemoryPerformanceManager
import com.blinkit.droiddex.network.NetworkPerformanceManager
import com.blinkit.droiddex.storage.StoragePerformanceManager

internal class PerformanceManagerFactory(private val applicationContext: Context) {

	private val performanceManagerMap = mutableMapOf<@PerformanceClass Int, PerformanceManager>()

	init {
		PerformanceClass.values().forEach { getOrPut(it) }
	}

	fun getPerformanceLevel(@PerformanceClass performanceClass: Int): PerformanceLevel =
		getOrPut(performanceClass).performanceLevel

	fun getPerformanceLevelLd(@PerformanceClass performanceClass: Int): LiveData<PerformanceLevel> =
		getOrPut(performanceClass).performanceLevelLd

	private fun getOrPut(@PerformanceClass performanceClass: Int): PerformanceManager =
		performanceManagerMap.getOrPut(performanceClass) {
			when (performanceClass) {
				PerformanceClass.CPU -> CpuPerformanceManager.create(applicationContext)
				PerformanceClass.MEMORY -> MemoryPerformanceManager.create(applicationContext)
				PerformanceClass.STORAGE -> StoragePerformanceManager.create(applicationContext)
				PerformanceClass.NETWORK -> NetworkPerformanceManager.create(applicationContext)
				PerformanceClass.BATTERY -> BatteryPerformanceManager.create(applicationContext)
				else -> throw IllegalArgumentException("NO SUCH PERFORMANCE CLASS EXISTS: $performanceClass")
			}.apply { init() }
		}
}
