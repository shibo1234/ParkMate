package com.example.parkmate.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parkmate.ui.navigation.Destinations
import com.example.parkmate.ui.screens.AttractionDetailScreen
import com.example.parkmate.ui.screens.CommunityScreen
import com.example.parkmate.ui.screens.HomeScreen
import com.example.parkmate.ui.screens.LoginScreen
import com.example.parkmate.ui.screens.ParkDetailScreen
import com.example.parkmate.ui.screens.ProfileScreen
import com.example.parkmate.ui.screens.UploadScreen
import com.example.parkmate.viewmodel.AuthState
import com.example.parkmate.viewmodel.AuthViewModel
import com.example.parkmate.viewmodel.ParkViewModel
import com.example.parkmate.viewmodel.UploadViewModel

@Composable
fun ParkMateApp(parkViewModel: ParkViewModel, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Destinations.LOGIN
    val showBottomBar = currentRoute in listOf(Destinations.HOME, Destinations.COMMUNITY, Destinations.PROFILE)

    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    // Auth gate: route to Home once signed in, back to Log-in on sign-out.
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.SignedIn ->
                if (currentRoute == Destinations.LOGIN) {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                }
            AuthState.SignedOut ->
                if (currentRoute != Destinations.LOGIN) {
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            AuthState.Loading -> Unit
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ParkMateBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            popUpTo(Destinations.HOME)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.LOGIN) {
                val isSubmitting by authViewModel.isSubmitting.collectAsStateWithLifecycle()
                val errorMessage by authViewModel.error.collectAsStateWithLifecycle()
                LoginScreen(
                    isSubmitting = isSubmitting,
                    errorMessage = errorMessage,
                    onLogin = authViewModel::signIn,
                    onSignUp = authViewModel::signUp
                )
            }

            composable(Destinations.HOME) {
                val state by parkViewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    state = state,
                    onSearchChange = parkViewModel::updateSearchQuery,
                    onParkClick = { parkId ->
                        parkViewModel.selectPark(parkId)
                        navController.navigate(Destinations.PARK_DETAIL)
                    },
                    onProfileClick = { navController.navigate(Destinations.PROFILE) }
                )
            }

            composable(Destinations.PARK_DETAIL) {
                val park by parkViewModel.selectedPark.collectAsStateWithLifecycle()
                ParkDetailScreen(
                    park = park,
                    onBack = { navController.popBackStack() },
                    onAttractionClick = { attractionId ->
                        parkViewModel.selectAttraction(attractionId)
                        navController.navigate(Destinations.ATTRACTION_DETAIL)
                    },
                    onCommunityClick = { navController.navigate(Destinations.COMMUNITY) }
                )
            }

            composable(Destinations.ATTRACTION_DETAIL) {
                val attraction by parkViewModel.selectedAttraction.collectAsStateWithLifecycle()
                AttractionDetailScreen(
                    attraction = attraction,
                    onBack = { navController.popBackStack() },
                    onUploadClick = { navController.navigate(Destinations.UPLOAD) }
                )
            }

            composable(Destinations.UPLOAD) {
                val uploadViewModel: UploadViewModel = viewModel()
                val park by parkViewModel.selectedPark.collectAsStateWithLifecycle()
                val attraction by parkViewModel.selectedAttraction.collectAsStateWithLifecycle()
                val profile by authViewModel.userProfile.collectAsStateWithLifecycle()
                val isSubmitting by uploadViewModel.isSubmitting.collectAsStateWithLifecycle()
                val errorMessage by uploadViewModel.error.collectAsStateWithLifecycle()
                val success by uploadViewModel.success.collectAsStateWithLifecycle()

                LaunchedEffect(success) {
                    if (success) {
                        uploadViewModel.consumeSuccess()
                        navController.navigate(Destinations.COMMUNITY) { popUpTo(Destinations.HOME) }
                    }
                }

                UploadScreen(
                    parkName = park?.name,
                    attractionName = attraction?.name,
                    isSubmitting = isSubmitting,
                    errorMessage = errorMessage,
                    onBack = { navController.popBackStack() },
                    onSubmit = { caption, imageUri ->
                        uploadViewModel.createPost(
                            authorId = profile?.id.orEmpty(),
                            authorName = profile?.displayName.orEmpty().ifBlank { "Traveler" },
                            parkId = park?.id.orEmpty(),
                            parkName = park?.name.orEmpty(),
                            attractionName = attraction?.name,
                            caption = caption,
                            imageUri = imageUri
                        )
                    }
                )
            }

            composable(Destinations.COMMUNITY) {
                CommunityScreen(
                    onBack = { navController.popBackStack() },
                    onUploadClick = { navController.navigate(Destinations.UPLOAD) }
                )
            }

            composable(Destinations.PROFILE) {
                val profile by authViewModel.userProfile.collectAsStateWithLifecycle()
                ProfileScreen(
                    user = profile,
                    onBack = { navController.popBackStack() },
                    onLogout = authViewModel::signOut
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
            Destinations.HOME to "Home",
            Destinations.COMMUNITY to "Community",
            Destinations.PROFILE to "Profile"
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
