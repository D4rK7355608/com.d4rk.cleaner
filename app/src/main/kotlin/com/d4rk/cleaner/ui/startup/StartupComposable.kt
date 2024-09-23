package com.d4rk.cleaner.ui.startup

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.ui.main.MainActivity
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.IntentUtils
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.compose.bounceClick
import com.d4rk.cleaner.utils.compose.components.TopAppBarScaffold

@Composable
fun StartupComposable(activity : StartupActivity) {
    val context : Context = LocalContext.current
    val fabEnabled : MutableState<Boolean> = remember { mutableStateOf(value = false) }
    LaunchedEffect(context) {
        if (! PermissionsUtils.hasNotificationPermission(context)) {
            PermissionsUtils.requestNotificationPermission(context as Activity)
        }
        activity.consentFormShown.collect { shown ->
            fabEnabled.value = shown
        }
    }

    TopAppBarScaffold(
        title = stringResource(id = R.string.welcome),
        onBackClicked = { activity.finish() }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) ,
            ) {
                item {
                    Image(
                        painter = painterResource(id = R.drawable.il_startup) ,
                        contentDescription = null
                    )
                    Icon(
                        Icons.Outlined.Info , contentDescription = null
                    )
                }
                item {
                    Text(
                        text = stringResource(id = R.string.summary_browse_terms_of_service_and_privacy_policy),
                        modifier = Modifier.padding(top = 24.dp , bottom = 24.dp)
                    )
                    val annotatedString : AnnotatedString = buildAnnotatedString {
                        val startIndex : Int = length
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary ,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(stringResource(id = R.string.learn_more))
                        }
                        val endIndex : Int = length

                        addStringAnnotation(
                            tag = "URL" ,
                            annotation = "https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy" ,
                            start = startIndex ,
                            end = endIndex
                        )
                    }
                    Text(text = annotatedString , modifier = Modifier
                        .bounceClick()
                        .clickable {
                            annotatedString
                                .getStringAnnotations(
                                    tag = "URL", start = 0, end = annotatedString.length
                                )
                                .firstOrNull()
                                ?.let { annotation ->
                                    IntentUtils.openUrl(context, annotation.item)
                                }
                        })
                }
            }
            ExtendedFloatingActionButton(modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .bounceClick() ,
                                         containerColor = if (fabEnabled.value) {
                                             FloatingActionButtonDefaults.containerColor
                                         }
                                         else {
                                             Gray
                                         } ,
                text = { Text(stringResource(id = R.string.agree)) },
                                         onClick = {
                                             IntentUtils.openActivity(
                                                 context , MainActivity::class.java
                                             )
                                         } ,
                                         icon = {
                                             Icon(
                                                 Icons.Outlined.CheckCircle ,
                                                 contentDescription = null
                                             )
                                         })
        }
    }
}