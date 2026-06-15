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
    const val LOGIN = "login"
    const val HOME = "home"
    const val COMMUNITY = "community"
    const val PROFILE = "profile"
    const val PARK_DETAIL = "park_detail"
    const val ATTRACTION_DETAIL = "attraction_detail"
    const val UPLOAD = "upload"
}

@Composable
fun ParkMateApp(parkViewModel: ParkViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME
    val showBottomBar = currentRoute in listOf(Routes.HOME, Routes.COMMUNITY, Routes.PROFILE)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ParkMateBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onContinue = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HOME) {
                val state by parkViewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    state = state,
                    onSearchChange = parkViewModel::updateSearchQuery,
                    onParkClick = { parkId ->
                        parkViewModel.selectPark(parkId)
                        navController.navigate(Routes.PARK_DETAIL)
                    },
                    onProfileClick = {
                        navController.navigate(Routes.PROFILE)
                    }
                )
            }
            composable(Routes.PARK_DETAIL) {
                val park by parkViewModel.selectedPark.collectAsStateWithLifecycle()
                ParkDetailScreen(
                    park = park,
                    onBack = { navController.popBackStack() },
                    onAttractionClick = { attractionId ->
                        parkViewModel.selectAttraction(attractionId)
                        navController.navigate(Routes.ATTRACTION_DETAIL)
                    },
                    onCommunityClick = {
                        navController.navigate(Routes.COMMUNITY)
                    }
                )
            }
            composable(Routes.ATTRACTION_DETAIL) {
                val attraction by parkViewModel.selectedAttraction.collectAsStateWithLifecycle()
                AttractionDetailScreen(
                    attraction = attraction,
                    onBack = { navController.popBackStack() },
                    onUploadClick = { navController.navigate(Routes.UPLOAD) }
                )
            }
            composable(Routes.UPLOAD) {
                UploadScreen(
                    onBack = { navController.popBackStack() },
                    onPostCreated = {
                        navController.navigate(Routes.COMMUNITY) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }
            composable(Routes.COMMUNITY) {
                CommunityScreen(
                    onBack = { navController.popBackStack() },
                    onUploadClick = { navController.navigate(Routes.UPLOAD) }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onBack = { navController.popBackStack() }
                )
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
            Routes.HOME to "Home",
            Routes.COMMUNITY to "Community",
            Routes.PROFILE to "Profile"
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
