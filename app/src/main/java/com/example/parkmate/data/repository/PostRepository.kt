package com.example.parkmate.data.repository

import android.net.Uri
import com.example.parkmate.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class PostRepository(
    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()
) {
    suspend fun createPost(
        authorId : String,
        authorName : String,
        parkId : String,
        parkName: String,
        attractionName : String?,
        imageUrl : Uri,
        caption : String
    ) : Result<Unit> = runCatching {
        val postId = firestore.collection("posts").document().id
        val postRef = storage.reference.child("post_images/$authorId/$postId.jpg")
        postRef.putFile(imageUrl).await()
        val imageUrl = postRef.downloadUrl.await().toString()
        val post = Post(
            id = postId,
            userId = authorId,
            userName = authorName,
            parkId = parkId,
            parkName = parkName,
            attractionName = attractionName,
            imageUrl = imageUrl,
            caption = caption
        )
        firestore.collection("posts").document(postId).set(post).await()
    }
}