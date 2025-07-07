package com.blinkit.droiddex.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import androidx.annotation.IntRange
import com.blinkit.droiddex.constants.PerformanceClass
import com.blinkit.droiddex.constants.PerformanceLevel
import com.blinkit.droiddex.factory.base.PerformanceManager
import com.blinkit.droiddex.factory.providers.PerformanceManagerProvider
import com.blinkit.droiddex.network.utils.BandwidthManager
import com.blinkit.droiddex.utils.getPerformanceLevelWithWeights

internal class NetworkPerformanceManager(private val applicationContext: Context): PerformanceManager() {

	private val bandwidthManager by lazy { BandwidthManager(logger) }

	override fun getPerformanceClass() = PerformanceClass.NETWORK

	override fun getDelayInSecs() = DELAY_IN_SECS

	override fun measurePerformanceLevel(): PerformanceLevel {
		val bandwidthAverage = bandwidthManager.addSampleAndRecalculateBandwidthAverage()

		if (isInternetConnected().not()) {
			logInfo("DEVICE HAS NO INTERNET")
			return PerformanceLevel.LOW
		}

		val bandwidthAverageStrengthLevel = getBandwidthAverageStrengthLevel(bandwidthAverage)
		val downloadSpeedLevel = getDownloadSpeedStrengthLevel()
		val signalStrengthLevel = getSignalStrengthLevel()

		return getPerformanceLevelWithWeights(mutableListOf<Pair<PerformanceLevel, Float>>().apply {
			bandwidthAverageStrengthLevel?.let { add(Pair(it, 2F)) }
			add(Pair(downloadSpeedLevel, 1F))
			signalStrengthLevel?.let { add(Pair(it, 1F)) }
		})
	}

	private fun isInternetConnected(): Boolean {
		val connectivityManager = getConnectivityManager() ?: return false
		val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
		return capabilities?.let {
			it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
		} ?: false
	}

	private fun getBandwidthAverageStrengthLevel(bandwidthAverage: Double) = when {
		bandwidthAverage <= 0 -> null
		bandwidthAverage < AVERAGE_BANDWIDTH_THRESHOLD -> PerformanceLevel.LOW
		bandwidthAverage < HIGH_BANDWIDTH_THRESHOLD -> PerformanceLevel.AVERAGE
		bandwidthAverage < EXCELLENT_BANDWIDTH_THRESHOLD -> PerformanceLevel.HIGH
		else -> PerformanceLevel.EXCELLENT
	}?.also { logDebug("BANDWIDTH AVERAGE STRENGTH TYPE: ${it.name}") }

	private fun getDownloadSpeed(): Int {
		val connectivityManager = getConnectivityManager() ?: return 0
		val network = connectivityManager.activeNetwork ?: return 0
		val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
		return (networkCapabilities?.linkDownstreamBandwidthKbps ?: 0).also { logDebug("DOWNLOAD SPEED: $it Kb/s") }
	}

	private fun getDownloadSpeedStrengthLevel(): PerformanceLevel {
		val downloadSpeed = getDownloadSpeed()

		val downloadSpeedStrengthLevel = when {
			downloadSpeed >= EXCELLENT_DOWNLOAD_SPEED_THRESHOLD -> PerformanceLevel.EXCELLENT
			downloadSpeed >= HIGH_DOWNLOAD_SPEED_THRESHOLD -> PerformanceLevel.HIGH
			downloadSpeed >= AVERAGE_DOWNLOAD_SPEED_THRESHOLD -> PerformanceLevel.AVERAGE
			else -> PerformanceLevel.LOW
		}

		logDebug("DOWNLOAD SPEED TYPE: ${downloadSpeedStrengthLevel.name}")

		return downloadSpeedStrengthLevel
	}

	private fun getNetworkType(): NetworkType {
		val connectivityManager = getConnectivityManager() ?: return NetworkType.UNKNOWN

		val activeNetwork =
			connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return NetworkType.UNKNOWN

		return when {
			activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
			activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
			else -> NetworkType.UNKNOWN
		}.also { logDebug("NETWORK TYPE: ${it.name}") }
	}

