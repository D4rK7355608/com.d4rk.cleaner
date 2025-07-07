package com.blinkit.droiddex.utils

import android.app.ActivityManager
import android.content.Context
import kotlin.math.min

internal fun getMemoryInfo(applicationContext: Context, logger: Logger): ActivityManager.MemoryInfo =
	ActivityManager.MemoryInfo().also { getActivityManager(applicationContext, logger)?.getMemoryInfo(it) }

/**
 * <a href="https://stackoverflow.com/a/9428660">Detailed Explanation</a>
 *
 * Max Memory gives the actual limit for the heap
 *
 */
internal fun getApproxHeapLimitInMB(logger: Logger): Float =
	convertBytesToMB(Runtime.getRuntime().maxMemory()).also { logger.logDebug("APPROXIMATE HEAP LIMIT: $it MB") }

internal fun getApproxHeapRemainingInMB(logger: Logger): Float {
	val heapLimitInMB = getApproxHeapLimitInMB(logger)
	val heapUsedInMB = convertBytesToMB(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())

	logger.logDebug("APPROXIMATE HEAP USED: $heapUsedInMB MB")

	return (heapLimitInMB - heapUsedInMB).also { logger.logDebug("APPROXIMATE HEAP REMAINING: $it MB") }
}

internal fun getTotalRamInGB(applicationContext: Context, logger: Logger) =
	convertBytesToGB(getMemoryInfo(applicationContext, logger).totalMem).also { logger.logDebug("TOTAL RAM: $it GB") }

internal fun getAvailableRamInGB(applicationContext: Context, logger: Logger) = convertBytesToGB(
	getMemoryInfo(applicationContext, logger).availMem
).also { logger.logDebug("AVAILABLE RAM: $it GB") }

private fun getActivityManager(applicationContext: Context, logger: Logger): ActivityManager? =
	applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: run {
		logger.logError(Throwable("ACTIVITY MANAGER IS NULL"))
		null
	}
