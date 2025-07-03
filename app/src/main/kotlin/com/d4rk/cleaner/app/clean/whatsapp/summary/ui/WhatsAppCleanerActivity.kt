package com.d4rk.cleaner.app.clean.whatsapp.summary.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.d4rk.android.libs.apptoolkit.app.theme.style.AppTheme
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.DetailsScreen
import com.d4rk.cleaner.app.clean.whatsapp.navigation.WhatsAppRoute
import com.d4rk.cleaner.app.clean.whatsapp.permission.ui.PermissionScreen
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper
import org.koin.compose.viewmodel.koinViewModel

class WhatsAppCleanerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WhatsappScreenContent(activity = this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WhatsappScreenContent(activity: Activity) {
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()) // TODO: map scroll behavior to the lists to ensure proper movement when scrolling in screen
    val navController = rememberNavController()
    val startDestination = if (PermissionsHelper.hasStoragePermissions(activity)) {
        WhatsAppRoute.Summary.route
    } else {
        WhatsAppRoute.Permission.route
    }

    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.image_optimizer), // TODO add new title
        onBackClicked = {
            activity.finish()
        },
        scrollBehavior = scrollBehavior,
    ) { paddingValues ->

        // (maybe not needed) TODO: move the navigation host to a separate file in the whatsapp module whatsapp.navigation
        // TODO: i want the permissions the cleaner screen and the details to be all activities. I think navigatrion can be removed
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(WhatsAppRoute.Permission.route) {
                PermissionScreen(onPermissionGranted = {
                    navController.navigate(WhatsAppRoute.Summary.route) {
                        popUpTo(WhatsAppRoute.Permission.route) { inclusive = true }
                    }
                })
            }
            composable(WhatsAppRoute.Summary.route) {
                WhatsAppCleanerScreen(navController = navController, paddingValues = PaddingValues())
            }
            composable(
                route = WhatsAppRoute.Details("").route,
                arguments = listOf(navArgument(WhatsAppRoute.Details.TYPE) { type = NavType.StringType })
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString(WhatsAppRoute.Details.TYPE) ?: ""
                DetailsScreenNav(type = type)
            }
        }
    }
}

// TODO: Check the HomeScreen.kt from the src/whatsappcleaner and add the missing items here and in the rest of the project
@Composable
private fun DetailsScreenNav(
    type: String,
    viewModel: WhatsAppCleanerViewModel = koinViewModel()
) {
    val state = viewModel.uiState.collectAsState().value
    val summary = state.data?.mediaSummary ?: com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.WhatsAppMediaSummary()
    val files = when (type) {
        "images" -> summary.images
        "videos" -> summary.videos
        "documents" -> summary.documents
        else -> emptyList()
    }
    DetailsScreen(title = type, files = files)
}
