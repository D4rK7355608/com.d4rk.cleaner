package com.d4rk.cleaner.data.core.datastore

import android.content.Context
import com.d4rk.cleaner.constants.ui.bottombar.BottomBarRoutes
import com.d4rk.cleaner.data.datastore.DataStore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull

open class DataStoreCoreManager(protected val context: Context) {

    private var isDataStoreLoaded = false
    lateinit var dataStore: DataStore

    suspend fun initializeDataStore(): Boolean = coroutineScope {
        dataStore = DataStore.getInstance(context.applicationContext)

        listOf(
            async { dataStore.getStartupPage().firstOrNull() ?: BottomBarRoutes.HOME },
            async { dataStore.getShowBottomBarLabels().firstOrNull() ?: true },
            async { dataStore.getLanguage().firstOrNull() ?: "en" },
        ).awaitAll()

        isDataStoreLoaded = true
        return@coroutineScope isDataStoreLoaded
    }
}