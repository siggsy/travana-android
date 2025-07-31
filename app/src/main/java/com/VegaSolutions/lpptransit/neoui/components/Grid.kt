package com.VegaSolutions.lpptransit.neoui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.math.sign

@Composable
fun Grid(
    columns: Int,
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    Column(modifier = modifier) {
        val rows = count / columns + (count % columns).sign
        var id = 0
        for (rowId in 0 ..< rows) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              for (colId in 0 ..< columns) {
                  Box(
                      modifier = Modifier
                          .fillMaxWidth()
                          .weight(1f)
                  ) {
                      if (id < count) {
                          content(id)
                      }
                  }
                  id += 1
              }
            }
        }
    }
}