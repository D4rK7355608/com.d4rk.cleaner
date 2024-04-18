@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.ui.support

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.SkuDetails
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportComposable(activity : SupportActivity) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val skuDetailsMap = remember { mutableStateMapOf<String , SkuDetails>() }
    val billingClient = rememberBillingClient(context , coroutineScope , activity , skuDetailsMap)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.support_us)) } ,
                       navigationIcon = {
                           IconButton(onClick = {
                               activity.finish()
                           }) {
                               Icon(
                                   Icons.AutoMirrored.Filled.ArrowBack ,
                                   contentDescription = null
                               )
                           }
                       } ,
                       scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues) ,
        ) {
            item {
                Text(
                    text = stringResource(R.string.paid_support) ,
                    modifier = Modifier.padding(start = 16.dp , top = 16.dp) ,
                    style = MaterialTheme.typography.titleLarge ,
                )
            }
            item {
                OutlinedCard(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.summary_donations) ,
                            modifier = Modifier.padding(16.dp)
                        )
                        LazyRow(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp) ,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            item {
                                FilledTonalButton(
                                    onClick = {
                                        activity.initiatePurchase(
                                            "low_donation" , skuDetailsMap , billingClient
                                        )
                                    } ,
                                ) {
                                    Icon(
                                        painterResource(R.drawable.ic_paid) ,
                                        contentDescription = null ,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(skuDetailsMap["low_donation"]?.price ?: "")
                                }
                            }
                            item {
                                FilledTonalButton(
                                    onClick = {
                                        activity.initiatePurchase(
                                            "normal_donation" , skuDetailsMap , billingClient
                                        )
                                    } ,
                                ) {
                                    Icon(
                                        painterResource(R.drawable.ic_paid) ,
                                        contentDescription = null ,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(skuDetailsMap["normal_donation"]?.price ?: "")
                                }
                            }
                        }
                        LazyRow(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp) ,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            item {
                                FilledTonalButton(
                                    onClick = {
                                        activity.initiatePurchase(
                                            "high_donation" , skuDetailsMap , billingClient
                                        )
                                    } ,
                                ) {
                                    Icon(
                                        painterResource(R.drawable.ic_paid) ,
                                        contentDescription = null ,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(skuDetailsMap["high_donation"]?.price ?: "")
                                }
                            }
                            item {
                                FilledTonalButton(
                                    modifier = Modifier.fillMaxWidth() ,
                                    onClick = {
                                        activity.initiatePurchase(
                                            "extreme_donation" , skuDetailsMap , billingClient
                                        )
                                    } ,
                                ) {
                                    Icon(
                                        painterResource(R.drawable.ic_paid) ,
                                        contentDescription = null ,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(skuDetailsMap["extreme_donation"]?.price ?: "")
                                }
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    text = stringResource(R.string.non_paid_support) ,
                    modifier = Modifier.padding(start = 16.dp) ,
                    style = MaterialTheme.typography.titleLarge ,
                )
            }
            item {
                FilledTonalButton(
                    onClick = {
                        Utils.openUrl(context , "https://bit.ly/3p8bpj")
                    } ,
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp) ,
                ) {
                    Icon(
                        painterResource(R.drawable.ic_paid) ,
                        contentDescription = null ,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(R.string.web_ad))
                }
            }
            // TODO: Add ad view composable
        }
    }
}

@Composable
fun rememberBillingClient(
    context : Context ,
    coroutineScope : CoroutineScope ,
    activity : SupportActivity ,
    skuDetailsMap : SnapshotStateMap<String , SkuDetails>
) : BillingClient {
    val billingClient = remember {
        BillingClient.newBuilder(context).setListener { _ , _ -> }.enablePendingPurchases().build()
    }
    DisposableEffect(billingClient) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult : BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    coroutineScope.launch {
                        activity.querySkuDetails(billingClient , skuDetailsMap)
                    }
                }
            }

            override fun onBillingServiceDisconnected() {}
        })

        onDispose {
            billingClient.endConnection()
        }
    }
    return billingClient
}