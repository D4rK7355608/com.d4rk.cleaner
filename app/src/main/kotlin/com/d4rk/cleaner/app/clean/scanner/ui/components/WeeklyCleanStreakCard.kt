package com.d4rk.cleaner.app.clean.scanner.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
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

    val message = when (streakDays) {
        0 -> stringResource(id = R.string.clean_streak_start)
        in 1..6 -> stringResource(id = R.string.clean_streak_in_progress, streakDays)
        else -> stringResource(id = R.string.clean_streak_perfect_week_message)
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
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.clean_streak_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = onDismiss
                ) {
                    Icon(
                        modifier = Modifier.size(SizeConstants.ButtonIconSize),
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null
                    )
                }
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 1..7) {
                    val filled = streakDays >= i
                    val scale = animateFloatAsState(
                        targetValue = if (filled) 1.4f else 1f,
                        animationSpec = tween(durationMillis = 300),
                        label = "StreakDotScale$i"
                    ).value
                    Icon(
                        imageVector = if (filled) Icons.Rounded.AutoAwesome else Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                    )
                }
            }
            if (streakDays >= 7) {
                Text(
                    text = stringResource(id = R.string.clean_streak_perfect_week_message),
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
