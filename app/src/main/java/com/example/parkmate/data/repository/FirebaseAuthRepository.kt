package com.example.parkmate.data.repository

import com.example.parkmate.data.model.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {
    override fun currentUser(): UserProfile? {
        val firebaseUser = auth.currentUser ?: return null
        return UserProfile(
            id = firebaseUser.uid,
            displayName = firebaseUser.displayName.orEmpty(),
            email = firebaseUser.email.orEmpty(),
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }

    override suspend fun signIn(email: String, password: String): Result<UserProfile> {
        return runCatching {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            authResult.toUserProfile()
        }
    }

    override suspend fun signUp(displayName: String, email: String, password: String): Result<UserProfile> {
        return runCatching {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.toUserProfile(displayName.trim())
            firestore.collection("users")
                .document(user.id)
                .set(
                    mapOf(
                        "displayName" to user.displayName,
                        "email" to user.email,
                        "photoUrl" to user.photoUrl,
                        "createdAtMillis" to System.currentTimeMillis()
                    )
                )
                .await()
            user
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    private fun AuthResult.toUserProfile(fallbackDisplayName: String = ""): UserProfile {
        val firebaseUser = user ?: error("Firebase did not return a user.")
        return UserProfile(
            id = firebaseUser.uid,
            displayName = firebaseUser.displayName.orEmpty().ifBlank { fallbackDisplayName },
            email = firebaseUser.email.orEmpty(),
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }
}

private suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
}
