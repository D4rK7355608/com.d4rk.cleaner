package com.d4rk.cleaner.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cleaner.R

@Composable
fun HomeComposable() {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()
    val progress by viewModel.progress.observeAsState(0f)
    val statusText by viewModel.statusText.observeAsState("")

   // val progress by viewModel.progress.observeAsState(0f) // fixme: Unresolved reference: observeAsState
   // val statusText by viewModel.statusText.observeAsState("") // fixme: Unresolved reference: observeAsState
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth()
        ) {

            // The circular progress bar does not shown
            CircularProgressIndicator(
                progress = { progress } ,
                modifier = Modifier.align(Alignment.Center) ,
                color = MaterialTheme.colorScheme.primary ,
                strokeWidth = 4.dp ,
            )

            viewModel.storageInfo.observeAsState().value?.let { (available, total) ->
                Text(
                    text = "${"%.2f".format(total - available)}/$total GB \n used",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )

            }

            Image(
                painter = painterResource(R.drawable.ic_clean) ,
                contentDescription = null ,
                modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(128.dp, 66.dp)
            )
        }

        // TODO: WIP
/*        Column(
            modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
        ) {
            // Add your items here
        }*/


/*        Text(
            text = "Status",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(24.dp)
        )*/
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(98.dp)
                    .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    viewModel.clean()
                },
                modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 12.dp, end = 6.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_broom),
                        contentDescription = null
                    )
                    Text(text = "Clean", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Button(
                onClick = {
                    viewModel.analyze()
                },
                modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 6.dp, end = 12.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_search),
                        contentDescription = null
                    )
                    Text(text = "Analyze", style = MaterialTheme.typography.bodyMedium)
                }
            }

        }
    }
}



/*
@Composable
fun CircularProgressbar(
    name: String = "",
    size: Dp = dimen_110dp,
    foregroundIndicatorColor: Color = MaterialTheme.colors.primary,
    shadowColor: Color = Color.LightGray,
    indicatorThickness: Dp = dimen_8dp,
    dataUsage: Float = 60f,
    animationDuration: Int = 1000,
    dataTextStyle: TextStyle = TextStyle(fontSize = 12.sp),
) {
    // State to hold the data usage value for animation
    var dataUsageRemember by remember {
        mutableFloatStateOf(-1f)
    }

    // State for animating the data usage value
    val dataUsageAnimate = animateFloatAsState(
        targetValue = dataUsageRemember,
        animationSpec = tween(
            durationMillis = animationDuration
        ), label = ""
    )

    // Trigger the LaunchedEffect to start the animation when the composable is first launched.
    LaunchedEffect(Unit) {
        dataUsageRemember = dataUsage
    }

    // Box to hold the entire composable
    Box(
        modifier = Modifier
                .size(size)
                .padding(top = dimen_8dp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas drawing for the circular progress bar
        Canvas(
            modifier = Modifier.size(size)
        ) {
            // Draw the shadow around the circular progress bar
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(shadowColor, Color.White),
                    center = Offset(x = this.size.width / 2, y = this.size.height / 2),
                    radius = this.size.height / 2
                ),
                radius = this.size.height / 2,
                center = Offset(x = this.size.width / 2, y = this.size.height / 2)
            )

            // Draw the white background of the circular progress bar
            drawCircle(
                color = Color.White,
                radius = (size / 2 - indicatorThickness).toPx(),
                center = Offset(x = this.size.width / 2, y = this.size.height / 2)
            )

            // Calculate and draw the progress indicator
            val sweepAngle = (dataUsageAnimate.value) * 360 / 100
            drawArc(
                color = foregroundIndicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = indicatorThickness.toPx(), cap = StrokeCap.Round),
                size = Size(
                    width = (size - indicatorThickness).toPx(),
                    height = (size - indicatorThickness).toPx()
                ),
                topLeft = Offset(
                    x = (indicatorThickness / 2).toPx(),
                    y = (indicatorThickness / 2).toPx()
                )
            )
        }

        // Display text below the circular progress bar
        DisplayText(
            name = name,
            animateNumber = dataUsageAnimate,
            dataTextStyle = dataTextStyle,
        )
    }

    // Spacer to add some padding below the circular progress bar
    Spacer(modifier = Modifier.height(dimen_16dp))
}

*/
/**
 * A private composable function to display the name and data usage percentage text.
 *
 * @param name The name or label associated with the circular progress bar.
 * @param animateNumber The animated data usage percentage value.
 * @param dataTextStyle The style for displaying the data usage text.
 *//*

@Composable
private fun DisplayText(
    name: String,
    animateNumber: State<Float>,
    dataTextStyle: TextStyle,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(dimen_8dp)
    ) {
        // Display the name with bold font and ellipsis for overflow
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            style = dataTextStyle,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Display the data usage percentage text
        Text(
            text = (animateNumber.value).toInt().toString() + "%",
            style = dataTextStyle
        )
    }
}*/
