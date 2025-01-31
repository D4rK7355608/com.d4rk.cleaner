package com.d4rk.cleaner.ui.screens.help

import android.app.Activity
import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Support
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.buttons.AnimatedExtendedFloatingActionButton
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.LargeHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.MediumVerticalSpacer
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.screens.UiHelpQuestion
import com.d4rk.cleaner.data.model.ui.screens.UiHelpScreen
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButtonAndActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(activity : Activity , viewModel : HelpViewModel) {
    val scrollBehavior : TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val isFabVisible : Boolean by viewModel.isFabVisible.collectAsState()
    val showDialog : MutableState<Boolean> = remember { mutableStateOf(value = false) }

    val uiState : UiHelpScreen by viewModel.uiState.collectAsState()

    val htmlData : State<Pair<String? , String?>> = com.d4rk.android.libs.apptoolkit.utils.rememberHtmlData(
        context = context , currentVersionName = BuildConfig.VERSION_NAME , packageName = BuildConfig.APPLICATION_ID
    )

    val changelogHtmlString : String? = htmlData.value.first
    val eulaHtmlString : String? = htmlData.value.second

    val isFabExtended : MutableState<Boolean> = remember { mutableStateOf(value = true) }
    LaunchedEffect(key1 = scrollBehavior.state.contentOffset) {
        isFabExtended.value = scrollBehavior.state.contentOffset >= 0f
    }

    Scaffold(
        modifier = Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection) ,
        topBar = {
            TopAppBarScaffoldWithBackButtonAndActions(
                context = context , activity = activity , showDialog = showDialog , eulaHtmlString = eulaHtmlString , changelogHtmlString = changelogHtmlString , scrollBehavior = scrollBehavior , view = view
            )
        } ,
        floatingActionButton = {
            AnimatedExtendedFloatingActionButton(visible = isFabVisible , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                uiState.reviewInfo?.let { safeReviewInfo ->
                    viewModel.launchReviewFlow(
                        activity = activity , reviewInfo = safeReviewInfo
                    )
                    viewModel.requestReviewFlow()
                }
            } , text = { Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.feedback)) } , icon = {
                Icon(
                    Icons.Outlined.RateReview , contentDescription = null
                )
            } , expanded = isFabExtended.value , modifier = Modifier.bounceClick())
        } ,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp) , state = rememberLazyListState()
        ) {
            item {
                Text(
                    text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.popular_help_resources)
                )

                MediumVerticalSpacer()

                Card(modifier = Modifier.fillMaxWidth()) {
                    FAQComposable(questions = uiState.questions)
                }

                MediumVerticalSpacer()
                ContactUsCard(onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    IntentsHelper.sendEmailToDeveloper(context = activity , applicationNameRes = R.string.app_name)
                })
                Spacer(modifier = Modifier.height(height = 64.dp))
            }
        }
    }
}

@Composable
fun FAQComposable(questions : List<UiHelpQuestion>) {
    val expandedStates : SnapshotStateMap<Int , Boolean> = remember { mutableStateMapOf() }

    Column {
        questions.forEachIndexed { index , question ->
            val isExpanded = expandedStates[index] ?: false
            QuestionComposable(title = question.question , summary = question.answer , isExpanded = isExpanded , onToggleExpand = {
                expandedStates[index] = ! isExpanded
            })
        }
    }
}


@Composable
fun QuestionComposable(
    title : String , summary : String , isExpanded : Boolean , onToggleExpand : () -> Unit
) {
    Card(modifier = Modifier
            .clip(shape = RoundedCornerShape(size = 12.dp))
            .clickable { onToggleExpand() }
            .padding(all = 16.dp)
            .animateContentSize()
            .fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically , modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.QuestionAnswer , contentDescription = null , tint = MaterialTheme.colorScheme.primary , modifier = Modifier
                            .size(size = 48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer , shape = CircleShape
                            )
                            .padding(all = 8.dp)
                )
                LargeHorizontalSpacer()

                Text(
                    text = title , style = MaterialTheme.typography.titleMedium , modifier = Modifier.weight(weight = 1f)
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore , contentDescription = null , tint = MaterialTheme.colorScheme.primary , modifier = Modifier.size(size = 24.dp)
                )
            }
            if (isExpanded) {
                SmallVerticalSpacer()
                Text(
                    text = summary ,
                    style = MaterialTheme.typography.bodyMedium ,
                )
            }
        }
    }
}

@Composable
fun ContactUsCard(onClick : () -> Unit) {
    Card(modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(size = 12.dp))
            .clickable {
                onClick()
            }) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp) , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.Support , contentDescription = null , modifier = Modifier.padding(end = 16.dp))
            Column(
                modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
            ) {
                Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.contact_us))
                Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.contact_us_description))
            }
        }
    }
}