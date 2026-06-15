package com.example.parkmate.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parkmate.ui.screens.AttractionDetailScreen
import com.example.parkmate.ui.screens.CommunityScreen
import com.example.parkmate.ui.screens.HomeScreen
import com.example.parkmate.ui.screens.LoginScreen
import com.example.parkmate.ui.screens.ParkDetailScreen
import com.example.parkmate.ui.screens.ProfileScreen
import com.example.parkmate.ui.screens.UploadScreen
import com.example.parkmate.viewmodel.ParkViewModel

private object Routes {
    const val Login = "login"
    const val Home = "home"
    const val Community = "community"
    const val Profile = "profile"
    const val ParkDetail = "park_detail"
    const val AttractionDetail = "attraction_detail"
    const val Upload = "upload"
}

@Composable
fun ParkMateApp(parkViewModel: ParkViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.Home
    val showBottomBar = currentRoute in listOf(Routes.Home, Routes.Community, Routes.Profile)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ParkMateBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            popUpTo(Routes.Home)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Login,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Login) {
                LoginScreen(
                    onContinue = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.Home) {
                val state by parkViewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    state = state,
                    onSearchChange = parkViewModel::updateSearchQuery,
                    onParkClick = { parkId ->
                        parkViewModel.selectPark(parkId)
                        navController.navigate(Routes.ParkDetail)
                    }
                )
            }
            composable(Routes.ParkDetail) {
                val park by parkViewModel.selectedPark.collectAsStateWithLifecycle()
                ParkDetailScreen(
                    park = park,
                    onBack = { navController.popBackStack() },
                    onAttractionClick = { attractionId ->
                        parkViewModel.selectAttraction(attractionId)
                        navController.navigate(Routes.AttractionDetail)
                    }
                )
            }
            composable(Routes.AttractionDetail) {
                val attraction by parkViewModel.selectedAttraction.collectAsStateWithLifecycle()
                AttractionDetailScreen(
                    attraction = attraction,
                    onBack = { navController.popBackStack() },
                    onUploadClick = { navController.navigate(Routes.Upload) }
                )
            }
            composable(Routes.Upload) {
                UploadScreen(
                    onBack = { navController.popBackStack() },
                    onPostCreated = {
                        navController.navigate(Routes.Community) {
                            popUpTo(Routes.Home)
                        }
                    }
                )
            }
            composable(Routes.Community) {
                CommunityScreen()
            }
            composable(Routes.Profile) {
                ProfileScreen()
            }
        }
    }
}

@Composable
private fun ParkMateBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        listOf(
            Routes.Home to "Home",
            Routes.Community to "Community",
            Routes.Profile to "Profile"
        ).forEach { (route, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { onNavigate(route) },
                icon = { Text(label.first().toString()) },
                label = { Text(label) }
            )
        }
    }
}
