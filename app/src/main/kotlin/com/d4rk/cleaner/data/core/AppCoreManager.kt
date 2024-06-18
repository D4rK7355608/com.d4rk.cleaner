@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.data.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.d4rk.cleaner.constants.ads.AdsConstants
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.notifications.managers.AppUsageNotificationsManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Date

@Suppress("SameParameterValue")
class AppCoreManager : MultiDexApplication(), Application.ActivityLifecycleCallbacks,
    LifecycleObserver {
    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null
    private lateinit var dataStore: DataStore
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        dataStore = DataStore.getInstance(this@AppCoreManager)
        appOpenAdManager = AppOpenAdManager()
        val notificationsManager = AppUsageNotificationsManager(this)
        notificationsManager.scheduleAppUsageCheck()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let { appOpenAdManager.showAdIfAvailable(it) }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    interface OnShowAdCompleteListener {
        @Suppress("EmptyMethod")
        fun onShowAdComplete()
    }

    private inner class AppOpenAdManager {
        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false
        private var loadTime: Long = 0

        fun loadAd(context: Context) {
            if (isLoadingAd || isAdAvailable()) {
                return
            }
            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(context,
                AdsConstants.APP_OPEN_UNIT_ID,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                    }
                })
        }

        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        @Suppress("BooleanMethodIsAlwaysInverted")
        private fun isAdAvailable(): Boolean {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        fun showAdIfAvailable(activity: Activity) {
            showAdIfAvailable(activity, object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                }
            })
        }

        fun showAdIfAvailable(
            activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener
        ) {
            val isAdsChecked = runBlocking { dataStore.ads.first() }
            if (isShowingAd || !isAdsChecked) {
                return
            }
            if (!isAdAvailable()) {
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }
            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                }
            }
            isShowingAd = true
            appOpenAd!!.show(activity)
        }
    }
}