package com.d4rk.cleaner.ui.appmanager

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppManagerViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppManagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppManagerViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}