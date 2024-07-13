package com.VegaSolutions.lpptransit.neoui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.VegaSolutions.lpptransit.R
import com.VegaSolutions.lpptransit.neoui.ui.theme.Typography

@Preview
@Composable
fun HomeScreen(navController: NavController = rememberNavController()) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(32.dp)
    ) {

        // Header
        item {
            Text(
                text = stringResource(R.string.home),
                style = Typography.titleLarge
            )
        }

        // Hints

        // Station info

    }
}