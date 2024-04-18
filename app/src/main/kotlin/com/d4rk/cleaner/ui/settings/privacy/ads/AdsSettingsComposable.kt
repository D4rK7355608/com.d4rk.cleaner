package com.d4rk.cleaner.ui.settings.privacy.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.store.DataStore
import com.d4rk.cleaner.utils.PreferenceItem
import com.d4rk.cleaner.utils.SwitchCardComposable
import com.d4rk.cleaner.utils.Utils
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsSettingsComposable(activity : AdsSettingsActivity) {
    val context = LocalContext.current
    val dataStore = DataStore(context)
    val switchState = dataStore.usageAndDiagnostics.collectAsState(initial = true)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.ads)) } , navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null
                )
            }
        } , scrollBehavior = scrollBehavior)
    }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding) ,
            ) {
                item {
                    SwitchCardComposable(
                        title = stringResource(R.string.turn_off_all_ads) ,
                        switchState = switchState
                    ) { isChecked ->
                        scope.launch(Dispatchers.IO) {
                            dataStore.saveAds(isChecked)
                        }
                    }
                }
                item {
                    PreferenceItem(title = stringResource(R.string.personalized_ads) ,
                                   summary = stringResource(R.string.summary_preference_settings_oss) ,
                                   onClick = {
                                       val params = ConsentRequestParameters.Builder()
                                               .setTagForUnderAgeOfConsent(false).build()
                                       val consentInformation =
                                               UserMessagingPlatform.getConsentInformation(context)
                                       consentInformation.requestConsentInfoUpdate(activity ,
                                                                                   params ,
                                                                                   {
                                                                                       if (consentInformation.isConsentFormAvailable) {
                                                                                           activity.loadForm()
                                                                                       }
                                                                                   } ,
                                                                                   {})
                                   })
                }
                item {
                    Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Info , contentDescription = null)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(stringResource(R.string.summary_ads))
                        val annotatedString = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary ,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append(stringResource(R.string.learn_more))
                            }
                            addStringAnnotation(
                                tag = "URL" ,
                                annotation = "https://www.example.com" ,
                                start = 0 ,
                                end = stringResource(R.string.learn_more).length
                            )
                        }
                        ClickableText(text = annotatedString , onClick = { offset ->
                            annotatedString.getStringAnnotations("URL" , offset , offset)
                                    .firstOrNull()?.let { annotation ->
                                        Utils.openUrl(context , annotation.item)
                                    }
                        })
                    }
                }
            }
        }
    }
}