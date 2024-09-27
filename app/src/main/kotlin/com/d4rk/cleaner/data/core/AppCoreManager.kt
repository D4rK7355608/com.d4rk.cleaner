@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.data.core

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppCoreManager : MultiDexApplication(), Application.ActivityLifecycleCallbacks,
    LifecycleObserver {

    private val dataStoreCoreManager: DataStoreCoreManager =
        DataStoreCoreManager(context = this)
    private val adsCoreManager: AdsCoreManager =
        AdsCoreManager(context = this)

    private enum class AppInitializationStage {
        DATA_STORE,
        ADS
    }

    private var currentStage = AppInitializationStage.DATA_STORE
    private var isAppLoaded = false

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer = this)

        CoroutineScope(Dispatchers.Main).launch {
            if (dataStoreCoreManager.initializeDataStore()) {
                proceedToNextStage()
            }
        }
    }

    private fun proceedToNextStage() {
        when (currentStage) {
            AppInitializationStage.DATA_STORE -> {
                currentStage = AppInitializationStage.ADS
                adsCoreManager.setDataStore(dataStoreCoreManager.dataStore)
                adsCoreManager.initializeAds()
                markAppAsLoaded()
            }

            else -> {
                // All stages completed
            }
        }
    }

    fun isAppLoaded(): Boolean {
        return isAppLoaded
    }

    private fun markAppAsLoaded() {
        isAppLoaded = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let { adsCoreManager.showAdIfAvailable(it) }
    }

    private var currentActivity: Activity? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (!adsCoreManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}