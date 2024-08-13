@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.ui.support

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SupportViewModel : ViewModel() {
    private val _skuDetails = mutableStateMapOf<String, SkuDetails>()
    val skuDetails: Map<String, SkuDetails> = _skuDetails

    fun querySkuDetails(billingClient: BillingClient) {
        viewModelScope.launch(Dispatchers.IO) {
            val skuList = listOf(
                "low_donation", "normal_donation", "high_donation", "extreme_donation"
            )
            val params = SkuDetailsParams.newBuilder().setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP).build()

            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    skuDetailsList.forEach { skuDetails ->
                        _skuDetails[skuDetails.sku] = skuDetails
                    }
                }
            }
        }
    }
}