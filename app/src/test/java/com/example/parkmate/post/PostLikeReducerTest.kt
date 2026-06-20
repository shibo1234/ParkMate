package com.example.parkmate.post

import com.example.parkmate.data.model.Post
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PostLikeReducerTest {
    private val posts = listOf(
        Post(id = "post-1", likeCount = 4),
        Post(id = "post-2", likeCount = 0)
    )

    @Test
    fun toggle_onUnlikedPost_likesAndIncrementsCount() {
        val result = PostLikeReducer.toggle(posts, emptySet(), "post-1")

        assertTrue(result.nowLiked)
        assertTrue("post-1" in result.likedPostIds)
        assertEquals(5, result.posts.first { it.id == "post-1" }.likeCount)
    }

    @Test
    fun toggle_onLikedPost_unlikesAndDecrementsCount() {
        val result = PostLikeReducer.toggle(posts, setOf("post-1"), "post-1")

        assertFalse(result.nowLiked)
        assertFalse("post-1" in result.likedPostIds)
        assertEquals(3, result.posts.first { it.id == "post-1" }.likeCount)
    }

    @Test
    fun toggle_neverDrivesCountBelowZero() {
        val result = PostLikeReducer.toggle(posts, setOf("post-2"), "post-2")

        assertEquals(0, result.posts.first { it.id == "post-2" }.likeCount)
    }

    @Test
    fun toggle_leavesOtherPostsUntouched() {
        val result = PostLikeReducer.toggle(posts, emptySet(), "post-1")

        assertEquals(0, result.posts.first { it.id == "post-2" }.likeCount)
    }

    @Test
    fun toggle_ignoresUnknownOrBlankPostId() {
        val unknown = PostLikeReducer.toggle(posts, setOf("post-1"), "missing")
        assertEquals(setOf("post-1"), unknown.likedPostIds)
        assertEquals(posts, unknown.posts)

        val blank = PostLikeReducer.toggle(posts, emptySet(), "")
        assertEquals(emptySet<String>(), blank.likedPostIds)
        assertEquals(posts, blank.posts)
    }

    @Test
    fun isLiked_reflectsMembership() {
        assertTrue(PostLikeReducer.isLiked(setOf("post-1"), "post-1"))
        assertFalse(PostLikeReducer.isLiked(setOf("post-1"), "post-2"))
    }
}
