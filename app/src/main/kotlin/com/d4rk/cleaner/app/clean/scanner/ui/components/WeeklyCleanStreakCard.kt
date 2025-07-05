package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R

@Composable
fun WeeklyCleanStreakCard(
    streakDays: Int,
    modifier: Modifier = Modifier,
) {
    val reward = when (streakDays) {
        1 -> stringResource(id = R.string.streak_reward_day1)
        3 -> stringResource(id = R.string.streak_reward_day3)
        5 -> stringResource(id = R.string.streak_reward_day5)
        7 -> stringResource(id = R.string.streak_reward_day7)
        else -> null
    }

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SizeConstants.ExtraLargeSize),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SizeConstants.LargeSize),
            verticalArrangement = Arrangement.spacedBy(SizeConstants.MediumSize),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.clean_streak_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.clean_streak_days_format, streakDays),
                style = MaterialTheme.typography.bodySmall
            )
            if (streakDays >= 7) {
                Text(
                    text = stringResource(id = R.string.clean_streak_perfect_week),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else if (reward != null) {
                Text(
                    text = reward,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
