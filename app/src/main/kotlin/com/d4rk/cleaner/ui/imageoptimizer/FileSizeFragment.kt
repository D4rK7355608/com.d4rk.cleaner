package com.d4rk.cleaner.ui.imageoptimizer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.FragmentFileSizeBinding
class FileSizeFragment : Fragment() {
    private lateinit var binding: FragmentFileSizeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFileSizeBinding.inflate(inflater, container, false)
        val fileSizeArray = resources.getStringArray(R.array.file_sizes)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, fileSizeArray)
        binding.autoCompleteTextViewFileSize.setAdapter(adapter)
        return binding.root
    }
    fun getCurrentFileSizeKB(): Int {
        val selectedValue = binding.autoCompleteTextViewFileSize.text.toString()
        return try {
            selectedValue.toInt()
        } catch (e: NumberFormatException) {
            -1
        }
    }
}