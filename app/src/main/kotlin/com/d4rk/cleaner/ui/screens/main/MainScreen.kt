package com.d4rk.cleaner.ui.screens.main

import android.content.Context
import android.view.View
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiMainModel
import com.d4rk.cleaner.ui.components.navigation.NavigationDrawer

@Composable
fun MainScreen(viewModel : MainViewModel) {
    val drawerState : DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController : NavHostController = rememberNavController()
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val dataStore : DataStore = AppCoreManager.dataStore
    val uiState : UiMainModel by viewModel.uiState.collectAsState()

    NavigationDrawer(
        navHostController = navController ,
        drawerState = drawerState ,
        view = view ,
        dataStore = dataStore ,
        context = context ,
        uiState = uiState ,
    )
}