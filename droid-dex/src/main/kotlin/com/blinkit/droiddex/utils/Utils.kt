package com.blinkit.droiddex.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blinkit.droiddex.constants.PerformanceLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.roundToInt

internal fun convertBytesToMB(value: Long): Float = (value * 1.0F) / (1024 * 1024)

internal fun convertBytesToGB(value: Long): Float = convertBytesToMB(value) / 1024

internal fun floor(value: Float): Int = kotlin.math.floor(value).roundToInt()

internal fun <T: Number> List<T>.average() = if (isNotEmpty()) ceil(sumOf { it.toDouble() } / size) else null

internal fun getPerformanceLevelWithWeights(performanceLevelWithWeights: List<Pair<PerformanceLevel, Float>>): PerformanceLevel {
	var weightedSum = 0F
	var totalWeight = 0F

	performanceLevelWithWeights.filter { it.first != PerformanceLevel.UNKNOWN }.forEach { (level, weight) ->
		weightedSum += level.level * weight
		totalWeight += weight
	}

	return PerformanceLevel.getPerformanceLevel(if (totalWeight != 0F) floor(weightedSum / totalWeight) else 0)
}

internal fun getPerformanceLevelLdWithWeights(
	performanceLevelLdWithWeights: List<Pair<LiveData<PerformanceLevel>, Float>>, onChanged: (PerformanceLevel) -> Unit
): LiveData<PerformanceLevel> {
	return MediatorLiveData<PerformanceLevel>().apply {
		performanceLevelLdWithWeights.forEach { performanceLevelLdWithWeight ->
			addSource(performanceLevelLdWithWeight.first) {
				val performanceLevel = getPerformanceLevelWithWeights(performanceLevelLdWithWeights.mapNotNull {
					it.first.value?.let { performanceLevel -> Pair(performanceLevel, it.second) }
				})
				if (value != performanceLevel) {
					value = performanceLevel
					onChanged(performanceLevel)
				}
			}
		}
	}
}

internal fun runAsyncPeriodically(block: () -> Unit, delayInSecs: Float) = with(ProcessLifecycleOwner.get()) {
	block()
	lifecycleScope.launch {
		repeatOnLifecycle(Lifecycle.State.RESUMED) {
			while (true) {
				withContext(Dispatchers.IO) { block() }
				delay((delayInSecs * 1000).toLong())
			}
		}
	}
}
