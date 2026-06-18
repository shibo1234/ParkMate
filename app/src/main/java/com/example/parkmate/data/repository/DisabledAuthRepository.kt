package com.example.parkmate.data.repository

import com.example.parkmate.data.model.UserProfile

class DisabledAuthRepository : AuthRepository {
    override fun currentUser(): UserProfile? = null

    override suspend fun signIn(email: String, password: String): Result<UserProfile> {
        return Result.failure(IllegalStateException(FIREBASE_NOT_CONFIGURED))
    }

    override suspend fun signUp(displayName: String, email: String, password: String): Result<UserProfile> {
        return Result.failure(IllegalStateException(FIREBASE_NOT_CONFIGURED))
    }

    override fun signOut() = Unit

    private companion object {
        const val FIREBASE_NOT_CONFIGURED =
            "Firebase is not configured yet. Add app/google-services.json and enable the Google Services plugin."
    }
}
