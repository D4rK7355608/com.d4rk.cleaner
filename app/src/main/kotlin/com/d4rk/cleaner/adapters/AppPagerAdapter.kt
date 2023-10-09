package com.d4rk.cleaner.adapters
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.d4rk.cleaner.ui.appmanager.apps.ApksFragment
import com.d4rk.cleaner.ui.appmanager.apps.InstalledAppsFragment
import com.d4rk.cleaner.ui.appmanager.apps.SystemAppsFragment
class AppPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InstalledAppsFragment()
            1 -> SystemAppsFragment()
            2 -> ApksFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}