package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.cleaner.R

@Composable
fun WeeklyCleanStreakCard(
    streakDays: Int,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
) {
    val reward = when (streakDays) {
        1 -> stringResource(id = R.string.streak_reward_day1)
        3 -> stringResource(id = R.string.streak_reward_day3)
        5 -> stringResource(id = R.string.streak_reward_day5)
        7 -> stringResource(id = R.string.streak_reward_day7)
        else -> null
    }

    val message = when {
        streakDays == 0 -> stringResource(id = R.string.clean_streak_start)
        streakDays == 1 -> stringResource(id = R.string.clean_streak_day1)
        streakDays in 2..3 -> stringResource(id = R.string.clean_streak_day2_3, streakDays)
        streakDays == 6 -> stringResource(id = R.string.clean_streak_almost_week)
        streakDays >= 7 && streakDays < 10 -> stringResource(id = R.string.clean_streak_week)
        streakDays >= 10 -> stringResource(id = R.string.clean_streak_long_format, streakDays)
        else -> stringResource(id = R.string.clean_streak_day2_3, streakDays)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.clean_streak_title),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(modifier = Modifier.bounceClick(), onClick = onDismiss) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
                }
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize)) {
                for (i in 1..7) {
                    val filled = streakDays >= i
                    Icon(
                        imageVector = if (filled) Icons.Rounded.LocalFireDepartment else Icons.Outlined.LocalFireDepartment,
                        contentDescription = null,
                        tint = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }
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
