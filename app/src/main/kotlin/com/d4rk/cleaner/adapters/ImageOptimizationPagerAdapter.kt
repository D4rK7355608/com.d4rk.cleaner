package com.d4rk.cleaner.adapters
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.d4rk.cleaner.ui.imageoptimizer.FileSizeFragment
import com.d4rk.cleaner.ui.imageoptimizer.ManualModeFragment
import com.d4rk.cleaner.ui.imageoptimizer.QuickCompressFragment
class ImageOptimizationPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> QuickCompressFragment()
            1 -> FileSizeFragment()
            2 -> ManualModeFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}