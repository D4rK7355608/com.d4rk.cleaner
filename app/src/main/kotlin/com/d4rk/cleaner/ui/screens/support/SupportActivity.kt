@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.ui.screens.support

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.d4rk.cleaner.ui.screens.settings.display.theme.style.AppTheme

class SupportActivity : AppCompatActivity() {
    private val viewModel : SupportViewModel by viewModels()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    SupportComposable(viewModel , activity = this@SupportActivity)
                }
            }
        }
    }

    fun initiatePurchase(
        sku : String , skuDetailsMap : Map<String , SkuDetails> , billingClient : BillingClient
    ) {
        val skuDetails : SkuDetails? = skuDetailsMap[sku]
        if (skuDetails != null) {
            val flowParams : BillingFlowParams =
                    BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
            billingClient.launchBillingFlow(this , flowParams)
        }
    }
}