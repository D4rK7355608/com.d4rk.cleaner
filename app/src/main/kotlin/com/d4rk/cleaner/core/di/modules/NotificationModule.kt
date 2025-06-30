package com.d4rk.cleaner.core.di.modules

import com.d4rk.cleaner.app.notifications.domain.usecases.ShouldShowCleanupNotificationUseCase
import com.d4rk.cleaner.app.notifications.notifications.CleanupNotifier
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val notificationModule: Module = module {
    single { CleanupNotifier(context = androidContext()) }
    single { ShouldShowCleanupNotificationUseCase(memoryRepository = get(), dataStore = get()) }
}
