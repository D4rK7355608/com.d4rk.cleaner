package com.d4rk.cleaner.ui.startup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.MainActivity
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.Utils
import com.d4rk.cleaner.utils.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartupComposable() {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(
            title = { Text(stringResource(R.string.welcome)) },
            scrollBehavior = scrollBehavior
        )
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                item {
                    Image(
                        painter = painterResource(id = R.drawable.il_startup),
                        contentDescription = null
                    )
                    Image(
                        Icons.Outlined.Info, contentDescription = null
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.summary_browse_terms_of_service_and_privacy_policy),
                        modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
                    )
                    ClickableText(
                        text = AnnotatedString(stringResource(R.string.browse_terms_of_service_and_privacy_policy)),
                        onClick = {
                            Utils.openUrl(
                                context,
                                "https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy"
                            )

                        },
                    )
                }
            }
            ExtendedFloatingActionButton(modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .bounceClick(),
                text = { Text(stringResource(R.string.agree)) },
                onClick = {
                    Utils.openActivity(
                        context, MainActivity::class.java
                    )
                },
                icon = {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        contentDescription = null
                    )
                })
        }
    }
}