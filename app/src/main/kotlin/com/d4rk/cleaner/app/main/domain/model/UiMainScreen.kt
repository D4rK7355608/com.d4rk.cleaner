package com.d4rk.cleaner.app.main.domain.model

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.cleaner.app.main.ui.MainViewModel
import kotlinx.coroutines.CoroutineScope

data class UiMainScreen(
    val showSnackbar : Boolean = false , val snackbarMessage : String = "" , val showDialog : Boolean = false , val navigationDrawerItems : List<NavigationDrawerItem> = listOf() , val bottomBarItems : List<BottomNavigationScreen> = listOf()
)

@OptIn(ExperimentalMaterial3Api::class)
data class MainScreenState(
    val navController : NavHostController ,
    val isFabVisible : MutableState<Boolean> ,
    val isFabExtended : MutableState<Boolean> ,
    val snackbarHostState : SnackbarHostState ,
    val scrollBehavior : TopAppBarScrollBehavior ,
    val coroutineScope : CoroutineScope ,
    val mainViewModel : MainViewModel ,
    val uiState : UiMainScreen = UiMainScreen()
)