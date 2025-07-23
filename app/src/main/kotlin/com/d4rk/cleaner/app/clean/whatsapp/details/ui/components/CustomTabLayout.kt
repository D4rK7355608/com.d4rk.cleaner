package com.d4rk.cleaner.app.clean.whatsapp.details.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.ui.state.ToggleableState
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun CustomTabLayout(
    modifier: Modifier = Modifier,
    selectedItemIndex: Int,
    items: List<String>,
    filesPerTab: List<List<File>>,
    selectedFiles: List<File>,
    onTabSelected: (index: Int) -> Unit,
    onTabCheckedChange: (index: Int, checked: Boolean) -> Unit,
) {
    TabRow(
        modifier = modifier.fillMaxWidth(),
        selectedTabIndex = selectedItemIndex,
        indicator = { tabPositions ->
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedItemIndex]),
                shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
            )
        }
    ) {
        items.forEachIndexed { index, text ->
            val files = filesPerTab.getOrNull(index) ?: emptyList()
            val allSelected = files.isNotEmpty() && files.all { it in selectedFiles }
            val noneSelected = files.none { it in selectedFiles }
            val toggleState = when {
                allSelected -> ToggleableState.On
                noneSelected -> ToggleableState.Off
                else -> ToggleableState.Indeterminate
            }

            Tab(
                modifier = Modifier
                    .clip(RoundedCornerShape(50)),
                selected = selectedItemIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        TriStateCheckbox(
                            state = toggleState,
                            onClick = {
                                onTabCheckedChange(index, toggleState != ToggleableState.On)
                            },
                        )
                        Text(
                            modifier = Modifier.basicMarquee(),
                            text = text
                        )
                    }
                }
            )
        }
    }
}
