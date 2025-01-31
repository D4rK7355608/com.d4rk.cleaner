package com.d4rk.cleaner.ui.screens.help

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.QuestionAnswer
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.LargeHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.screens.UiHelpQuestion
import com.d4rk.cleaner.ui.components.buttons.AnimatedExtendedFloatingActionButton
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButtonAndActions
import com.d4rk.cleaner.utils.rememberHtmlData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(activity : HelpActivity , viewModel : HelpViewModel) {
    val scrollBehavior : TopAppBarScrollBehavior =
            TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val isFabVisible by viewModel.isFabVisible.collectAsState()
    val showDialog : MutableState<Boolean> = remember { mutableStateOf(value = false) }

    val uiState by viewModel.uiState.collectAsState()

    val htmlData = rememberHtmlData()
    val changelogHtmlString = htmlData.value.first
    val eulaHtmlString = htmlData.value.second

    val isFabExtended = remember { mutableStateOf(value = true) }
    LaunchedEffect(key1 = scrollBehavior.state.contentOffset) {
        isFabExtended.value = scrollBehavior.state.contentOffset >= 0f
    }

    Scaffold(
        modifier = Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection) ,
        topBar = {
            TopAppBarScaffoldWithBackButtonAndActions(
                context = context ,
                activity = activity ,
                showDialog = showDialog ,
                eulaHtmlString = eulaHtmlString ,
                changelogHtmlString = changelogHtmlString ,
                scrollBehavior = scrollBehavior ,
                view = view
            )
        } ,
        floatingActionButton = {
            AnimatedExtendedFloatingActionButton(visible = isFabVisible , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                uiState.reviewInfo?.let { safeReviewInfo ->
                    viewModel.launchReviewFlow(
                        activity = activity , reviewInfo = safeReviewInfo
                    )
                }
            } , text = { Text(text = stringResource(id = R.string.feedback)) } , icon = {
                Icon(
                    Icons.Default.MailOutline , contentDescription = null
                )
            } , expanded = isFabExtended.value)
        } ,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 16.dp) , state = rememberLazyListState()
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.faq) ,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    FAQComposable(questions = uiState.questions)
                }
            }
        }
    }
}

@Composable
fun FAQComposable(questions : List<UiHelpQuestion>) {
    val expandedStates = remember { mutableStateMapOf<Int , Boolean>() }

    Column {
        questions.forEachIndexed { index , question ->
            val isExpanded = expandedStates[index] ?: false
            QuestionComposable(title = question.question ,
                               summary = question.answer ,
                               isExpanded = isExpanded ,
                               onToggleExpand = {
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
                    imageVector = Icons.Outlined.QuestionAnswer ,
                    contentDescription = null ,
                    tint = MaterialTheme.colorScheme.primary ,
                    modifier = Modifier
                            .size(size = 48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer ,
                                shape = CircleShape
                            )
                            .padding(all = 8.dp)
                )
                LargeHorizontalSpacer()

                Text(
                    text = title ,
                    style = MaterialTheme.typography.titleMedium ,
                    modifier = Modifier.weight(weight = 1f)
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore ,
                    contentDescription = null ,
                    tint = MaterialTheme.colorScheme.primary ,
                    modifier = Modifier.size(size = 24.dp)
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