package com.example.parkmate.data.repository

import android.net.Uri
import com.example.parkmate.data.model.UserProfile

interface AuthRepository {
    fun currentUser(): UserProfile?

    suspend fun signIn(email: String, password: String): Result<UserProfile>

    suspend fun signUp(displayName: String, email: String, password: String): Result<UserProfile>

    /** Uploads a new profile photo for [userId] and returns its download URL. */
    suspend fun updateProfilePhoto(userId: String, photoUri: Uri): Result<String>

    fun signOut()
}
