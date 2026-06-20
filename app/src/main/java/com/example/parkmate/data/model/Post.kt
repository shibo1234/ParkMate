package com.example.parkmate.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val parkId: String = "",
    val parkName: String = "",
    val attractionName: String? = null,
    val attractionId: String? = null,
    val imageUrl: String = "",
    val caption: String = "",
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val createdAtMillis: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
)
