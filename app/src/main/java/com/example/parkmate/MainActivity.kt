package com.example.parkmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.parkmate.data.repository.DisabledAuthRepository
import com.example.parkmate.data.repository.DisabledPostRepository
import com.example.parkmate.data.repository.DisabledSavedParkRepository
import com.example.parkmate.data.repository.FirebaseAuthRepository
import com.example.parkmate.data.repository.FirebasePostRepository
import com.example.parkmate.data.repository.FirebaseSavedParkRepository
import com.example.parkmate.ui.ParkMateApp
import com.example.parkmate.ui.theme.ParkMateTheme
import com.example.parkmate.viewmodel.AuthViewModel
import com.example.parkmate.viewmodel.AuthViewModelFactory
import com.example.parkmate.viewmodel.ParkViewModel
import com.example.parkmate.viewmodel.PostViewModel
import com.example.parkmate.viewmodel.PostViewModelFactory
import com.example.parkmate.viewmodel.ProfileViewModel
import com.example.parkmate.viewmodel.ProfileViewModelFactory
import com.google.firebase.FirebaseApp

/**
 * What: App entry point. Builds the Auth/Post/Profile ViewModels (choosing Firebase or
 *       Disabled repositories based on whether Firebase initialized) and hosts the Compose UI
 * Who:  Launched by the OS; wires repositories into ViewModels via factories
 * When: Created once at process start (cold launch)
 */
class MainActivity : ComponentActivity() {
    private val parkViewModel: ParkViewModel by viewModels()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var postViewModel: PostViewModel
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(createAuthRepository())
        )[AuthViewModel::class.java]
        postViewModel = ViewModelProvider(
            this,
            PostViewModelFactory(createPostRepository())
        )[PostViewModel::class.java]
        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(createSavedParkRepository())
        )[ProfileViewModel::class.java]
        setContent {
            ParkMateTheme {
                ParkMateApp(
                    parkViewModel = parkViewModel,
                    authViewModel = authViewModel,
                    postViewModel = postViewModel,
                    profileViewModel = profileViewModel
                )
            }
        }
    }

    /** What/Who/When: True when a valid google-services.json initialized Firebase; selects real vs Disabled repos. */
    private val firebaseReady: Boolean
        get() = FirebaseApp.getApps(this).isNotEmpty()

    private fun createAuthRepository() = if (firebaseReady) {
        FirebaseAuthRepository()
    } else {
        DisabledAuthRepository()
    }

    private fun createPostRepository() = if (firebaseReady) {
        FirebasePostRepository()
    } else {
        DisabledPostRepository()
    }

    private fun createSavedParkRepository() = if (firebaseReady) {
        FirebaseSavedParkRepository()
    } else {
        DisabledSavedParkRepository()
    }
}
