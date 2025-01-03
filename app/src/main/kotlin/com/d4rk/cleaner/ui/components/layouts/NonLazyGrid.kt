package com.d4rk.cleaner.ui.components.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NonLazyGrid(
    columns: Int,
    itemCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    Column(modifier = modifier) {
        val rows = (itemCount + columns - 1) / columns

        (0 until rows).forEach { row ->
            Row {
                (0 until columns).forEachIndexed { col, _ ->
                    val index = row * columns + col
                    if (index < itemCount) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                                .aspectRatio(ratio = 1f)
                        ) {
                            content(index)
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}