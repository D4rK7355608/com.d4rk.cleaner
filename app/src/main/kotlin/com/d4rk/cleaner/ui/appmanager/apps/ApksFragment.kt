package com.d4rk.cleaner.ui.appmanager.apps
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.d4rk.cleaner.adapters.ApksListAdapter
import com.d4rk.cleaner.databinding.FragmentInstalledAppsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
class ApksFragment : Fragment() {
    private lateinit var binding: FragmentInstalledAppsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInstalledAppsBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val apkFiles: List<File> = findApkFiles(Environment.getExternalStorageDirectory())
            val apkListAdapter = ApksListAdapter(apkFiles)
            if (apkFiles.isEmpty()) {
                binding.textViewNoItemToShow.visibility = View.VISIBLE
                binding.appList.visibility = View.GONE
            } else {
                binding.textViewNoItemToShow.visibility = View.GONE
                binding.appList.visibility = View.VISIBLE
                binding.appList.adapter = apkListAdapter
                binding.appList.layoutManager = LinearLayoutManager(requireContext())
            }
        }
        return binding.root
    }
    private suspend fun findApkFiles(directory: File): List<File> = withContext(Dispatchers.IO) {
        val apkFiles = mutableListOf<File>()
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                apkFiles.addAll(findApkFiles(file))
            } else if (file.name.endsWith(".apk")) {
                apkFiles.add(file)
            }
        }
        return@withContext apkFiles
    }
}