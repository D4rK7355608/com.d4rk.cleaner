package com.d4rk.cleaner.app.clean.whatsapp.summary.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun WhatsAppEmptyState(
    adsConfig: AdsConfig = koinInject(qualifier = named(name = "large_banner"))
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.finish_anim))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SizeConstants.MediumSize),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.padding(bottom = SizeConstants.MediumSize)
        )

        Text(
            text = stringResource(id = R.string.whatsapp_cleaner_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        LargeVerticalSpacer()

        AdBanner(
            adsConfig = adsConfig,
            modifier = Modifier.padding(top = SizeConstants.MediumSize)
        )
    }
}

