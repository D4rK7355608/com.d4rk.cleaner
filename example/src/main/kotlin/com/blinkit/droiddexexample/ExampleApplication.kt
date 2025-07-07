package com.blinkit.droiddexexample

import android.app.Application
import com.blinkit.droiddex.DroidDex
import timber.log.Timber

class ExampleApplication: Application() {

	override fun onCreate() {
		super.onCreate()

		Timber.plant(Timber.DebugTree())

		DroidDex.init(this)
	}
}
