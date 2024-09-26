package com.d4rk.cleaner.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.ui.main.repository.MainRepository
import com.d4rk.cleaner.ui.startup.StartupActivity
import com.d4rk.cleaner.utils.IntentUtils
import com.d4rk.cleaner.utils.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val repository = MainRepository(DataStore(application), application)

    init {
        checkAndHandleStartup()
        configureSettings()
    }

    private fun checkAndHandleStartup() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.checkAndHandleStartup { isFirstTime ->
                if (isFirstTime) {
                    IntentUtils.openActivity(getApplication(), StartupActivity::class.java)
                }
            }
        }
    }

    private fun configureSettings() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.setupSettings()
        }
    }
}