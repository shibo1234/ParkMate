package com.example.parkmate.post

/**
 * What: Validates the post and comment forms (signed-in user and non-empty text)
 * Who:  Called by PostViewModel before writing; covered by PostFormValidatorTest
 * When: On create-post and submit-comment
 */
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
