package com.d4rk.cleaner.core.di.modules

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.di.StandardDispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

val dispatchersModule : Module = module {
    single<DispatcherProvider> { StandardDispatchers() }
}