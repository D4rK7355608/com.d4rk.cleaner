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
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.FileScanner
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.FragmentHomeBinding
import com.d4rk.cleaner.ui.whitelist.WhitelistActivity.Companion.getWhiteList
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.io.File
import java.text.DecimalFormat
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var currentPreferenceButtonPositions: Boolean = false
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext())
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FastScrollerBuilder(binding.scrollViewFiles).useMd2Style().build()
        binding.adView.loadAd(AdRequest.Builder().build())
        getWhiteList(preferences)
        binding.buttonClean.setOnClickListener {
            reset()
            clean()
        }
        binding.buttonAnalyze.setOnClickListener {
            reset()
            analyze()
        }
        if (isAdded) {
            if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(requireActivity().getString(R.string.key_custom_animations), true)) {
                setAnimations()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (preferences != null) {
            swapButtonsPositions(preferences!!.getBoolean(getString(R.string.key_swap_buttons), false))
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
    private fun resetScan() {
        binding.progressBarScan.progress = 0
        binding.textViewPercentage.text = getString(R.string.main_progress_0)
    }
    private fun setAnimations() {
        binding.root.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_entry))
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
            scope.launch {
                scan(false)
            }
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
                scope.launch {
                    scan(true)
                }
            } else {
                val mDialog = MaterialDialog.Builder(requireContext() as Activity)
                    .setTitle(getString(R.string.clean_confirm_title))
                    .setAnimation(R.raw.delete)
                    .setMessage(getString(R.string.summary_dialog_button_clean))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.clean)) { dialogInterface, _ ->
                        scope.launch {
                            scan(true)
                        }
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
                val snackbar = Snackbar.make(binding.root, R.string.snack_clipboard_clean_failed, Snackbar.LENGTH_SHORT)
                snackbar.setAction(android.R.string.ok) {
                    snackbar.dismiss()
                }
                snackbar.show()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun scan(delete: Boolean) = CoroutineScope(Dispatchers.Main).launch {
        reset()
        if (preferences!!.getBoolean(getString(R.string.key_clipboard), false)) clearClipboard()
        arrangeViews(delete)
        binding.textViewStatus.text = getString(R.string.status_running)
        val path = Environment.getExternalStorageDirectory()
        val fileScanner = FileScanner(path, requireContext(), this@HomeFragment)
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
            binding.linearLayoutFiles.addView(textView)
        }
        val kilobytesTotal = withContext(Dispatchers.IO) { fileScanner.startScan() }
        resetScan()
        if (delete) {
            binding.textViewStatus.text = getString(R.string.freed) + " " + convertSize(kilobytesTotal)
        } else {
            binding.textViewStatus.text = getString(R.string.found) + " " + convertSize(kilobytesTotal)
        }
        binding.progressBarScan.progress = binding.progressBarScan.max
        binding.scrollViewFiles.post { binding.scrollViewFiles.fullScroll(ScrollView.FOCUS_DOWN) }
    }
    private fun printTextView(text: String, color: Int): TextView {
        val textView = TextView(requireContext())
        textView.setTextColor(color)
        textView.text = text
        textView.setPadding(3, 3, 3, 3)
        return textView
    }
    private fun printTextViewWithIcon(text: String, iconResId: Int, textColor: Int): TextView {
        val spannableStringBuilder = SpannableStringBuilder()
        val icon = context?.let { ContextCompat.getDrawable(it, iconResId) }
        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        spannableStringBuilder.append(" ")
        spannableStringBuilder.setSpan(
            icon?.let { ImageSpan(it, ImageSpan.ALIGN_BASELINE) },
            0,
            1,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.append(" $text")
        val textView = TextView(context)
        textView.setTextColor(textColor)
        textView.text = spannableStringBuilder
        textView.setPadding(3, 3, 3, 3)
        return textView
    }
    fun displayDeletion(file: File): TextView {
        getFormattedPath(file)
        val fileName = file.name
        val isFolder = file.isDirectory
        val iconResId = if (isFolder) R.drawable.ic_folder else R.drawable.ic_file_present
        val textView = printTextViewWithIcon(
            fileName,
            iconResId,
            resources.getColor(R.color.colorPrimary, requireContext().theme)
        )
        requireActivity().runOnUiThread {
            binding.linearLayoutFiles.addView(textView)
        }
        binding.scrollViewFiles.post {
            binding.scrollViewFiles.fullScroll(ScrollView.FOCUS_DOWN)
        }
        return textView
    }
    private fun getFormattedPath(file: File): String {
        val basePath = Environment.getExternalStorageDirectory().absolutePath
        val relativePath = file.absolutePath.removePrefix(basePath)
        val segments = relativePath.split("/").drop(1)
        val formattedPath = StringBuilder()
        for (segment in segments) {
            formattedPath.append("├── ").append(segment).append("\n")
        }
        return formattedPath.toString()
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
            resetScan()
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
            @Suppress("DEPRECATION")
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