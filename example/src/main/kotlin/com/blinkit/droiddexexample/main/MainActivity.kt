package com.blinkit.droiddexexample.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blinkit.droiddex.DroidDex
import com.blinkit.droiddex.constants.PerformanceClass
import com.blinkit.droiddex.constants.PerformanceClass.Companion.name
import com.blinkit.droiddexexample.R
import com.blinkit.droiddexexample.databinding.ActivityMainBinding
import com.blinkit.droiddexexample.utils.dpToPx
import com.blinkit.droiddexexample.views.ItemView

class MainActivity: AppCompatActivity() {

	private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)

		binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
			binding.logo.elevation = (if (scrollY > 0.dpToPx()) 2 else 0).dpToPx()
		}

		binding.headingIndividual.setTextAppearance(R.style.TextAppearanceBold)
		binding.headingAggregate.setTextAppearance(R.style.TextAppearanceBold)
		binding.headingWeightedAggregate.setTextAppearance(R.style.TextAppearanceBold)

		setupClasses(binding.cpu, PerformanceClass.CPU)
		setupClasses(binding.memory, PerformanceClass.MEMORY)
		setupClasses(binding.network, PerformanceClass.NETWORK)
		setupClasses(binding.storage, PerformanceClass.STORAGE)
		setupClasses(binding.battery, PerformanceClass.BATTERY)

		setupClasses(binding.cpuAndMemory, PerformanceClass.CPU, PerformanceClass.MEMORY)
		setupClasses(binding.networkAndStorage, PerformanceClass.NETWORK, PerformanceClass.STORAGE)
		setupClasses(
			binding.cpuMemoryAndNetwork, PerformanceClass.CPU, PerformanceClass.MEMORY, PerformanceClass.NETWORK
		)

		setupWeightedClasses(
			binding.memoryAndNetworkWeighted, PerformanceClass.MEMORY to 2F, PerformanceClass.NETWORK to 1F
		)
		setupWeightedClasses(
			binding.cpuAndBatteryWeighted, PerformanceClass.CPU to 2F, PerformanceClass.BATTERY to 3F
		)
	}

	private fun setupClasses(item: ItemView, @PerformanceClass vararg performanceClasses: Int) {
		DroidDex.getPerformanceLevelLd(*performanceClasses).observe(this) { level ->
			item.set(level, performanceClasses.joinToString(" + ") { getClassName(it) })
		}
	}

	private fun setupWeightedClasses(
		item: ItemView, vararg performanceClassesWithWeights: Pair<@PerformanceClass Int, Float>
	) {
		DroidDex.getWeightedPerformanceLevelLd(*performanceClassesWithWeights).observe(this) { level ->
			item.set(level, performanceClassesWithWeights.joinToString(" +\n") {
				getClassName(it.first) + " * " + it.second
			})
		}
	}

	private fun getClassName(@PerformanceClass performanceClass: Int) = when (performanceClass) {
		PerformanceClass.CPU -> performanceClass.name()
		else -> performanceClass.name().lowercase().replaceFirstChar { it.uppercase() }
	}
}
