package com.blinkit.droiddex.cpu

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.performance.play.services.PlayServicesDevicePerformance
import com.blinkit.droiddex.constants.PerformanceClass
import com.blinkit.droiddex.constants.PerformanceLevel
import com.blinkit.droiddex.cpu.utils.CpuInfoManager
import com.blinkit.droiddex.factory.base.PerformanceManager
import com.blinkit.droiddex.factory.providers.PerformanceManagerProvider
import com.blinkit.droiddex.utils.getApproxHeapLimitInMB
import com.blinkit.droiddex.utils.getTotalRamInGB
import java.util.Locale

/**
 * Inspired By <a href="https://github.com/DrKLO/Telegram/blob/dfd74f809e97d1ecad9672fc7388cb0223a95dfc/TMessagesProj/src/main/java/org/telegram/messenger/SharedConfig.java#L1361">Telegram's CPU Division</a>
 */
internal class CpuPerformanceManager(private val applicationContext: Context): PerformanceManager() {

	private val cpuInfoManager by lazy { CpuInfoManager(logger) }

	private val devicePerformance by lazy { PlayServicesDevicePerformance(applicationContext) }

	private val lowSocModels = intArrayOf(
		-1775228513,  // EXYNOS 850
		802464304,  // EXYNOS 7872
		802464333,  // EXYNOS 7880
		802464302,  // EXYNOS 7870
		2067362118,  // MSM8953
		2067362060,  // MSM8937
		2067362084,  // MSM8940
		2067362241,  // MSM8992
		2067362117,  // MSM8952
		2067361998,  // MSM8917
		-1853602818 // SDM439
	)

	override fun getPerformanceClass() = PerformanceClass.CPU

	override fun getDelayInSecs() = DELAY_IN_SECS

	override fun measurePerformanceLevel(): PerformanceLevel {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			val socModel = getBuildSocModel()
			if (lowSocModels.any { it == socModel }) {
				logInfo("SOC MODEL: $socModel IN LIST OF LOW SOC MODELS")
				return PerformanceLevel.LOW
			}
		}

		val coresCount = cpuInfoManager.noOfCores.also { logger.logDebug("CORE COUNT: $it") }

		val maxCpuFreq = cpuInfoManager.maxCpuFreqInMHz

		val ramInGB = getTotalRamInGB(applicationContext, logger)

		val androidVersion = getAndroidVersion()

		val mediaPerformanceClass = getMediaPerformanceClass()

		val approxHeapLimitInMB = getApproxHeapLimitInMB(logger)

		return if (mediaPerformanceClass >= Build.VERSION_CODES.TIRAMISU || ramInGB >= 12) {
			PerformanceLevel.EXCELLENT
		} else if (androidVersion < 21 || coresCount <= 2 || approxHeapLimitInMB <= 100 || coresCount <= 4 && maxCpuFreq <= 1250 || coresCount <= 4 && maxCpuFreq <= 1600 && approxHeapLimitInMB <= 128 && androidVersion <= 21 || coresCount <= 4 && maxCpuFreq <= 1300 && approxHeapLimitInMB <= 128 && androidVersion <= 24 || ramInGB <= 2) {
			PerformanceLevel.LOW
		} else if (coresCount < 8 || approxHeapLimitInMB <= 160 || maxCpuFreq <= 2055 || coresCount == 8 && androidVersion <= 23 || ramInGB <= 6) {
			PerformanceLevel.AVERAGE
		} else {
			PerformanceLevel.HIGH
		}
	}

	@RequiresApi(Build.VERSION_CODES.S)
	private fun getBuildSocModel() = Build.SOC_MODEL.uppercase(Locale.getDefault()).hashCode()

	private fun getMediaPerformanceClass() =
		devicePerformance.mediaPerformanceClass.also { logDebug("MEDIA PERFORMANCE CLASS: $it") }

	private fun getAndroidVersion() = Build.VERSION.SDK_INT.also { logDebug("ANDROID VERSION: $it") }

	companion object: PerformanceManagerProvider {

		override fun create(applicationContext: Context): PerformanceManager = CpuPerformanceManager(applicationContext)

		private const val DELAY_IN_SECS = 60F
	}
}
