package com.example.parkmate.data.model

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val parkId: String = "",
    val attractionId: String? = null,
    val imageUrl: String = "",
    val caption: String = "",
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val createdAtMillis: Long = 0L
)
