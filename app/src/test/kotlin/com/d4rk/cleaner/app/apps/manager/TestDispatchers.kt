package com.d4rk.cleaner.app.apps.manager

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

class TestDispatchers(val mTestDispatcher: TestDispatcher = StandardTestDispatcher()) : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = mTestDispatcher
    override val io: CoroutineDispatcher
        get() = mTestDispatcher
    override val default: CoroutineDispatcher
        get() = mTestDispatcher
    override val unconfined: CoroutineDispatcher
        get() = mTestDispatcher
}
