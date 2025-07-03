package com.d4rk.cleaner.app.clean.whatsapp.summary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.d4rk.cleaner.BuildConfig
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.shimmerEffect
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.whatsapp.navigation.WhatsAppRoute
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.UiWhatsAppCleanerModel
import org.koin.compose.viewmodel.koinViewModel

private data class DirectoryItem(
    val type: String,
    val name: String,
    val icon: Int,
    val count: Int
)

private sealed class ViewState<out T> {
    data object Loading : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
    data class Error(val message: String) : ViewState<Nothing>()
}

@Composable
fun WhatsAppCleanerScreen(
    navController: NavHostController,
    viewModel: WhatsAppCleanerViewModel = koinViewModel(),
    paddingValues: PaddingValues
) {
    val state: UiStateScreen<UiWhatsAppCleanerModel> by viewModel.uiState.collectAsState()

    Scaffold(topBar = { HomeTopBar() }) { innerPadding ->
        val combinedPadding = PaddingValues(
            start = paddingValues.calculateStartPadding(layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr),
            top = paddingValues.calculateTopPadding() + innerPadding.calculateTopPadding(),
            end = paddingValues.calculateEndPadding(layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr),
            bottom = paddingValues.calculateBottomPadding() + innerPadding.calculateBottomPadding()
        )
        Content(
            summaryState = state,
            onClean = { viewModel.onEvent(WhatsAppCleanerEvent.CleanAll) },
            paddingValues = combinedPadding,
            onOpenDetails = { type ->
                navController.navigate(WhatsAppRoute.Details.create(type))
            }
        )
    }
}

@Composable
private fun Content(
    summaryState: UiStateScreen<UiWhatsAppCleanerModel>,
    onClean: () -> Unit,
    paddingValues: PaddingValues,
    onOpenDetails: (String) -> Unit
) {
    val summary = summaryState.data?.mediaSummary ?: com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.WhatsAppMediaSummary()

    val directoryState = remember(summaryState.screenState, summary) {
        when (summaryState.screenState) {
            is ScreenState.Success -> {
                val list = listOf(
                    DirectoryItem("images", stringResource(id = R.string.images), R.drawable.ic_image, summary.images.size),
                    DirectoryItem("videos", stringResource(id = R.string.videos), R.drawable.ic_video_file, summary.videos.size),
                    DirectoryItem("documents", stringResource(id = R.string.documents), R.drawable.ic_apk_document, summary.documents.size)
                )
                val total = list.sumOf { it.count }
                ViewState.Success(total.toString() to list)
            }
            is ScreenState.IsLoading -> ViewState.Loading
            is ScreenState.Error -> ViewState.Error("Error")
            else -> ViewState.Loading
        }
    }

    Column(
        Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val modifier = if (directoryState is ViewState.Success) Modifier else Modifier.shimmerEffect()

        when (directoryState) {
            is ViewState.Success -> {
                LazyColumn(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { ListSizeHeader(modifier, directoryState.data.first) }
                    items(directoryState.data.second) { DirectoryCard(it, onOpenDetails) }
                }
            }
            is ViewState.Loading -> LazyColumn(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { ListSizeHeader(modifier, "0") }
                items(
                    listOf(
                        DirectoryItem("images", stringResource(id = R.string.images), R.drawable.ic_image, 0),
                        DirectoryItem("videos", stringResource(id = R.string.videos), R.drawable.ic_video_file, 0),
                        DirectoryItem("documents", stringResource(id = R.string.documents), R.drawable.ic_apk_document, 0)
                    )
                ) { DirectoryCard(it, onOpenDetails) }
            }
            is ViewState.Error -> Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = directoryState.message,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Button(onClick = onClean, modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = R.string.clean_whatsapp))
        }

        if (BuildConfig.DEBUG)
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp),
                text = "Debug Build ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall,
            )
    }
}

@Composable
private fun ListSizeHeader(modifier: Modifier, total: String) = Banner(
    modifier.padding(16.dp),
    buildAnnotatedString {
        withStyle(SpanStyle(fontSize = 24.sp)) { append(total) }
        withStyle(SpanStyle(fontSize = 18.sp)) { append(" files") }
    }
)

@Composable
private fun DirectoryCard(item: DirectoryItem, onOpenDetails: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        onClick = { onOpenDetails(item.type) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .fillMaxWidth(0.15f)
                    .aspectRatio(1f)
                    .shadow(4.dp, CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = item.count.toString(), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


@Composable
private fun Title(modifier: Modifier, text: String) {
    Text(
        modifier = modifier.padding(8.dp),
        text = text,
        fontSize = 24.sp,
        textAlign = TextAlign.Start,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun Banner(modifier: Modifier, text: AnnotatedString) {
    val bgColor = MaterialTheme.colorScheme.primaryContainer
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(0.4f)
                .aspectRatio(1f)
                .shadow(elevation = 16.dp, shape = CircleShape)
                .background(bgColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {
            Title(modifier = Modifier, text = stringResource(R.string.app_name))
        }
    )
}
