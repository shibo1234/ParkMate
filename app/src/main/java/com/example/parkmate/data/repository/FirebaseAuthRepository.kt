package com.example.parkmate.data.repository

import android.net.Uri
import com.example.parkmate.data.model.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
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
            val firebaseUser = authResult.user ?: error("Firebase did not return a user.")
            val uid = firebaseUser.uid

            val snapshot = firestore.collection("users").document(uid).get().await()
            val storedName = snapshot.getString("displayName").orEmpty()
            val authName = firebaseUser.displayName.orEmpty()
            val displayName = storedName.ifBlank { authName }

            if (authName.isBlank() && displayName.isNotBlank()) {
                firebaseUser.updateProfile(
                    userProfileChangeRequest { this.displayName = displayName }
                ).await()
            }
            if (storedName.isBlank() && displayName.isNotBlank()) {
                firestore.collection("users").document(uid)
                    .set(
                        mapOf("displayName" to displayName, "email" to firebaseUser.email.orEmpty()),
                        SetOptions.merge()
                    )
                    .await()
            } else {
                firestore.collection("users").document(uid)
                    .set(mapOf("email" to firebaseUser.email.orEmpty()), SetOptions.merge())
                    .await()
            }

            UserProfile(
                id = uid,
                displayName = displayName,
                email = firebaseUser.email.orEmpty(),
                photoUrl = snapshot.getString("photoUrl") ?: firebaseUser.photoUrl?.toString()
            )
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

    override suspend fun updateProfilePhoto(userId: String, photoUri: Uri): Result<String> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("A signed-in user is required."))
        }

        return runCatching {
            val photoRef = storage.reference.child("profile_images/$userId.jpg")
            photoRef.putFile(photoUri).await()
            val downloadUrl = photoRef.downloadUrl.await().toString()

            firestore.collection("users")
                .document(userId)
                .set(mapOf("photoUrl" to downloadUrl), SetOptions.merge())
                .await()

            auth.currentUser?.updateProfile(
                userProfileChangeRequest { this.photoUri = Uri.parse(downloadUrl) }
            )?.await()

            downloadUrl
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
