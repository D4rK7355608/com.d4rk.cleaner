package com.d4rk.cleaner.ui.imageoptimizer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.FragmentQuickCompressBinding
import com.d4rk.cleaner.ui.viewmodel.ImageOptimizerViewModel
class QuickCompressFragment : Fragment() {
    private lateinit var binding: FragmentQuickCompressBinding
    private lateinit var viewModel: ImageOptimizerViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQuickCompressBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ImageOptimizerViewModel::class.java]
        setCompressionLevel(50)
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val compressionLevel = when (checkedId) {
                    R.id.button_low -> 30
                    R.id.button_medium -> 50
                    R.id.button_high -> 70
                    else -> {
                        binding.toggleGroup.clearChecked()
                        return@addOnButtonCheckedListener
                    }
                }
                setCompressionLevel(compressionLevel)
            }
        }
        binding.sliderQuickCompress.addOnChangeListener { slider, _, _ ->
            val percentage = slider.value.toInt()
            updateUI(percentage)
            viewModel.setCompressionLevel(percentage)
        }

        return binding.root
    }
    private fun setCompressionLevel(level: Int) {
        binding.sliderQuickCompress.value = level.toFloat()
        updateUI(level)
    }
    private fun updateUI(percentage: Int) {
        updatePercentageText(percentage)
        setButtonFromSliderValue(percentage)
    }
    private fun updatePercentageText(percentage: Int) {
        binding.textViewQuickCompressPercentage.text = getString(R.string.image_compressor_percentage_format, percentage)
    }
    private fun setButtonFromSliderValue(percentage: Int) {
        val buttonId = when (percentage) {
            30 -> R.id.button_low
            50 -> R.id.button_medium
            70 -> R.id.button_high
            else -> {
                binding.toggleGroup.clearChecked()
                return
            }
        }
        binding.toggleGroup.check(buttonId)
    }
    fun getCurrentCompressionLevel(): Int {
        return binding.sliderQuickCompress.value.toInt()
    }
}