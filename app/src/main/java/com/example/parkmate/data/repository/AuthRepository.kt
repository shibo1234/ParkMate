package com.example.parkmate.data.repository

import android.net.Uri
import com.example.parkmate.data.model.UserProfile

/**
 * What: Contract for authentication and the user's profile document
 * Who:  Implemented by FirebaseAuthRepository (real) and DisabledAuthRepository (fallback); used by AuthViewModel
 * When: Called on sign-in, sign-up, logout, and profile-photo updates
 */
interface AuthRepository {
    fun currentUser(): UserProfile?

    suspend fun signIn(email: String, password: String): Result<UserProfile>

    suspend fun signUp(displayName: String, email: String, password: String): Result<UserProfile>

    /** Uploads a new profile photo for [userId] and returns its download URL. */
    suspend fun updateProfilePhoto(userId: String, photoUri: Uri): Result<String>

    fun signOut()
}
