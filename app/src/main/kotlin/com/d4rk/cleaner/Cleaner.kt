@file:Suppress("DEPRECATION")

package com.d4rk.cleaner

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import com.d4rk.android.libs.apptoolkit.data.core.BaseCoreManager
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import com.d4rk.cleaner.app.notifications.work.CleanupReminderScheduler
import com.d4rk.cleaner.app.notifications.work.StreakReminderScheduler
import com.d4rk.cleaner.core.di.initializeKoin
import com.d4rk.cleaner.core.utils.constants.ads.AdsConstants
import com.d4rk.cleaner.core.utils.helpers.StreakTracker
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import org.koin.android.ext.android.getKoin

class Cleaner : BaseCoreManager(), SingletonImageLoader.Factory {
    private var currentActivity : Activity? = null

    private val adsCoreManager : AdsCoreManager by lazy { getKoin().get<AdsCoreManager>() }

    override fun onCreate() {
        initializeKoin(context = this)
        StreakTracker.initialize()
        SingletonImageLoader.setSafe { newImageLoader(this) }
        super.onCreate()
        CleanupReminderScheduler.schedule(this)
        StreakReminderScheduler.schedule(this)
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer = this)
    }

    override suspend fun onInitializeApp() : Unit = supervisorScope {
        listOf(async { initializeAds() }).awaitAll()
    }

    private fun initializeAds() {
        adsCoreManager.initializeAds(appOpenUnitId = AdsConstants.APP_OPEN_UNIT_ID)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let { adsCoreManager.showAdIfAvailable(it) }
    }

    override fun onActivityCreated(activity : Activity , savedInstanceState : Bundle?) {}

    override fun onActivityStarted(activity : Activity) {
        currentActivity = activity
    }

    override fun newImageLoader(context: android.content.Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder().maxSizePercent(context = context, percent = 0.24).build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(percent = 0.02)
                    .build()
            }
            .build()
    }
}