package com.d4rk.cleaner.ui.appmanager

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import com.d4rk.cleaner.utils.PermissionsUtils
import kotlinx.coroutines.launch
import java.io.File

/**
 * Composable function for managing and displaying different app categories.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppManagerComposable() {
    val viewModel: AppManagerViewModel = viewModel(
        factory = AppManagerViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val context = LocalContext.current
    val tabs = listOf("Installed Apps", "System Apps", "App Install Files")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    val transition = updateTransition(targetState = ! isLoading, label = "LoadingTransition")

    val contentAlpha by transition.animateFloat(label = "Content Alpha") {
        if (it) 1f else 0f
    }

    LaunchedEffect(context) {
        if (!PermissionsUtils.hasStoragePermissions(context)) {
            PermissionsUtils.requestStoragePermissions(context as Activity)
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.alpha(contentAlpha),
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        shape = RoundedCornerShape(
                            topStart = 3.dp, topEnd = 3.dp, bottomEnd = 0.dp, bottomStart = 0.dp
                        ),
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
            ) { page ->
                when (page) {
                    0 -> AppsComposable(
                        apps = viewModel.installedApps.collectAsState().value.filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                    )
                    1 -> AppsComposable(
                        apps = viewModel.installedApps.collectAsState().value.filter { it.flags and ApplicationInfo.FLAG_SYSTEM != 0 }
                    )
                    2 -> ApksComposable(apkFiles = viewModel.apkFiles.collectAsState().value)
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
fun AppsComposable(apps: List<ApplicationInfo>) {
    LazyColumn {
        items(apps) { app ->
            AppItemComposable(app)
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
    app: ApplicationInfo
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val appName = app.loadLabel(packageManager).toString()
    val apkPath = app.publicSourceDir
    val apkFile = File(apkPath)
    val sizeInBytes = apkFile.length()
    val sizeInKB = sizeInBytes / 1024
    val sizeInMB = sizeInKB / 1024
    val appSize = "%.2f MB".format(sizeInMB.toFloat())
    val appIcon = remember(app.packageName) {
        val drawable = app.loadIcon(packageManager)
        val bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
        bitmap.asImageBitmap()
    }
    var showMenu by remember { mutableStateOf(false) }
    OutlinedCard(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = appIcon, contentDescription = null, modifier = Modifier.size(48.dp)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = appSize, style = MaterialTheme.typography.bodyMedium
                )
            }


            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = {
                        Text(stringResource(R.string.uninstall))
                    },
                        onClick = {
                            val uri = Uri.fromParts("package", app.packageName, null)
                            val intent = Intent(Intent.ACTION_DELETE, uri)
                            context.startActivity(intent)
                        })
                    DropdownMenuItem(text = { Text(stringResource(R.string.share)) }, onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app")
                        @Suppress("DEPRECATION") val isFromPlayStore =
                            context.packageManager.getInstallerPackageName(app.packageName) == "com.android.vending"
                        if (isFromPlayStore) {
                            val playStoreLink =
                                "https://play.google.com/store/apps/details?id=${app.packageName}"
                            val shareMessage = "Check out this app: $appName\n$playStoreLink"
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        } else {
                            val shareMessage = "Check out this app: $appName\n$app.packageName"
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share App"))
                    })
                    DropdownMenuItem(text = { Text(stringResource(R.string.app_info)) }, onClick = {
                        val appInfoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val packageUri = Uri.fromParts("package", app.packageName, null)
                        appInfoIntent.data = packageUri
                        context.startActivity(appInfoIntent)
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
fun ApksComposable(apkFiles: List<ApkInfo>) {

    LazyColumn {
        items(apkFiles) { apkInfo ->
            ApkItemComposable(apkPath = apkInfo.path)
        }
    }
}


/**
 * Composable function for displaying detailed information about an APK file.
 *
 * @param apkPath Path to the APK file.
 */
@Composable
fun ApkItemComposable(apkPath: String) {
    val context = LocalContext.current
    val apkFile = File(apkPath)
    val sizeInBytes = apkFile.length()
    val sizeInKB = sizeInBytes / 1024
    val sizeInMB = sizeInKB / 1024
    val apkSize = "%.2f MB".format(sizeInMB.toFloat())
    val apkName = apkFile.name

    val packageInfo = context.packageManager.getPackageArchiveInfo(apkPath, 0)
    val appIcon = packageInfo?.applicationInfo?.loadIcon(context.packageManager)?.let {
        val bitmap = if (it is BitmapDrawable) {
            it.bitmap
        } else {
            val bitmap = Bitmap.createBitmap(
                it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
            bitmap
        }
        bitmap.asImageBitmap()
    } ?: ImageBitmap.imageResource(id = R.mipmap.ic_launcher)

    var showMenu by remember { mutableStateOf(false) }

    OutlinedCard(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = appIcon, contentDescription = null, modifier = Modifier.size(48.dp)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = apkName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = apkSize, style = MaterialTheme.typography.bodyMedium
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.share)) }, onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "application/vnd.android.package-archive"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(apkFile))
                        context.startActivity(Intent.createChooser(shareIntent, "Share APK"))
                    })

                    DropdownMenuItem(text = { Text("Install") }, onClick = {
                        val installIntent = Intent(Intent.ACTION_VIEW)
                        installIntent.setDataAndType(
                            Uri.fromFile(apkFile),
                            "application/vnd.android.package-archive"
                        )
                        installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(installIntent)
                    })
                }
            }
        }
    }
}