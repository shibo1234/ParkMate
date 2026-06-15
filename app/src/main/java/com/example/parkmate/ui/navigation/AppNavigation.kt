package com.example.parkmate.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.parkmate.ui.screens.LoginScreen
import com.example.parkmate.ui.screens.HomeScreen
import com.example.parkmate.ui.screens.ParkDetailScreen
import com.example.parkmate.ui.screens.AttractionDetailScreen
import com.example.parkmate.ui.screens.CommunityScreen
import com.example.parkmate.ui.screens.UploadScreen
import com.example.parkmate.ui.screens.ProfileScreen
import com.example.parkmate.viewmodel.ParkViewModel

@Composable
fun AppNavigation(viewModel: ParkViewModel = viewModel()) {

    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedPark by viewModel.selectedPark.collectAsStateWithLifecycle()
    val selectedAttraction by viewModel.selectedAttraction.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN,
    ) {

        composable(Destinations.LOGIN) {
            LoginScreen {
                navController.navigate(Destinations.HOME)
            }
        }

        composable(Destinations.HOME) {
            HomeScreen(
                state = uiState,
                onSearchChange = { viewModel.updateSearchQuery(it) },
                onParkClick = { parkId ->
                    viewModel.selectPark(parkId)
                    navController.navigate(Destinations.PARK_DETAIL)
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE)
                }
            )
        }

        composable(Destinations.PARK_DETAIL) {
            ParkDetailScreen(
                park = selectedPark,
                onBack = {
                    navController.popBackStack()
                },
                onAttractionClick = { attractionId ->
                    viewModel.selectAttraction(attractionId)
                    navController.navigate(Destinations.ATTRACTION_DETAIL)
                },
                onCommunityClick = {
                    navController.navigate(Destinations.COMMUNITY)
                }
            )
        }

        composable(Destinations.ATTRACTION_DETAIL) {
            AttractionDetailScreen(
                attraction = selectedAttraction,
                onBack = {
                    navController.popBackStack()
                },
                onUploadClick = {
                    navController.navigate(Destinations.UPLOAD)
                }
            )
        }

        composable(Destinations.COMMUNITY) {
            CommunityScreen(
                onBack = {
                    navController.popBackStack()
                },
                onUploadClick = {
                    navController.navigate(Destinations.UPLOAD)
                }
            )
        }

        composable(Destinations.UPLOAD) {
            UploadScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPostCreated = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.PROFILE) {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
