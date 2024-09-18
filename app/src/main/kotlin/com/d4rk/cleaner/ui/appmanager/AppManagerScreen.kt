package com.d4rk.cleaner.ui.appmanager

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import com.d4rk.cleaner.data.model.ui.error.UiErrorModel
import com.d4rk.cleaner.data.model.ui.screens.UiAppManagerModel
import com.d4rk.cleaner.ui.dialogs.ErrorAlertDialog
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.cleaning.toBitmapDrawable
import com.d4rk.cleaner.utils.compose.bounceClick
import com.d4rk.cleaner.utils.compose.hapticPagerSwipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * Composable function for managing and displaying different app categories.
 */
@Composable
fun AppManagerScreen() {
    val viewModel : AppManagerViewModel = viewModel()
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val tabs : List<String> = listOf(
        stringResource(id = R.string.installed_apps) ,
        stringResource(id = R.string.system_apps) ,
        stringResource(id = R.string.app_install_files) ,
    )
    val pagerState : PagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val isLoading : Boolean by viewModel.isLoading.collectAsState()
    val transition : Transition<Boolean> =
            updateTransition(targetState = ! isLoading , label = "LoadingTransition")
    val contentAlpha : Float by transition.animateFloat(label = "Content Alpha") {
        if (it) 1f else 0f
    }

    val uiState : UiAppManagerModel by viewModel.uiState.collectAsState()
    val uiErrorModel : UiErrorModel by viewModel.uiErrorModel.collectAsState()

    LaunchedEffect(context) {
        if (! PermissionsUtils.hasUsageAccessPermissions(context)) {
            PermissionsUtils.requestUsageAccess(context as Activity)
        }
    }

    if (uiErrorModel.showErrorDialog) {
        ErrorAlertDialog(errorMessage = uiErrorModel.errorMessage ,
                         onDismiss = { viewModel.dismissErrorDialog() })
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else {
        Column(
            modifier = Modifier.alpha(contentAlpha) ,
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage ,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]) ,
                        shape = RoundedCornerShape(
                            topStart = 3.dp ,
                            topEnd = 3.dp ,
                            bottomEnd = 0.dp ,
                            bottomStart = 0.dp ,
                        ) ,
                    )
                } ,
            ) {
                tabs.forEachIndexed { index , title ->
                    Tab(modifier = Modifier.bounceClick() , text = {
                        Text(
                            text = title ,
                            maxLines = 1 ,
                            overflow = TextOverflow.Ellipsis ,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } , selected = pagerState.currentPage == index , onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
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
                    0 -> AppsComposable(apps = uiState.installedApps.filter { app : ApplicationInfo ->
                        app.flags and ApplicationInfo.FLAG_SYSTEM == 0
                    } , isLoading , viewModel = viewModel)

                    1 -> AppsComposable(apps = uiState.installedApps.filter { app : ApplicationInfo ->
                        app.flags and ApplicationInfo.FLAG_SYSTEM != 0
                    } , isLoading , viewModel = viewModel)

                    2 -> ApksComposable(
                        apkFiles = uiState.apkFiles , isLoading , viewModel = viewModel
                    )
                }
            }
        }
    }
}

/**
 * Composable function for displaying a list of apps.
 *
 * @param apps List of ApplicationInfo objects representing the apps to display.
 */
@Composable
fun AppsComposable(
    apps : List<ApplicationInfo> , isLoading : Boolean , viewModel : AppManagerViewModel
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    else if (apps.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.no_app_installed))
        }
    }
    else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = apps , key = { app -> app.packageName }) { app ->
                AppItemComposable(app , viewModel = viewModel)
            }
        }
    }
}

/**
 * Composable function for displaying detailed information about a single app.
 *
 * @param app ApplicationInfo object representing the app to display.
 */
