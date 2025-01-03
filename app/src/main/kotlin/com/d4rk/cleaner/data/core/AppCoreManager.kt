@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.data.core

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.d4rk.cleaner.data.core.ads.AdsCoreManager
import com.d4rk.cleaner.data.core.datastore.DataStoreCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.utils.error.ErrorHandler.handleInitializationFailure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class AppCoreManager : MultiDexApplication() , Application.ActivityLifecycleCallbacks ,
    LifecycleObserver {

    private val dataStoreCoreManager by lazy {
        DataStoreCoreManager(context = this)
    }

    private val adsCoreManager by lazy {
        AdsCoreManager(context = this)
    }

    private var currentActivity : Activity? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer = this)
        CoroutineScope(context = Dispatchers.IO).launch {
            initializeApp()
        }
    }

    private suspend fun initializeApp() = supervisorScope  {
        val dataStore = async { initializeDataStore() }
        dataStore.await()

        adsCoreManager.initializeAds()

        initializeAds()
        finalizeInitialization()
    }


    private suspend fun initializeDataStore() {
        runCatching {
            dataStore = DataStore.getInstance(context = this@AppCoreManager)
            dataStoreCoreManager.initializeDataStore()
        }.onFailure {
            handleInitializationFailure(
                message = "DataStore initialization failed" ,
                exception = it as Exception ,
                applicationContext = applicationContext
            )
        }
    }

    private fun initializeAds() {
        runCatching {
            adsCoreManager.initializeAds()
        }.onFailure {
            handleInitializationFailure(
                message = "Ads initialization failed" ,
                exception = it as Exception ,
                applicationContext = applicationContext
            )
        }
    }

    private fun finalizeInitialization() {
        markAppAsLoaded()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let { adsCoreManager.showAdIfAvailable(activity = it) }
    }

    private fun markAppAsLoaded() {
        isAppLoaded = true
    }

    fun isAppLoaded() : Boolean {
        return isAppLoaded
    }

    override fun onActivityCreated(activity : Activity , savedInstanceState : Bundle?) {}

    override fun onActivityStarted(activity : Activity) {
        if (! adsCoreManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity : Activity) {}
    override fun onActivityPaused(activity : Activity) {}
    override fun onActivityStopped(activity : Activity) {}
    override fun onActivitySaveInstanceState(activity : Activity , outState : Bundle) {}
    override fun onActivityDestroyed(activity : Activity) {}

    companion object {

        lateinit var dataStore : DataStore
            private set

        @SuppressLint("StaticFieldLeak")
        lateinit var instance : AppCoreManager
            private set

        var isAppLoaded : Boolean = false
            private set
    }
}