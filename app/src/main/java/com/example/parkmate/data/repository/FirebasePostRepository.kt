package com.example.parkmate.data.repository

import android.net.Uri
import com.example.parkmate.data.model.Comment
import com.example.parkmate.data.model.Post
import com.example.parkmate.data.model.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebasePostRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : PostRepository {
    override fun observePosts(): Flow<Result<List<Post>>> = callbackFlow {
        val listener = firestore.collection(POSTS_COLLECTION)
            .orderBy("createdAtMillis", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents.orEmpty().map { document ->
                    Post(
                        id = document.id,
                        userId = document.getString("userId").orEmpty(),
                        userName = document.getString("userName").orEmpty(),
                        parkId = document.getString("parkId").orEmpty(),
                        attractionId = document.getString("attractionId"),
                        imageUrl = document.getString("imageUrl").orEmpty(),
                        caption = document.getString("caption").orEmpty(),
                        likeCount = document.getLong("likeCount")?.toInt() ?: 0,
                        commentCount = document.getLong("commentCount")?.toInt() ?: 0,
                        createdAtMillis = document.getLong("createdAtMillis") ?: 0L
                    )
                }
                trySend(Result.success(posts))
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createPost(
        user: UserProfile,
        caption: String,
        imageUri: Uri?,
        parkId: String,
        attractionId: String?
    ): Result<Unit> {
        return runCatching {
            val postDocument = firestore.collection(POSTS_COLLECTION).document()
            val imageUrl = imageUri?.let { uri ->
                val imageRef = storage.reference.child("post-images/${user.id}/${postDocument.id}")
                imageRef.putFile(uri).await()
                imageRef.downloadUrl.await().toString()
            }.orEmpty()

            postDocument
                .set(
                    mapOf(
                        "userId" to user.id,
                        "userName" to user.displayName.ifBlank { user.email },
                        "parkId" to parkId,
                        "attractionId" to attractionId,
                        "imageUrl" to imageUrl,
                        "caption" to caption.trim(),
                        "likeCount" to 0,
                        "commentCount" to 0,
                        "createdAtMillis" to System.currentTimeMillis()
                    )
                )
                .await()
            Unit
        }
    }

    override suspend fun getLikedPostIds(
        postIds: List<String>,
        userId: String
    ): Result<Set<String>> {
        if (userId.isBlank() || postIds.isEmpty()) {
            return Result.success(emptySet())
        }

        // Read each post's likes/{userId} document directly. These are point reads on a known
        // path, so they need no Firestore index (unlike a collectionGroup query on userId).
        return runCatching {
            coroutineScope {
                postIds.distinct().map { postId ->
                    async {
                        val likeDoc = firestore.collection(POSTS_COLLECTION)
                            .document(postId)
                            .collection(LIKES_COLLECTION)
                            .document(userId)
                            .get()
                            .await()
                        if (likeDoc.exists()) postId else null
                    }
                }.awaitAll().filterNotNull().toSet()
            }
        }
    }

    override suspend fun setPostLiked(
        postId: String,
        userId: String,
        liked: Boolean
    ): Result<Unit> {
        if (postId.isBlank() || userId.isBlank()) {
            return Result.failure(IllegalArgumentException("postId and userId are required to like a post."))
        }

        return runCatching {
            val postRef = firestore.collection(POSTS_COLLECTION).document(postId)
            val likeRef = postRef.collection(LIKES_COLLECTION).document(userId)

            firestore.runTransaction { transaction ->
                val alreadyLiked = transaction.get(likeRef).exists()
                when {
                    liked && !alreadyLiked -> {
                        transaction.set(
                            likeRef,
                            mapOf(
                                "userId" to userId,
                                "createdAtMillis" to System.currentTimeMillis()
                            )
                        )
                        transaction.update(postRef, "likeCount", FieldValue.increment(1))
                    }
                    !liked && alreadyLiked -> {
                        transaction.delete(likeRef)
                        transaction.update(postRef, "likeCount", FieldValue.increment(-1))
                    }
                }
                Unit
            }.await()
            Unit
        }
    }

    override fun observeComments(postId: String): Flow<Result<List<Comment>>> = callbackFlow {
        if (postId.isBlank()) {
            trySend(Result.success(emptyList()))
            awaitClose { }
            return@callbackFlow
        }

        val listener = firestore.collection(POSTS_COLLECTION)
            .document(postId)
            .collection(COMMENTS_COLLECTION)
            .orderBy("createdAtMillis", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val comments = snapshot?.documents.orEmpty().map { document ->
                    Comment(
                        id = document.id,
                        userId = document.getString("userId").orEmpty(),
                        userName = document.getString("userName").orEmpty(),
                        text = document.getString("text").orEmpty(),
                        createdAtMillis = document.getLong("createdAtMillis") ?: 0L
                    )
                }
                trySend(Result.success(comments))
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addComment(
        postId: String,
        user: UserProfile,
        text: String
    ): Result<Unit> {
        val trimmed = text.trim()
        if (postId.isBlank() || user.id.isBlank() || trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("A signed-in user and comment text are required."))
        }

        return runCatching {
            val postRef = firestore.collection(POSTS_COLLECTION).document(postId)
            val commentRef = postRef.collection(COMMENTS_COLLECTION).document()

            firestore.runTransaction { transaction ->
                transaction.set(
                    commentRef,
                    mapOf(
                        "userId" to user.id,
                        "userName" to user.displayName.ifBlank { user.email },
                        "text" to trimmed,
                        "createdAtMillis" to System.currentTimeMillis()
                    )
                )
                transaction.update(postRef, "commentCount", FieldValue.increment(1))
                Unit
            }.await()
            Unit
        }
    }

    private companion object {
        const val POSTS_COLLECTION = "posts"
        const val LIKES_COLLECTION = "likes"
        const val COMMENTS_COLLECTION = "comments"
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
