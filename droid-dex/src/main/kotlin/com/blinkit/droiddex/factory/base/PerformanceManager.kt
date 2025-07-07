package com.blinkit.droiddex.factory.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blinkit.droiddex.constants.PerformanceClass
import com.blinkit.droiddex.constants.PerformanceLevel
import com.blinkit.droiddex.utils.Logger
import com.blinkit.droiddex.utils.runAsyncPeriodically
import kotlin.concurrent.Volatile

internal abstract class PerformanceManager {

	@Volatile
	var performanceLevel = PerformanceLevel.UNKNOWN
		private set

	private val _performanceLevelLd = MutableLiveData(PerformanceLevel.UNKNOWN)
	val performanceLevelLd: LiveData<PerformanceLevel>
		get() = _performanceLevelLd

	protected val logger by lazy { Logger(getPerformanceClass()) }

	fun init() {
		runAsyncPeriodically({
			try {
				measurePerformanceLevel().also {
					val hasPerformanceLevelChanged = performanceLevelLd.value != it
					if (hasPerformanceLevelChanged) {
						performanceLevel = it
						_performanceLevelLd.postValue(it)
					}
					logger.logPerformanceLevelChange(it, hasPerformanceLevelChanged)
				}
			} catch (e: Exception) {
				logger.logError(e)
			}
		}, delayInSecs = getDelayInSecs())
	}

	@PerformanceClass
	protected abstract fun getPerformanceClass(): Int

	protected abstract fun getDelayInSecs(): Float

	protected abstract fun measurePerformanceLevel(): PerformanceLevel

	protected fun logInfo(message: String) = logger.logInfo(message)

	protected fun logDebug(message: String) = logger.logDebug(message)

	protected fun logError(throwable: Throwable) = logger.logError(throwable)
}