	@IntRange(from = 0, to = 4)
	private fun getWifiSignalLevel(): Int {
		val wifiManger =
			applicationContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager ?: return 0
		@Suppress("DEPRECATION") val wifiInfo = wifiManger.connectionInfo
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			wifiManger.calculateSignalLevel(wifiInfo.rssi)
		} else {
			@Suppress("DEPRECATION") WifiManager.calculateSignalLevel(wifiInfo.rssi, 5)
		}.also { logDebug("WIFI SIGNAL LEVEL: $it") }
	}

	@IntRange(from = 0, to = 4)
	private fun getCellularInternetStrength(): Int {
		val telephonyManager =
			applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager ?: return 0

		val signalLevel = (try {
			when (val info = telephonyManager.allCellInfo?.firstOrNull()) {
				is CellInfoLte -> info.cellSignalStrength.level
				is CellInfoGsm -> info.cellSignalStrength.level
				is CellInfoCdma -> info.cellSignalStrength.level
				is CellInfoWcdma -> info.cellSignalStrength.level
				else -> 0
			}
		} catch (_: SecurityException) {
			0
		}).also { logDebug("CELLULAR SIGNAL LEVEL: $it") }

		val networkGeneration = (try {
			val networkType = telephonyManager.dataNetworkType
			@Suppress("DEPRECATION") when (networkType) {
				TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NetworkGeneration.NETWORK_2G
				TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkGeneration.NETWORK_3G
				TelephonyManager.NETWORK_TYPE_LTE -> NetworkGeneration.NETWORK_4G
				TelephonyManager.NETWORK_TYPE_NR -> NetworkGeneration.NETWORK_5G
				else -> NetworkGeneration.UNKNOWN
			}
		} catch (_: SecurityException) {
			NetworkGeneration.UNKNOWN
		}).also { logDebug("CELLULAR NETWORK GENERATION: ${it.name}") }

		return when (networkGeneration) {
			NetworkGeneration.UNKNOWN -> 0
			NetworkGeneration.NETWORK_2G -> (1 + signalLevel) / 2
			NetworkGeneration.NETWORK_3G -> (2 + signalLevel) / 2
			NetworkGeneration.NETWORK_4G -> (3 + signalLevel) / 2
			NetworkGeneration.NETWORK_5G -> (4 + signalLevel) / 2
		}.also { logDebug("CELLULAR SIGNAL STRENGTH: $it") }
	}

	private fun categorizeSignalThreshold(signal: Int): PerformanceLevel = when {
		signal >= EXCELLENT_SIGNAL_THRESHOLD -> PerformanceLevel.EXCELLENT
		signal >= HIGH_SIGNAL_THRESHOLD -> PerformanceLevel.HIGH
		signal >= AVERAGE_SIGNAL_THRESHOLD -> PerformanceLevel.AVERAGE
		else -> PerformanceLevel.LOW
	}

	private fun getSignalStrengthLevel() = when (getNetworkType()) {
		NetworkType.WIFI -> categorizeSignalThreshold(getWifiSignalLevel())
		NetworkType.CELLULAR -> categorizeSignalThreshold(getCellularInternetStrength())
		else -> null
	}?.also { logDebug("SIGNAL STRENGTH TYPE: ${it.name}") }

	private fun getConnectivityManager(): ConnectivityManager? =
		applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: run {
			logger.logError(Throwable("CONNECTIVITY MANAGER IS NULL"))
			null
		}

	private enum class NetworkType { UNKNOWN, CELLULAR, WIFI }

	private enum class NetworkGeneration { UNKNOWN, NETWORK_2G, NETWORK_3G, NETWORK_4G, NETWORK_5G }

	companion object: PerformanceManagerProvider {

		override fun create(applicationContext: Context): PerformanceManager =
			NetworkPerformanceManager(applicationContext)

		private const val DELAY_IN_SECS = 2.5F

		private const val EXCELLENT_BANDWIDTH_THRESHOLD = 2000F // 2 Mbps
		private const val HIGH_BANDWIDTH_THRESHOLD = 550F // 0.55 Mbps
		private const val AVERAGE_BANDWIDTH_THRESHOLD = 150F // 0.15 Mbps

		private const val EXCELLENT_DOWNLOAD_SPEED_THRESHOLD = 10000 // 10 Mbps
		private const val HIGH_DOWNLOAD_SPEED_THRESHOLD = 5000 // 5 Mbps
		private const val AVERAGE_DOWNLOAD_SPEED_THRESHOLD = 2000 // 2 Mbps

		private const val EXCELLENT_SIGNAL_THRESHOLD = 4
		private const val HIGH_SIGNAL_THRESHOLD = 3
		private const val AVERAGE_SIGNAL_THRESHOLD = 2
	}
}
