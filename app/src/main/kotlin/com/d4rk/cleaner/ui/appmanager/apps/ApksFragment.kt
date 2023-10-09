package com.d4rk.cleaner.ui.appmanager.apps
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.d4rk.cleaner.adapters.ApksListAdapter
import com.d4rk.cleaner.databinding.FragmentInstalledAppsBinding
import java.io.File
class ApksFragment : Fragment() {
    private lateinit var binding: FragmentInstalledAppsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInstalledAppsBinding.inflate(inflater, container, false)
        val rootView = binding.root
        val apkFiles: List<File> = findApkFiles(Environment.getExternalStorageDirectory())
        val apkListAdapter = ApksListAdapter(apkFiles)
        binding.appList.adapter = apkListAdapter
        binding.appList.layoutManager = LinearLayoutManager(requireContext())
        return rootView
    }
    private fun findApkFiles(directory: File): List<File> {
        val apkFiles = mutableListOf<File>()
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                apkFiles.addAll(findApkFiles(file))
            } else if (file.name.endsWith(".apk")) {
                apkFiles.add(file)
            }
        }
        return apkFiles
    }
}