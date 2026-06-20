package com.example.parkmate.post

object PostFormValidator {
    fun validateCreatePost(userId: String, caption: String): String? {
        return when {
            userId.isBlank() -> "Please sign in before creating a post."
            caption.isBlank() -> "Add a caption before posting."
            else -> null
        }
    }

    fun validateComment(userId: String, text: String): String? {
        return when {
            userId.isBlank() -> "Please sign in before commenting."
            text.isBlank() -> "Write a comment before sending."
            else -> null
        }
    }
}
