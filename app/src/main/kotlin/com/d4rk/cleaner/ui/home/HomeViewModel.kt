package com.d4rk.cleaner.ui.home

import android.annotation.SuppressLint
import android.app.Application
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.FileScanner
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.store.DataStore
import com.d4rk.cleaner.ui.home.HomeFragment.Companion.convertSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = getApplication<Application>().applicationContext
    private val _progress = MutableLiveData(0f)
    val progress: LiveData<Float> = _progress

    private val _statusText = MutableLiveData("")
    val statusText: LiveData<String> = _statusText

    private val dataStore = DataStore(appContext)

    private fun reset() {
        _progress.value = 0f
        _statusText.value = appContext.getString(R.string.main_progress_0)
    }

    fun analyze() {
        if (!FileScanner.isRunning) {
            viewModelScope.launch {
                scan(false)
            }
        }
    }

    fun clean() {
        if (!FileScanner.isRunning) {
            viewModelScope.launch {
                scan(true)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun scan(delete: Boolean) = viewModelScope.launch {
        reset()
        _statusText.value = appContext.getString(R.string.status_running)
        val path = Environment.getExternalStorageDirectory()
        val fileScanner = FileScanner(path, appContext, appContext as Fragment)
                .setEmptyDir(dataStore.genericFilter.first())
                .setAutoWhite(dataStore.autoWhitelist.first())
                .setInvalid(dataStore.deleteInvalidMedia.first())
                .setDelete(delete)
                .setCorpse(dataStore.deleteCorpseFiles.first())
                .setContext(appContext)
                .setUpFilters(
                    dataStore.genericFilter.first(),
                    dataStore.aggressiveFilter.first(),
                    dataStore.deleteApkFiles.first(),
                    dataStore.deleteArchives.first()
                )

        if (path.listFiles() == null) {
            // Handle this in your Composable
        }
        val kilobytesTotal = withContext(Dispatchers.IO) { fileScanner.startScan() }
        if (delete) {
            _statusText.value = appContext.getString(R.string.freed) + " " + convertSize(kilobytesTotal)
        } else {
            _statusText.value = appContext.getString(R.string.found) + " " + convertSize(kilobytesTotal)
        }
        _progress.value = 1f
        // Handle this in your Composable
    }

    companion object {
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
