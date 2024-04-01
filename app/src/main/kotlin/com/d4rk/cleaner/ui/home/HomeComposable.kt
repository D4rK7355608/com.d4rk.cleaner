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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cleaner.R

@Composable
fun HomeComposable() {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()

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
            CircularProgressIndicator(
                progress = {
                    0f
                } ,
                modifier = Modifier.align(Alignment.Center) ,
                color = MaterialTheme.colorScheme.primary ,
                strokeWidth = 4.dp ,
            )
            Text(
                text = "0%",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center)
            )
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


        Text(
            text = "Status",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(24.dp)
        )
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