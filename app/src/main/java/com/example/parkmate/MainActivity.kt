package com.example.parkmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.parkmate.data.repository.DisabledAuthRepository
import com.example.parkmate.data.repository.FirebaseAuthRepository
import com.example.parkmate.ui.ParkMateApp
import com.example.parkmate.ui.theme.ParkMateTheme
import com.example.parkmate.viewmodel.AuthViewModel
import com.example.parkmate.viewmodel.AuthViewModelFactory
import com.example.parkmate.viewmodel.ParkViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private val parkViewModel: ParkViewModel by viewModels()
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(createAuthRepository())
        )[AuthViewModel::class.java]
        setContent {
            ParkMateTheme {
                ParkMateApp(
                    parkViewModel = parkViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }

    private fun createAuthRepository() = if (FirebaseApp.getApps(this).isNotEmpty()) {
        FirebaseAuthRepository()
    } else {
        DisabledAuthRepository()
    }
}
