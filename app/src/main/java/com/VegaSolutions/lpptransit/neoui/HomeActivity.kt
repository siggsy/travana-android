package com.VegaSolutions.lpptransit.neoui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.VegaSolutions.lpptransit.R
import com.VegaSolutions.lpptransit.TravanaApp
import com.VegaSolutions.lpptransit.data.TravanaRepository
import com.VegaSolutions.lpptransit.neoui.screens.home.HomeScreen
import com.VegaSolutions.lpptransit.neoui.screens.SettingsScreen
import com.VegaSolutions.lpptransit.neoui.screens.home.HomeViewModel
import com.VegaSolutions.lpptransit.neoui.ui.theme.LppTransitTheme

sealed class Place(
    val route: String,
    @StringRes val label: Int,
    val icon: @Composable () -> Unit,
    val content: @Composable (NavController, TravanaRepository) -> Unit,
) {
    data object Home : Place(
        "home",
        R.string.title_activity_home,
        { Icon(Icons.Default.Home, "Home button") },
        { nav, repo -> HomeScreen(nav, HomeViewModel(repo)) },
    )

    data object Search : Place(
        "search",
        R.string.search,
        { Icon(Icons.Default.Search, "Search button") },
        { nav, repo -> SettingsScreen(nav) },
    )

    data object Settings : Place(
        "settings",
        R.string.settings,
        { Icon(Icons.Default.Settings, "Settings button") },
        { nav, repo -> SettingsScreen(nav) },
    )
}

val places = listOf(
    Place.Home,
    Place.Search,
    Place.Settings,
)

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = (application as TravanaApp).repository

        setContent {
            LppTransitTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            places.forEach { place ->
                                NavigationBarItem(
                                    label = { Text(stringResource(id = place.label)) },
                                    selected = currentDestination?.hierarchy?.any { it.route == place.route } ?: false,
                                    onClick = {
                                        navController.navigate(place.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = place.icon,
                                )
                            }
                        }
                    },

                    content = { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Place.Home.route,
                            modifier = Modifier.padding(innerPadding),
                        ) {
                            places.forEach { place ->
                                composable(place.route) { place.content(navController, repo) }
                            }
                        }
                    }
                )
            }
        }
    }
}