@Composable
fun AppItemComposable(
    app : ApplicationInfo , viewModel : AppManagerViewModel
) {
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val packageManager : PackageManager = context.packageManager
    val appName : String = app.loadLabel(packageManager).toString()
    val apkPath : String = app.publicSourceDir
    val apkFile = File(apkPath)
    val sizeInBytes : Long = apkFile.length()
    val sizeInKB : Long = sizeInBytes / 1024
    val sizeInMB : Long = sizeInKB / 1024
    val appSize : String = "%.2f MB".format(sizeInMB.toFloat())
    var showMenu : Boolean by remember { mutableStateOf(value = false) }
    val model : Drawable = app.loadIcon(packageManager)
    OutlinedCard(modifier = Modifier.padding(start = 8.dp , end = 8.dp , top = 8.dp)) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp)) ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
            ) {
                Text(
                    text = appName ,
                    style = MaterialTheme.typography.titleMedium ,
                )
                Text(
                    text = appSize , style = MaterialTheme.typography.bodyMedium
                )
            }

            Box {
                IconButton(modifier = Modifier.bounceClick() , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    showMenu = true
                }) {
                    Icon(Icons.Outlined.MoreVert , contentDescription = null)
                }

                DropdownMenu(expanded = showMenu , onDismissRequest = {
                    showMenu = false
                }) {
                    DropdownMenuItem(modifier = Modifier.bounceClick() , text = {
                        Text(stringResource(R.string.uninstall))
                    } , onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        viewModel.uninstallApp(app.packageName)
                    })
                    DropdownMenuItem(
                        modifier = Modifier.bounceClick() ,
                        text = { Text(stringResource(R.string.share)) } ,
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            viewModel.shareApp(app.packageName)
                        })
                    DropdownMenuItem(
                        modifier = Modifier.bounceClick() ,
                        text = { Text(stringResource(R.string.app_info)) } ,
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            viewModel.openAppInfo(app.packageName)
                        })
                }
            }
        }
    }
}

/**
 * Composable function for displaying a list of APK files on the device.
 */
@Composable
fun ApksComposable(
    apkFiles : List<ApkInfo> , isLoading : Boolean , viewModel : AppManagerViewModel
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    else if (apkFiles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.no_apk_found))
        }
    }
    else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = apkFiles , key = { apkInfo -> apkInfo.id }) { apkInfo ->
                ApkItemComposable(apkPath = apkInfo.path , viewModel = viewModel)
            }
        }
    }
}

/**
 * Composable function for displaying detailed information about an APK file.
 *
 * @param apkPath Path to the APK file.
 */
@Composable
fun ApkItemComposable(apkPath : String , viewModel : AppManagerViewModel) {
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val apkFile = File(apkPath)
    val sizeInBytes : Long = apkFile.length()
    val sizeInKB : Long = sizeInBytes / 1024
    val sizeInMB : Long = sizeInKB / 1024
    val apkSize : String = "%.2f MB".format(sizeInMB.toFloat())
    val apkName : String = apkFile.name

    val packageInfo : PackageInfo? = context.packageManager.getPackageArchiveInfo(apkPath , 0)

    var showMenu : Boolean by remember { mutableStateOf(value = false) }

    val iconDrawable : Drawable? = remember(apkPath) {
        packageInfo?.applicationInfo?.loadIcon(context.packageManager)
    }

    OutlinedCard(modifier = Modifier.padding(start = 8.dp , end = 8.dp , top = 8.dp)) {
        val model = iconDrawable?.toBitmapDrawable(context.resources)
            ?: ImageBitmap.imageResource(id = R.mipmap.ic_launcher)
        Log.d("ApkItemComposable1" , "Model type: ${model::class.java.name}")

        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp)) ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
            ) {
                Text(
                    text = apkName ,
                    style = MaterialTheme.typography.titleMedium ,
                )
                Text(
                    text = apkSize , style = MaterialTheme.typography.bodyMedium
                )
            }

            Box {
                IconButton(modifier = Modifier.bounceClick() , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    showMenu = true
                }) {
                    Icon(Icons.Outlined.MoreVert , contentDescription = null)
                }

                DropdownMenu(expanded = showMenu , onDismissRequest = {
                    showMenu = false
                }) {
                    DropdownMenuItem(
                        modifier = Modifier.bounceClick() ,
                        text = { Text(stringResource(R.string.share)) } ,
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            viewModel.shareApk(apkPath)
                        })

                    DropdownMenuItem(
                        modifier = Modifier.bounceClick() ,
                        text = { Text(stringResource(id = R.string.install)) } ,
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            viewModel.installApk(apkPath)
                        })
                }
            }
        }
    }
}