@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.ui.support

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.d4rk.cleaner.ui.settings.display.theme.AppTheme

class SupportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    SupportComposable(this@SupportActivity)
                }
            }
        }
    }

    fun initiatePurchase(
        sku : String ,
        skuDetailsMap : Map<String , SkuDetails> ,
        billingClient : BillingClient
    ) {
        val skuDetails = skuDetailsMap[sku]
        if (skuDetails != null) {
            val flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
            billingClient.launchBillingFlow(this , flowParams)
        }
    }

    fun querySkuDetails(
        billingClient : BillingClient ,
        skuDetailsMap : SnapshotStateMap<String , SkuDetails>
    ) {
        val skuList =
                listOf("low_donation" , "normal_donation" , "high_donation" , "extreme_donation")
        val params = SkuDetailsParams.newBuilder().setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP).build()
        billingClient.querySkuDetailsAsync(params) { billingResult , skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    skuDetailsMap[skuDetails.sku] = skuDetails
                }
            }
        }
    }
}