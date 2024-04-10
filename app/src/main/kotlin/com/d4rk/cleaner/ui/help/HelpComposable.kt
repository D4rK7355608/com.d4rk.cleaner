package com.d4rk.cleaner.ui.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpComposable(activity: HelpActivity) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val isVisible = rememberSaveable { mutableStateOf(true) }

    // Nested scroll for control FAB
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset , source: NestedScrollSource): Offset {
                // Hide FAB if scrolling down
                if (available.y < -1) {
                    isVisible.value = false
                }
                // Show FAB if scrolling up
                if (available.y > 1) {
                    isVisible.value = true
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.help)) },
                navigationIcon = {
                    IconButton(onClick = {
                        activity.finish()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.view_in_google_play_store)) } ,
                            onClick = {
                                Utils.openUrl(context, "https://play.google.com/store/apps/details?id=${activity.packageName}")
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.version_info)) } ,
                            onClick = {
                                activity.versionInfo()
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.beta_program)) } ,
                            onClick = {
                                Utils.openUrl(context, "https://play.google.com/apps/testing/${activity.packageName}")
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.terms_of_service)) } ,
                            onClick = {
                                Utils.openUrl(context, "https://sites.google.com/view/d4rk7355608/more/apps/terms-of-service")
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.privacy_policy)) } ,
                            onClick = {
                                Utils.openUrl(context,"https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy")
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(com.google.android.gms.oss.licenses.R.string.oss_license_title)) } ,
                            onClick = {
                                activity.openSourceLicenses()
                            })

                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        ConstraintLayout(modifier = Modifier.padding(paddingValues)) {
            val (faqTitle, faqCard, fabButton) = createRefs()
            Text(
                text = stringResource(R.string.faq),
                modifier = Modifier
                        .padding(24.dp)
                        .constrainAs(faqTitle) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
            )
            Card(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp, bottom = 52.dp)
                        .constrainAs(faqCard) {
                            top.linkTo(faqTitle.bottom)
                            bottom.linkTo(parent.bottom)
                        }
            ) {
                FAQComposable()
            }
            FloatingActionButton(
                onClick = {
                          activity.feedback()
                          },
                modifier = Modifier
                        .padding(16.dp)
                        .constrainAs(fabButton) {
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        }
            ) {
                Icon(Icons.Default.MailOutline, contentDescription = "Localized description")
            }
        }

    }
}

@Composable
fun FAQComposable() {
    LazyColumn  {
        item { QuestionComposable(
            title = stringResource(R.string.question_1),
            summary = stringResource(R.string.summary_preference_faq_1)
        )}
        item { QuestionComposable(
            title = stringResource(R.string.question_2),
            summary = stringResource(R.string.summary_preference_faq_2)
        )}
        item { QuestionComposable(
            title = stringResource(R.string.question_3),
            summary = stringResource(R.string.summary_preference_faq_3)
        )}
        item { QuestionComposable(
            title = stringResource(R.string.question_4),
            summary = stringResource(R.string.summary_preference_faq_5)
        )}
        item { QuestionComposable(
            title = stringResource(R.string.question_5),
            summary = stringResource(R.string.summary_preference_faq_5)
        )}
        item { QuestionComposable(
            title = stringResource(R.string.question_6),
            summary = stringResource(R.string.summary_preference_faq_6)
        )}
        item { QuestionComposable(
            title = stringResource(R.string.question_7),
            summary = stringResource(R.string.summary_preference_faq_7)
        )}
        item { QuestionComposable(
            title = stringResource(R.string.question_8),
            summary = stringResource(R.string.summary_preference_faq_8)
        )}
        item { QuestionComposable(
            title = stringResource(R.string.question_9),
            summary = stringResource(R.string.summary_preference_faq_9)
        )}
    }
}

@Composable
fun QuestionComposable(title: String, summary: String) {
    Column(
        modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = summary)
    }
}