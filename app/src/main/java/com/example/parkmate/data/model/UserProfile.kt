package com.example.parkmate.data.model

/**
 * What: The app's view of a signed-in user (id, name, email, optional photo)
 * Who:  Produced by AuthRepository; consumed across the UI and stamped onto posts/comments
 * When: Built at sign-in and on profile reads
 */
data class UserProfile(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null
)
