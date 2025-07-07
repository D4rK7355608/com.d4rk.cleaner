package com.blinkit.droiddex.network.utils

import android.net.TrafficStats
import android.os.SystemClock
import com.blinkit.droiddex.utils.Logger
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.ln

/**
 * Inspired By <a href="https://github.com/facebookarchive/network-connection-class/tree/master/connectionclass/src/main/java/com/facebook/network/connectionclass">Facebook's Network Connection Class</a>
 */
internal class BandwidthManager(private val logger: Logger) {

	private var lastReadTime: Long = SystemClock.elapsedRealtime()

	private var lastDownloadBytes: Long = TrafficStats.getTotalRxBytes()

	private val downloadBandwidth = ExponentialGeometricAverage(DECAY_CONSTANT)

	fun addSampleAndRecalculateBandwidthAverage(): Double {
		val currentBytes = TrafficStats.getTotalRxBytes()
		val currentTime = SystemClock.elapsedRealtime()

		addBandwidth(currentBytes - lastDownloadBytes, currentTime - lastReadTime)

		lastReadTime = currentTime
		lastDownloadBytes = currentBytes

		return downloadBandwidth.average.also { logger.logDebug("BANDWIDTH AVERAGE: $it Kb/s") }
	}

	private fun addBandwidth(bytes: Long, timeInMs: Long) {
		val bandwidth = ((bytes * 1.0) / timeInMs) * BYTES_TO_BITS
		if (timeInMs == 0L || bandwidth < BANDWIDTH_LOWER_BOUND) {
			return
		}

		downloadBandwidth.addValue(bandwidth)
	}

	private class ExponentialGeometricAverage(private val decayConstant: Double) {

		var average = -1.0
			private set

		private val cutOver: Int = if (decayConstant == 0.0) Int.MAX_VALUE else ceil(1 / decayConstant).toInt()

		private var count = 0

		// Adds a new value to the moving average
		fun addValue(value: Double) {
			val keepConstant = 1 - decayConstant
			average = if (count > cutOver) {
				exp(keepConstant * ln(average) + decayConstant * ln(value))
			} else if (count > 0) {
				val retained = keepConstant * count / (count + 1.0)
				val newcomer = 1.0 - retained
				exp(retained * ln(average) + newcomer * ln(value))
			} else {
				value
			}
			count++
		}

		// Resets the moving average
		fun reset() {
			average = -1.0
			count = 0
		}
	}

	companion object {

		private const val BYTES_TO_BITS = 8

		/**
		 * The factor used to calculate the current bandwidth
		 * depending upon the previous calculated value for bandwidth.
		 *
		 * The smaller this value is, the less responsive to new samples the moving average becomes.
		 */
		private const val DECAY_CONSTANT = 0.05

		/**
		 * The lower bound for measured bandwidth in bits/ms. Readings
		 * lower than this are treated as effectively zero (therefore ignored).
		 */
		private const val BANDWIDTH_LOWER_BOUND = 10
	}
}
