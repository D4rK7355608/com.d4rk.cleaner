package com.blinkit.droiddex

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blinkit.droiddex.constants.PerformanceClass
import com.blinkit.droiddex.constants.PerformanceClass.Companion.name
import com.blinkit.droiddex.constants.PerformanceLevel
import com.blinkit.droiddex.factory.factory.PerformanceManagerFactory
import com.blinkit.droiddex.utils.Logger
import com.blinkit.droiddex.utils.getPerformanceLevelLdWithWeights
import com.blinkit.droiddex.utils.getPerformanceLevelWithWeights

public object DroidDex {

	private lateinit var performanceManagerFactory: PerformanceManagerFactory

	private val logger: Logger by lazy { Logger() }

	public fun init(applicationContext: Context) {
		if (::performanceManagerFactory.isInitialized) {
			logger.logError(IllegalStateException("Droid Dex is already initialized"))
			return
		}

		try {
			performanceManagerFactory = PerformanceManagerFactory(applicationContext)
		} catch (e: Exception) {
			logger.logError(e)
		}
	}

	/**
	 * @param classes spread list of performance classes
	 * @return Average PerformanceLevel for the input classes
	 *
	 * Steps for getting PerformanceLevel:
	 * - Check if the class has been initialized or not
	 * - Get the PerformanceLevel of individual PerformanceClass
	 * - Ignore the ones for which the PerformanceLevel is UNKNOWN
	 * - Take average of all these PerformanceClass
	 */
	public fun getPerformanceLevel(@PerformanceClass vararg classes: Int): PerformanceLevel =
		getWeightedPerformanceLevel(*classes.map { Pair(it, 1F) }.toTypedArray())

	/**
	 * @param classes spread list of performance classes
	 * @return LiveData of Average PerformanceLevel for the input classes
	 *
	 * Steps for getting PerformanceLevel:
	 * - Check if the class has been initialized or not
	 * - Get the PerformanceLevel of individual PerformanceClass
	 * - Ignore the ones for which the PerformanceLevel is UNKNOWN
	 * - Take average of all these PerformanceClass
	 */
	public fun getPerformanceLevelLd(@PerformanceClass vararg classes: Int): LiveData<PerformanceLevel> =
		getWeightedPerformanceLevelLd(*classes.map { Pair(it, 1F) }.toTypedArray())

	/**
	 * @param classes spread list of performance classes mapped with weight
	 * @return Weighted Average PerformanceLevel for the input classes
	 *
	 * Steps for getting PerformanceLevel:
	 * - Check if the class has been initialized or not
	 * - Get the PerformanceLevel of individual PerformanceClass
	 * - Ignore the ones for which the PerformanceLevel is UNKNOWN
	 * - Take weighted average of all these PerformanceClass
	 */
	public fun getWeightedPerformanceLevel(vararg classes: Pair<@PerformanceClass Int, Float>): PerformanceLevel =
		checkInitialized(*classes.map { it.first }.toIntArray()) ?: getPerformanceLevelWithWeights(classes.map {
			Pair(performanceManagerFactory.getPerformanceLevel(it.first), it.second)
		}).also { logger.logPerformanceLevelResult(*classes, performanceLevel = it) }

	/**
	 * @param classes spread list of performance classes mapped with weight
	 * @return LiveData of Weighted Average PerformanceLevel for the input classes
	 *
	 * Steps for getting PerformanceLevel:
	 * - Check if the class has been initialized or not
	 * - Get the PerformanceLevel of individual PerformanceClass
	 * - Ignore the ones for which the PerformanceLevel is UNKNOWN
	 * - Take weighted average of all these PerformanceClass
	 */
	public fun getWeightedPerformanceLevelLd(vararg classes: Pair<@PerformanceClass Int, Float>): LiveData<PerformanceLevel> =
		checkInitialized(*classes.map { it.first }.toIntArray())?.let { MutableLiveData(it) }
			?: getPerformanceLevelLdWithWeights(classes.map {
				Pair(performanceManagerFactory.getPerformanceLevelLd(it.first), it.second)
			}) { logger.logPerformanceLevelResult(*classes, performanceLevel = it) }

	private fun checkInitialized(vararg classes: Int): PerformanceLevel? {
		if (::performanceManagerFactory.isInitialized) return null

		val classesNames = classes.joinToString(", ") { it.name() }
		logger.logError(UninitializedPropertyAccessException("Droid Dex is not initialized for parameters: $classesNames"))
		return PerformanceLevel.UNKNOWN
	}
}
