package com.d4rk.cleaner.core.di

import android.content.Context
import com.d4rk.cleaner.core.di.modules.adsModule
import com.d4rk.cleaner.core.di.modules.appModule
import com.d4rk.cleaner.core.di.modules.appToolkitModule
import com.d4rk.cleaner.core.di.modules.dispatchersModule
import com.d4rk.cleaner.core.di.modules.notificationModule
import com.d4rk.cleaner.core.di.modules.settingsModule
import com.d4rk.cleaner.core.di.modules.whatsappCleanerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initializeKoin(context : Context) {
    startKoin {
        androidContext(androidContext = context)
        modules(modules = listOf(dispatchersModule , appModule , settingsModule , adsModule , appToolkitModule , notificationModule, whatsappCleanerModule))
    }
}