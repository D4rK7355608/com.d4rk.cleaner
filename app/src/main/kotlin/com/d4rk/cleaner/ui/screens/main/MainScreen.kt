package com.d4rk.cleaner.ui.screens.main

import android.content.Context
import android.view.View
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.ui.components.navigation.NavigationDrawer

@Composable
fun MainComposable() {
    val drawerState : DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController : NavHostController = rememberNavController()
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val dataStore : DataStore = DataStore.getInstance(context)

    NavigationDrawer(
        navHostController = navController ,
        drawerState = drawerState ,
        view = view ,
        dataStore = dataStore ,
        context = context
    )
}