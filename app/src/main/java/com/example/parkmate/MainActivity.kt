package com.example.parkmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.parkmate.ui.ParkMateApp
import com.example.parkmate.ui.theme.ParkMateTheme
import com.example.parkmate.viewmodel.AuthViewModel
import com.example.parkmate.viewmodel.ParkViewModel

class MainActivity : ComponentActivity() {
    private val parkViewModel: ParkViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParkMateTheme {
                ParkMateApp(parkViewModel = parkViewModel, authViewModel = authViewModel)
            }
        }
    }
}
