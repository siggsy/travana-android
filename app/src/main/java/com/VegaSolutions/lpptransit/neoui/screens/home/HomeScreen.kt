package com.VegaSolutions.lpptransit.neoui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.VegaSolutions.lpptransit.R
import com.VegaSolutions.lpptransit.data.TravanaRepository
import com.VegaSolutions.lpptransit.neoui.components.Station
import com.VegaSolutions.lpptransit.neoui.ui.theme.Typography
import com.VegaSolutions.lpptransit.utility.Resource
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController = rememberNavController(),
    viewModel: HomeViewModel
) {
    viewModel.fetchFavouriteArrivals()
    val stations by viewModel.stations.observeAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {

        // Header
        item {
            Text(
                text = stringResource(R.string.home),
                style = Typography.titleLarge
            )
        }

        // Hints
        // TODO

        // Station info
        val res = stations
        when (res) {
            is Resource.Loading -> {}
            is Resource.Error -> {}
            is Resource.Success -> {
                items(res.data) { station ->
                    Station(station)
                }
            }

            null -> {}
        }
        item {
            Station()
        }
    }
}