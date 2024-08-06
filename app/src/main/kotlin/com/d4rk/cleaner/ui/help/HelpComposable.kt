package com.d4rk.cleaner.ui.help

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.dialogs.VersionInfoDialog
import com.d4rk.cleaner.utils.IntentUtils
import com.d4rk.cleaner.utils.compose.bounceClick
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.play.core.review.ReviewInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpComposable(activity : HelpActivity , viewModel : HelpViewModel) {
    val scrollBehavior : TopAppBarScrollBehavior =
            TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showMenu : Boolean by remember { mutableStateOf(value = false) }
    val context : Context = LocalContext.current
    val showDialog : MutableState<Boolean> = remember { mutableStateOf(value = false) }
    val reviewInfo : ReviewInfo? = viewModel.reviewInfo.value

    if (reviewInfo != null) {
        LaunchedEffect(key1 = reviewInfo) {
            viewModel.requestReviewFlow()
        }
    }

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.help)) } , navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null)
            }
        } , actions = {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert , contentDescription = "Localized description")
            }
            DropdownMenu(expanded = showMenu , onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text(stringResource(R.string.view_in_google_play_store)) } ,
                                 onClick = {
                                     IntentUtils.openUrl(
                                         context ,
                                         url = "https://play.google.com/store/apps/details?id=${activity.packageName}"
                                     )
                                 })
                DropdownMenuItem(text = { Text(stringResource(R.string.version_info)) } ,
                                 onClick = { showDialog.value = true })
                DropdownMenuItem(text = { Text(stringResource(R.string.beta_program)) } ,
                                 onClick = {
                                     IntentUtils.openUrl(
                                         context ,
                                         url = "https://play.google.com/apps/testing/${activity.packageName}"
                                     )
                                 })
                DropdownMenuItem(text = { Text(stringResource(R.string.terms_of_service)) } ,
                                 onClick = {
                                     IntentUtils.openUrl(
                                         context ,
                                         url = "https://sites.google.com/view/d4rk7355608/more/apps/terms-of-service"
                                     )
                                 })
                DropdownMenuItem(text = { Text(stringResource(R.string.privacy_policy)) } ,
                                 onClick = {
                                     IntentUtils.openUrl(
                                         context ,
                                         url = "https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy"
                                     )
                                 })
                DropdownMenuItem(text = { Text(stringResource(com.google.android.gms.oss.licenses.R.string.oss_license_title)) } ,
                                 onClick = {
                                     IntentUtils.openActivity(
                                         context , OssLicensesMenuActivity::class.java
                                     )
                                 })
            }
            if (showDialog.value) {
                VersionInfoDialog(onDismiss = { showDialog.value = false })
            }
        } , scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        Box(
            modifier = Modifier
                    .padding(start = 16.dp , end = 16.dp)
                    .fillMaxSize()
                    .safeDrawingPadding()
        ) {
            ConstraintLayout(modifier = Modifier.padding(paddingValues)) {
                val (faqTitle : ConstrainedLayoutReference , faqCard : ConstrainedLayoutReference) = createRefs()
                Text(text = stringResource(R.string.faq) ,
                     modifier = Modifier
                             .padding(bottom = 24.dp)
                             .constrainAs(faqTitle) {
                                 top.linkTo(parent.top)
                                 start.linkTo(parent.start)
                             })
                Card(modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(faqCard) {
                            top.linkTo(faqTitle.bottom)
                            bottom.linkTo(parent.bottom)
                        }) {
                    FAQComposable()
                }
            }
            ExtendedFloatingActionButton(
                text = { Text(stringResource(id = R.string.feedback)) } ,
                onClick = {
                    viewModel.reviewInfo.value?.let { safeReviewInfo ->
                        viewModel.launchReviewFlow(activity , safeReviewInfo)
                    }
                } ,
                icon = {
                    Icon(
                        Icons.Default.MailOutline , contentDescription = null
                    )
                } ,
                modifier = Modifier
                        .bounceClick()
                        .padding(bottom = 16.dp)
                        .align(Alignment.BottomEnd) ,
            )
        }
    }
}

@Composable
fun FAQComposable() {
    LazyColumn {
        item {
            QuestionComposable(
                title = stringResource(R.string.question_1) ,
                summary = stringResource(R.string.summary_preference_faq_1)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_2) ,
                summary = stringResource(R.string.summary_preference_faq_2)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_3) ,
                summary = stringResource(R.string.summary_preference_faq_3)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_4) ,
                summary = stringResource(R.string.summary_preference_faq_4)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_5) ,
                summary = stringResource(R.string.summary_preference_faq_5)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_6) ,
                summary = stringResource(R.string.summary_preference_faq_6)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_7) ,
                summary = stringResource(R.string.summary_preference_faq_7)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_8) ,
                summary = stringResource(R.string.summary_preference_faq_8)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_9) ,
                summary = stringResource(R.string.summary_preference_faq_9)
            )
        }
    }
}

@Composable
fun QuestionComposable(title : String , summary : String) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.QuestionAnswer ,
            contentDescription = null ,
            modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer , shape = CircleShape
                    )
                    .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title , style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = summary)
        }
    }
}