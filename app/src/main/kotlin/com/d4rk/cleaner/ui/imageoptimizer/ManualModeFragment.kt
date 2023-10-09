package com.d4rk.cleaner.ui.imageoptimizer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.FragmentManualModeBinding
class ManualModeFragment : Fragment() {
    private lateinit var binding: FragmentManualModeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentManualModeBinding.inflate(inflater, container, false)
        binding.sliderManualMode.value = 50f
        binding.textViewManualModePercentage.text = getString(R.string.image_compressor_percentage_format, 50)
        binding.sliderManualMode.addOnChangeListener { slider, _, _ ->
            val compressionLevel = slider.value.toInt()
            updateUI(compressionLevel)
        }
        return binding.root
    }
    private fun updateUI(percentage: Int) {
        updatePercentageText(percentage)
    }
    private fun updatePercentageText(percentage: Int) {
        binding.textViewManualModePercentage.text = getString(
            R.string.image_compressor_percentage_format,
            percentage
        )
    }
    fun getCurrentCompressionSettings(): Triple<Int, Int, Int> {
        val width = binding.editTextWidth.text.toString().toIntOrNull() ?: 0
        val height = binding.editTextHeight.text.toString().toIntOrNull() ?: 0
        val quality = binding.sliderManualMode.value.toInt()
        return Triple(width, height, quality)
    }
}