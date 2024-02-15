package com.d4rk.cleaner.ui.appmanager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.R
import com.d4rk.cleaner.adapters.AppPagerAdapter
import com.d4rk.cleaner.databinding.FragmentAppManagerBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
class AppManagerFragment : Fragment() {
    private lateinit var binding: FragmentAppManagerBinding
    private val scope = CoroutineScope(Dispatchers.Main)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAppManagerBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(requireActivity().getString(R.string.key_custom_animations), true)) {
                setAnimations()
            }
        }
        scope.launch {
            try {
                binding.viewPager.adapter = AppPagerAdapter(this@AppManagerFragment)
                TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                    tab.text = when (position) {
                        0 -> getString(R.string.installed_apps)
                        1 -> getString(R.string.system_apps)
                        2 -> getString(R.string.app_install_files)
                        else -> null
                    }
                }.attach()
            } catch (_: Exception) { }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
    }
    private fun setAnimations() {
        binding.root.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_entry))
    }
}