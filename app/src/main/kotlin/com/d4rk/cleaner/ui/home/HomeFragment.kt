package com.d4rk.cleaner.ui.home
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.FileScanner
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.FragmentHomeBinding
import com.d4rk.cleaner.ui.viewmodel.ViewModel
import com.d4rk.cleaner.ui.whitelist.WhitelistActivity.Companion.getWhiteList
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.io.File
import java.text.DecimalFormat
class HomeFragment : Fragment() {
    private lateinit var viewModel: ViewModel
    private lateinit var binding: FragmentHomeBinding
    private var currentPreferenceButtonPositions: Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(getString(R.string.key_custom_animations), true)) {
            setAnimations()
        }
        MobileAds.initialize(requireContext())
        binding.adView.loadAd(AdRequest.Builder().build())
        getWhiteList(preferences)
        FastScrollerBuilder(binding.scrollViewFiles).useMd2Style().build()
        binding.buttonClean.setOnClickListener {
            clean()
        }
        binding.buttonAnalyze.setOnClickListener {
            analyze()
        }
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (preferences != null) {
            swapButtonsPositions(preferences!!.getBoolean(getString(R.string.key_swap_buttons), false))
        }
    }
    private fun setAnimations() {
        binding.frameLayoutMain.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_swipe_up_center_400))
        binding.textViewStatus.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_fade_in_long))
        binding.buttonClean.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_swipe_up_center_500))
        binding.buttonAnalyze.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_swipe_up_center_600))
    }
    private fun swapButtonsPositions(preferenceButtonPositions: Boolean) {
        if (currentPreferenceButtonPositions == preferenceButtonPositions) return
        currentPreferenceButtonPositions = preferenceButtonPositions
        val parentLayout = binding.gridLayoutButtons
        val children = parentLayout.children.toList()
        parentLayout.removeAllViews()
        for (child in if (preferenceButtonPositions) children.asReversed() else children) {
            parentLayout.addView(child)
        }
    }
    private fun analyze() {
        requestStoragePermissions()
        if (!FileScanner.isRunning) {
            Thread {
                scan(false)
            }.start()
        }
    }
    private fun arrangeViews(isDelete: Boolean) {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (isDelete) {
                binding.frameLayoutMain.visibility = View.VISIBLE
                binding.scrollViewFiles.visibility = View.GONE
            } else {
                binding.frameLayoutMain.visibility = View.GONE
                binding.scrollViewFiles.visibility = View.VISIBLE
            }
        }
    }
    private fun clean() {
        requestStoragePermissions()
        if (!FileScanner.isRunning) {
            val oneClickCleanEnabled = preferences!!.getBoolean(getString(R.string.key_one_click_clean), false)
            if (oneClickCleanEnabled) {
                Thread {
                    scan(true)
                }.start()
            } else {
                val mDialog = MaterialDialog.Builder(requireContext() as Activity)
                    .setTitle(getString(R.string.clean_confirm_title))
                    .setAnimation(R.raw.delete)
                    .setMessage(getString(R.string.summary_dialog_button_clean))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.clean)) { dialogInterface, _ ->
                        Thread {
                            scan(true)
                        }.start()
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(getString(android.R.string.cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()
                mDialog.animationView.scaleType = ImageView.ScaleType.FIT_CENTER
                mDialog.show()
            }
        }
    }
    private fun clearClipboard() {
        try {
            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                clipboardManager.clearPrimaryClip()
            } else {
                val clipData = ClipData.newPlainText("", "")
                clipboardManager.setPrimaryClip(clipData)
            }
        } catch (e: NullPointerException) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), R.string.snack_clipboard_clean_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun scan(delete: Boolean) {
        Looper.prepare()
        requireActivity().runOnUiThread {
            binding.buttonClean.isEnabled = !FileScanner.isRunning
            binding.buttonAnalyze.isEnabled = !FileScanner.isRunning
        }
        reset()
        if (preferences!!.getBoolean(getString(R.string.key_clipboard), false)) clearClipboard()
        requireActivity().runOnUiThread {
            arrangeViews(delete)
            binding.textViewStatus.text = getString(R.string.status_running)

        }
        val path = Environment.getExternalStorageDirectory()
        val fileScanner = FileScanner(path, requireContext(), this)
            .setEmptyDir(preferences!!.getBoolean(getString(R.string.key_filter_empty), false))
            .setAutoWhite(preferences!!.getBoolean(getString(R.string.key_auto_whitelist), true))
            .setInvalid(preferences!!.getBoolean(getString(R.string.key_invalid_media_cleaner), false))
            .setDelete(delete)
            .setCorpse(preferences!!.getBoolean(getString(R.string.key_filter_corpse), false))
            .setGUI(binding)
            .setContext(requireContext())
            .setUpFilters(
                preferences!!.getBoolean(getString(R.string.key_filter_generic), true),
                preferences!!.getBoolean(getString(R.string.key_filter_aggressive), false),
                preferences!!.getBoolean(getString(R.string.key_filter_apk), false),
                preferences!!.getBoolean(getString(R.string.key_filter_archive), false)
            )
        if (path.listFiles() == null) {
            val textView = printTextView(getString(R.string.snack_clipboard_clean_failed), Color.RED)
            requireActivity().runOnUiThread {
                binding.linearLayoutFiles.addView(textView)
            }
        }
        val kilobytesTotal = fileScanner.startScan()
        binding.progressBarScan.progress = 0
        requireActivity().runOnUiThread {
            if (delete) {
                binding.textViewStatus.text = getString(R.string.freed) + " " + convertSize(kilobytesTotal)
            } else {
                binding.textViewStatus.text = getString(R.string.found) + " " + convertSize(kilobytesTotal)
            }
            binding.progressBarScan.progress = binding.progressBarScan.max
        }
        binding.scrollViewFiles.post { binding.scrollViewFiles.fullScroll(ScrollView.FOCUS_DOWN) }
        requireActivity().runOnUiThread {
            binding.buttonClean.isEnabled = !FileScanner.isRunning
            binding.buttonAnalyze.isEnabled = !FileScanner.isRunning
        }
        Looper.loop()
    }
    private fun printTextView(text: String, color: Int): TextView {
        val textView = TextView(requireContext())
        textView.setTextColor(color)
        textView.text = text
        textView.setPadding(3, 3, 3, 3)
        return textView
    }
    fun displayDeletion(file: File): TextView {
        val textView = printTextView(file.absolutePath, resources.getColor(R.color.colorPrimary, requireContext().theme))
        requireActivity().runOnUiThread {
            binding.linearLayoutFiles.addView(textView)
        }
        binding.scrollViewFiles.post {
            binding.scrollViewFiles.fullScroll(ScrollView.FOCUS_DOWN)
        }
        return textView
    }
    fun displayText(text: String) {
        val textColor = resources.getColor(R.color.colorSecondary, requireContext().theme)
        val textView = printTextView(text, textColor)
        requireActivity().runOnUiThread {
            binding.linearLayoutFiles.addView(textView)
        }
        binding.scrollViewFiles.post {
            binding.scrollViewFiles.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun reset() {
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        requireActivity().runOnUiThread {
            binding.linearLayoutFiles.removeAllViews()
            binding.progressBarScan.progress = 0
            binding.progressBarScan.max = 1
        }
    }
    @Suppress("DEPRECATION")
    private val isAccessGranted: Boolean
        get() = try {
            val packageManager = requireContext().packageManager
            val applicationInfo = packageManager.getApplicationInfo(requireContext().packageName, 0)
            val appOpsManager = requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode: Int = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    private fun requestStoragePermissions() {
        val requiredPermissions = mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            if (!isAccessGranted) {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO), 1)
        }
        ActivityCompat.requestPermissions(requireActivity(), requiredPermissions.toTypedArray(), 1)
    }
    companion object {
        @JvmField
        var preferences: SharedPreferences? = null
        @JvmStatic
        fun convertSize(length: Long): String {
            val format = DecimalFormat("#.##")
            val mib = (1024 * 1024).toLong()
            val kib: Long = 1024
            return when {
                length > mib -> "${format.format(length / mib)} MB"
                length > kib -> "${format.format(length / kib)} KB"
                else -> "${format.format(length)} B"
            }
        }
    }
}