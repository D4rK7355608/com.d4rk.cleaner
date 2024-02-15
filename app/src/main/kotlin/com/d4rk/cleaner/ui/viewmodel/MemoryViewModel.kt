package com.d4rk.cleaner.ui.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
class MemoryViewModel : ViewModel() {
    private val cpuTemperatureLiveData = MutableLiveData<Double?>()
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val updateInterval = 1000L
    init {
        startCpuTemperatureUpdates()
    }
    fun startCpuTemperatureUpdates() {
        viewModelScope.launch {
            while (isActive) {
                val temperature = calculateCpuTemperature()
                cpuTemperatureLiveData.postValue(temperature)
                delay(updateInterval)
            }
        }
    }
    fun stopCpuTemperatureUpdates() {
        viewModelJob.cancelChildren()
    }
    fun getCpuTemperature(): Double {
        return cpuTemperatureLiveData.value ?: 0.0
    }
    private suspend fun calculateCpuTemperature(): Double? = withContext(Dispatchers.IO) {
        var temperature: Double? = null
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
        return@withContext temperature
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}