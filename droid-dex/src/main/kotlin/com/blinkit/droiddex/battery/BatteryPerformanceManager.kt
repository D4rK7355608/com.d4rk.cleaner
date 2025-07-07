package com.blinkit.droiddex.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.blinkit.droiddex.constants.PerformanceClass
import com.blinkit.droiddex.constants.PerformanceLevel
import com.blinkit.droiddex.factory.base.PerformanceManager
import com.blinkit.droiddex.factory.providers.PerformanceManagerProvider

internal class BatteryPerformanceManager(private val applicationContext: Context): PerformanceManager() {

	override fun getPerformanceClass() = PerformanceClass.BATTERY

	override fun getDelayInSecs() = DELAY_IN_SECS

	override fun measurePerformanceLevel(): PerformanceLevel {
		val batteryStatusIntent =
			IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { applicationContext.registerReceiver(null, it) }
		batteryStatusIntent ?: return PerformanceLevel.UNKNOWN

		val isCharging = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1).let {
			it == BatteryManager.BATTERY_STATUS_CHARGING || it == BatteryManager.BATTERY_STATUS_FULL
		}.also { logDebug("IS BATTERY CHARGING: $it") }

		val batteryPercentage = batteryStatusIntent.let { intent ->
			val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1).takeUnless { it == -1 } ?: return@let -1F
			val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1).takeUnless { it == -1 } ?: return@let -1F
			level * 100 / scale.toFloat()
		}.also { logDebug("BATTERY PERCENTAGE: $it%") }

		return when {
			batteryPercentage >= 80 || (isCharging && batteryPercentage >= 70) -> PerformanceLevel.EXCELLENT
			batteryPercentage >= 55 || (isCharging && batteryPercentage >= 50) -> PerformanceLevel.HIGH
			batteryPercentage >= 40 || (isCharging && batteryPercentage >= 35) -> PerformanceLevel.AVERAGE
			else -> PerformanceLevel.LOW
		}
	}

	companion object: PerformanceManagerProvider {

		override fun create(applicationContext: Context): PerformanceManager =
			BatteryPerformanceManager(applicationContext)

		private const val DELAY_IN_SECS = 60F
	}
}
