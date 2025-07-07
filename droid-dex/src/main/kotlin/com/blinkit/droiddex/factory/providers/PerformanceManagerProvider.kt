package com.blinkit.droiddex.factory.providers

import android.content.Context
import com.blinkit.droiddex.factory.base.PerformanceManager

internal interface PerformanceManagerProvider {

	fun create(applicationContext: Context): PerformanceManager
}
