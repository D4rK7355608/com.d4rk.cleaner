package com.d4rk.cleaner.app.backup.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File

class BackupActivity : AppCompatActivity() {
    private val dataStore: DataStore by inject()
    private val files: ArrayList<String> by lazy {
        intent.getStringArrayListExtra(EXTRA_FILES) ?: arrayListOf()
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                dataStore.saveBackupUri(uri.toString())
                performBackup(uri)
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            val savedUri = dataStore.backupUri.first()
            if (savedUri == null) {
                launchPicker()
            } else {
                performBackup(Uri.parse(savedUri))
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun launchPicker() {
        launcher.launch(null)
    }

    private fun performBackup(uri: Uri) {
        val tree = DocumentFile.fromTreeUri(this, uri) ?: return
        val resolver = contentResolver
        files.forEach { path ->
            val file = File(path)
            if (file.exists()) {
                val mime = resolver.getType(Uri.fromFile(file)) ?: "application/octet-stream"
                val target = tree.createFile(mime, file.name)
                if (target != null) {
                    resolver.openOutputStream(target.uri)?.use { out ->
                        file.inputStream().use { input ->
                            input.copyTo(out)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_FILES = "extra_files"
    }
}
