package com.d4rk.cleaner.ui.main.repository

import android.app.Application
import com.d4rk.cleaner.data.datastore.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class MainRepository(dataStore : DataStore , application : Application) :
    MainRepositoryImplementation(application) {

    suspend fun checkAndHandleStartup(onSuccess : (Boolean) -> Unit) {

        val dataStore : DataStore = DataStore.getInstance(application)
        val isFirstTime : Boolean = dataStore.startup.first()
        if (isFirstTime) {
            dataStore.saveStartup(isFirstTime = false)
        }
        withContext(Dispatchers.Main) {
            onSuccess(isFirstTime)
        }
    }
}