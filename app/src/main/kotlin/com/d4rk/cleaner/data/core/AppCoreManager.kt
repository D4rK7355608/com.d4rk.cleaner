@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.data.core

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.d4rk.android.libs.apptoolkit.data.core.BaseCoreManager
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import com.d4rk.android.libs.apptoolkit.utils.error.ErrorHandler
import com.d4rk.cleaner.constants.ads.AdsConstants
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.utils.error.CrashlyticsErrorReporter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

class AppCoreManager : BaseCoreManager() {

    private var currentActivity : Activity? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance : AppCoreManager
            private set

        lateinit var dataStore : DataStore
            private set

        val adsCoreManager : AdsCoreManager by lazy {
            AdsCoreManager(context = instance)
        }

        val isAppLoaded : Boolean
            get() = BaseCoreManager.isAppLoaded
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        val crashlyticsReporter = CrashlyticsErrorReporter()
        ErrorHandler.init(reporter = crashlyticsReporter)

        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer = this)
    }

    override suspend fun onInitializeApp() = supervisorScope {
        val dataStoreInitialization : Deferred<Unit> = async { initializeDataStore() }
        val adsInitialization : Deferred<Unit> = async { initializeAds() }

        dataStoreInitialization.await()
        adsInitialization.await()
    }

    private fun initializeDataStore() {
        runCatching {
            dataStore = DataStore.getInstance(context = this@AppCoreManager)
        }.onFailure {
            ErrorHandler.handleInitializationFailure(
                message = "DataStore initialization failed" , exception = it as Exception , applicationContext = applicationContext
            )
        }
    }

    private fun initializeAds() {
        adsCoreManager.initializeAds(AdsConstants.APP_OPEN_UNIT_ID)
    }

    fun isAppLoaded() : Boolean {
        return isAppLoaded
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let { adsCoreManager.showAdIfAvailable(it) }
    }

    override fun onActivityCreated(activity : Activity , savedInstanceState : Bundle?) {}

    override fun onActivityStarted(activity : Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity : Activity) {}
    override fun onActivityPaused(activity : Activity) {}
    override fun onActivityStopped(activity : Activity) {}
    override fun onActivitySaveInstanceState(activity : Activity , outState : Bundle) {}
    override fun onActivityDestroyed(activity : Activity) {}
}