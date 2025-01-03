package com.d4rk.cleaner.ui.components.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.d4rk.cleaner.constants.ads.AdsConstants
import com.d4rk.cleaner.data.core.AppCoreManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(
    modifier : Modifier = Modifier
) {
    val showAds : Boolean by AppCoreManager.dataStore.ads.collectAsState(initial = true)

    if (showAds) {
        AndroidView(modifier = modifier.fillMaxWidth() , factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdsConstants.BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        })
    }
}

@Composable
fun AdBannerFull(
    modifier : Modifier = Modifier
) {
    val showAds : Boolean by AppCoreManager.dataStore.ads.collectAsState(initial = true)

    if (showAds) {
        AndroidView(modifier = modifier.fillMaxWidth() , factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.FULL_BANNER)
                adUnitId = AdsConstants.BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        })
    }
}

@Composable
fun LargeBannerAdsComposable(
    modifier : Modifier = Modifier
) {
    val showAds : Boolean by AppCoreManager.dataStore.ads.collectAsState(initial = true)

    if (showAds) {
        AndroidView(modifier = modifier.fillMaxWidth() , factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.LARGE_BANNER)
                adUnitId = AdsConstants.BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        })
    }
}