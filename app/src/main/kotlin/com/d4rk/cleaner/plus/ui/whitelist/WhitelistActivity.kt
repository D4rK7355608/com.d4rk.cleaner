package com.d4rk.cleaner.plus.ui.whitelist
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree
import androidx.appcompat.app.AppCompatActivity
import com.d4rk.cleaner.plus.MainActivity
import com.d4rk.cleaner.plus.R
import com.d4rk.cleaner.plus.databinding.ActivityWhitelistBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dev.shreyaspatil.MaterialDialog.MaterialDialog
class WhitelistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWhitelistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhitelistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())
        binding.buttonAddToWhitelist.setOnClickListener { addToWhiteList() }
        getWhiteList(MainActivity.preferences)
        loadViews()
    }
    private fun loadViews() {
        binding.linearLayoutPaths.removeAllViews()
        if (whiteList.isEmpty()) {
            val textView = TextView(this)
            textView.text = getString(R.string.empty_whitelist)
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.textSize = 18f
            binding.linearLayoutPaths.addView(textView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                setMargins(0, 20, 0, 20)
            })
        } else {
            for (path in whiteList) {
                val button = Button(this).apply {
                    text = path
                    textSize = 18f
                    isAllCaps = false
                    setBackgroundResource(R.drawable.whitelist_card)
                    setOnClickListener { removePath(path, this) }
                    setPadding(50, 50, 50, 50)
                }
                binding.linearLayoutPaths.addView(button, LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(0, 20, 0, 20)
                })
            }
        }
    }
    private fun removePath(path: String?, button: Button?) {
        MaterialDialog.Builder(this)
            .setTitle(getString(R.string.remove))
            .setMessage(path!!)
            .setAnimation(R.raw.whitelist)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.clean)) { dialogInterface, _ ->
                whiteList.remove(path)
                MainActivity.preferences?.edit()?.putStringSet("whitelist", HashSet(whiteList))?.apply()
                binding.linearLayoutPaths.removeView(button)
                dialogInterface.dismiss()
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build()
            .show()
    }
    private fun addToWhiteList() {
        mGetContent.launch(Uri.fromFile(Environment.getDataDirectory()))
    }
    private var mGetContent = registerForActivityResult(OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            whiteList.add(uri.path!!.substring(uri.path!!.indexOf(":") + 1))
            MainActivity.preferences?.edit()!!.putStringSet("whitelist", HashSet(whiteList)).apply()
            loadViews()
        }
    }
    companion object {
        private val whiteList = mutableListOf<String>()
        fun getWhiteList(preferences: SharedPreferences?): List<String?> {
            if (whiteList.isEmpty()) {
                preferences?.getStringSet("whitelist", emptySet())?.let { whiteList.addAll(it) }
                whiteList.remove("[")
                whiteList.remove("]")
            }
            return whiteList
        }
    }
}