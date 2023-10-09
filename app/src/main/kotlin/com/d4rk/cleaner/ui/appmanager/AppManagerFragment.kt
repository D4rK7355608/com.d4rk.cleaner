package com.d4rk.cleaner.ui.appmanager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.adapters.AppPagerAdapter
import com.d4rk.cleaner.databinding.FragmentAppManagerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
class AppManagerFragment : Fragment() {
    private lateinit var binding: FragmentAppManagerBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAppManagerBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext())
        binding.adView.loadAd(AdRequest.Builder().build())
        binding.viewPager.adapter = AppPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.installed_apps)
                1 -> getString(R.string.system_apps)
                2 -> getString(R.string.app_install_files)
                else -> null
            }
        }.attach()
        return binding.root
    }
}