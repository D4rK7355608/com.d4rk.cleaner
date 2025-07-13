package com.d4rk.cleaner.app.apps.manager.ui

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.hapticPagerSwipe
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHandler
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.domain.actions.AppManagerAction
import com.d4rk.cleaner.app.apps.manager.domain.actions.AppManagerEvent
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ui.UiAppManagerModel
import com.d4rk.cleaner.app.apps.manager.ui.components.tabs.ApksTab
import com.d4rk.cleaner.app.apps.manager.ui.components.tabs.AppsTab
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppManagerScreen(snackbarHostState : SnackbarHostState , paddingValues : PaddingValues) {
    val viewModel : AppManagerViewModel = koinViewModel()
    val context : Context = LocalContext.current
    val uiState : UiStateScreen<UiAppManagerModel> by viewModel.uiState.collectAsState()

    LaunchedEffect(context) {
        if (! PermissionsHelper.hasUsageAccessPermissions(context)) {
            PermissionsHelper.requestUsageAccess(context as Activity)
        }
    }

    LaunchedEffect(viewModel.actionEvent) {
        viewModel.actionEvent.collectLatest { action ->
            when (action) {
                is AppManagerAction.LaunchShareIntent -> context.startActivity(action.intent)
            }
        }
    }

    uiState.data?.let {
        AppManagerScreenContent(viewModel = viewModel , screenData = it , paddingValues = paddingValues)
    }

    DefaultSnackbarHandler(screenState = uiState , snackbarHostState = snackbarHostState , getDismissEvent = { AppManagerEvent.DismissSnackbar } , onEvent = { viewModel.onEvent(event = it) })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppManagerScreenContent(viewModel : AppManagerViewModel , screenData : UiAppManagerModel , paddingValues : PaddingValues) {
    val tabs : List<String> = listOf(
        stringResource(id = R.string.installed_apps) ,
        stringResource(id = R.string.system_apps) ,
        stringResource(id = R.string.app_install_files) ,
    )

    val pagerState : PagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
            placeholder = { Text(text = stringResource(id = R.string.search)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SizeConstants.LargeSize, vertical = SizeConstants.SmallSize)
        )
        TabRow(
            selectedTabIndex = pagerState.currentPage ,
            indicator = { tabPositions ->
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier
                            .fillMaxWidth()
                            .tabIndicatorOffset(currentTabPosition = tabPositions[pagerState.currentPage]) ,
                    shape = RoundedCornerShape(
                        topStart = 3.dp ,
                        topEnd = 3.dp ,
                    ) ,
                )
            } ,
        ) {
            tabs.forEachIndexed { index , title ->
                Tab(modifier = Modifier
                        .bounceClick()
                        .clip(RoundedCornerShape(50)) , text = {
                    Text(
                        text = title , maxLines = 1 , overflow = TextOverflow.Ellipsis , color = MaterialTheme.colorScheme.onSurface
                    )
                } , selected = pagerState.currentPage == index , onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                })
            }
        }

        HorizontalPager(
            modifier = Modifier.hapticPagerSwipe(pagerState) ,
            state = pagerState ,
        ) { page ->
            when (page) {
                0 -> AppsTab(
                    apps = screenData.installedApps.filter { app: ApplicationInfo ->
                        app.flags and ApplicationInfo.FLAG_SYSTEM == 0 &&
                            context.packageManager.getApplicationLabel(app).toString().contains(searchQuery, ignoreCase = true)
                    },
                    isLoading = screenData.userAppsLoading,
                    usageStats = screenData.appUsageStats,
                    viewModel = viewModel,
                    paddingValues = paddingValues
                )

                1 -> AppsTab(
                    apps = screenData.installedApps.filter { app: ApplicationInfo ->
                        app.flags and ApplicationInfo.FLAG_SYSTEM != 0 &&
                            context.packageManager.getApplicationLabel(app).toString().contains(searchQuery, ignoreCase = true)
                    },
                    isLoading = screenData.systemAppsLoading,
                    usageStats = screenData.appUsageStats,
                    viewModel = viewModel,
                    paddingValues = paddingValues
                )

                2 -> ApksTab(
                    apkFiles = screenData.apkFiles.filter { apk ->
                        apk.path.substringAfterLast('/').contains(searchQuery, ignoreCase = true)
                    } ,
                    isLoading = screenData.apkFilesLoading ,
                    viewModel = viewModel ,
                    paddingValues = paddingValues
                )
            }
        }
    }
}