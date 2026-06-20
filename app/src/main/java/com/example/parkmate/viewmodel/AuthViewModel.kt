package com.example.parkmate.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.parkmate.auth.AuthFormValidator
import com.example.parkmate.data.model.UserProfile
import com.example.parkmate.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val currentUser: UserProfile? = null,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val errorMessage: String? = null
) {
    val isAuthenticated: Boolean
        get() = currentUser != null
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AuthUiState(currentUser = authRepository.currentUser())
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateDisplayName(displayName: String) {
        _uiState.update { it.copy(displayName = displayName, errorMessage = null) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun signIn() {
        val state = uiState.value
        val validationError = AuthFormValidator.validateLogin(state.email, state.password)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        authenticate {
            authRepository.signIn(state.email, state.password)
        }
    }

    fun signUp() {
        val state = uiState.value
        val validationError = AuthFormValidator.validateSignUp(
            displayName = state.displayName,
            email = state.email,
            password = state.password
        )
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        authenticate {
            authRepository.signUp(state.displayName, state.email, state.password)
        }
    }

    fun updateProfilePhoto(photoUri: Uri?) {
        val userId = uiState.value.currentUser?.id
        if (photoUri == null || userId.isNullOrBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true, errorMessage = null) }
            val result = authRepository.updateProfilePhoto(userId, photoUri)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { url ->
                        state.copy(
                            isUploadingPhoto = false,
                            currentUser = state.currentUser?.copy(photoUrl = url)
                        )
                    },
                    onFailure = { error ->
                        state.copy(
                            isUploadingPhoto = false,
                            errorMessage = error.message ?: "Photo upload failed."
                        )
                    }
                )
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState()
    }

    private fun authenticate(authCall: suspend () -> Result<UserProfile>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authCall()
            _uiState.update {
                result.fold(
                    onSuccess = { user ->
                        it.copy(currentUser = user, isLoading = false, errorMessage = null)
                    },
                    onFailure = { error ->
                        it.copy(isLoading = false, errorMessage = error.message ?: "Authentication failed.")
                    }
                )
            }
        }
    }
}

class AuthViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
