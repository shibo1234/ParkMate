package com.example.parkmate.post

import com.example.parkmate.data.model.Post

/**
 * Pure, side-effect-free logic for toggling a post's like state.
 *
 * Kept separate from [com.example.parkmate.viewmodel.PostViewModel] so the optimistic
 * like/unlike behaviour can be unit tested without Firebase or coroutines, matching the
 * project's existing validator-style tests.
 */
data class LikeToggleResult(
    val posts: List<Post>,
    val likedPostIds: Set<String>,
    val nowLiked: Boolean
)

object PostLikeReducer {
    fun isLiked(likedPostIds: Set<String>, postId: String): Boolean {
        return postId in likedPostIds
    }

    /**
     * Returns the optimistic state after the current user taps like/unlike on [postId].
     *
     * Liking adds the id and increments that post's [Post.likeCount]; unliking removes the id
     * and decrements the count (never below zero). Other posts are left untouched. The call is a
     * no-op when [postId] is blank or not present in [posts].
     */
    fun toggle(
        posts: List<Post>,
        likedPostIds: Set<String>,
        postId: String
    ): LikeToggleResult {
        if (postId.isBlank() || posts.none { it.id == postId }) {
            return LikeToggleResult(posts, likedPostIds, isLiked(likedPostIds, postId))
        }

        val nowLiked = postId !in likedPostIds
        val delta = if (nowLiked) 1 else -1
        val updatedPosts = posts.map { post ->
            if (post.id == postId) {
                post.copy(likeCount = (post.likeCount + delta).coerceAtLeast(0))
            } else {
                post
            }
        }
        val updatedLikedIds = if (nowLiked) likedPostIds + postId else likedPostIds - postId
        return LikeToggleResult(updatedPosts, updatedLikedIds, nowLiked)
    }
}
