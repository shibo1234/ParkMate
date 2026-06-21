package com.example.parkmate.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * What: Contract for a user's saved parks (users/{uid}/savedParks)
 * Who:  Implemented by FirebaseSavedParkRepository and DisabledSavedParkRepository; used by ProfileViewModel
 * When: Called when saving/unsaving a park or loading the profile
 */
interface SavedParkRepository {
    /** Emits the ids of parks the given user has saved, updating in real time. */
    fun observeSavedParkIds(userId: String): Flow<Result<Set<String>>>

    /** Saves ([saved] = true) or removes ([saved] = false) a park for [userId]. */
    suspend fun setParkSaved(userId: String, parkId: String, saved: Boolean): Result<Unit>
}
