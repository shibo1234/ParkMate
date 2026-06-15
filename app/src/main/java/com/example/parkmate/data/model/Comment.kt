package com.example.parkmate.data.model

data class Comment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAtMillis: Long = 0L
)
