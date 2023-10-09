package com.d4rk.cleaner.ui.appmanager.apps
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.d4rk.cleaner.adapters.AppListAdapter
import com.d4rk.cleaner.databinding.FragmentInstalledAppsBinding
class SystemAppsFragment : Fragment() {
    private var _binding: FragmentInstalledAppsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInstalledAppsBinding.inflate(inflater, container, false)
        val rootView = binding.root
        val packageManager = requireContext().packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA).filter { it.flags and ApplicationInfo.FLAG_SYSTEM != 0 }
        binding.appList.adapter = AppListAdapter(apps)
        binding.appList.layoutManager = LinearLayoutManager(requireContext())
        return rootView
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}