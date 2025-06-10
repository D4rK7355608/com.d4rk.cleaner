package com.d4rk.cleaner.app.clean.memory.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.home.ui.components.StorageProgressBar
import com.d4rk.cleaner.app.clean.memory.domain.data.model.RamInfo
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo

@Composable
fun RamInfoCard(ramInfo : RamInfo) {
    Column(modifier = Modifier.padding(all = SizeConstants.LargeSize)) {
        Text(text = stringResource(id = R.string.ram_information) , style = MaterialTheme.typography.headlineSmall , fontWeight = FontWeight.Bold)
        SmallVerticalSpacer()
        StorageProgressBar(StorageInfo(storageUsageProgress = ramInfo.totalRam.toFloat() , usedStorage = ramInfo.usedRam , freeStorage = ramInfo.availableRam))
        SmallVerticalSpacer()
        StorageInfoText(label = stringResource(id = R.string.used_ram) , size = ramInfo.usedRam)
        StorageInfoText(label = stringResource(id = R.string.free_ram) , size = ramInfo.availableRam)
        StorageInfoText(label = stringResource(id = R.string.total_ram) , size = ramInfo.totalRam)
    }
}