package com.d4rk.cleaner.ui.viewmodel
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
class MemoryViewModel : ViewModel() {
    private val cpuTemperatureLiveData = MutableLiveData<Double>()
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L
    init {
        startCpuTemperatureUpdates()
    }
    fun startCpuTemperatureUpdates() {
        handler.post(updateCpuTemperatureRunnable)
    }
    fun stopCpuTemperatureUpdates() {
        handler.removeCallbacks(updateCpuTemperatureRunnable)
    }
    fun getCpuTemperature(): Double {
        return cpuTemperatureLiveData.value ?: 0.0
    }
    private val updateCpuTemperatureRunnable = object : Runnable {
        override fun run() {
            val temperature = calculateCpuTemperature()
            cpuTemperatureLiveData.postValue(temperature)
            handler.postDelayed(this, updateInterval)
        }
    }
    private fun calculateCpuTemperature(): Double {
        var temperature = 0.0
        try {
            val process = Runtime.getRuntime().exec("cat /sys/class/thermal/thermal_zone0/temp")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val tempStr = reader.readLine()
            reader.close()
            process.waitFor()

            if (!tempStr.isNullOrEmpty()) {
                val tempMilliCelsius = tempStr.toIntOrNull()
                if (tempMilliCelsius != null) {
                    temperature = tempMilliCelsius / 1000.0
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return temperature
    }
}