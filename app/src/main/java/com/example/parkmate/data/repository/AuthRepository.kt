package com.example.parkmate.data.repository

import com.example.parkmate.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val registration = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(UserProfile::class.java))
            }
        awaitClose { registration.remove() }
    }

    suspend fun signUp(displayName: String, email: String, password: String): Result<FirebaseUser> =
        runCatching {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user!!
            val profile = mapOf(
                "id" to user.uid,
                "displayName" to displayName,
                "email" to email,
                "photoUrl" to null,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(user.uid).set(profile).await()
            user
        }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> =
        runCatching {
            auth.signInWithEmailAndPassword(email, password).await().user!!
        }

    fun signOut() = auth.signOut()

}