package com.example.parkmate.post

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PostFormValidatorTest {
    @Test
    fun validateCreatePostRequiresSignedInUser() {
        val result = PostFormValidator.validateCreatePost(
            userId = "",
            caption = "Great view"
        )

        assertEquals("Please sign in before creating a post.", result)
    }

    @Test
    fun validateCreatePostRequiresCaption() {
        val result = PostFormValidator.validateCreatePost(
            userId = "user-1",
            caption = "   "
        )

        assertEquals("Add a caption before posting.", result)
    }

    @Test
    fun validateCreatePostAcceptsSignedInUserAndCaption() {
        val result = PostFormValidator.validateCreatePost(
            userId = "user-1",
            caption = "Sunrise at Tunnel View"
        )

        assertNull(result)
    }

    @Test
    fun validateCommentRequiresSignedInUser() {
        val result = PostFormValidator.validateComment(userId = "", text = "Nice shot")

        assertEquals("Please sign in before commenting.", result)
    }

    @Test
    fun validateCommentRequiresText() {
        val result = PostFormValidator.validateComment(userId = "user-1", text = "   ")

        assertEquals("Write a comment before sending.", result)
    }

    @Test
    fun validateCommentAcceptsSignedInUserAndText() {
        val result = PostFormValidator.validateComment(userId = "user-1", text = "Great tip!")

        assertNull(result)
    }
}
