package com.example.parkmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.data.model.UserProfile
import com.example.parkmate.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthWebException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Loading : AuthState()
    data object SignedOut : AuthState()
    data class SignedIn(val uid : String, val email : String?) : AuthState()
}

class AuthViewModel(
    private val repo : AuthRepository = AuthRepository()
) : ViewModel() {

    val authState : StateFlow<AuthState> = repo.observeAuthState()
        .map { user ->
            if (user == null) AuthState.SignedOut
            else AuthState.SignedIn(user.uid, user.email)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading)

    private val _error = MutableStateFlow<String?>(null)
    val error : StateFlow<String?> = _error.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting : StateFlow<Boolean> = _isSubmitting.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile : StateFlow<UserProfile?> = repo.observeAuthState()
        .flatMapLatest { user ->
            if (user == null) flowOf(null)
            else repo.observeUserProfile(user.uid)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun clearError() {
        _error.value = null
    }

    fun signIn(email : String, password : String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Please enter email and password."
            return
        }
        launchAuth {
            repo.signIn(email, password)
        }
    }

    fun signUp(displayName : String, email : String, password : String) {
        when {
            displayName.isBlank() -> { _error.value = "Please enter a display name."; return }
            email.isBlank() || password.isBlank() -> { _error.value = "Please enter email and password."; return }
            password.length < 6 -> { _error.value = "Password must be at least 6 characters."; return }
        }
        launchAuth {
            repo.signUp(displayName.trim(), email.trim(), password)
        }
    }

    fun signOut() {
        repo.signOut()
    }

    private fun launchAuth(block : suspend () -> Result<*>) {
        _error.value = null
        viewModelScope.launch {
            _isSubmitting.value = true
            block().onFailure {
                _error.value = friendlierMessage(it)
            }
            _isSubmitting.value = false
        }
    }

    private fun friendlierMessage(t : Throwable) : String = when (t) {
        is FirebaseAuthWeakPasswordException -> "Password must be at least 6 characters."
        is FirebaseAuthInvalidUserException -> "No account with this email."
        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
        is FirebaseAuthUserCollisionException -> "An account with this email already exists."
        else -> t.localizedMessage ?: "Something went wrong. Please try again later."
    }
}