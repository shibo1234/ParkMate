package com.example.parkmate.data.repository

import android.net.Uri
import com.example.parkmate.data.model.Comment
import com.example.parkmate.data.model.Post
import com.example.parkmate.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun observePosts(): Flow<Result<List<Post>>>

    suspend fun createPost(
        user: UserProfile,
        caption: String,
        imageUri: Uri? = null,
        parkId: String = "yosemite"
    ): Result<Unit>

    /** Returns which of [postIds] the given user has liked. */
    suspend fun getLikedPostIds(postIds: List<String>, userId: String): Result<Set<String>>

    /** Likes ([liked] = true) or unlikes ([liked] = false) a post on behalf of [userId]. */
    suspend fun setPostLiked(postId: String, userId: String, liked: Boolean): Result<Unit>

    /** Emits the comments on a post, oldest first, updating in real time. */
    fun observeComments(postId: String): Flow<Result<List<Comment>>>

    /** Adds [text] as a comment on [postId] authored by [user]. */
    suspend fun addComment(postId: String, user: UserProfile, text: String): Result<Unit>
}
