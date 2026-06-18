package com.example.parkmate.data.repository

import com.example.parkmate.data.model.UserProfile

interface AuthRepository {
    fun currentUser(): UserProfile?

    suspend fun signIn(email: String, password: String): Result<UserProfile>

    suspend fun signUp(displayName: String, email: String, password: String): Result<UserProfile>

    fun signOut()
}
