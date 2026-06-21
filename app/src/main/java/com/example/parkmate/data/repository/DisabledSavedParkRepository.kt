package com.example.parkmate.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * What: No-op SavedParkRepository used when Firebase isn't configured; returns empty / fails to save
 * Who:  Selected by MainActivity when FirebaseApp didn't initialize
 * When: Active only when google-services.json is missing/invalid
 */
class DisabledSavedParkRepository : SavedParkRepository {
    override fun observeSavedParkIds(userId: String): Flow<Result<Set<String>>> {
        return flowOf(Result.success(emptySet()))
    }

    override suspend fun setParkSaved(
        userId: String,
        parkId: String,
        saved: Boolean
    ): Result<Unit> {
        return Result.failure(IllegalStateException(SETUP_MESSAGE))
    }

    private companion object {
        const val SETUP_MESSAGE =
            "Firebase is not configured yet. Add app/google-services.json to save parks."
    }
}
