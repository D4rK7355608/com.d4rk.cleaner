@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.ui.support

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.Paid
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ads.LargeBannerAdsComposable
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.utils.IntentUtils
import com.d4rk.cleaner.utils.compose.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportComposable(viewModel : SupportViewModel , activity : SupportActivity) {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val billingClient = rememberBillingClient(context , viewModel)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) , topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.support_us)) } , navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null
                )
            }
        } , scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        Box(
            modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxHeight()
        ) {
            LazyColumn {
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
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .bounceClick() ,
                                        onClick = {
                                            activity.initiatePurchase(
                                                "low_donation" ,
                                                viewModel.skuDetails ,
                                                billingClient ,
                                            )
                                        } ,
                                    ) {
                                        Icon(
                                            Icons.Outlined.Paid ,
                                            contentDescription = null ,
                                            modifier = Modifier.size(ButtonDefaults.IconSize)
                                        )
                                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                        Text(viewModel.skuDetails["low_donation"]?.price ?: "")
                                    }
                                }
                                item {
                                    FilledTonalButton(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .bounceClick() ,
                                        onClick = {
                                            activity.initiatePurchase(
                                                "normal_donation" ,
                                                viewModel.skuDetails ,
                                                billingClient ,
                                            )
                                        } ,
                                    ) {
                                        Icon(
                                            Icons.Outlined.Paid ,
                                            contentDescription = null ,
                                            modifier = Modifier.size(ButtonDefaults.IconSize)
                                        )
                                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                        Text(viewModel.skuDetails["normal_donation"]?.price ?: "")
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
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .bounceClick() ,
                                        onClick = {
                                            activity.initiatePurchase(
                                                "high_donation" ,
                                                viewModel.skuDetails ,
                                                billingClient ,
                                            )
                                        } ,
                                    ) {
                                        Icon(
                                            Icons.Outlined.Paid ,
                                            contentDescription = null ,
                                            modifier = Modifier.size(ButtonDefaults.IconSize)
                                        )
                                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                        Text(viewModel.skuDetails["high_donation"]?.price ?: "")
                                    }
                                }
                                item {
                                    FilledTonalButton(

                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .bounceClick() ,
                                        onClick = {
                                            activity.initiatePurchase(
                                                "extreme_donation" ,
                                                viewModel.skuDetails ,
                                                billingClient ,
                                            )
                                        } ,
                                    ) {
                                        Icon(
                                            Icons.Outlined.Paid ,
                                            contentDescription = null ,
                                            modifier = Modifier.size(ButtonDefaults.IconSize)
                                        )
                                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                        Text(viewModel.skuDetails["extreme_donation"]?.price ?: "")
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
                            IntentUtils.openUrl(
                                context , "https://direct-link.net/548212/agOqI7123501341"
                            )
                        } ,
                        modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick()
                                .padding(16.dp) ,
                    ) {
                        Icon(
                            Icons.Outlined.Paid ,
                            contentDescription = null ,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(R.string.web_ad))
                    }
                }
                item {
                    LargeBannerAdsComposable(
                        modifier = Modifier.padding(bottom = 12.dp) , dataStore = dataStore
                    )
                }
            }
        }
    }
}

@Composable
fun rememberBillingClient(
    context : Context , viewModel : SupportViewModel
) : BillingClient {
    val billingClient = remember {
        BillingClient.newBuilder(context).setListener { _ , _ -> }.enablePendingPurchases().build()
    }

    DisposableEffect(billingClient) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult : BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    viewModel.querySkuDetails(billingClient)
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