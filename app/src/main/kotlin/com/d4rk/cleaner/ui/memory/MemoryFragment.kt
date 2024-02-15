package com.d4rk.cleaner.ui.memory
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.R
import com.d4rk.cleaner.adapters.CpuAdapter
import com.d4rk.cleaner.data.CpuApp
import com.d4rk.cleaner.databinding.FragmentMemoryBinding
import com.d4rk.cleaner.ui.viewmodel.MemoryViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class MemoryFragment : Fragment() {
    private lateinit var binding: FragmentMemoryBinding
    private lateinit var viewModel: MemoryViewModel
    private var updateMemoryJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L
    private lateinit var cpuAdapter: CpuAdapter
    private val cpuAppsList = mutableListOf<CpuApp>()
    private val navController: NavController by lazy {
        findNavController()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMemoryBinding.inflate(inflater, container, false)
        cpuAdapter = CpuAdapter(cpuAppsList)
        CoroutineScope(Dispatchers.Main).launch {
            updateMemoryInfo()
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        MobileAds.initialize(requireContext())
        binding.adBannerView.loadAd(AdRequest.Builder().build())
        binding.buttonAnalyze.setOnClickListener {
            navController.navigate(R.id.navigation_home)
        }
        if (isAdded) {
            if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(requireActivity().getString(R.string.key_custom_animations), true)) {
                setAnimations()
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[MemoryViewModel::class.java]
        viewModel.startCpuTemperatureUpdates()
    }
    override fun onDetach() {
        super.onDetach()
        viewModel.stopCpuTemperatureUpdates()
        handler.removeCallbacks(updateMemoryInfoRunnable)
        updateMemoryJob?.cancel()
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateMemoryInfoRunnable)
    }
    override fun onResume() {
        super.onResume()
        handler.post(updateMemoryInfoRunnable)
        updateCpuTemperature()
        updateRunningAppsList()
    }
    private suspend fun getStorageInfo(): Pair<Long, Long> = withContext(Dispatchers.IO) {
        val totalSize = getTotalInternalMemorySize()
        val availableSize = getAvailableInternalMemorySize()
        Pair(totalSize, availableSize)
    }
    private suspend fun getTotalInternalMemorySize(): Long = withContext(Dispatchers.IO) {
        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        blockSize * totalBlocks
    }
    private suspend fun getAvailableInternalMemorySize(): Long = withContext(Dispatchers.IO) {
        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        blockSize * availableBlocks
    }
    private val updateMemoryInfoRunnable = object : Runnable {
        override fun run() {
            if (isAdded) {
                updateMemoryInfo()
                handler.postDelayed(this, updateInterval)
            }
        }
    }
    private fun getUsedMemorySize(): Long {
        return try {
            val mi = ActivityManager.MemoryInfo()
            val mActivityManager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            mActivityManager.getMemoryInfo(mi)
            mi.availMem / 1048576L
        } catch (e: Exception) {
            200
        }
    }
    private fun getRamInfo(): Pair<Long, Long> {
        val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem
        val availableRam = memoryInfo.availMem
        return Pair(totalRam, availableRam)
    }
    private fun calculateRunningProcessesPercentage(): Int {
        val totalRam = getRamInfo().first
        val availableRam = getRamInfo().second
        val usedRam = totalRam - availableRam
        val percentage = ((usedRam.toDouble() / totalRam.toDouble()) * 100).toInt()
        return 100 - percentage
    }
    private fun updateMemoryInfo() = CoroutineScope(Dispatchers.Main).launch {
        val (totalSize, availableSize) = withContext(Dispatchers.IO) { getStorageInfo() }
        val usedSize = totalSize - availableSize
        val progress = (usedSize.toDouble() / totalSize.toDouble() * 100).toInt()
        binding.progressBarHorizontal.progress = progress
        val memoryUsageString = getString(R.string.memory_used, "%.2f".format(usedSize / (1024.0 * 1024.0 * 1024.0)), "%.2f".format(totalSize / (1024.0 * 1024.0 * 1024.0)), "GB")
        binding.textViewMemory.text = memoryUsageString
        val (totalRam, availableRam) = withContext(Dispatchers.IO) { getRamInfo() }
        val usedRam = totalRam - availableRam
        val ramUsagePercentage = (usedRam.toDouble() / totalRam.toDouble()) * 100
        binding.textViewRamPercentage.text = getString(R.string.ram_usage_percentage, ramUsagePercentage)
        val usedRamString = "%.2f GB/".format(usedRam / (1024.0 * 1024.0 * 1024.0))
        val totalRamString = "%.2f GB".format(totalRam / (1024.0 * 1024.0 * 1024.0))
        binding.textViewUsedRam.text = usedRamString
        binding.textViewTotalRam.text = totalRamString
        val y = withContext(Dispatchers.IO) { calculateRunningProcessesPercentage() }
        binding.textViewAppUsage.text = y.toString()
        binding.textViewAppsFreed.text = totalRamString
        val x = withContext(Dispatchers.IO) { getUsedMemorySize() }
        binding.textViewAppsUsed.text = getString(R.string.memory_used_mb, x - 30)
        if (progress >= 90) {
            binding.imageViewTextViewIcon.setImageResource(R.drawable.ic_disc_full)
        } else {
            binding.imageViewTextViewIcon.setImageResource(R.drawable.ic_phone_android)
        }
    }
    private fun updateRunningAppsList() {
        val safeToStopFlags = ApplicationInfo.FLAG_STOPPED or ApplicationInfo.FLAG_SYSTEM
        val packageManager = requireContext().packageManager
        val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val currentlyOpenedPackage = requireActivity().packageName
        val runningAppList = mutableListOf<CpuApp>()
        val runningApps = activityManager.runningAppProcesses.filter { it.pkgList[0] != currentlyOpenedPackage }
        for (appProcess in runningApps) {
            val packageName = appProcess.pkgList[0]
            val packageInfo = try {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            if (packageInfo != null && packageInfo.applicationInfo.flags and safeToStopFlags == 0) {
                continue
            }
            val appName = packageInfo?.let { packageManager.getApplicationLabel(it.applicationInfo).toString() } ?: ""
            val appIcon = packageInfo?.let { packageManager.getApplicationIcon(it.applicationInfo) }
            if (appName.isNotEmpty() && appIcon != null) {
                runningAppList.add(CpuApp(appName, appIcon))
            }
        }
        cpuAppsList.clear()
        cpuAppsList.addAll(runningAppList)
        cpuAdapter.updateAppsList(cpuAppsList)
    }
    private fun updateCpuTemperature() {
        if (isAdded) {
            val cpuTemperature = viewModel.getCpuTemperature()
            val cpuStatusText = when {
                cpuTemperature <= 0 -> getString(R.string.cpu_status_error)
                cpuTemperature >= 70 -> getString(R.string.cpu_status, getString(R.string.cpu_status_overheated), "%.2f°C".format(cpuTemperature))
                else -> getString(R.string.cpu_status, getString(R.string.cpu_status_normal), "%.2f°C".format(cpuTemperature))
            }
            binding.textViewCpu.text = cpuStatusText
            handler.postDelayed({
                updateCpuTemperature()
            }, updateInterval)
        }
    }
    private fun setAnimations() {
        binding.root.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_entry))
    }
}