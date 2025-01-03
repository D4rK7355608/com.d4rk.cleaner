package com.d4rk.cleaner.data.core.datastore

import android.content.Context
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.constants.ui.bottombar.BottomBarRoutes
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull

open class DataStoreCoreManager(protected val context : Context) {

    var dataStore : DataStore = AppCoreManager.dataStore

    suspend fun initializeDataStore() = coroutineScope {

        listOf(async {
            dataStore.getStartupPage().firstOrNull() ?: BottomBarRoutes.HOME
        } , async {
            dataStore.getShowBottomBarLabels().firstOrNull() ?: true
        } , async {
            dataStore.getLanguage().firstOrNull() ?: "en"
        } , async {
            dataStore.lastUsed.firstOrNull() ?: 0L
        } , async {
            dataStore.startup.firstOrNull() ?: true
        } , async {
            dataStore.themeMode.firstOrNull() ?: "follow_system"
        } , async {
            dataStore.amoledMode.firstOrNull() ?: false
        } , async {
            dataStore.dynamicColors.firstOrNull() ?: false
        } , async {
            dataStore.bouncyButtons.firstOrNull() ?: false
        } , async {
            dataStore.ads.firstOrNull() ?: (BuildConfig.DEBUG)
        } , async {
            dataStore.usageAndDiagnostics.firstOrNull() ?: ! BuildConfig.DEBUG
        }).awaitAll()
    }
}