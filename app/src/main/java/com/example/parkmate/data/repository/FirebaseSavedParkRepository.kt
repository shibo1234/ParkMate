package com.example.parkmate.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * What: Firebase-backed saved parks at users/{uid}/savedParks; streams ids and toggles save state
 * Who:  Selected by MainActivity when Firebase is configured; used by ProfileViewModel
 * When: Active whenever Firebase is configured
 */
class FirebaseSavedParkRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : SavedParkRepository {
    override fun observeSavedParkIds(userId: String): Flow<Result<Set<String>>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(Result.success(emptySet()))
            awaitClose { }
            return@callbackFlow
        }

        // A plain subcollection query on a known path, so no Firestore index is required.
        val listener = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(SAVED_PARKS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val savedParkIds = snapshot?.documents.orEmpty().map { it.id }.toSet()
                trySend(Result.success(savedParkIds))
            }

        awaitClose { listener.remove() }
    }

    override suspend fun setParkSaved(
        userId: String,
        parkId: String,
        saved: Boolean
    ): Result<Unit> {
        if (userId.isBlank() || parkId.isBlank()) {
            return Result.failure(IllegalArgumentException("A signed-in user and park are required."))
        }

        return runCatching {
            val docRef = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SAVED_PARKS_COLLECTION)
                .document(parkId)

            if (saved) {
                docRef.set(
                    mapOf(
                        "parkId" to parkId,
                        "savedAtMillis" to System.currentTimeMillis()
                    )
                ).await()
            } else {
                docRef.delete().await()
            }
            Unit
        }
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val SAVED_PARKS_COLLECTION = "savedParks"
    }
}

private suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result -> continuation.resume(result) }
        addOnFailureListener { exception -> continuation.resumeWithException(exception) }
        addOnCanceledListener { continuation.cancel() }
    }
}
