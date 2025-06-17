package com.d4rk.cleaner.core.di.modules

import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.cleaner.core.utils.constants.ads.AdsConstants
import com.google.android.gms.ads.AdSize
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val adsModule : Module = module {

    single<AdsConfig> {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.BANNER)
    }

    single<AdsConfig>(named(name = "full_banner")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.FULL_BANNER)
    }

    single<AdsConfig>(named(name = "large_banner")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.LARGE_BANNER)
    }

    single<AdsConfig>(named(name = "banner_medium_rectangle")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.MEDIUM_RECTANGLE)
    }
}