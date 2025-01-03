package com.d4rk.cleaner.ui.screens.settings.privacy.usage

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.ui.components.preferences.SwitchCardComposable
import com.d4rk.cleaner.ui.components.modifiers.bounceClick
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton
import com.d4rk.cleaner.utils.helpers.IntentsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UsageAndDiagnosticsComposable(activity: UsageAndDiagnosticsActivity) {
    val context: Context = LocalContext.current
    val dataStore: DataStore = AppCoreManager.dataStore
    val switchState: State<Boolean> = dataStore.usageAndDiagnostics.collectAsState(initial = ! BuildConfig.DEBUG)
    val scope: CoroutineScope = rememberCoroutineScope()
    TopAppBarScaffoldWithBackButton(
        title = stringResource(id = R.string.usage_and_diagnostics),
        onBackClicked = { activity.finish() }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                item {
                    SwitchCardComposable(
                        title = stringResource(id = R.string.usage_and_diagnostics),
                        switchState = switchState
                    ) { isChecked ->
                        scope.launch(Dispatchers.IO) {
                            dataStore.saveUsageAndDiagnostics(isChecked)
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = stringResource(id = R.string.summary_usage_and_diagnostics))
                        val annotatedString: AnnotatedString = buildAnnotatedString {
                            val startIndex: Int = length
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append(stringResource(id = R.string.learn_more))
                            }
                            val endIndex: Int = length

                            addStringAnnotation(
                                tag = "URL",
                                annotation = "https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy",
                                start = startIndex,
                                end = endIndex
                            )
                        }
                        Text(text = annotatedString, modifier = Modifier
                            .bounceClick()
                            .clickable {
                                annotatedString
                                    .getStringAnnotations(
                                        tag = "URL",
                                        start = 0,
                                        end = annotatedString.length
                                    )
                                    .firstOrNull()
                                    ?.let { annotation ->
                                        IntentsHelper.openUrl(context , annotation.item)
                                    }
                            })
                    }
                }
            }
        }
    }
}