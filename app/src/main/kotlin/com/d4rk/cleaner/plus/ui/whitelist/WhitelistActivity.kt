package com.d4rk.cleaner.plus.ui.whitelist
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.d4rk.cleaner.plus.MainActivity
import com.d4rk.cleaner.plus.R
import com.d4rk.cleaner.plus.databinding.ActivityWhitelistBinding
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface
class WhitelistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWhitelistBinding
    private var whiteList: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhitelistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonAddToWhitelist.setOnClickListener { addToWhiteList() }
        getWhiteList(MainActivity.preferences)
        loadViews()
    }
    private fun loadViews() {
        binding.linearLayoutPaths.removeAllViews()
        val layout = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.setMargins(0, 20, 0, 20)
        if (whiteList.isEmpty()) {
            val textView = TextView(this)
            textView.text = getString(R.string.empty_whitelist)
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.textSize = 18f
            binding.linearLayoutPaths.addView(textView, layout)
        } else {
            for (path in whiteList) {
                val button = Button(this)
                button.text = path
                button.textSize = 18f
                button.isAllCaps = false
                button.setOnClickListener { removePath(path, button) }
                button.setPadding(50, 50, 50, 50)
                button.setBackgroundResource(R.drawable.whitelist_card)
                binding.linearLayoutPaths.addView(button, layout)
            }
        }
    }
    private fun removePath(path: String?, button: Button?) {
        val mDialog = MaterialDialog.Builder(this)
            .setTitle(getString(R.string.clean))
            .setMessage(path ?: "")
            .setAnimation(R.raw.whitelist)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.clean)) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                whiteList.remove(path)
                MainActivity.preferences?.edit()?.putStringSet(getString(R.string.key_whitelist), HashSet(whiteList))?.apply()
                binding.linearLayoutPaths.removeView(button)
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .build()
        mDialog.show()
    }
    private fun addToWhiteList() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        mGetContent.launch(intent)
    }
    private var mGetContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                val document = DocumentFile.fromTreeUri(this, uri)
                val path = getDirectoryPath(document)
                if (path != null && !whiteList.contains(path)) {
                    whiteList.add(path)
                    MainActivity.preferences?.edit()?.putStringSet(getString(R.string.key_whitelist), HashSet(whiteList))?.apply()
                    loadViews()
                }
            }
        }
    }
    private fun getDirectoryPath(document: DocumentFile?): String? {
        if (document == null || !document.isDirectory || !document.canRead()) {
            return null
        }
        val documentUri = document.uri
        val contentResolver = applicationContext.contentResolver
        val cursor = contentResolver.query(documentUri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val pathIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            var path = it.getString(pathIndex)
            if (path.startsWith("primary:")) {
                path = path.substringAfter("primary:")
            }
            return path
        }
        return null
    }
    companion object {
        private var whiteList: ArrayList<String> = ArrayList()
        fun getWhiteList(preferences: SharedPreferences?): List<String?> {
            if (whiteList.isEmpty()) {
                if (preferences != null) {
                    whiteList = (preferences.getStringSet("whitelist", emptySet())?.toList()?.toMutableList() ?: ArrayList()) as ArrayList<String>
                }
                whiteList.remove("[")
                whiteList.remove("]")
            }
            return whiteList
        }
    }
}