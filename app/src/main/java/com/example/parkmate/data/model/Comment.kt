package com.example.parkmate.data.model

/**
 * What: A single comment on a post (posts/{id}/comments/{commentId})
 * Who:  Written/read by FirebasePostRepository's comment APIs; shown in CommunityScreen
 * When: Created when a user sends a comment
 */
data class Comment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAtMillis: Long = 0L
)
